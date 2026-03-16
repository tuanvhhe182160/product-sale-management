package com.techshop.dao;

import com.techshop.dal.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAnalyticsDAO extends DBContext {

    // 1. Thống kê tổng quan & Yêu cầu 4 (Kèm Thời gian trung bình và Chi phí mô phỏng)
    public Map<String, Object> getGeneralWarrantyStats() {
        Map<String, Object> stats = new HashMap<>();
        // Tính thời gian trung bình (Theo giờ) cho các ca đã hoàn thành
        String sql = "SELECT " +
                     "  COUNT(*) AS total_requests, " +
                     "  SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS total_completed, " +
                     "  SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS total_pending, " +
                     "  SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS total_in_progress, " +
                     "  SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) AS total_rejected, " +
                     "  AVG(CASE WHEN status = 'COMPLETED' THEN DATEDIFF(HOUR, request_date, completion_date) ELSE NULL END) AS avg_repair_hours " +
                     "FROM WarrantyRequest";
                     
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalRequests", rs.getInt("total_requests"));
                stats.put("totalCompleted", rs.getInt("total_completed"));
                stats.put("totalPending", rs.getInt("total_pending"));
                stats.put("totalInProgress", rs.getInt("total_in_progress"));
                stats.put("totalRejected", rs.getInt("total_rejected"));
                
                int avgHours = rs.getInt("avg_repair_hours");
                stats.put("avgRepairTime", avgHours > 0 ? avgHours + " giờ" : "Chưa có data");
                
                int total = rs.getInt("total_requests");
                int completed = rs.getInt("total_completed");
                double completionRate = (total > 0) ? Math.round(((double) completed / total) * 100.0) : 0;
                stats.put("completionRate", completionRate);

                // Mô phỏng Yêu cầu 2 (Cost Analysis): Giả sử mỗi ca hoàn thành tốn 350,000đ tiền linh kiện
                long estimatedCost = completed * 350000L; 
                stats.put("totalCost", estimatedCost);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // 2. Yêu cầu 1: Hiệu suất Kỹ thuật viên (Kèm thời gian xử lý trung bình)
    public List<Map<String, Object>> getTechnicianPerformance() {
        List<Map<String, Object>> list = new ArrayList<>();
        // Dùng LEFT JOIN User để lấy cả những thợ chưa có tên, tránh bị trống list
        String sql = "SELECT " +
                     "  ISNULL(u.full_name, 'Chưa phân công') AS full_name, " +
                     "  COUNT(wr.request_id) AS handled_requests, " +
                     "  SUM(CASE WHEN wr.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_requests, " +
                     "  AVG(CASE WHEN wr.status = 'COMPLETED' THEN DATEDIFF(HOUR, wr.request_date, wr.completion_date) ELSE NULL END) AS avg_hours " +
                     "FROM WarrantyRequest wr " +
                     "LEFT JOIN [User] u ON wr.technician_id = u.user_id " +
                     "WHERE wr.technician_id IS NOT NULL " +
                     "GROUP BY u.full_name " +
                     "ORDER BY handled_requests DESC";
                     
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("technicianName", rs.getString("full_name"));
                row.put("handledRequests", rs.getInt("handled_requests"));
                row.put("completedRequests", rs.getInt("completed_requests"));
                row.put("avgTime", rs.getInt("avg_hours"));
                list.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. Phân tích lỗi theo Sản phẩm (Warranty by Product)
    public List<Map<String, Object>> getDefectRateByProduct() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TOP 10 " +
                     "  pv.variant_name, " +
                     "  COUNT(wr.request_id) AS defect_count " +
                     "FROM WarrantyRequest wr " +
                     "JOIN PhysicalProduct pp ON wr.physical_id = pp.physical_id " +
                     "JOIN ProductVariant pv ON pp.variant_id = pv.variant_id " +
                     "GROUP BY pv.variant_name " +
                     "ORDER BY defect_count DESC";
                     
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("productName", rs.getString("variant_name"));
                row.put("defectCount", rs.getInt("defect_count"));
                list.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    
}