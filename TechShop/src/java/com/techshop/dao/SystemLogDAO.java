package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.SystemLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemLogDAO extends DBContext {

    // Hàm lấy danh sách log có kèm bộ lọc (Filter)
    public List<SystemLog> getLogsWithFilters(String startDate, String endDate, String action, String searchKeyword) {
        List<SystemLog> list = new ArrayList<>();
        
        // JOIN với bảng User để lấy tên người thực hiện
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, u.full_name AS user_name " +
            "FROM SystemLog s " +
            "LEFT JOIN [User] u ON s.user_id = u.user_id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // 1. Lọc theo ngày
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND CAST(s.created_at AS DATE) >= ? ");
            params.add(startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND CAST(s.created_at AS DATE) <= ? ");
            params.add(endDate);
        }

        // 2. Lọc theo Action (VD: LOGIN, CREATE_INVOICE, UPDATE_USER)
        if (action != null && !action.trim().isEmpty() && !action.equals("ALL")) {
            sql.append(" AND s.action = ? ");
            params.add(action);
        }

        // 3. Tìm kiếm theo keyword (Tên User, Entity Type, hoặc Details)
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR s.entity_type LIKE ? OR s.details LIKE ?) ");
            String likeKeyword = "%" + searchKeyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        sql.append(" ORDER BY s.created_at DESC"); // Luôn đưa log mới nhất lên đầu

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            // Đổ tham số vào PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SystemLog log = new SystemLog();
                    log.setLogId(rs.getInt("log_id"));
                    log.setUserId((Integer) rs.getObject("user_id"));
                    log.setAction(rs.getString("action"));
                    log.setEntityType(rs.getString("entity_type"));
                    log.setEntityId((Integer) rs.getObject("entity_id"));
                    log.setIpAddress(rs.getString("ip_address"));
                    log.setDetails(rs.getString("details"));
                    
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) log.setCreatedAt(createdAt.toLocalDateTime());
                    
                    log.setUserName(rs.getString("user_name"));
                    
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("SystemLogDAO.getLogsWithFilters Error: " + e.getMessage());
        }
        return list;
    }
    
    // Hàm lấy danh sách các Action duy nhất để đổ vào thẻ <select> lúc lọc
    public List<String> getUniqueActions() {
        List<String> actions = new ArrayList<>();
        String sql = "SELECT DISTINCT action FROM SystemLog WHERE action IS NOT NULL ORDER BY action";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                actions.add(rs.getString("action"));
            }
        } catch (SQLException e) {
            System.err.println("SystemLogDAO.getUniqueActions Error: " + e.getMessage());
        }
        return actions;
    }
    
    // Hàm ghi action vào db
    public void logAction(Integer userId, LogAction action, EntityType entityType, Integer entityId, String ipAddress, String details) {
        String sql = "INSERT INTO SystemLog (user_id, action, entity_type, entity_id, ip_address, details, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
    
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId); 
            else ps.setNull(1, java.sql.Types.INTEGER);
        
            ps.setString(2, action.name());
        
            if (entityType != null) ps.setString(3, entityType.name()); 
            else ps.setNull(3, java.sql.Types.NVARCHAR);
        
            if (entityId != null) ps.setInt(4, entityId); 
            else ps.setNull(4, java.sql.Types.INTEGER);
        
            if (ipAddress != null) ps.setString(5, ipAddress); 
            else ps.setNull(5, java.sql.Types.NVARCHAR);
        
            if (details != null) ps.setString(6, details); 
            else ps.setNull(6, java.sql.Types.NVARCHAR);
        
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SystemLogDAO.logAction Error: " + e.getMessage());
        }
    }
}