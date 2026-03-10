package com.techshop.servlet;

import com.techshop.dao.UserDAO;
import com.techshop.dao.BranchDAO;
import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.ProductModelDAO;
import com.techshop.dao.ReportDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.FinancialReportItem;
import com.techshop.dao.UserDAOTest;
import com.techshop.dao.BranchDAOTest;
import com.techshop.dao.ProductCategoryDAOTest;
import com.techshop.dao.ProductModelDAOTest;
import com.techshop.dao.ProductVariantDAOTest;
import com.techshop.dao.SalesHistoryDAO;
import com.techshop.model.User;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        User currentUser = (User) session.getAttribute("user");
        String userRole = (String) session.getAttribute("userRole");
        
        loadDashboardData(request, currentUser, userRole);      
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }
    
    //dash board data
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
                    
                case "Accounting Staff":
                    loadAccountingDashboard(request, user);
                    break;

                case "Customer Service":
                    loadCustomerServiceDashboard(request, user);
                    break;

                case "Technician":
                    loadTechnicianDashboard(request, user);
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
        UserDAO userDAO = new UserDAO();
        int totalUsers = userDAO.getAll().size();
        
        BranchDAO branchDAO = new BranchDAO();
        int totalBranches = branchDAO.getAll().size();
        int activeBranches = branchDAO.getAllActive().size();
        
        ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
        int totalCategories = categoryDAO.getAllCategories().size();
        
        ProductModelDAO modelDAO = new ProductModelDAO();
        int totalModels = modelDAO.getAll().size();
        
        VariantDAO variantDAO = new VariantDAO();
        int totalVariants = variantDAO.getAllVariants().size();
        int activeVariants = variantDAO.getAllActive().size();
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalBranches", totalBranches);
        request.setAttribute("activeBranches", activeBranches);
        request.setAttribute("totalCategories", totalCategories);
        request.setAttribute("totalModels", totalModels);
        request.setAttribute("totalVariants", totalVariants);
        request.setAttribute("activeVariants", activeVariants);
        request.setAttribute("dashboardType", "admin");
    }
    
    //shop manager
    private void loadManagerDashboard(HttpServletRequest request, User user) {
        if (user.getBranchId() != null) {
            UserDAO userDAO = new UserDAO();
            int branchStaff = userDAO.getAllByBranch(user.getBranchId()).size();                      
            request.setAttribute("branchStaff", branchStaff);
        }
        
        request.setAttribute("dashboardType", "manager");
    }
    
    //cashier
    private void loadCashierDashboard(HttpServletRequest request, User user) {
        SalesHistoryDAO dao = new SalesHistoryDAO();
        int cashierId = user.getUserId();

        // Thống kê bán hàng: [todayCount, todayRevenue, monthRevenue]
        BigDecimal[] stats = dao.getStats(cashierId);
        request.setAttribute("todayCount", stats[0].intValue());
        request.setAttribute("todayRevenue", stats[1]);
        request.setAttribute("monthRevenue", stats[2]);

        // Tổng hóa đơn đã hoàn thành
        int totalCompleted = dao.countTotalCompleted(cashierId);
        request.setAttribute("totalCompleted", totalCompleted);

        // Số sản phẩm vật lý (PhysicalProduct) IN_STOCK tại chi nhánh
        int branchId = user.getBranchId() != null ? user.getBranchId() : 0;
        int inStockCount = branchId > 0 ? dao.countInStockByBranch(branchId) : 0;
        request.setAttribute("inStockCount", inStockCount);

        request.setAttribute("dashboardType", "cashier");
    }
    
    //accounting
    private void loadAccountingDashboard(HttpServletRequest request, User user) {
        try {
            // Sử dụng ReportDAO để lấy dữ liệu 30 ngày gần nhất
            ReportDAO reportDAO = new com.techshop.dao.ReportDAO();
            LocalDate today = java.time.LocalDate.now();
            LocalDate thirtyDaysAgo = today.minusDays(30);
        
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("user");
            int branchId = currentUser.getBranchId();
            List<FinancialReportItem> recentData = 
                reportDAO.getFinancialReport(thirtyDaysAgo.toString(), today.toString(), branchId);
            
            double totalRevenue30Days = 0;
            double totalProfit30Days = 0;
            int totalInvoices30Days = 0;
        
            for (FinancialReportItem item : recentData) {
                totalRevenue30Days += item.getTotalRevenue();
                totalProfit30Days += item.getTotalProfit();
                totalInvoices30Days += item.getTotalOrders();
            }

            // Đẩy dữ liệu lên JSP
            request.setAttribute("totalRevenue30Days", totalRevenue30Days);
            request.setAttribute("totalProfit30Days", totalProfit30Days);
            request.setAttribute("totalInvoices30Days", totalInvoices30Days);
        
        } catch (Exception e) {
            System.err.println("Error loading Accounting Dashboard: " + e.getMessage());
        }    
        
        request.setAttribute("dashboardType", "accounting");
    }

    //customer service
    private void loadCustomerServiceDashboard(HttpServletRequest request, User user) {
        request.setAttribute("dashboardType", "customer_service");
    }

    // technician
    private void loadTechnicianDashboard(HttpServletRequest request, User user) {
        request.setAttribute("dashboardType", "technician");
    }
    
    //default 
    private void loadDefaultDashboard(HttpServletRequest request) {
        request.setAttribute("dashboardType", "default");
    }
}

