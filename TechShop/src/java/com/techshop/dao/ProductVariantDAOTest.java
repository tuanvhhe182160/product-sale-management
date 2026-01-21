package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductVariant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ProductVariant table
 * Member 5's work - adapted for Dashboard integration
 * 
 * @author Member 5
 * @version 1.0
 */
public class ProductVariantDAOTest extends DBContext {
    
    /**
     * Get all product variants with model and category info
     */
    public List<ProductVariant> getAll() {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT v.variant_id, v.model_id, v.sku, v.variant_name, v.base_price, v.cost_price, " +
                     "       v.warranty_months, v.image_url, v.status, v.created_at, v.updated_at, " +
                     "       m.model_name, m.brand, " +
                     "       c.category_name " +
                     "FROM ProductVariant v " +
                     "LEFT JOIN ProductModel m ON v.model_id = m.model_id " +
                     "LEFT JOIN ProductCategory c ON m.category_id = c.category_id " +
                     "ORDER BY v.variant_id";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductVariant variant = extractVariantFromResultSet(rs);
                list.add(variant);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("ProductVariantDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get all active variants (for sales/inventory)
     */
    public List<ProductVariant> getAllActive() {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT v.variant_id, v.model_id, v.sku, v.variant_name, v.base_price, v.cost_price, " +
                     "       v.warranty_months, v.image_url, v.status, v.created_at, v.updated_at, " +
                     "       m.model_name, m.brand, " +
                     "       c.category_name " +
                     "FROM ProductVariant v " +
                     "LEFT JOIN ProductModel m ON v.model_id = m.model_id " +
                     "LEFT JOIN ProductCategory c ON m.category_id = c.category_id " +
                     "WHERE v.status = 'ACTIVE' " +
                     "ORDER BY v.variant_name";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ProductVariant variant = extractVariantFromResultSet(rs);
                list.add(variant);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("ProductVariantDAO.getAllActive() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get variant by ID
     */
    public ProductVariant getById(int id) {
        String sql = "SELECT v.variant_id, v.model_id, v.sku, v.variant_name, v.base_price, v.cost_price, " +
                     "       v.warranty_months, v.image_url, v.status, v.created_at, v.updated_at, " +
                     "       m.model_name, m.brand, " +
                     "       c.category_name " +
                     "FROM ProductVariant v " +
                     "LEFT JOIN ProductModel m ON v.model_id = m.model_id " +
                     "LEFT JOIN ProductCategory c ON m.category_id = c.category_id " +
                     "WHERE v.variant_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ProductVariant variant = extractVariantFromResultSet(rs);
                rs.close();
                ps.close();
                return variant;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("ProductVariantDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Insert new variant
     */
    public boolean insert(ProductVariant variant) {
        String sql = "INSERT INTO ProductVariant (model_id, sku, variant_name, base_price, cost_price, warranty_months, image_url, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variant.getModelId());
            ps.setString(2, variant.getSku());
            ps.setString(3, variant.getVariantName());
            ps.setBigDecimal(4, variant.getBasePrice());
            ps.setBigDecimal(5, variant.getCostPrice());
            ps.setInt(6, variant.getWarrantyMonths());
            ps.setString(7, variant.getImageUrl());
            ps.setString(8, variant.getStatus());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("ProductVariantDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update existing variant
     */
    public boolean update(ProductVariant variant) {
        String sql = "UPDATE ProductVariant " +
                     "SET model_id = ?, sku = ?, variant_name = ?, base_price = ?, cost_price = ?, " +
                     "    warranty_months = ?, image_url = ?, status = ?, updated_at = GETDATE() " +
                     "WHERE variant_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variant.getModelId());
            ps.setString(2, variant.getSku());
            ps.setString(3, variant.getVariantName());
            ps.setBigDecimal(4, variant.getBasePrice());
            ps.setBigDecimal(5, variant.getCostPrice());
            ps.setInt(6, variant.getWarrantyMonths());
            ps.setString(7, variant.getImageUrl());
            ps.setString(8, variant.getStatus());
            ps.setInt(9, variant.getVariantId());
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("ProductVariantDAO.update() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to extract ProductVariant from ResultSet
     */
    private ProductVariant extractVariantFromResultSet(ResultSet rs) throws SQLException {
        ProductVariant variant = new ProductVariant();
        variant.setVariantId(rs.getInt("variant_id"));
        variant.setModelId(rs.getInt("model_id"));
        variant.setSku(rs.getString("sku"));
        variant.setVariantName(rs.getString("variant_name"));
        variant.setBasePrice(rs.getBigDecimal("base_price"));
        variant.setCostPrice(rs.getBigDecimal("cost_price"));
        variant.setWarrantyMonths(rs.getInt("warranty_months"));
        variant.setImageUrl(rs.getString("image_url"));
        variant.setStatus(rs.getString("status"));
        
        // JOIN data
        variant.setModelName(rs.getString("model_name"));
        variant.setBrand(rs.getString("brand"));
        variant.setCategoryName(rs.getString("category_name"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            variant.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            variant.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return variant;
    }
}