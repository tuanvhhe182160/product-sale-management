package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.FinancialReportItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO extends DBContext {

    public List<FinancialReportItem> getFinancialReport(String startDate, String endDate) {
        List<FinancialReportItem> list = new ArrayList<>();
        
        // Nhóm theo ngày (CAST AS DATE), tính doanh thu từ subtotal, chi phí từ cost_price
        String sql = "SELECT " +
                     "    CAST(i.invoice_date AS DATE) AS report_date, " +
                     "    COUNT(DISTINCT i.invoice_id) AS total_orders, " +
                     "    SUM(ii.subtotal) AS total_revenue, " +
                     "    SUM(ii.quantity * pv.cost_price) AS total_cost, " +
                     "    SUM(ii.subtotal) - SUM(ii.quantity * pv.cost_price) AS total_profit " +
                     "FROM Invoice i " +
                     "JOIN InvoiceItem ii ON i.invoice_id = ii.invoice_id " +
                     "JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
                     "WHERE i.status = 'COMPLETED' ";
        
        // Thêm điều kiện lọc theo ngày nếu có
        boolean hasStart = (startDate != null && !startDate.isEmpty());
        boolean hasEnd = (endDate != null && !endDate.isEmpty());
        
        if (hasStart) sql += " AND CAST(i.invoice_date AS DATE) >= ? ";
        if (hasEnd) sql += " AND CAST(i.invoice_date AS DATE) <= ? ";
        
        sql += "GROUP BY CAST(i.invoice_date AS DATE) " +
               "ORDER BY report_date DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int paramIndex = 1;
            
            if (hasStart) ps.setString(paramIndex++, startDate);
            if (hasEnd) ps.setString(paramIndex, endDate);
            
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