/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.PasswordDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.util.EmailUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author justi
 */
@WebServlet(name="ForgotPasswordServlet", urlPatterns={"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {  
    private PasswordDAO passDAO = new PasswordDAO();
    private SystemLogDAO logDAO = new SystemLogDAO();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        String token = passDAO.generateResetToken(email);
        
        if (token != null) {
            try {
                //Tạo link reset
                String resetLink = request.getScheme() + "://" + request.getServerName() + ":" 
                                 + request.getServerPort() + request.getContextPath() 
                                 + "/reset-password?token=" + token;
                
                //Gửi mail
                EmailUtil.sendResetEmail(email, resetLink);
                // --- GHI LOG YÊU CẦU THÀNH CÔNG ---
                logDAO.logAction(
                    null, // User chưa đăng nhập nên để null
                    LogAction.REQUEST_PASSWORD_RESET, 
                    EntityType.USER, 
                    null, 
                    request.getRemoteAddr(), 
                    "Yêu cầu đặt lại mật khẩu thành công cho email: " + email
                );
                // ----------------------------------
                request.setAttribute("message", "Link đặt lại mật khẩu đã được gửi vào email của bạn.");
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi gửi email: " + e.getMessage());
            }
        } else {
            // --- GHI LOG CẢNH BÁO DÒ EMAIL ---
            logDAO.logAction(
                null, 
                LogAction.REQUEST_PASSWORD_RESET, 
                EntityType.USER, 
                null, 
                request.getRemoteAddr(), 
                "Cảnh báo dò tài khoản: Yêu cầu reset cho email không tồn tại (" + email + ")"
            );
            // ---------------------------------
            request.setAttribute("error", "Email không tồn tại trong hệ thống.");
        }
        doGet(request, response);
    }
}
