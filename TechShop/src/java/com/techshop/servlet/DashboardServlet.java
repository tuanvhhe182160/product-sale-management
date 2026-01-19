package com.techshop.servlet;

import com.techshop.dao.UserDAOTest;
import com.techshop.dao.BranchDAOTest;
import com.techshop.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Get user from session (guaranteed to exist because of AuthFilter)
        User currentUser = (User) session.getAttribute("user");
        String userRole = (String) session.getAttribute("userRole");
        
        // Load dashboard data based on role
        loadDashboardData(request, currentUser, userRole);
        
        // Forward to dashboard JSP
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }
    
    private void loadDashboardData(HttpServletRequest request, User user, String role) {
        try {            
            switch (role) {
                case "Admin":
                    loadAdminDashboard(request);
                    break;
                    
                case "Shop Manager":
                    loadManagerDashboard(request, user);
                    break;
                    
                case "Cashier":
                    loadCashierDashboard(request, user);
                    break;
                    
                default:
                    loadDefaultDashboard(request);
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadAdminDashboard(HttpServletRequest request) {        
        UserDAOTest userDAO = new UserDAOTest();
        int totalUsers = userDAO.getAll().size();
        
        //BranchDAO branchDAO = new BranchDAO();
        BranchDAOTest branchDAO = new BranchDAOTest();
        int totalBranches = branchDAO.getAll().size();
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalBranches", totalBranches);
        request.setAttribute("dashboardType", "admin");
    }
    
    private void loadManagerDashboard(HttpServletRequest request, User user) {       
        if (user.getBranchId() != null) {
            UserDAOTest userDAO = new UserDAOTest();
            int branchStaff = userDAO.getAllByBranch(user.getBranchId()).size();
            
            request.setAttribute("branchStaff", branchStaff);
        }
        
        request.setAttribute("dashboardType", "manager");
    }
    
    private void loadCashierDashboard(HttpServletRequest request, User user) {       
        request.setAttribute("dashboardType", "cashier");
    }
    
    private void loadDefaultDashboard(HttpServletRequest request) {
        request.setAttribute("dashboardType", "default");
    }
}
