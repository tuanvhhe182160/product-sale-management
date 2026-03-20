package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.FinancialReportItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO extends DBContext {

    public List<FinancialReportItem> getFinancialReport(String startDate, String endDate, Integer currentBranchId) {
        List<FinancialReportItem> list = new ArrayList<>();

        // FIX: Revenue must come from Invoice (one row per invoice).
        // Cost must come from InvoiceItem × cost_price.
        // Direct JOIN inflates SUM(final_amount) when invoice has multiple items.
        // Solution: compute cost per invoice in a subquery, then join once.
        String sql =
            "SELECT " +
            "    CAST(i.invoice_date AS DATE)          AS report_date, " +
            "    COUNT(i.invoice_id)                   AS total_orders, " +
            "    SUM(i.final_amount)                   AS total_revenue, " +
            "    ISNULL(SUM(c.invoice_cost), 0)        AS total_cost, " +
            "    SUM(i.final_amount) - ISNULL(SUM(c.invoice_cost), 0) AS total_profit " +
            "FROM Invoice i " +
            "LEFT JOIN ( " +
            "    SELECT ii.invoice_id, " +
            "           SUM(ii.quantity * pv.cost_price) AS invoice_cost " +
            "    FROM InvoiceItem ii " +
            "    JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
            "    GROUP BY ii.invoice_id " +
            ") c ON i.invoice_id = c.invoice_id " +
            "WHERE i.status = 'COMPLETED' ";

        if (currentBranchId != null && currentBranchId > 0) {
            sql += "AND i.branch_id = ? ";
        }

        boolean hasStart = (startDate != null && !startDate.isEmpty());
        boolean hasEnd   = (endDate   != null && !endDate.isEmpty());
        if (hasStart) sql += "AND CAST(i.invoice_date AS DATE) >= ? ";
        if (hasEnd)   sql += "AND CAST(i.invoice_date AS DATE) <= ? ";

        sql += "GROUP BY CAST(i.invoice_date AS DATE) " +
               "ORDER BY report_date ASC";   // ASC so chart renders left→right chronologically

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int idx = 1;
            if (currentBranchId != null && currentBranchId > 0) ps.setInt(idx++, currentBranchId);
            if (hasStart) ps.setString(idx++, startDate);
            if (hasEnd)   ps.setString(idx,   endDate);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FinancialReportItem item = new FinancialReportItem();
                item.setPeriod(rs.getString("report_date"));
                item.setTotalOrders(rs.getInt("total_orders"));
                item.setTotalRevenue(rs.getDouble("total_revenue"));
                item.setTotalCost(rs.getDouble("total_cost"));
                item.setTotalProfit(rs.getDouble("total_profit"));
                list.add(item);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("ReportDAO.getFinancialReport error: " + e.getMessage());
        }
        return list;
    }
}