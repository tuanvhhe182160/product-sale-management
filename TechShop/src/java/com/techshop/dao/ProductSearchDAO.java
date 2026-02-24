package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.ProductVariant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchDAO extends DBContext {

    public List<ProductVariant> searchForCashier(
            String keyword,
            String categoryId,
            String modelId,
            String sku,
            int branchId,
            int page,
            int pageSize) {

        List<ProductVariant> list = new ArrayList<>();

        if (keyword    == null) keyword    = "";
        if (categoryId == null) categoryId = "";
        if (modelId    == null) modelId    = "";
        if (sku        == null) sku        = "";

        keyword = keyword.trim();
        sku     = sku.trim();

        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    v.variant_id, " +
            "    v.variant_name, " +
            "    v.sku, " +
            "    v.base_price, " +
            "    v.warranty_months, " +
            "    m.model_name, " +
            "    m.model_code, " +
            "    m.brand, " +
            "    c.category_name, " +
            "    ISNULL(stock.cnt, 0) AS stock " +
            "FROM ProductVariant v " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            "LEFT JOIN ( " +
            "    SELECT variant_id, COUNT(*) AS cnt " +
            "    FROM PhysicalProduct " +
            "    WHERE status = 'IN_STOCK' AND branch_id = ? " +
            "    GROUP BY variant_id " +
            ") stock ON v.variant_id = stock.variant_id " +
            "WHERE v.status = 'ACTIVE' "
        );

        if (!keyword.isEmpty()) {
            sql.append(
                "AND ( " +
                "    m.model_name     LIKE ? " +
                "    OR v.variant_name LIKE ? " +
                "    OR v.sku          LIKE ? " +
                "    OR c.category_name LIKE ? " +
                "    OR m.model_code   LIKE ? " +
                ") "
            );
        }

        if (!categoryId.isEmpty()) {
            sql.append("AND c.category_id = ? ");
        }

        if (!modelId.isEmpty()) {
            sql.append("AND m.model_id = ? ");
        }

        if (!sku.isEmpty()) {
            sql.append("AND v.sku LIKE ? ");
        }

        sql.append(
            "ORDER BY c.category_name, m.model_name, v.variant_name " +
            "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"
        );

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, branchId);

            if (!keyword.isEmpty()) {
                String like = "%" + keyword + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }

            if (!categoryId.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(categoryId));
            }

            if (!modelId.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(modelId));
            }

            if (!sku.isEmpty()) {
                ps.setString(idx++, "%" + sku + "%");
            }

            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant v = new ProductVariant();
                    v.setVariantId(rs.getInt("variant_id"));
                    v.setVariantName(rs.getString("variant_name"));
                    v.setSku(rs.getString("sku"));
                    v.setBasePrice(rs.getBigDecimal("base_price"));
                    v.setWarrantyMonths(rs.getInt("warranty_months"));
                    v.setModelName(rs.getString("model_name"));
                    v.setBrand(rs.getString("brand"));
                    v.setCategoryName(rs.getString("category_name"));
                    v.setStock(rs.getInt("stock"));
                    list.add(v);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // overload tương thích ngược
    public List<ProductVariant> searchForCashier(
            String keyword, String categoryId,
            int branchId, int page, int pageSize) {
        return searchForCashier(keyword, categoryId, "", "", branchId, page, pageSize);
    }

    public int countForCashier(
            String keyword,
            String categoryId,
            String modelId,
            String sku,
            int branchId) {

        if (keyword    == null) keyword    = "";
        if (categoryId == null) categoryId = "";
        if (modelId    == null) modelId    = "";
        if (sku        == null) sku        = "";

        keyword = keyword.trim();
        sku     = sku.trim();

        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM ProductVariant v " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            "WHERE v.status = 'ACTIVE' "
        );

        if (!keyword.isEmpty()) {
            sql.append(
                "AND ( " +
                "    m.model_name     LIKE ? " +
                "    OR v.variant_name LIKE ? " +
                "    OR v.sku          LIKE ? " +
                "    OR c.category_name LIKE ? " +
                "    OR m.model_code   LIKE ? " +
                ") "
            );
        }

        if (!categoryId.isEmpty()) {
            sql.append("AND c.category_id = ? ");
        }

        if (!modelId.isEmpty()) {
            sql.append("AND m.model_id = ? ");
        }

        if (!sku.isEmpty()) {
            sql.append("AND v.sku LIKE ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int idx = 1;

            if (!keyword.isEmpty()) {
                String like = "%" + keyword + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }

            if (!categoryId.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(categoryId));
            }

            if (!modelId.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(modelId));
            }

            if (!sku.isEmpty()) {
                ps.setString(idx++, "%" + sku + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // overload tương thích ngược
    public int countForCashier(String keyword, String categoryId, int branchId) {
        return countForCashier(keyword, categoryId, "", "", branchId);
    }
}