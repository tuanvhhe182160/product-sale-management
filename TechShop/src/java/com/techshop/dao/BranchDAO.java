package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Branch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO extends DBContext {
    
    /**
     * Get all branches
     */
    public List<Branch> getAll() {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT branch_id, branch_code, branch_name, address, phone, status, created_at, updated_at " +
                     "FROM Branch " +
                     "ORDER BY branch_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Branch branch = extractBranchFromResultSet(rs);
                list.add(branch);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    public List<Branch> getAllWithPagination(int offset, int pageSize) {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT branch_id, branch_code, branch_name, address, phone, status, created_at, updated_at "
                + "FROM Branch "
                + "ORDER BY branch_id "
                + "OFFSET " + offset + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Branch branch = extractBranchFromResultSet(rs);
                list.add(branch);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("BranchDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public int countAllBranches() {
        String sql = "SELECT COUNT(*) FROM Branch";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getInt(1);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("BranchDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Search/filter branches with optional keyword (name, code, address, phone) and status.
     * Pass offset=0, pageSize=0 to return all matching rows without pagination.
     */
    public List<Branch> search(String keyword, String status, int offset, int pageSize) {
        List<Branch> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT branch_id, branch_code, branch_name, address, phone, status, created_at, updated_at " +
            "FROM Branch WHERE 1=1 "
        );

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasStatus  = status  != null && !status.isBlank();

        if (hasKeyword) sql.append("AND (branch_name LIKE ? OR branch_code LIKE ? OR address LIKE ? OR phone LIKE ?) ");
        if (hasStatus)  sql.append("AND status = ? ");

        sql.append("ORDER BY branch_id ");

        if (pageSize > 0) sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;

            if (hasKeyword) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (hasStatus)  ps.setString(idx++, status.trim());
            if (pageSize > 0) {
                ps.setInt(idx++, offset);
                ps.setInt(idx,   pageSize);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extractBranchFromResultSet(rs));
            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("BranchDAO.search() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Count branches matching the same keyword/status filters as search().
     */
    public int countSearch(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Branch WHERE 1=1 ");

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasStatus  = status  != null && !status.isBlank();

        if (hasKeyword) sql.append("AND (branch_name LIKE ? OR branch_code LIKE ? OR address LIKE ? OR phone LIKE ?) ");
        if (hasStatus)  sql.append("AND status = ? ");

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;

            if (hasKeyword) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (hasStatus) ps.setString(idx, status.trim());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count;
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("BranchDAO.countSearch() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get a single branch by ID
     */
    public Branch getById(int id) {
        String sql = "SELECT branch_id, branch_code, branch_name, address, phone, status, created_at, updated_at " +
                     "FROM Branch " +
                     "WHERE branch_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Branch branch = extractBranchFromResultSet(rs);
                rs.close();
                ps.close();
                return branch;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get branch by code
     */
    public Branch getByCode(String branchCode) {
        String sql = "SELECT branch_id, branch_code, branch_name, address, phone, status, created_at, updated_at " +
                     "FROM Branch " +
                     "WHERE branch_code = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, branchCode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Branch branch = extractBranchFromResultSet(rs);
                rs.close();
                ps.close();
                return branch;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.getByCode() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all active branches
     */
    public List<Branch> getAllActive() {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT branch_id, branch_code, branch_name, address, phone, status, created_at, updated_at " +
                     "FROM Branch " +
                     "WHERE status = 'ACTIVE' " +
                     "ORDER BY branch_name";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Branch branch = extractBranchFromResultSet(rs);
                list.add(branch);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.getAllActive() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Insert a new branch
     */
    public boolean insert(Branch branch) {
        String sql = "INSERT INTO Branch (branch_code, branch_name, address, phone, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, branch.getBranchCode());
            ps.setString(2, branch.getBranchName());
            ps.setString(3, branch.getAddress());
            ps.setString(4, branch.getPhone());
            ps.setString(5, branch.getStatus());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing branch
     */
    public boolean update(Branch branch) {
        String sql = "UPDATE Branch " +
                     "SET branch_code = ?, branch_name = ?, address = ?, phone = ?, status = ?, updated_at = GETDATE() " +
                     "WHERE branch_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, branch.getBranchCode());
            ps.setString(2, branch.getBranchName());
            ps.setString(3, branch.getAddress());
            ps.setString(4, branch.getPhone());
            ps.setString(5, branch.getStatus());
            ps.setInt(6, branch.getBranchId());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.update() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update branch status
     */
    public boolean updateStatus(int branchId, String status) {
        String sql = "UPDATE Branch SET status = ?, updated_at = GETDATE() WHERE branch_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, branchId);
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("BranchDAO.updateStatus() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if branch id exists
     */
    public boolean isBranchIdExist(int branchId) {
        String sql = "SELECT COUNT(*) FROM Branch WHERE branch_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
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
            System.err.println("BranchDAO.isBranchIdExist() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean isPhoneNumberUsedInBranch(String phone) {
        String sql = "SELECT COUNT(*) FROM Branch WHERE phone = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, phone);
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
            System.err.println("BranchDAO.isBranchIdExist() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
    
    /**
     * Check if branch code exists
     */
    public boolean isBranchCodeExist(String branchCode) {
        String sql = "SELECT COUNT(*) FROM Branch WHERE branch_code = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, branchCode);
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
            System.err.println("BranchDAO.isBranchCodeExist() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if branch code exists excluding specific ID
     */
    public boolean isBranchCodeExistExcludeId(String branchCode, int excludeId) {
        String sql = "SELECT COUNT(*) FROM Branch WHERE branch_code = ? AND branch_id != ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, branchCode);
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
            System.err.println("BranchDAO.isBranchCodeExistExcludeId() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Helper method to extract Branch from ResultSet
     */
    private Branch extractBranchFromResultSet(ResultSet rs) throws SQLException {
        Branch branch = new Branch();
        branch.setBranchId(rs.getInt("branch_id"));
        branch.setBranchCode(rs.getString("branch_code"));
        branch.setBranchName(rs.getString("branch_name"));
        branch.setAddress(rs.getString("address"));
        branch.setPhone(rs.getString("phone"));
        branch.setStatus(rs.getString("status"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            branch.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            branch.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return branch;
    }
    
    //Test
    public static void main(String[] args) {
        BranchDAO dao = new BranchDAO();
        
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          BRANCH DAO - UNIT TEST                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        System.out.println("TEST 1: getAll()");
        System.out.println("─────────────────────────────────────────────────────");
        List<Branch> branches = dao.getAll();
        System.out.println("✅ Found " + branches.size() + " branches:");
        for (Branch branch : branches) {
            System.out.println("   - " + branch.getBranchCode() + ": " + branch.getBranchName());
        }
        
        System.out.println("\nTEST 2: getAllActive()");
        System.out.println("─────────────────────────────────────────────────────");
        List<Branch> active = dao.getAllActive();
        System.out.println("✅ Found " + active.size() + " active branches");
        
        System.out.println("\n╚════════════════════════════════════════════════════╝");
    }
}