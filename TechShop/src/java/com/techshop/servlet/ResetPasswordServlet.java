package com.techshop.servlet;

import com.techshop.dao.PasswordDAO;
import com.techshop.dao.UserDAO;
import com.techshop.model.User;
import com.techshop.util.AuthenticationUtil;
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
@WebServlet(name="ResetPasswordServlet", urlPatterns={"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {
   //private UserDAO userDAO = new UserDAO();
   private PasswordDAO passDAO = new PasswordDAO();
   
   protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String token = request.getParameter("token");
        // Kiểm tra token hợp lệ/hết hạn
        User user = passDAO.verifyResetToken(token);
        
        if (user == null) {
            request.setAttribute("error", "Link không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
        } else {
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
        }
    }
   
   protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 1. Kiểm tra mật khẩu khớp nhau (Server-side)
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        // 2. Kiểm tra độ mạnh mật khẩu theo chuẩn util
        if (!AuthenticationUtil.isPasswordStrong(newPassword)) {
            String feedback = AuthenticationUtil.getPasswordStrengthFeedback(newPassword);
            request.setAttribute("error", feedback);
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        boolean success = passDAO.resetPassword(token, newPassword);
        if (success) {
            // Redirect về login với thông báo thành công
            response.sendRedirect(request.getContextPath() + "/login?message=Reset success! Please login again.");
            return;
        } else {
            // Nếu thất bại (do token hết hạn hoặc sai), quay lại trang forgot-password
            request.setAttribute("error", "Link is invalid or has expired. Please request a new one.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
    }
}
