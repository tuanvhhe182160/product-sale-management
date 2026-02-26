/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dao;

/**
 *
 * @author Admin
 */

import com.techshop.dal.DBContext;
import com.techshop.model.CashierSaleItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO cho trang Product Detail của Cashier.
 * Tất cả query đều READ-ONLY (SELECT thuần).
 */
public class ProductDetailDAO extends DBContext {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── 1. Thông tin chính của 1 IMEI ────────────────────────────────────────
    public CashierSaleItem getPhysicalDetail(int physicalId) {
        String sql =
            "SELECT " +
            "    p.physical_id, p.variant_id, p.branch_id, p.imei, p.serial_number, " +
            "    p.status, p.import_date, p.sale_date, " +
            "    v.sku, v.variant_name, v.base_price, v.warranty_months, v.image_url, " +
            "    m.model_id, m.model_name, m.brand, m.description AS model_desc, " +
            "    c.category_name, " +
            "    b.branch_name " +
            "FROM PhysicalProduct p " +
            "JOIN ProductVariant  v ON p.variant_id  = v.variant_id " +
            "JOIN ProductModel    m ON v.model_id    = m.model_id " +
            "JOIN ProductCategory c ON m.category_id = c.category_id " +
            "JOIN Branch          b ON p.branch_id   = b.branch_id " +
            "WHERE p.physical_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CashierSaleItem item = new CashierSaleItem();
                    item.setPhysicalId(rs.getInt("physical_id"));
                    item.setVariantId(rs.getInt("variant_id"));
                    item.setBranchId(rs.getInt("branch_id"));
                    item.setImei(rs.getString("imei"));
                    item.setSerialNumber(rs.getString("serial_number"));
                    item.setStatus(rs.getString("status"));
                    item.setVariantName(rs.getString("variant_name"));
                    item.setSku(rs.getString("sku"));
                    item.setUnitPrice(rs.getBigDecimal("base_price"));
                    item.setWarrantyMonths(rs.getInt("warranty_months"));
                    item.setImageUrl(rs.getString("image_url"));
                    item.setModelName(rs.getString("model_name"));
                    item.setBrand(rs.getString("brand"));
                    item.setCategoryName(rs.getString("category_name"));
                    item.setBranchName(rs.getString("branch_name"));
                    item.setModelDesc(rs.getString("model_desc"));

                    Timestamp imp = rs.getTimestamp("import_date");
                    if (imp != null) item.setImportDateStr(imp.toLocalDateTime().format(FMT));

                    Timestamp sale = rs.getTimestamp("sale_date");
                    if (sale != null) item.setSaleDateStr(sale.toLocalDateTime().format(FMT));

                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── 2. Thông số kỹ thuật (VariantAttribute) ──────────────────────────────
    public Map<String, String> getVariantAttributes(int variantId) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String sql =
            "SELECT attribute_name, attribute_value " +
            "FROM VariantAttribute " +
            "WHERE variant_id = ? " +
            "ORDER BY attribute_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    attrs.put(rs.getString("attribute_name"),
                              rs.getString("attribute_value"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attrs;
    }

    // ── 3. Lịch sử giao dịch kho (InventoryTransaction) ─────────────────────
    public List<Map<String, String>> getInventoryHistory(int physicalId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql =
            "SELECT " +
            "    t.transaction_type, t.note, t.transaction_date, " +
            "    u.full_name AS performed_by, " +
            "    fb.branch_name AS from_branch, " +
            "    tb.branch_name AS to_branch " +
            "FROM InventoryTransaction t " +
            "JOIN [User] u ON t.performed_by = u.user_id " +
            "LEFT JOIN Branch fb ON t.from_branch_id = fb.branch_id " +
            "LEFT JOIN Branch tb ON t.to_branch_id   = tb.branch_id " +
            "WHERE t.physical_id = ? " +
            "ORDER BY t.transaction_date ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("type",        rs.getString("transaction_type"));
                    row.put("performedBy", rs.getString("performed_by"));
                    row.put("fromBranch",  rs.getString("from_branch"));
                    row.put("toBranch",    rs.getString("to_branch"));
                    row.put("note",        rs.getString("note"));

                    Timestamp ts = rs.getTimestamp("transaction_date");
                    row.put("date", ts != null ? ts.toLocalDateTime().format(FMT) : "—");

                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── 4. Lịch sử bảo hành (WarrantyRequest) ───────────────────────────────
    public List<Map<String, String>> getWarrantyHistory(int physicalId) {
        List<Map<String, String>> list = new ArrayList<>();
        String sql =
            "SELECT " +
            "    w.request_code, w.status, w.issue_description, " +
            "    w.resolution, w.request_date, w.completion_date, " +
            "    cs.full_name AS cs_name, " +
            "    tech.full_name AS tech_name " +
            "FROM WarrantyRequest w " +
            "JOIN [User] cs ON w.customer_service_id = cs.user_id " +
            "LEFT JOIN [User] tech ON w.technician_id = tech.user_id " +
            "WHERE w.physical_id = ? " +
            "ORDER BY w.request_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("code",        rs.getString("request_code"));
                    row.put("status",      rs.getString("status"));
                    row.put("issue",       rs.getString("issue_description"));
                    row.put("resolution",  rs.getString("resolution"));
                    row.put("csName",      rs.getString("cs_name"));
                    row.put("techName",    rs.getString("tech_name"));

                    Timestamp req = rs.getTimestamp("request_date");
                    row.put("requestDate", req != null ? req.toLocalDateTime().format(FMT) : "—");

                    Timestamp comp = rs.getTimestamp("completion_date");
                    row.put("completionDate", comp != null ? comp.toLocalDateTime().format(FMT) : "—");

                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}