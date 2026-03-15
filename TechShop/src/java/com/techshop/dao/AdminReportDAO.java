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
    public List<Map<String, Object>> getSalesByProduct(String from, String to, 
            Integer categoryId, Integer modelId, Integer variantId) {
        StringBuilder sql = new StringBuilder(
            "SELECT c.category_name, pm.model_name, pv.variant_name, " +
            "SUM(ii.quantity) as total_qty, SUM(ii.subtotal) as total_sales " +
            "FROM Invoice i " +
            "JOIN InvoiceItem ii ON i.invoice_id = ii.invoice_id " +
            "JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
            "JOIN ProductModel pm ON pv.model_id = pm.model_id " +
            "JOIN ProductCategory c ON pm.category_id = c.category_id " +
            "WHERE i.status = 'COMPLETED' " +
            "AND i.invoice_date BETWEEN ? AND ? "
        );
        
        List<Object> params = new ArrayList<>();

        params.add(from + " 00:00:00");
        params.add(to + " 23:59:59");

        if (categoryId != null) {
            sql.append(" AND c.category_id = ? ");
            params.add(categoryId);
        }

        if (modelId != null) {
            sql.append(" AND pm.model_id = ? ");
            params.add(modelId);
        }

        if (variantId != null) {
            sql.append(" AND pv.variant_id = ? ");
            params.add(variantId);
        }

        sql.append(" GROUP BY c.category_name, pm.model_name, pv.variant_name ");
        sql.append(" ORDER BY total_sales DESC ");
    
        return executeDynamicQuery(sql.toString(), params);
    }

    // Lấy doanh thu theo Chi nhánh (Sales by Branch)
    public List<Map<String, Object>> getSalesByBranch(String from, String to, Integer branchId, Integer employeeId) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Thêm tham số ngày tháng dùng chung cho cả 2 trường hợp
        params.add(from + " 00:00:00");
        params.add(to + " 23:59:59");

        if (branchId == null) {
            // TRƯỜNG HỢP 1: CHƯA CHỌN CHI NHÁNH -> Chỉ gom nhóm theo Chi nhánh
            sql.append("SELECT b.branch_name, ")
            .append("COUNT(i.invoice_id) as total_orders, ")
            .append("SUM(i.final_amount) as total_sales ")
            .append("FROM Invoice i ")
            .append("JOIN Branch b ON i.branch_id = b.branch_id ")
            .append("WHERE i.status = 'COMPLETED' ")
            .append("AND i.invoice_date BETWEEN ? AND ? ");
           
            // (Tùy chọn) Nếu vì lý do nào đó có truyền employeeId mà không có branchId
            if (employeeId != null) {
                sql.append("AND i.cashier_id = ? ");
                params.add(employeeId);
            }

            sql.append("GROUP BY b.branch_id, b.branch_name ");

        } else {
            // TRƯỜNG HỢP 2: ĐÃ CHỌN CHI NHÁNH -> Gom nhóm theo Nhân viên (đặt alias là employee_name để khớp với JSP)
            sql.append("SELECT u.full_name as employee_name, ")
            .append("COUNT(i.invoice_id) as total_orders, ")
            .append("SUM(i.final_amount) as total_sales ")
            .append("FROM Invoice i ")
            .append("JOIN dbo.[User] u ON i.cashier_id = u.user_id ")
            .append("WHERE i.status = 'COMPLETED' ")
            .append("AND i.invoice_date BETWEEN ? AND ? ")
            .append("AND i.branch_id = ? ");
        
            params.add(branchId);

            if (employeeId != null) {
                sql.append("AND u.user_id = ? ");
                params.add(employeeId);
            }

            sql.append("GROUP BY u.user_id, u.full_name ");
        }

        // Sắp xếp doanh thu giảm dần
        sql.append("ORDER BY total_sales DESC ");

        return executeDynamicQuery(sql.toString(), params);
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
    // Thống kê tổng quan tất cả chi nhánh: tổng đơn, tổng doanh thu
    public Map<String, Object> getBranchOverallStats(String from, String to) {
        String sql = "SELECT COUNT(i.invoice_id) as total_orders, " +
                     "ISNULL(SUM(i.final_amount), 0) as total_revenue, " +
                     "COUNT(DISTINCT i.branch_id) as total_branches " +
                     "FROM Invoice i " +
                     "WHERE i.status = 'COMPLETED' " +
                     "AND i.invoice_date BETWEEN ? AND ?";
        Map<String, Object> result = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, from + " 00:00:00");
            ps.setString(2, to + " 23:59:59");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.put("total_orders", rs.getInt("total_orders"));
                result.put("total_revenue", rs.getDouble("total_revenue"));
                result.put("total_branches", rs.getInt("total_branches"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Doanh thu theo từng chi nhánh (dùng cho biểu đồ + bảng)
    public List<Map<String, Object>> getBranchSalesChart(String from, String to) {
        String sql = "SELECT b.branch_id, b.branch_name, " +
                     "COUNT(i.invoice_id) as total_orders, " +
                     "ISNULL(SUM(i.final_amount), 0) as total_sales " +
                     "FROM Branch b " +
                     "LEFT JOIN Invoice i ON b.branch_id = i.branch_id " +
                     "AND i.status = 'COMPLETED' " +
                     "AND i.invoice_date BETWEEN ? AND ? " +
                     "WHERE b.status = 'ACTIVE' " +
                     "GROUP BY b.branch_id, b.branch_name " +
                     "ORDER BY total_sales DESC";
        List<Object> params = new ArrayList<>();
        params.add(from + " 00:00:00");
        params.add(to + " 23:59:59");
        return executeDynamicQuery(sql, params);
    }

    // Chi tiết doanh thu theo nhân viên trong 1 chi nhánh
    public List<Map<String, Object>> getBranchEmployeeDetail(String from, String to, int branchId) {
        String sql = "SELECT u.user_id, u.full_name as employee_name, " +
                     "COUNT(i.invoice_id) as total_orders, " +
                     "ISNULL(SUM(i.final_amount), 0) as total_sales " +
                     "FROM dbo.[User] u " +
                     "LEFT JOIN Invoice i ON u.user_id = i.cashier_id " +
                     "AND i.status = 'COMPLETED' " +
                     "AND i.invoice_date BETWEEN ? AND ? " +
                     "WHERE u.branch_id = ? AND u.status = 'ACTIVE' " +
                     "GROUP BY u.user_id, u.full_name " +
                     "ORDER BY total_sales DESC";
        List<Object> params = new ArrayList<>();
        params.add(from + " 00:00:00");
        params.add(to + " 23:59:59");
        params.add(branchId);
        return executeDynamicQuery(sql, params);
    }

    // Doanh thu theo ngày của 1 chi nhánh (dùng cho biểu đồ line)
    public List<Map<String, Object>> getBranchDailyRevenue(String from, String to, int branchId) {
        String sql = "SELECT CAST(i.invoice_date AS DATE) as sale_date, " +
                     "COUNT(i.invoice_id) as total_orders, " +
                     "SUM(i.final_amount) as total_sales " +
                     "FROM Invoice i " +
                     "WHERE i.status = 'COMPLETED' " +
                     "AND i.branch_id = ? " +
                     "AND i.invoice_date BETWEEN ? AND ? " +
                     "GROUP BY CAST(i.invoice_date AS DATE) " +
                     "ORDER BY sale_date";
        List<Object> params = new ArrayList<>();
        params.add(branchId);
        params.add(from + " 00:00:00");
        params.add(to + " 23:59:59");
        return executeDynamicQuery(sql, params);
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
    
    private List<Map<String, Object>> executeDynamicQuery(
        String sql, List<Object> params) {

        List<Map<String, Object>> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    map.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                result.add(map);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
