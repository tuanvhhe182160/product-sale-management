package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAOTest extends DBContext {
    
    public List<Role> getAll() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT role_id, role_name, description, created_at, updated_at " +
                     "FROM Role " +
                     "ORDER BY role_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Role role = extractRoleFromResultSet(rs);
                list.add(role);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("RoleDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    public Role getById(int id) {
        String sql = "SELECT role_id, role_name, description, created_at, updated_at " +
                     "FROM Role " +
                     "WHERE role_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Role role = extractRoleFromResultSet(rs);
                rs.close();
                ps.close();
                return role;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("RoleDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Role getByName(String roleName) {
        String sql = "SELECT role_id, role_name, description, created_at, updated_at " +
                     "FROM Role " +
                     "WHERE role_name = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, roleName);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Role role = extractRoleFromResultSet(rs);
                rs.close();
                ps.close();
                return role;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("RoleDAO.getByName() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean insert(Role role) {
        String sql = "INSERT INTO Role (role_name, description, created_at, updated_at) " +
                     "VALUES (?, ?, GETDATE(), GETDATE())";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role.getRoleName());
            ps.setString(2, role.getDescription());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("RoleDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean update(Role role) {
        String sql = "UPDATE Role " +
                     "SET role_name = ?, description = ?, updated_at = GETDATE() " +
                     "WHERE role_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role.getRoleName());
            ps.setString(2, role.getDescription());
            ps.setInt(3, role.getRoleId());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("RoleDAO.update() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isRoleNameExist(String roleName) {
        String sql = "SELECT COUNT(*) FROM Role WHERE role_name = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, roleName);
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
            System.err.println("RoleDAO.isRoleNameExist() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean isRoleNameExistExcludeId(String roleName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM Role WHERE role_name = ? AND role_id != ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, roleName);
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
            System.err.println("RoleDAO.isRoleNameExistExcludeId() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Role extractRoleFromResultSet(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setRoleId(rs.getInt("role_id"));
        role.setRoleName(rs.getString("role_name"));
        role.setDescription(rs.getString("description"));
        
        // Handle timestamp conversion
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            role.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            role.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return role;
    }
    
    //Test
    public static void main(String[] args) {
        RoleDAOTest dao = new RoleDAOTest();
        
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║           ROLE DAO - UNIT TEST                     ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Test 1: getAll()
        System.out.println("TEST 1: getAll()");
        System.out.println("─────────────────────────────────────────────────────");
        List<Role> roles = dao.getAll();
        if (roles.isEmpty()) {
            System.out.println("❌ No roles found in database!");
        } else {
            System.out.println("✅ Found " + roles.size() + " roles:");
            for (Role role : roles) {
                System.out.println("   - " + role);
            }
        }
        
        // Test 2: getById()
        System.out.println("\nTEST 2: getById(1)");
        System.out.println("─────────────────────────────────────────────────────");
        Role admin = dao.getById(1);
        if (admin != null) {
            System.out.println("✅ Found: " + admin);
        } else {
            System.out.println("❌ Role ID 1 not found!");
        }
        
        // Test 3: getByName()
        System.out.println("\nTEST 3: getByName('Admin')");
        System.out.println("─────────────────────────────────────────────────────");
        Role adminByName = dao.getByName("Admin");
        if (adminByName != null) {
            System.out.println("✅ Found: " + adminByName);
        } else {
            System.out.println("❌ Role 'Admin' not found!");
        }
        
        // Test 4: isRoleNameExist()
        System.out.println("\nTEST 4: isRoleNameExist()");
        System.out.println("─────────────────────────────────────────────────────");
        boolean existAdmin = dao.isRoleNameExist("Admin");
        boolean existFake = dao.isRoleNameExist("SuperAdmin");
        System.out.println("'Admin' exists: " + (existAdmin ? "✅ YES" : "❌ NO"));
        System.out.println("'SuperAdmin' exists: " + (existFake ? "❌ YES (ERROR!)" : "✅ NO (correct)"));
        
        // Test 5: isRoleNameExistExcludeId()
        System.out.println("\nTEST 5: isRoleNameExistExcludeId('Admin', 1)");
        System.out.println("─────────────────────────────────────────────────────");
        boolean duplicateCheck = dao.isRoleNameExistExcludeId("Admin", 1);
        System.out.println("Duplicate 'Admin' excluding ID 1: " + (duplicateCheck ? "❌ YES (duplicate!)" : "✅ NO (OK to update)"));
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              ALL TESTS COMPLETED!                  ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}
