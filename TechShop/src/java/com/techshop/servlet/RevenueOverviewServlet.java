package com.techshop.servlet;

import com.techshop.dao.BranchDAO;
import com.techshop.dao.SalesHistoryDAO;
import com.techshop.model.Branch;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/report")
public class RevenueOverviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String dateFrom = req.getParameter("dateFrom");
        String dateTo = req.getParameter("dateTo");
        String branchIdStr = req.getParameter("branchId");
        int branchId = 0;
        if (branchIdStr != null && !branchIdStr.isEmpty()) {
            try { branchId = Integer.parseInt(branchIdStr); } catch (NumberFormatException ignored) {}
        }

        SalesHistoryDAO dao = new SalesHistoryDAO();

        // Tổng quan
        BigDecimal[] overall = dao.getOverallStats(branchId, dateFrom, dateTo);
        req.setAttribute("totalInvoices", overall[0].intValue());
        req.setAttribute("totalRevenue", overall[1]);
        req.setAttribute("avgPerInvoice", overall[2]);

        // Doanh thu theo chi nhánh
        List<Object[]> byBranch = dao.getRevenueByBranch(dateFrom, dateTo);
        req.setAttribute("byBranch", byBranch);

        // Doanh thu theo ngày
        List<Object[]> daily = dao.getDailyRevenue(branchId, dateFrom, dateTo);
        req.setAttribute("daily", daily);

        // Dropdown chi nhánh
        List<Branch> branches = new BranchDAO().getAllActive();
        req.setAttribute("branches", branches);

        req.setAttribute("dateFrom", dateFrom != null ? dateFrom : "");
        req.setAttribute("dateTo", dateTo != null ? dateTo : "");
        req.setAttribute("branchId", branchId);
        req.setAttribute("pageTitle", "Báo cáo Doanh thu - TechShop");

        req.getRequestDispatcher("/views/Admin/revenue_overview.jsp").forward(req, resp);
    }
}
