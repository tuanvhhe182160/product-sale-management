package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;
import com.techshop.model.VariantAttribute;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VariantDAO extends DBContext {

    // Get all variants with JOIN info (with search and filter support)
    public List<ProductVariant> getAllVariants(String search, Integer modelId) {
        List<ProductVariant> variants = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT v.variant_id, v.model_id, v.sku, v.variant_name, "
                + "v.base_price, v.cost_price, v.warranty_months, v.image_url, "
                + "v.status, v.created_at, v.updated_at, "
                + "m.model_name, m.brand, c.category_name "
                + "FROM ProductVariant v "
                + "INNER JOIN ProductModel m ON v.model_id = m.model_id "
                + "INNER JOIN ProductCategory c ON m.category_id = c.category_id ");
        
        // Add WHERE conditions
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        
        if (search != null && !search.trim().isEmpty()) {
            conditions.add("(v.sku LIKE ? OR v.variant_name LIKE ? OR m.model_name LIKE ? OR m.brand LIKE ?)");
            String searchPattern = "%" + search.trim() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }
        
        if (modelId != null && modelId > 0) {
            conditions.add("v.model_id = ?");
            parameters.add(modelId);
        }
        
        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions));
        }
        
        // Order by created_at ASC (newest at bottom)
        sql.append(" ORDER BY v.created_at ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant variant = mapVariantFromResultSet(rs);
                    variants.add(variant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all variants: " + e.getMessage());
            e.printStackTrace();
        }

        return variants;
    }
    
    // Overload method for backward compatibility
    public List<ProductVariant> getAllVariants() {
        return getAllVariants(null, null);
    }

    /**
     * Get all active variants — used by DashboardServlet
     */
    public List<ProductVariant> getAllActive() {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT v.variant_id, v.model_id, v.sku, v.variant_name, v.base_price, v.cost_price, "
                   + "v.warranty_months, v.image_url, v.status, v.created_at, v.updated_at, "
                   + "m.model_name, m.brand, c.category_name "
                   + "FROM ProductVariant v "
                   + "INNER JOIN ProductModel m ON v.model_id = m.model_id "
                   + "INNER JOIN ProductCategory c ON m.category_id = c.category_id "
                   + "WHERE v.status = 'ACTIVE' ORDER BY v.variant_name";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapVariantFromResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Get variant by ID
    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT v.variant_id, v.model_id, v.sku, v.variant_name, "
                + "v.base_price, v.cost_price, v.warranty_months, v.image_url, "
                + "v.status, v.created_at, v.updated_at, "
                + "m.model_name, m.brand, c.category_name "
                + "FROM ProductVariant v "
                + "INNER JOIN ProductModel m ON v.model_id = m.model_id "
                + "INNER JOIN ProductCategory c ON m.category_id = c.category_id "
                + "WHERE v.variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVariantFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Check if SKU exists
    public boolean isSkuExists(String sku) {
        return isSkuExists(sku, 0);
    }

    public boolean isSkuExists(String sku, int excludeVariantId) {
        String sql = "SELECT COUNT(*) FROM ProductVariant WHERE sku = ? AND variant_id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, sku);
            ps.setInt(2, excludeVariantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking SKU: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Create new variant
    public int createVariant(ProductVariant variant) {
        String sql = "INSERT INTO ProductVariant (model_id, sku, variant_name, base_price, "
                + "cost_price, warranty_months, image_url, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, variant.getModelId());
            ps.setString(2, variant.getSku());
            ps.setString(3, variant.getVariantName());
            ps.setBigDecimal(4, variant.getBasePrice());
            ps.setBigDecimal(5, variant.getCostPrice());
            ps.setInt(6, variant.getWarrantyMonths());
            ps.setString(7, variant.getImageUrl());
            ps.setString(8, variant.getStatus() != null ? variant.getStatus() : "ACTIVE");

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating variant: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Update variant
    public boolean updateVariant(ProductVariant variant) {
        String sql = "UPDATE ProductVariant SET model_id = ?, sku = ?, variant_name = ?, "
                + "base_price = ?, cost_price = ?, warranty_months = ?, image_url = ?, "
                + "status = ?, updated_at = GETDATE() WHERE variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, variant.getModelId());
            ps.setString(2, variant.getSku());
            ps.setString(3, variant.getVariantName());
            ps.setBigDecimal(4, variant.getBasePrice());
            ps.setBigDecimal(5, variant.getCostPrice());
            ps.setInt(6, variant.getWarrantyMonths());
            ps.setString(7, variant.getImageUrl());
            ps.setString(8, variant.getStatus());
            ps.setInt(9, variant.getVariantId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating variant: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Delete variant (soft delete by setting status to INACTIVE)
    public boolean deleteVariant(int variantId) {
        String sql = "UPDATE ProductVariant SET status = 'INACTIVE', updated_at = GETDATE() WHERE variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, variantId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting variant: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Get variants by model ID
    public List<ProductVariant> getVariantsByModelId(int modelId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT v.variant_id, v.model_id, v.sku, v.variant_name, "
                + "v.base_price, v.cost_price, v.warranty_months, v.image_url, "
                + "v.status, v.created_at, v.updated_at, "
                + "m.model_name, m.brand, c.category_name "
                + "FROM ProductVariant v "
                + "INNER JOIN ProductModel m ON v.model_id = m.model_id "
                + "INNER JOIN ProductCategory c ON m.category_id = c.category_id "
                + "WHERE v.model_id = ? AND v.status = 'ACTIVE' "
                + "ORDER BY v.variant_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, modelId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    variants.add(mapVariantFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variants by model ID: " + e.getMessage());
            e.printStackTrace();
        }

        return variants;
    }

    // Get all active models for dropdown
    public List<ProductModel> getAllActiveModels() {
        List<ProductModel> models = new ArrayList<>();
        String sql = "SELECT m.model_id, m.category_id, m.model_code, m.model_name, "
                + "m.brand, m.description, m.status, m.created_at, m.updated_at, "
                + "c.category_name "
                + "FROM ProductModel m "
                + "INNER JOIN ProductCategory c ON m.category_id = c.category_id "
                + "WHERE m.status = 'ACTIVE' "
                + "ORDER BY c.category_name, m.brand, m.model_name";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductModel model = new ProductModel();
                model.setModelId(rs.getInt("model_id"));
                model.setCategoryId(rs.getInt("category_id"));
                model.setModelCode(rs.getString("model_code"));
                model.setModelName(rs.getString("model_name"));
                model.setBrand(rs.getString("brand"));
                model.setDescription(rs.getString("description"));
                model.setStatus(rs.getString("status"));
                model.setCategoryName(rs.getString("category_name"));

                java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    model.setCreatedAt(createdAt.toLocalDateTime());
                }

                java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    model.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                models.add(model);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active models: " + e.getMessage());
            e.printStackTrace();
        }

        return models;
    }

    // Get attributes for a variant
    public List<VariantAttribute> getVariantAttributes(int variantId) {
        List<VariantAttribute> attributes = new ArrayList<>();
        String sql = "SELECT attribute_id, variant_id, attribute_name, attribute_value, created_at "
                + "FROM VariantAttribute WHERE variant_id = ? ORDER BY attribute_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, variantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VariantAttribute attr = new VariantAttribute();
                    attr.setAttributeId(rs.getInt("attribute_id"));
                    attr.setVariantId(rs.getInt("variant_id"));
                    attr.setAttributeName(rs.getString("attribute_name"));
                    attr.setAttributeValue(rs.getString("attribute_value"));

                    java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        attr.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    attributes.add(attr);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant attributes: " + e.getMessage());
            e.printStackTrace();
        }

        return attributes;
    }

    // Helper method to map ResultSet to ProductVariant
    private ProductVariant mapVariantFromResultSet(ResultSet rs) throws SQLException {
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

        // JOIN fields
        variant.setModelName(rs.getString("model_name"));
        variant.setBrand(rs.getString("brand"));
        variant.setCategoryName(rs.getString("category_name"));

        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            variant.setCreatedAt(createdAt.toLocalDateTime());
        }

        java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            variant.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return variant;
    }
}
