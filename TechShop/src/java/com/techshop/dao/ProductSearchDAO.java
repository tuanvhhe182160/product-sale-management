package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.CashierSaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchDAO extends DBContext {

    // ── Build WHERE clause dùng chung ────────────────────────────────────────
    private String buildWhere(String keyword, String categoryId,
                               String modelId,  String sku) {
        StringBuilder w = new StringBuilder(
            "WHERE p.status    = 'IN_STOCK' " +
            "  AND v.status    = 'ACTIVE'   " +
            "  AND p.branch_id = ?          "
        );

        if (!keyword.isEmpty()) {
            w.append("AND (v.variant_name LIKE ? OR v.sku LIKE ? " +
                     "     OR m.model_name LIKE ? OR m.brand LIKE ? " +
                     "     OR c.category_name LIKE ?) ");
        }
        if (!categoryId.isEmpty()) w.append("AND c.category_id = ? ");
        if (!modelId.isEmpty())    w.append("AND m.model_id    = ? ");
        if (!sku.isEmpty())        w.append("AND v.sku LIKE ?       ");

        return w.toString();
    }

    // ── Bind parameters ──────────────────────────────────────────────────────
    private int bindParams(PreparedStatement ps, int idx,
                           int branchId,
                           String keyword, String categoryId,
                           String modelId, String sku) throws SQLException {
        ps.setInt(idx++, branchId);

        if (!keyword.isEmpty()) {
            String like = "%" + keyword + "%";
            ps.setString(idx++, like);
            ps.setString(idx++, like);
            ps.setString(idx++, like);
            ps.setString(idx++, like);
            ps.setString(idx++, like);
        }
        if (!categoryId.isEmpty()) ps.setInt(idx++, Integer.parseInt(categoryId));
        if (!modelId.isEmpty())    ps.setInt(idx++, Integer.parseInt(modelId));
        if (!sku.isEmpty())        ps.setString(idx++, "%" + sku + "%");

        return idx;
    }

    // ── Search: trả về List<CashierSaleItem> (1 row = 1 IMEI) ────────────────
    public List<CashierSaleItem> searchForCashier(
            String keyword, String categoryId, String modelId, String sku,
            int branchId, int page, int pageSize) {

        List<CashierSaleItem> list = new ArrayList<>();

        keyword    = keyword    == null ? "" : keyword.trim();
        categoryId = categoryId == null ? "" : categoryId.trim();
        modelId    = modelId    == null ? "" : modelId.trim();
        sku        = sku        == null ? "" : sku.trim();

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
            buildWhere(keyword, categoryId, modelId, sku) +
            "ORDER BY c.category_name, m.model_name, v.variant_name, p.imei " +
            "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = bindParams(ps, 1, branchId, keyword, categoryId, modelId, sku);
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx,   pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Count ─────────────────────────────────────────────────────────────────
    public int countForCashier(
            String keyword, String categoryId, String modelId, String sku,
            int branchId) {

        keyword    = keyword    == null ? "" : keyword.trim();
        categoryId = categoryId == null ? "" : categoryId.trim();
        modelId    = modelId    == null ? "" : modelId.trim();
        sku        = sku        == null ? "" : sku.trim();

        String sql =
            "SELECT COUNT(*) " +
            "FROM PhysicalProduct p " +
            "JOIN ProductVariant  v ON p.variant_id  = v.variant_id " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            buildWhere(keyword, categoryId, modelId, sku);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, 1, branchId, keyword, categoryId, modelId, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}