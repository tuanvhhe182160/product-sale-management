package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ProductCategory table
 * Member 4's work - adapted for Dashboard integration
 * 
 * @author Member 4
 * @version 1.0
 */
public class ProductCategoryDAOTest extends DBContext {
    
    /**
     * Get all product categories
     */
    public List<ProductCategory> getAll() {
        List<ProductCategory> list = new ArrayList<>();
        String sql = "SELECT category_id, category_code, category_name, description, status, created_at, updated_at " +
                     "FROM ProductCategory " +
                     "ORDER BY category_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductCategory category = extractCategoryFromResultSet(rs);
                list.add(category);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("ProductCategoryDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get all active categories (for dropdowns)
     */
    public List<ProductCategory> getAllActive() {
        List<ProductCategory> list = new ArrayList<>();
        String sql = "SELECT category_id, category_code, category_name, description, status, created_at, updated_at " +
                     "FROM ProductCategory " +
                     "WHERE status = 'ACTIVE' " +
                     "ORDER BY category_name";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductCategory category = extractCategoryFromResultSet(rs);
                list.add(category);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("ProductCategoryDAO.getAllActive() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get category by ID
     */
    public ProductCategory getById(int id) {
        String sql = "SELECT category_id, category_code, category_name, description, status, created_at, updated_at " +
                     "FROM ProductCategory " +
                     "WHERE category_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ProductCategory category = extractCategoryFromResultSet(rs);
                rs.close();
                ps.close();
                return category;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("ProductCategoryDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Insert new category
     */
    public boolean insert(ProductCategory category) {
        String sql = "INSERT INTO ProductCategory (category_code, category_name, description, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());
            ps.setString(3, category.getDescription());
            ps.setString(4, category.getStatus());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("ProductCategoryDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update existing category
     */
    public boolean update(ProductCategory category) {
        String sql = "UPDATE ProductCategory " +
                     "SET category_code = ?, category_name = ?, description = ?, status = ?, updated_at = GETDATE() " +
                     "WHERE category_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());
            ps.setString(3, category.getDescription());
            ps.setString(4, category.getStatus());
            ps.setInt(5, category.getCategoryId());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("ProductCategoryDAO.update() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract ProductCategory from ResultSet
     */
    private ProductCategory extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        ProductCategory category = new ProductCategory();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryCode(rs.getString("category_code"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setStatus(rs.getString("status"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            category.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            category.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return category;
    }
}