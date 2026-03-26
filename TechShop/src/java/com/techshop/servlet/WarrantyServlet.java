package com.techshop.servlet;

import com.techshop.dao.CustomerServiceDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.TechnicianDAO;
import com.techshop.model.User;
import com.techshop.model.WarrantyCheckDTO;
import com.techshop.model.WarrantyRequest;
import com.techshop.util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.util.UUID;

@WebServlet(name = "WarrantyServlet",
        urlPatterns = {"/cs/warranty", "/cs/warranty/list", "/cs/warranty/detail"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,   // 1 MB buffer
        maxFileSize       = 5 * 1024 * 1024,  // 5 MB / file
        maxRequestSize    = 10 * 1024 * 1024  // 10 MB tổng
)
public class WarrantyServlet extends HttpServlet {

    private static final String[] ALLOWED_EXT = {"jpg", "jpeg", "png", "webp"};

    private final CustomerServiceDAO csDAO  = new CustomerServiceDAO();
    private final SystemLogDAO       logDAO = new SystemLogDAO();

    // ── GET ──────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path   = request.getServletPath();
        String action = request.getParameter("action");

        // Danh sách phiếu CS
        if (path.endsWith("/list")) {
            User user = currentUser(request);
            if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
            int branchId = (user.getBranchId() != null) ? user.getBranchId() : 0;
            List<WarrantyRequest> list = csDAO.getWarrantyRequestsForCS(
                    branchId,
                    request.getParameter("search"),
                    request.getParameter("status"),
                    request.getParameter("fromDate"),
                    request.getParameter("toDate"));
            request.setAttribute("warrantyList", list);
            request.getRequestDispatcher("/views/cs/cs-warranty-list.jsp").forward(request, response);
            return;
        }

        // Chi tiết phiếu — dùng TechnicianDAO (đã có đủ JOIN)
        if (path.endsWith("/detail")) {
            try {
                int requestId = Integer.parseInt(request.getParameter("id"));
                TechnicianDAO techDAO = new TechnicianDAO();
                WarrantyRequest detail  = techDAO.getWarrantyDetail(requestId);
                List<com.techshop.model.WarrantyHistory> history = techDAO.getWarrantyHistory(requestId);
                if (detail != null) {
                    request.setAttribute("reqDetail",   detail);
                    request.setAttribute("historyList", history);
                    request.getRequestDispatcher("/views/cs/cs-warranty-detail.jsp")
                           .forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath()
                            + "/cs/warranty/list?error=Không tìm thấy yêu cầu.");
                }
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath()
                        + "/cs/warranty/list?error=Lỗi dữ liệu.");
            }
            return;
        }

        // Trang tra cứu IMEI
        if ("check".equals(action)) {
            String imei = request.getParameter("imei");
            if (imei != null && !imei.trim().isEmpty()) {
                WarrantyCheckDTO info = csDAO.checkWarrantyByImei(imei.trim());
                if (info != null) {
                    request.setAttribute("warrantyInfo", info);
                } else {
                    request.setAttribute("error", "Không tìm thấy thông tin cho IMEI: " + imei);
                }
                request.setAttribute("imei", imei);
            } else {
                request.setAttribute("error", "Vui lòng nhập số IMEI cần tra cứu.");
            }
        }
        request.getRequestDispatcher("/views/cs/warranty-check.jsp").forward(request, response);
    }

    // ── POST ─────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        if (!"create".equals(request.getParameter("action"))) {
            response.sendRedirect(request.getContextPath() + "/cs/warranty");
            return;
        }

        String imei = request.getParameter("imei");

        try {
            int    invoiceId  = Integer.parseInt(request.getParameter("invoiceId"));
            int    physicalId = Integer.parseInt(request.getParameter("physicalId"));
            int    customerId = Integer.parseInt(request.getParameter("customerId"));
            String issueDesc  = request.getParameter("issueDescription");

            User user = currentUser(request);
            int  csId = (user != null) ? user.getUserId() : 1;

            // Chặn trùng phiếu
            if (csDAO.hasActiveWarrantyRequest(physicalId)) {
                redirect(response, request.getContextPath()
                        + "/cs/warranty?action=check&imei=" + enc(imei)
                        + "&error=" + enc("Máy này đang có phiếu bảo hành chưa xử lý xong!"));
                return;
            }

            String imageUrl = null;
            Part filePart = request.getPart("image");

            if (filePart != null && filePart.getSize() > 0) {

                String ct = filePart.getContentType();
                if (ct == null || !ct.startsWith("image/")) {
                    redirect(response, request.getContextPath()
                            + "/cs/warranty?action=check&imei=" + enc(imei)
                            + "&error=" + enc("File không phải ảnh hợp lệ."));
                    return;
                }

                String origName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String ext      = getExt(origName).toLowerCase();
                if (!isAllowed(ext)) {
                    redirect(response, request.getContextPath()
                            + "/cs/warranty?action=check&imei=" + enc(imei)
                            + "&error=" + enc("Chỉ chấp nhận ảnh JPG, PNG, WEBP."));
                    return;
                }

                // Xác nhận file là ảnh thật (tránh file giả đổi đuôi)
                try (InputStream imgStream = filePart.getInputStream()) {
                    BufferedImage testImg = ImageIO.read(imgStream);
                    if (testImg == null) {
                        redirect(response, request.getContextPath()
                                + "/cs/warranty?action=check&imei=" + enc(imei)
                                + "&error=" + enc("File ảnh không đọc được hoặc bị hỏng."));
                        return;
                    }
                }

                // Lưu file với tên ngẫu nhiên (tránh collision và path traversal)
                String newName   = UUID.randomUUID().toString() + "." + ext;
                String uploadDir = getServletContext().getRealPath("/uploads/warranty");
                new File(uploadDir).mkdirs();
                filePart.write(uploadDir + File.separator + newName);
                imageUrl = "uploads/warranty/" + newName;
            }

            // Lưu DB
            boolean ok = csDAO.createWarrantyRequest(
                    invoiceId, physicalId, customerId, csId, issueDesc, imageUrl);

            if (ok) {
                // Gửi email thông báo
                try {
                    EmailUtil.sendNewWarrantyEmail(
                            request.getParameter("customerEmail"),
                            request.getParameter("customerName"),
                            "Mới tạo",
                            request.getParameter("variantName"),
                            imei, issueDesc);
                } catch (Exception ignored) {}

                redirect(response, request.getContextPath()
                        + "/cs/warranty?action=check&imei=" + enc(imei)
                        + "&message=" + enc("Tạo phiếu bảo hành thành công!"));
            } else {
                redirect(response, request.getContextPath()
                        + "/cs/warranty?action=check&imei=" + enc(imei)
                        + "&error=" + enc("Lỗi hệ thống khi lưu phiếu."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirect(response, request.getContextPath()
                    + "/cs/warranty?action=check&imei=" + enc(imei)
                    + "&error=" + enc("Dữ liệu không hợp lệ: " + e.getMessage()));
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? (User) s.getAttribute("user") : null;
    }

    private static void redirect(HttpServletResponse response, String url) throws IOException {
        response.sendRedirect(url);
    }

    private static String enc(String s) {
        try { return URLEncoder.encode(s == null ? "" : s, "UTF-8"); }
        catch (Exception e) { return ""; }
    }

    private static String getExt(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot + 1) : "";
    }

    private static boolean isAllowed(String ext) {
        for (String a : ALLOWED_EXT) if (a.equals(ext)) return true;
        return false;
    }
}