package com.techshop.servlet;

import com.techshop.dao.CustomerServiceDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.TechnicianDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;
import com.techshop.model.WarrantyCheckDTO;
import com.techshop.model.WarrantyRequest;
import com.techshop.util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "WarrantyServlet", urlPatterns = {"/cs/warranty", "/cs/warranty/list", "/cs/warranty/detail"})
public class WarrantyServlet extends HttpServlet {

    private final CustomerServiceDAO csDAO = new CustomerServiceDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    // Xử lý luồng GET: Hiển thị trang tra cứu và kết quả tra cứu IMEI
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String action = request.getParameter("action");
        
        // Màn hình theo dõi danh sách bảo hành cho CS
        if (path.endsWith("/list")) {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            int branchId = (user.getBranchId() != null) ? user.getBranchId() : 0;
            
            String keyword = request.getParameter("search");
            String status = request.getParameter("status");
            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");

            List<WarrantyRequest> warrantyList = csDAO.getWarrantyRequestsForCS(branchId, keyword, status, fromDate, toDate);
            
            request.setAttribute("warrantyList", warrantyList);
            request.getRequestDispatcher("/views/cs/cs-warranty-list.jsp").forward(request, response);
            return;
        }

        if (path.endsWith("/detail")) {
            try {
                int requestId = Integer.parseInt(request.getParameter("id"));
                
                // Tái sử dụng TechnicianDAO để lấy dữ liệu
                TechnicianDAO techDAO = new TechnicianDAO();
                WarrantyRequest detail = techDAO.getWarrantyDetail(requestId);
                List<com.techshop.model.WarrantyHistory> history = techDAO.getWarrantyHistory(requestId);
                
                if (detail != null) {
                    request.setAttribute("reqDetail", detail);
                    request.setAttribute("historyList", history);
                    request.getRequestDispatcher("/views/cs/cs-warranty-detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/cs/warranty/list?error=Không tìm thấy yêu cầu bảo hành.");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/cs/warranty/list?error=Mã bảo hành không hợp lệ.");
            }
            return;
        }
        
        // Use Case: Check Warranty Status / Search Order by IMEI
        if ("check".equals(action)) {
            String imei = request.getParameter("imei");
            if (imei != null && !imei.trim().isEmpty()) {
                WarrantyCheckDTO warrantyInfo = csDAO.checkWarrantyByImei(imei.trim());
                
                if (warrantyInfo != null) {
                    request.setAttribute("warrantyInfo", warrantyInfo);
                } else {
                    request.setAttribute("error", "Không tìm thấy thông tin hóa đơn cho IMEI: " + imei + " (hoặc sản phẩm chưa được bán).");
                }
                request.setAttribute("imei", imei); // Giữ lại giá trị ô search
            } else {
                request.setAttribute("error", "Vui lòng nhập số IMEI cần tra cứu.");
            }
        }

        // Forward sang trang giao diện của CS
        request.getRequestDispatcher("/views/cs/warranty-check.jsp").forward(request, response);
    }

    // Xử lý luồng POST: Tạo mới yêu cầu bảo hành & Cập nhật trạng thái
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            try {
                int invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
                int physicalId = Integer.parseInt(request.getParameter("physicalId"));
                int customerId = Integer.parseInt(request.getParameter("customerId"));
                String imei = request.getParameter("imei");
                String issueDesc = request.getParameter("issueDescription");
                
                // Lấy ID nhân viên CS đang đăng nhập
                HttpSession session = request.getSession(false);
                com.techshop.model.User user = (session != null) ? (com.techshop.model.User) session.getAttribute("user") : null;
                int csId = (user != null) ? user.getUserId() : 1; 

                // 👉 1. KIỂM TRA: MÁY CÓ ĐANG NẰM VIỆN KHÔNG?
                if (csDAO.hasActiveWarrantyRequest(physicalId)) {
                    String errorMsg = java.net.URLEncoder.encode("Lỗi: Máy có IMEI " + imei + " đang có yêu cầu bảo hành chưa xử lý xong!", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/cs/warranty?action=check&imei=" + imei + "&error=" + errorMsg);
                    return;
                }

                // 👉 2. TẠO PHIẾU
                boolean success = csDAO.createWarrantyRequest(invoiceId, physicalId, customerId, csId, issueDesc);

                // 👉 3. REDIRECT CHỐNG TRẮNG TRANG
                if (success) {
                    String customerEmail = request.getParameter("customerEmail");
                    String customerName = request.getParameter("customerName");
                    String productName = request.getParameter("variantName");
                    String tempRequestCode = "Mới tạo (Đang đồng bộ)";
                    EmailUtil.sendNewWarrantyEmail(customerEmail, customerName, tempRequestCode, productName, imei, issueDesc);
                    
                    String successMsg = URLEncoder.encode("Tạo yêu cầu bảo hành thành công!", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/cs/warranty?action=check&imei=" + imei + "&message=" + successMsg);
                } else {
                    String errorMsg = java.net.URLEncoder.encode("Lỗi hệ thống khi lưu yêu cầu.", "UTF-8");
                    response.sendRedirect(request.getContextPath() + "/cs/warranty?action=check&imei=" + imei + "&error=" + errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errorMsg = java.net.URLEncoder.encode("Dữ liệu đầu vào không hợp lệ.", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/cs/warranty?error=" + errorMsg);
            }
        }
    }
}