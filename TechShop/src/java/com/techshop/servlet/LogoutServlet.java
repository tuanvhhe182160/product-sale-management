package com.techshop.servlet;

import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }
    

    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get current session (don't create new one if doesn't exist)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Log logout activity
            String userEmail = (String) session.getAttribute("userEmail");
            String userName = (String) session.getAttribute("userName");
            Integer userId = (Integer) session.getAttribute("userId");
            
            if (userEmail != null) {
                System.out.println("User logged out: " + userEmail + " (" + userName + ")");
            }
            
            // --- GHI LOG ---
            if (userId != null) {
                SystemLogDAO logDAO = new SystemLogDAO();
                logDAO.logAction(
                    userId, 
                    LogAction.LOGOUT, 
                    EntityType.SYSTEM, 
                    userId, 
                    request.getRemoteAddr(), 
                    "Người dùng đăng xuất khỏi hệ thống"
                );
            }
            // ----------------------------------------------
            
            // Invalidate session (destroy all session data)
            session.invalidate();
        }
        
        // Redirect to login page with success message
        response.sendRedirect(request.getContextPath() + "/login?message=You have been logged out successfully");
    }
}
