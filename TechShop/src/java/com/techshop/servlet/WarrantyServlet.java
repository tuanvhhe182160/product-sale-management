package com.techshop.servlet;

import com.techshop.dao.CustomerServiceDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;
import com.techshop.model.WarrantyCheckDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "WarrantyServlet", urlPatterns = {"/cs/warranty"})
public class WarrantyServlet extends HttpServlet {

    private final CustomerServiceDAO csDAO = new CustomerServiceDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    // Xử lý luồng GET: Hiển thị trang tra cứu và kết quả tra cứu IMEI
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

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
        
        // Lấy thông tin nhân viên CS đang thao tác
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer csUserId = (user != null) ? user.getUserId() : null;

        if (csUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Use Case: Create Warranty Request
            if ("create".equals(action)) {
                int invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
                int physicalId = Integer.parseInt(request.getParameter("physicalId"));
                int customerId = Integer.parseInt(request.getParameter("customerId"));
                String issueDescription = request.getParameter("issueDescription");
                String imei = request.getParameter("imei"); // Để ghi log/redirect

                boolean success = csDAO.createWarrantyRequest(invoiceId, physicalId, customerId, csUserId, issueDescription);

                if (success) {
                    // --- GHI LOG ---
                    logDAO.logAction(
                        csUserId, 
                        LogAction.CREATE_WARRANTY_REQUEST, 
                        EntityType.WARRANTY_REQUEST, 
                        null, 
                        request.getRemoteAddr(), 
                        "Tạo yêu cầu bảo hành mới cho IMEI: " + imei
                    );
                    
                    response.sendRedirect(request.getContextPath() + "/cs/warranty?action=check&imei=" + imei + "&message=Tạo yêu cầu bảo hành thành công!");
                } else {
                    response.sendRedirect(request.getContextPath() + "/cs/warranty?action=check&imei=" + imei + "&error=Có lỗi xảy ra khi tạo yêu cầu.");
                }
            } 
            
            // Use Case: Update Request Status
            else if ("update".equals(action)) {
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                String newStatus = request.getParameter("status"); 
                String note = request.getParameter("note");

                boolean success = csDAO.updateWarrantyStatus(requestId, newStatus, note, csUserId);

                if (success) {
                    // --- GHI LOG ---
                    logDAO.logAction(
                        csUserId, 
                        LogAction.UPDATE_WARRANTY_REQUEST, 
                        EntityType.WARRANTY_REQUEST, 
                        requestId, 
                        request.getRemoteAddr(), 
                        "Cập nhật trạng thái yêu cầu bảo hành thành: " + newStatus
                    );
                    
                    response.sendRedirect(request.getContextPath() + "/cs/warranty/list?message=Cập nhật trạng thái thành công!");
                } else {
                    response.sendRedirect(request.getContextPath() + "/cs/warranty/list?error=Có lỗi xảy ra khi cập nhật.");
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cs/warranty?error=Dữ liệu đầu vào không hợp lệ.");
        }
    }
}