package com.techshop.servlet;

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
            
            if (userEmail != null) {
                System.out.println("User logged out: " + userEmail + " (" + userName + ")");
            }
            
            // Invalidate session (destroy all session data)
            session.invalidate();
        }
        
        // Redirect to login page with success message
        response.sendRedirect(request.getContextPath() + "/login?message=You have been logged out successfully");
    }
}
