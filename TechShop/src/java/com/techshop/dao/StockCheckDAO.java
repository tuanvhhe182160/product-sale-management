package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StockCheckDAO extends DBContext {

    public ProductVariant getVariantDetail(int variantId, int branchId) {
        String sql =
            "SELECT v.variant_id, v.sku, v.variant_name, v.base_price, " +
            "       v.warranty_months, v.image_url, v.status, " +
            "       m.model_name, m.brand, c.category_name, " +
            "       ISNULL(stock.cnt, 0) AS stock " +
            "FROM ProductVariant v " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            "LEFT JOIN ( " +
            "    SELECT variant_id, COUNT(*) AS cnt " +
            "    FROM PhysicalProduct " +
            "    WHERE status = 'IN_STOCK' AND branch_id = ? " +
            "    GROUP BY variant_id " +
            ") stock ON v.variant_id = stock.variant_id " +
            "WHERE v.variant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductVariant v = new ProductVariant();
                    v.setVariantId(rs.getInt("variant_id"));
                    v.setSku(rs.getString("sku"));
                    v.setVariantName(rs.getString("variant_name"));
                    v.setBasePrice(rs.getBigDecimal("base_price"));
                    v.setWarrantyMonths(rs.getInt("warranty_months"));
                    v.setImageUrl(rs.getString("image_url"));
                    v.setStatus(rs.getString("status"));
                    v.setModelName(rs.getString("model_name"));
                    v.setBrand(rs.getString("brand"));
                    v.setCategoryName(rs.getString("category_name"));
                    v.setStock(rs.getInt("stock"));
                    return v;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PhysicalProduct> getPhysicalList(int variantId, int branchId, String statusFilter) {
        List<PhysicalProduct> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT p.physical_id, p.variant_id, p.branch_id, " +
            "       p.imei, p.serial_number, p.status, " +
            "       p.import_date, p.sale_date " +
            "FROM PhysicalProduct p " +
            "WHERE p.variant_id = ? AND p.branch_id = ? "
        );

        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("ALL")) {
            sql.append("AND p.status = ? ");
        }

        sql.append("ORDER BY p.status ASC, p.import_date DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, variantId);
            ps.setInt(2, branchId);
            if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("ALL")) {
                ps.setString(3, statusFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhysicalProduct p = new PhysicalProduct();
                    p.setPhysicalId(rs.getInt("physical_id"));
                    p.setVariantId(rs.getInt("variant_id"));
                    p.setBranchId(rs.getInt("branch_id"));
                    p.setImei(rs.getString("imei"));
                    p.setSerialNumber(rs.getString("serial_number"));
                    p.setStatus(rs.getString("status"));

                    Timestamp imp = rs.getTimestamp("import_date");
                    if (imp != null) p.setImportDate(imp.toLocalDateTime());

                    Timestamp sale = rs.getTimestamp("sale_date");
                    if (sale != null) p.setSaleDate(sale.toLocalDateTime());

                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countByStatus(int variantId, int branchId, String status) {
        String sql =
            "SELECT COUNT(*) FROM PhysicalProduct " +
            "WHERE variant_id = ? AND branch_id = ? AND status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.setInt(2, branchId);
            ps.setString(3, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}