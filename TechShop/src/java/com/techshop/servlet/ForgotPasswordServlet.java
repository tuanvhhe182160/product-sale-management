/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.PasswordDAO;
import com.techshop.dao.UserDAO;
import com.techshop.util.EmailUtil;
import java.io.IOException;
import java.io.PrintWriter;
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
                request.setAttribute("message", "Link đặt lại mật khẩu đã được gửi vào email của bạn.");
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi khi gửi email: " + e.getMessage());
            }
        } else {
            request.setAttribute("error", "Email không tồn tại trong hệ thống.");
        }
        doGet(request, response);
    }
}
