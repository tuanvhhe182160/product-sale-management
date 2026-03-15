package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DBContext {
    
    private static final String SELECT_USER = 
        "SELECT u.user_id, u.email, u.full_name, u.phone, u.avatar_url, " +
        "       u.role_id, u.branch_id, u.status, " +
        "       u.created_at, u.updated_at, " +
        "       r.role_name, b.branch_name " +
        "FROM [User] u " +
        "LEFT JOIN Role r ON u.role_id = r.role_id " +
        "LEFT JOIN Branch b ON u.branch_id = b.branch_id ";

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = SELECT_USER + "ORDER BY u.user_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(extractUserFromResultSet(rs)); }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public User getById(int id) {
        String sql = SELECT_USER + "WHERE u.user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { User u = extractUserFromResultSet(rs); rs.close(); ps.close(); return u; }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getByEmail(String email) {
        String sql = SELECT_USER + "WHERE u.email = ? AND u.status = 'ACTIVE'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { User u = extractUserFromResultSet(rs); rs.close(); ps.close(); return u; }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getAllByRole(int roleId) {
        List<User> list = new ArrayList<>();
        String sql = SELECT_USER + "WHERE u.role_id = ? ORDER BY u.user_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(extractUserFromResultSet(rs)); }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> getAllByBranch(int branchId) {
        List<User> list = new ArrayList<>();
        String sql = SELECT_USER + "WHERE u.branch_id = ? ORDER BY u.user_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(extractUserFromResultSet(rs)); }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> getAllCashier() {
        List<User> list = new ArrayList<>();
        String sql = SELECT_USER + "WHERE u.role_id = 3 ORDER BY u.user_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(extractUserFromResultSet(rs)); }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> getCashiersByBranchId(int branchId) {
        List<User> list = new ArrayList<>();
        String sql = SELECT_USER + "WHERE u.role_id = 3 AND u.branch_id = ? ORDER BY u.user_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(extractUserFromResultSet(rs)); }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(User user) {
        String sql = "INSERT INTO [User] (email, full_name, phone, role_id, branch_id, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getRoleId());
            if (user.getBranchId() != null) { ps.setInt(5, user.getBranchId()); }
            else { ps.setNull(5, Types.INTEGER); }
            ps.setString(6, user.getStatus());
            int rows = ps.executeUpdate(); ps.close();
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(User user) {
        String sql = "UPDATE [User] SET email=?, full_name=?, phone=?, role_id=?, branch_id=?, status=?, updated_at=GETDATE() WHERE user_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getRoleId());
            if (user.getBranchId() != null) { ps.setInt(5, user.getBranchId()); }
            else { ps.setNull(5, Types.INTEGER); }
            ps.setString(6, user.getStatus());
            ps.setInt(7, user.getUserId());
            int rows = ps.executeUpdate(); ps.close();
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE [User] SET status=?, updated_at=GETDATE() WHERE user_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate(); ps.close();
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM [User] WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { int c = rs.getInt(1); rs.close(); ps.close(); return c > 0; }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean isEmailExistExcludeId(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM [User] WHERE email = ? AND user_id != ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { int c = rs.getInt(1); rs.close(); ps.close(); return c > 0; }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setRoleId(rs.getInt("role_id"));
        int branchId = rs.getInt("branch_id");
        if (!rs.wasNull()) { user.setBranchId(branchId); } else { user.setBranchId(null); }
        user.setStatus(rs.getString("status"));
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) user.setCreatedAt(createdTs.toLocalDateTime());
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) user.setUpdatedAt(updatedTs.toLocalDateTime());
        user.setRoleName(rs.getString("role_name"));
        user.setBranchName(rs.getString("branch_name"));
        return user;
    }
}
