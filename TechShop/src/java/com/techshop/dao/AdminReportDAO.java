package com.techshop.dao;

import com.techshop.dal.DBContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 *
 * @author justi
 */
public class AdminReportDAO extends DBContext {
    // Lấy doanh thu theo Sản phẩm (Sales by Product)
    public List<Map<String, Object>> getSalesByProduct(String from, String to) {
        String sql = "SELECT pv.variant_name, SUM(ii.quantity) as total_qty, SUM(ii.subtotal) as total_sales " +
                 "FROM Invoice i JOIN InvoiceItem ii ON i.invoice_id = ii.invoice_id " +
                 "JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
                 "WHERE i.status = 'COMPLETED' AND i.invoice_date BETWEEN ? AND ? " +
                 "GROUP BY pv.variant_name ORDER BY total_sales DESC";
        return executeReportQuery(sql, from, to);
    }

    // Lấy doanh thu theo Chi nhánh (Sales by Branch)
    public List<Map<String, Object>> getSalesByBranch(String from, String to) {
        String sql = "SELECT b.branch_name, COUNT(i.invoice_id) as total_orders, SUM(i.final_amount) as total_sales " +
                 "FROM Invoice i JOIN Branch b ON i.branch_id = b.branch_id " +
                 "WHERE i.status = 'COMPLETED' AND i.invoice_date BETWEEN ? AND ? " +
                 "GROUP BY b.branch_name ORDER BY total_sales DESC";
        return executeReportQuery(sql, from, to);
    }
    
    //Lấy top 5 sản phẩm
    public List<Map<String, Object>> getTopProductChart(String start, String end) {
        String sql = "SELECT TOP 5 pv.variant_name as label, SUM(ii.quantity) as value " +
                 "FROM Invoice i JOIN InvoiceItem ii ON i.invoice_id = ii.invoice_id " +
                 "JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
                 "WHERE i.status = 'COMPLETED' AND i.invoice_date BETWEEN ? AND ? " +
                 "GROUP BY pv.variant_name ORDER BY value DESC";
        return executeReportQuery(sql, start, end);
    }
    
    //helper
    private List<Map<String, Object>> executeReportQuery(String sql, String start, String end) {
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, start + " 00:00:00");
            ps.setString(2, end + " 23:59:59");
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    map.put(meta.getColumnName(i), rs.getObject(i));
                }
                result.add(map);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
}
