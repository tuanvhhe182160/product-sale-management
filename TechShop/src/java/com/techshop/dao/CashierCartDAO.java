package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.CashierSaleItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO hỗ trợ giỏ hàng Cashier.
 */
public class CashierCartDAO extends DBContext {

    /**
     * Lấy thông tin Variant để thêm vào giỏ hàng (không cần physicalId cụ thể).
     * Kèm đếm số PhysicalProduct IN_STOCK tại chi nhánh.
     */
    public CashierSaleItem getVariantForCart(int variantId, int branchId) {
        String sql =
            "SELECT " +
            "    v.variant_id, v.variant_name, v.sku, v.base_price, " +
            "    v.warranty_months, v.image_url, v.status, " +
            "    m.model_name, m.brand, m.description AS model_desc, " +
            "    c.category_name, " +
            "    (SELECT COUNT(*) FROM PhysicalProduct pp " +
            "     WHERE pp.variant_id = v.variant_id " +
            "       AND pp.branch_id = ? AND pp.status = 'IN_STOCK') AS stock_count " +
            "FROM ProductVariant v " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            "WHERE v.variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CashierSaleItem item = new CashierSaleItem();
                    item.setVariantId(rs.getInt("variant_id"));
                    item.setVariantName(rs.getString("variant_name"));
                    item.setSku(rs.getString("sku"));
                    item.setUnitPrice(rs.getBigDecimal("base_price"));
                    item.setWarrantyMonths(rs.getInt("warranty_months"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setModelName(rs.getString("model_name"));
                    item.setBrand(rs.getString("brand"));
                    item.setStatus(rs.getString("status"));
                    item.setModelDesc(rs.getString("model_desc"));
                    item.setCategoryName(rs.getString("category_name"));
                    item.setStockCount(rs.getInt("stock_count"));
                    item.setQuantity(1);
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Đếm số PhysicalProduct IN_STOCK của 1 variant tại chi nhánh.
     */
    public int countInStock(int variantId, int branchId) {
        String sql = "SELECT COUNT(*) FROM PhysicalProduct " +
                     "WHERE variant_id = ? AND branch_id = ? AND status = 'IN_STOCK'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Pick ngẫu nhiên N PhysicalProduct IN_STOCK của variant tại chi nhánh.
     * Dùng khi checkout — lấy đúng số lượng cần bán.
     * Trả về list physical_id.
     */
    public List<Integer> pickPhysicalProducts(int variantId, int branchId, int quantity) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT TOP (?) physical_id FROM PhysicalProduct " +
                     "WHERE variant_id = ? AND branch_id = ? AND status = 'IN_STOCK' " +
                     "ORDER BY NEWID()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, variantId);
            ps.setInt(3, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("physical_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }
}
