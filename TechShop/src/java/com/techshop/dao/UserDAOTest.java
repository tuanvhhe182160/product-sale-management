package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOTest extends DBContext {
    
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "ORDER BY u.user_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                list.add(user);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    public User getById(int id) {
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                rs.close();
                ps.close();
                return user;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public User getByEmail(String email) {
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.email = ? AND u.status = 'ACTIVE'";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                rs.close();
                ps.close();
                return user;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getByEmail() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<User> getAllByRole(int roleId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.role_id = ? " +
                     "ORDER BY u.user_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                list.add(user);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getAllByRole() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    public List<User> getAllByBranch(int branchId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.branch_id = ? " +
                     "ORDER BY u.user_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                list.add(user);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getAllByBranch() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    public List<User> getAllCashier() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.role_id = 3 " +
                     "ORDER BY u.user_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                list.add(user);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getAllByRole() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
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
            
            // Handle nullable branch_id (Admin can have NULL)
            if (user.getBranchId() != null) {
                ps.setInt(5, user.getBranchId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            
            ps.setString(6, user.getStatus());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean update(User user) {
        String sql = "UPDATE [User] " +
                     "SET email = ?, full_name = ?, phone = ?, role_id = ?, branch_id = ?, status = ?, updated_at = GETDATE() " +
                     "WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getRoleId());
            
            // Handle nullable branch_id
            if (user.getBranchId() != null) {
                ps.setInt(5, user.getBranchId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            
            ps.setString(6, user.getStatus());
            ps.setInt(7, user.getUserId());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.update() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE [User] SET status = ?, updated_at = GETDATE() WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.updateStatus() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM [User] WHERE email = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count > 0;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.isEmailExist() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
/*    public boolean isEmailExistExcludeId(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM [User] WHERE email = ? AND user_id != ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count > 0;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.isEmailExistExcludeId() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        
        // Handle nullable branch_id
        int branchId = rs.getInt("branch_id");
        if (!rs.wasNull()) {
            user.setBranchId(branchId);
        } else {
            user.setBranchId(null);
        }
        
        user.setStatus(rs.getString("status"));
        
        // Handle timestamp conversion
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            user.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        // JOIN data
        user.setRoleName(rs.getString("role_name"));
        user.setBranchName(rs.getString("branch_name"));
        
        return user;
    }
    
    //Test
    public static void main(String[] args) {
        UserDAOTest dao = new UserDAOTest();
        
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║           USER DAO - UNIT TEST                     ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Test 1: getAll()
        System.out.println("TEST 1: getAll()");
        System.out.println("─────────────────────────────────────────────────────");
        List<User> users = dao.getAll();
        if (users.isEmpty()) {
            System.out.println("❌ No users found in database!");
        } else {
            System.out.println("✅ Found " + users.size() + " users:");
            for (User user : users) {
                System.out.println("   - " + user.getEmail() + " (" + user.getRoleName() + 
                                 (user.getBranchName() != null ? " @ " + user.getBranchName() : " @ NO BRANCH") + ")");
            }
        }
        
        // Test 2: getById()
        System.out.println("\nTEST 2: getById(1)");
        System.out.println("─────────────────────────────────────────────────────");
        User admin = dao.getById(1);
        if (admin != null) {
            System.out.println("✅ Found: " + admin.getFullName() + " - " + admin.getEmail());
            System.out.println("   Role: " + admin.getRoleName());
            System.out.println("   Branch: " + (admin.getBranchName() != null ? admin.getBranchName() : "NULL (Admin)"));
        } else {
            System.out.println("❌ User ID 1 not found!");
        }
        
        // Test 3: getByEmail() - CRITICAL for login
        System.out.println("\nTEST 3: getByEmail('tuanvhhe182160@fpt.edu.vn') - LOGIN TEST");
        System.out.println("─────────────────────────────────────────────────────");
        User loginTest = dao.getByEmail("tuanvhhe182160@fpt.edu.vn");
        if (loginTest != null) {
            System.out.println("✅ Login would succeed!");
            System.out.println("   User: " + loginTest.getFullName());
            System.out.println("   Role: " + loginTest.getRoleName());
            System.out.println("   Status: " + loginTest.getStatus());
        } else {
            System.out.println("❌ Login would fail - email not found or inactive!");
        }
        
        // Test 4: getAllByRole()
        System.out.println("\nTEST 4: getAllByRole(3) - All Cashiers");
        System.out.println("─────────────────────────────────────────────────────");
        List<User> cashiers = dao.getAllByRole(3);
        System.out.println("Found " + cashiers.size() + " cashier(s)");
        for (User cashier : cashiers) {
            System.out.println("   - " + cashier.getFullName());
        }
        
        // Test 5: isEmailExist()
        System.out.println("\nTEST 5: isEmailExist()");
        System.out.println("─────────────────────────────────────────────────────");
        boolean existAdmin = dao.isEmailExist("tuanvhhe182160@fpt.edu.vn");
        boolean existFake = dao.isEmailExist("fake@email.com");
        System.out.println("'tuanvhhe182160@fpt.edu.vn' exists: " + (existAdmin ? "✅ YES" : "❌ NO"));
        System.out.println("'fake@email.com' exists: " + (existFake ? "❌ YES (ERROR!)" : "✅ NO (correct)"));
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              ALL TESTS COMPLETED!                  ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}
