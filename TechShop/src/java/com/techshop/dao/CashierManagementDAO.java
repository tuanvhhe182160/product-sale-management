package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO quản lý Cashier cho Admin / Shop Manager.
 * Chỉ chứa các query riêng cho trang quản lý.
 * Các hàm chung (insert, updateStatus, isEmailExist) dùng UserDAO.
 * Danh sách chi nhánh dùng BranchDAO.
 */
public class CashierManagementDAO extends DBContext {

    /**
     * Danh sách cashier theo chi nhánh, có search + filter status.
     * branchId=0 → tất cả chi nhánh.
     */
    public List<User> getCashiers(int branchId, String search, String status) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT u.user_id, u.email, u.full_name, u.phone, " +
            "       u.role_id, u.branch_id, u.status, " +
            "       u.created_at, u.updated_at, " +
            "       r.role_name, b.branch_name " +
            "FROM [User] u " +
            "LEFT JOIN Role r ON u.role_id = r.role_id " +
            "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
            "WHERE r.role_name = 'Cashier' "
        );
        List<Object> params = new ArrayList<>();

        if (branchId > 0) {
            sql.append("AND u.branch_id = ? ");
            params.add(branchId);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (u.full_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?) ");
            String like = "%" + search.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (status != null && !status.trim().isEmpty() && !"ALL".equals(status)) {
            sql.append("AND u.status = ? ");
            params.add(status.trim());
        }
        sql.append("ORDER BY u.full_name");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractUser(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Thống kê bán hàng của 1 cashier, hỗ trợ filter tháng/năm.
     * Trả BigDecimal[5]: [todayCount, todayRevenue, monthCount, monthRevenue, totalCount]
     * month=0 hoặc year=0 → dùng tháng/năm hiện tại.
     */
    public BigDecimal[] getCashierStats(int cashierId, int month, int year) {
        BigDecimal[] s = new BigDecimal[5];
        for (int i = 0; i < 5; i++) s[i] = BigDecimal.ZERO;

        String mc = (month > 0 && year > 0)
            ? "MONTH(invoice_date)=" + month + " AND YEAR(invoice_date)=" + year
            : "MONTH(invoice_date)=MONTH(GETDATE()) AND YEAR(invoice_date)=YEAR(GETDATE())";

        String sql =
            "SELECT " +
            "  SUM(CASE WHEN CAST(invoice_date AS DATE)=CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS tc, " +
            "  SUM(CASE WHEN CAST(invoice_date AS DATE)=CAST(GETDATE() AS DATE) THEN final_amount ELSE 0 END) AS tr, " +
            "  SUM(CASE WHEN " + mc + " THEN 1 ELSE 0 END) AS mc, " +
            "  SUM(CASE WHEN " + mc + " THEN final_amount ELSE 0 END) AS mr, " +
            "  COUNT(*) AS total " +
            "FROM Invoice WHERE cashier_id=? AND status='COMPLETED'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cashierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s[0] = nvl(rs.getBigDecimal("tc"));
                    s[1] = nvl(rs.getBigDecimal("tr"));
                    s[2] = nvl(rs.getBigDecimal("mc"));
                    s[3] = nvl(rs.getBigDecimal("mr"));
                    s[4] = nvl(rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return s;
    }

    /**
     * Thống kê tổng hợp chi nhánh: HĐ hôm nay, DT hôm nay, DT tháng, số cashier active.
     * Trả BigDecimal[4].
     */
    public BigDecimal[] getBranchStats(int branchId) {
        BigDecimal[] s = new BigDecimal[4];
        for (int i = 0; i < 4; i++) s[i] = BigDecimal.ZERO;

        String sql =
            "SELECT " +
            "  SUM(CASE WHEN CAST(i.invoice_date AS DATE)=CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS tc, " +
            "  SUM(CASE WHEN CAST(i.invoice_date AS DATE)=CAST(GETDATE() AS DATE) THEN i.final_amount ELSE 0 END) AS tr, " +
            "  SUM(CASE WHEN MONTH(i.invoice_date)=MONTH(GETDATE()) AND YEAR(i.invoice_date)=YEAR(GETDATE()) THEN i.final_amount ELSE 0 END) AS mr, " +
            "  (SELECT COUNT(*) FROM [User] u JOIN Role r ON u.role_id=r.role_id " +
            "   WHERE r.role_name='Cashier' AND u.branch_id=? AND u.status='ACTIVE') AS ac " +
            "FROM Invoice i WHERE i.branch_id=? AND i.status='COMPLETED'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s[0] = nvl(rs.getBigDecimal("tc"));
                    s[1] = nvl(rs.getBigDecimal("tr"));
                    s[2] = nvl(rs.getBigDecimal("mr"));
                    s[3] = nvl(rs.getBigDecimal("ac"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return s;
    }

    private BigDecimal nvl(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }

    /**
     * Lấy role_id của Cashier từ bảng Role.
     */
    public int getCashierRoleId() {
        String sql = "SELECT role_id FROM Role WHERE role_name = 'Cashier'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("role_id");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setEmail(rs.getString("email"));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setRoleId(rs.getInt("role_id"));
        int bid = rs.getInt("branch_id");
        u.setBranchId(rs.wasNull() ? null : bid);
        u.setStatus(rs.getString("status"));
        Timestamp c = rs.getTimestamp("created_at");
        if (c != null) u.setCreatedAt(c.toLocalDateTime());
        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null) u.setUpdatedAt(up.toLocalDateTime());
        u.setRoleName(rs.getString("role_name"));
        u.setBranchName(rs.getString("branch_name"));
        return u;
    }
}
