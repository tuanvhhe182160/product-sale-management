package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//TODO: Implement methods
public class PhysicalProductDAO extends DBContext {
    
    private static final String BASE_SELECT_QUERY_STRING = "SELECT p.physical_id, p.variant_id, p.branch_id, p.imei, p.serial_number, "
            + "p.status, p.import_date, p.sale_date, p.created_at, p.updated_at, "
            + "v.variant_name, v.sku, b.branch_name "
            + "FROM PhysicalProduct p "
            + "LEFT JOIN ProductVariant v ON p.variant_id = v.variant_id "
            + "LEFT JOIN Branch b ON p.branch_id = b.branch_id ";
    
    public List<PhysicalProduct> getAll() {
        List<PhysicalProduct> list = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(BASE_SELECT_QUERY_STRING)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPhysicalProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("PhysicalProductDAO.getAll() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }
    
    public PhysicalProduct getById(int physicalId) {
        return null;
    }
    
    public List<PhysicalProduct> getByVariantId(int variantId) {
        return null;
    }

    public List<PhysicalProduct> getByBranchId(int branchId) {
        return null;
    }
    
    private PhysicalProduct mapPhysicalProduct(ResultSet rs) throws SQLException {
        PhysicalProduct product = new PhysicalProduct();
        product.setPhysicalId(rs.getInt("physical_id"));
        product.setVariantId(rs.getInt("variant_id"));
        product.setBranchId(rs.getInt("branch_id"));
        product.setImei(rs.getString("imei"));
        product.setSerialNumber(rs.getString("serial_number"));
        product.setStatus(rs.getString("status"));

        Timestamp importTs = rs.getTimestamp("import_date");
        if (importTs != null) {
            product.setImportDate(importTs.toLocalDateTime());
        }

        Timestamp saleTs = rs.getTimestamp("sale_date");
        if (saleTs != null) {
            product.setSaleDate(saleTs.toLocalDateTime());
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            product.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            product.setUpdatedAt(updatedTs.toLocalDateTime());
        }

        product.setVariantName(rs.getString("variant_name"));
        product.setBranchName(rs.getString("branch_name"));
        product.setSku(rs.getString("sku"));

        return product;
    }
}
