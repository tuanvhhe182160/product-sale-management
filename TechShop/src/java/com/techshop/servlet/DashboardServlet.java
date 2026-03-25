/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.AccountingDashboardDAO;
import com.techshop.dao.AdminDashboardDAO;
import com.techshop.dao.UserDAO;
import com.techshop.dao.BranchDAO;
import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.ProductModelDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.dao.SalesHistoryDAO;
import com.techshop.dao.TechnicianDAO;
import com.techshop.model.User;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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
        BranchDAO branchDAO = new BranchDAO();
        ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
        ProductModelDAO modelDAO = new ProductModelDAO();
        VariantDAO variantDAO = new VariantDAO();
 
        request.setAttribute("totalUsers",      userDAO.getAll().size());
        request.setAttribute("totalBranches",   branchDAO.getAll().size());
        request.setAttribute("activeBranches",  branchDAO.getAllActive().size());
        request.setAttribute("totalCategories", categoryDAO.getAllCategories().size());
        request.setAttribute("totalModels",     modelDAO.getAll().size());
        request.setAttribute("totalVariants",   variantDAO.getAllVariants().size());
        request.setAttribute("activeVariants",  variantDAO.getAllActive().size());
 
        // ── Live business data (from AdminDashboardDAO) ──────────────────
        AdminDashboardDAO dao = new AdminDashboardDAO();
 
        // Today KPI: [revenue, invoiceCount]
        BigDecimal[] today = dao.getTodayStats();
        request.setAttribute("todayRevenue",  today[0]);
        request.setAttribute("todayInvoices", today[1].intValue());
 
        // This month KPI: [revenue, invoiceCount, profit]
        BigDecimal[] month = dao.getThisMonthStats();
        request.setAttribute("monthRevenue",  month[0]);
        request.setAttribute("monthInvoices", month[1].intValue());
        request.setAttribute("monthProfit",   month[2]);
 
        // Alerts
        request.setAttribute("totalInStock",      dao.getTotalInStock());
        request.setAttribute("pendingInvoices",   dao.getTotalPendingInvoices());
        request.setAttribute("pendingWarranty",   dao.getTotalPendingWarranty());
 
        // Chart data
        request.setAttribute("branchRevenue",  dao.getRevenuByBranchThisMonth());
        request.setAttribute("trend30",        dao.getLast30DaysRevenue());
        request.setAttribute("top5Products",   dao.getTop5ProductsThisMonth());
        request.setAttribute("userByRole",     dao.getUserCountByRole());
 
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
        int branchId = (user.getBranchId() != null) ? user.getBranchId() : 0;
        if (branchId == 0) {
            // Admin should not land here, but guard anyway
            request.setAttribute("dashboardType", "accounting");
            return;
        }
 
        AccountingDashboardDAO dao = new AccountingDashboardDAO();
 
        // This month KPI: [revenue, profit, invoiceCount]
        BigDecimal[] monthKpi = dao.getThisMonthKpi(branchId);
        request.setAttribute("monthRevenue",  monthKpi[0]);
        request.setAttribute("monthProfit",   monthKpi[1]);
        request.setAttribute("monthInvoices", monthKpi[2].intValue());
 
        // Today KPI: [revenue, count]
        BigDecimal[] todayKpi = dao.getTodayKpi(branchId);
        request.setAttribute("todayRevenue",  todayKpi[0]);
        request.setAttribute("todayInvoices", todayKpi[1].intValue());
 
        // Pending reconciliation count (TRANSFER/MIXED awaiting confirmation)
        int pendingRecon = dao.getPendingReconciliationCount(branchId);
        request.setAttribute("pendingRecon", pendingRecon);
 
        // Last closed period [month, year]
        int[] lastPeriod = dao.getLastClosedPeriod(branchId);
        if (lastPeriod != null) {
            request.setAttribute("lastClosedMonth", lastPeriod[0]);
            request.setAttribute("lastClosedYear",  lastPeriod[1]);
        }
 
        // 7-day revenue trend for sparkline chart
        List<Object[]> trend = dao.getLast7DaysRevenue(branchId);
        request.setAttribute("revenueTrend", trend);
 
        // Payment method breakdown this month
        Map<String, BigDecimal> payBreakdown =
            dao.getPaymentMethodBreakdown(branchId);
        request.setAttribute("payBreakdown", payBreakdown);
 
        // Top 5 invoices today
        request.setAttribute("topInvoices", dao.getTodayTopInvoices(branchId));
        request.setAttribute("dashboardType", "accounting");
    }

    //customer service
    private void loadCustomerServiceDashboard(HttpServletRequest request, User user) {
        request.setAttribute("dashboardType", "customer_service");
    }

    // technician
    private void loadTechnicianDashboard(HttpServletRequest request, User user) {
        int branchId = (user.getBranchId() != null) ? user.getBranchId() : 0;
        
        TechnicianDAO techDAO = new TechnicianDAO();
        request.setAttribute("pendingCount", techDAO.countPendingRequests(branchId));
        request.setAttribute("inProgressCount", techDAO.countInProgressRequests(user.getUserId()));
        
        request.setAttribute("dashboardType", "technician");
    }
    
    //default 
    private void loadDefaultDashboard(HttpServletRequest request) {
        request.setAttribute("dashboardType", "default");
    }
}

