package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DBContext {

    private static final int CASHIER_ROLE_ID = 3;

    private static final String USER_SELECT =
            "SELECT u.user_id, u.email, u.full_name, u.phone, u.avatar_url, " +
            "       u.role_id, u.branch_id, u.status, " +
            "       u.created_at, u.updated_at, " +
            "       r.role_name, b.branch_name " +
            "FROM [User] u " +
            "LEFT JOIN Role r ON u.role_id = r.role_id " +
            "LEFT JOIN Branch b ON u.branch_id = b.branch_id ";

    public List<User> getAll() {
        String sql = USER_SELECT + "ORDER BY u.user_id";
        return queryUsers(sql);
    }

    public User getById(int id) {
        String sql = USER_SELECT + "WHERE u.user_id = ?";
        return queryUser(sql, id);
    }

    public User getByEmail(String email) {
        String sql = USER_SELECT + "WHERE u.email = ? AND u.status = 'ACTIVE'";
        return queryUser(sql, email);
    }

    public List<User> getAllByRole(int roleId) {
        String sql = USER_SELECT + "WHERE u.role_id = ? ORDER BY u.user_id";
        return queryUsers(sql, roleId);
    }

    public List<User> getAllByBranch(int branchId) {
        String sql = USER_SELECT + "WHERE u.branch_id = ? ORDER BY u.user_id";
        return queryUsers(sql, branchId);
    }

    public List<User> getAllCashier() {
        String sql = USER_SELECT + "WHERE u.role_id = ? ORDER BY u.user_id";
        return queryUsers(sql, CASHIER_ROLE_ID);
    }

    public List<User> getCashiersByBranchId(int branchId) {
        String sql = USER_SELECT + "WHERE u.role_id = ? AND u.branch_id = ? ORDER BY u.user_id";
        return queryUsers(sql, CASHIER_ROLE_ID, branchId);
    }

    public int insert(User user) {
        String sql = "INSERT INTO [User] (email, full_name, phone, role_id, branch_id, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getRoleId());
            setNullableInt(ps, 5, user.getBranchId());
            ps.setString(6, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            logError("insert", e);
            return -1;
        }
    }

    public boolean update(User user) {
        String sql = "UPDATE [User] " +
                     "SET email = ?, full_name = ?, phone = ?, role_id = ?, branch_id = ?, status = ?, updated_at = GETDATE() " +
                     "WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getRoleId());
            setNullableInt(ps, 5, user.getBranchId());
            ps.setString(6, user.getStatus());
            ps.setInt(7, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("update", e);
            return false;
        }
    }

    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE [User] SET status = ?, updated_at = GETDATE() WHERE user_id = ?";
        return executeUpdate(sql, status, userId);
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT COUNT(1) FROM [User] WHERE email = ?";
        return queryCount(sql, email) > 0;
    }

    public boolean isEmailExistExcludeId(String email, int excludeId) {
        String sql = "SELECT COUNT(1) FROM [User] WHERE email = ? AND user_id != ?";
        return queryCount(sql, email, excludeId) > 0;
    }

    private List<User> queryUsers(String sql, Object... params) {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logError("queryUsers", e);
        }
        return users;
    }

    private User queryUser(String sql, Object... params) {
        List<User> users = queryUsers(sql, params);
        return users.isEmpty() ? null : users.get(0);
    }

    private int queryCount(String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logError("queryCount", e);
        }
        return 0;
    }

    private boolean executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("executeUpdate", e);
            return false;
        }
    }

    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            int index = i + 1;
            if (value == null) {
                ps.setNull(index, Types.NULL);
            } else if (value instanceof Integer) {
                ps.setInt(index, (Integer) value);
            } else if (value instanceof String) {
                ps.setString(index, (String) value);
            } else {
                ps.setObject(index, value);
            }
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setRoleId(rs.getInt("role_id"));

        user.setBranchId((Integer) rs.getObject("branch_id"));
        user.setStatus(rs.getString("status"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            user.setUpdatedAt(updatedTs.toLocalDateTime());
        }

        user.setRoleName(rs.getString("role_name"));
        user.setBranchName(rs.getString("branch_name"));
        return user;
    }

    private void logError(String methodName, SQLException e) {
        System.err.println("UserDAO." + methodName + "() Error: " + e.getMessage());
        e.printStackTrace();
    }
}
