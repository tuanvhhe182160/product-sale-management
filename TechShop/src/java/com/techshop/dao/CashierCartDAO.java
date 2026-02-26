package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.CashierSaleItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CashierCartDAO extends DBContext {

    /**
     * Lấy đầy đủ thông tin CashierSaleItem từ physicalId.
     * Chỉ trả về nếu IMEI đang IN_STOCK tại branch.
     */
    public CashierSaleItem getCashierSaleItemByPhysical(int physicalId, int branchId) {
        String sql =
            "SELECT " +
            "    p.physical_id, p.variant_id, p.branch_id, " +
            "    p.imei, p.serial_number, " +
            "    v.variant_name, v.sku, v.base_price, v.warranty_months, v.image_url, " +
            "    m.model_name, m.brand, " +
            "    c.category_name " +
            "FROM PhysicalProduct p " +
            "JOIN ProductVariant  v ON p.variant_id  = v.variant_id " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            "WHERE p.physical_id = ? " +
            "  AND p.branch_id   = ? " +
            "  AND p.status      = 'IN_STOCK'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CashierSaleItem item = new CashierSaleItem();
                    item.setPhysicalId(rs.getInt("physical_id"));
                    item.setVariantId(rs.getInt("variant_id"));
                    item.setBranchId(rs.getInt("branch_id"));
                    item.setImei(rs.getString("imei"));
                    item.setSerialNumber(rs.getString("serial_number"));
                    item.setVariantName(rs.getString("variant_name"));
                    item.setSku(rs.getString("sku"));
                    item.setUnitPrice(rs.getBigDecimal("base_price"));
                    item.setWarrantyMonths(rs.getInt("warranty_months"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setModelName(rs.getString("model_name"));
                    item.setBrand(rs.getString("brand"));
                    item.setCategoryName(rs.getString("category_name"));
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   
    public boolean isStillInStock(int physicalId, int branchId) {
        String sql =
            "SELECT COUNT(*) FROM PhysicalProduct " +
            "WHERE physical_id = ? AND branch_id = ? AND status = 'IN_STOCK'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}