package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.PhysicalProduct;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DAO cho trang chi tiết sản phẩm.
 * Thông tin variant dùng chung CashierCartDAO.getVariantForCart().
 * File này chỉ chứa các query riêng cho trang detail.
 */
public class ProductDetailDAO extends DBContext {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Lấy danh sách PhysicalProduct thuộc variant tại chi nhánh.
     */
    public List<PhysicalProduct> getPhysicalProductsByVariant(int variantId, int branchId) {
        List<PhysicalProduct> list = new ArrayList<>();
        String sql =
            "SELECT p.physical_id, p.imei, p.serial_number, p.status, " +
            "       p.import_date, p.sale_date, b.branch_name " +
            "FROM PhysicalProduct p " +
            "JOIN Branch b ON p.branch_id = b.branch_id " +
            "WHERE p.variant_id = ? AND p.branch_id = ? " +
            "ORDER BY p.status, p.import_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ps.setInt(2, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhysicalProduct pp = new PhysicalProduct();
                    pp.setPhysicalId(rs.getInt("physical_id"));
                    pp.setImei(rs.getString("imei"));
                    pp.setSerialNumber(rs.getString("serial_number"));
                    pp.setStatus(rs.getString("status"));
                    pp.setBranchName(rs.getString("branch_name"));

                    Timestamp imp = rs.getTimestamp("import_date");
                    if (imp != null) pp.setImportDateStr(imp.toLocalDateTime().format(FMT));

                    Timestamp sale = rs.getTimestamp("sale_date");
                    if (sale != null) pp.setSaleDateStr(sale.toLocalDateTime().format(FMT));

                    list.add(pp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thông số kỹ thuật (VariantAttribute).
     */
    public Map<String, String> getVariantAttributes(int variantId) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String sql =
            "SELECT attribute_name, attribute_value " +
            "FROM VariantAttribute WHERE variant_id = ? ORDER BY attribute_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    attrs.put(rs.getString("attribute_name"), rs.getString("attribute_value"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attrs;
    }
}
