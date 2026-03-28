package com.techshop.servlet;

import com.techshop.dao.TechnicianDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.ProductVariant;
import com.techshop.model.User;
import com.techshop.model.VariantAttribute;
import com.techshop.model.WarrantyHistory;
import com.techshop.model.WarrantyRequest;
import com.techshop.model.WarrantyStatus;
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

@WebServlet(name = "TechnicianServlet", urlPatterns = {"/tech/warranty", "/tech/warranty/list", "/tech/warranty/detail"})
public class TechnicianServlet extends HttpServlet {
    private final TechnicianDAO techDAO = new TechnicianDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Luồng 1: Hiển thị danh sách và bộ lọc
            if (path.endsWith("/list") || path.equals("/tech/warranty")) {
                String search = request.getParameter("search");
                String status = request.getParameter("status");
                String fromDate = request.getParameter("fromDate");
                String toDate = request.getParameter("toDate");
                int branchId = (user.getBranchId() != null) ? user.getBranchId() : 0;

                // Lấy danh sách từ DAO
                List<WarrantyRequest> list = techDAO.getWarrantyRequests(branchId, search, status, fromDate, toDate);
                
                request.setAttribute("requestList", list);
                request.getRequestDispatcher("/views/tech/tech-warranty-list.jsp").forward(request, response);
            } 
            // Luồng 2: Xem chi tiết 1 yêu cầu bảo hành
            else if (path.endsWith("/detail")) {
                int requestId = Integer.parseInt(request.getParameter("id"));
                
                // Lấy chi tiết và lịch sử
                WarrantyRequest detail = techDAO.getWarrantyDetail(requestId);
                List<WarrantyHistory> history = techDAO.getWarrantyHistory(requestId);
                
                if (detail != null) {
                    VariantDAO variantDAO = new VariantDAO();
               
                    // 1. Lấy thông tin gốc (Để lấy warrantyMonths)
                    ProductVariant variantDetail = variantDAO.getVariantById(detail.getVariantId());
                    // 2. Lấy danh sách thông số linh hoạt (RAM, ROM, CPU, Màu...)
                    List<VariantAttribute> variantAttributes = variantDAO.getAttributesByVariantId(detail.getVariantId());
                
                    request.setAttribute("variantDetail", variantDetail);
                    request.setAttribute("variantAttributes", variantAttributes);
                    request.setAttribute("reqDetail", detail);
                    request.setAttribute("historyList", history);
                    request.getRequestDispatcher("/views/tech/tech-warranty-detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/tech/warranty/list?error=Không tìm thấy yêu cầu bảo hành.");
                }
            }
        } catch (Exception e) {
            System.err.println("=== LỖI KHI CẬP NHẬT BẢO HÀNH ===");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/tech/warranty/list?error=Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer techId = (user != null) ? user.getUserId() : null;

        if (techId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String requestCode = request.getParameter("requestCode");
            String customerEmail = request.getParameter("customerEmail");
            String customerName = request.getParameter("customerName");
            
            WarrantyStatus newStatus = null;
            String note = request.getParameter("note");
            note = (note != null && !note.trim().isEmpty()) ? note.trim() : "Không có ghi chú thêm.";
            
            String resolution = request.getParameter("resolution");
            resolution = (resolution != null) ? resolution.trim() : "";

            // Phân loại Action theo yêu cầu bằng Enum
            if ("accept".equals(action)) {
                newStatus = WarrantyStatus.IN_PROGRESS;
                note = "Kỹ thuật viên tiếp nhận và bắt đầu chẩn đoán: " + note;
            } else if ("update".equals(action)) {
                newStatus = WarrantyStatus.IN_PROGRESS;
                note = "Cập nhật tiến độ: " + note;
            } else if ("complete".equals(action)) {
                newStatus = WarrantyStatus.COMPLETED;
                note = "Hoàn thành sửa chữa. Hướng giải quyết: " + resolution;
            } else if ("reject".equals(action)) {
                newStatus = WarrantyStatus.REJECTED;
                note = "Từ chối bảo hành. Lý do: " + resolution;
            }

            // Thực thi cập nhật DB
            boolean success = techDAO.processWarrantyRequest(requestId, techId, newStatus, note, resolution);

            if (success) {
                // --- KÍCH HOẠT GỬI EMAIL TỰ ĐỘNG ---
                EmailUtil.sendWarrantyStatusEmail(customerEmail, customerName, requestCode, newStatus.name(), note);

                // --- GHI AUDIT LOG ---
                logDAO.logAction(
                    techId, 
                    LogAction.UPDATE_WARRANTY_REQUEST, 
                    EntityType.WARRANTY_REQUEST, 
                    requestId, 
                    request.getRemoteAddr(), 
                    "Kỹ thuật viên cập nhật trạng thái #" + requestCode + " thành: " + newStatus.name()
                );

                String successMsg = URLEncoder.encode("Cập nhật tiến độ thành công!", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/tech/warranty/detail?id=" + requestId + "&message=" + successMsg);
            } else {
                response.sendRedirect(request.getContextPath() + "/tech/warranty/detail?id=" + requestId + "&error=Có lỗi xảy ra khi lưu dữ liệu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/tech/warranty?error=Dữ liệu không hợp lệ.");
        }
    }
}