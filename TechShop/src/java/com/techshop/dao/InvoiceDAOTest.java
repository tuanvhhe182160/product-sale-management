/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Invoice;
import com.techshop.model.InvoiceItem;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 *
 * @author justi
 */
public class InvoiceDAOTest extends DBContext{
    public List<Invoice> getInvoicesForAccounting(String startDate, String endDate, Integer currentBranchId) {
        List<Invoice> list = new ArrayList<>();
        // JOIN để lấy tên thu ngân, tên khách hàng và tên chi nhánh
        String sql = "SELECT i.*, u.full_name AS cashier_name, c.full_name AS customer_name, b.branch_name " +
                 "FROM Invoice i " +
                 "LEFT JOIN [User] u ON i.cashier_id = u.user_id " +
                 "LEFT JOIN Customer c ON i.customer_id = c.customer_id " +
                 "LEFT JOIN Branch b ON i.branch_id = b.branch_id " +
                 "WHERE 1=1 AND i.branch_id = ? ";

        boolean hasStart = (startDate != null && !startDate.trim().isEmpty());
        boolean hasEnd = (endDate != null && !endDate.trim().isEmpty());

        if (hasStart) sql += " AND CAST(i.invoice_date AS DATE) >= ? ";
        if (hasEnd) sql += " AND CAST(i.invoice_date AS DATE) <= ? ";

        sql += " ORDER BY i.invoice_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, currentBranchId);
            if (hasStart) ps.setString(paramIndex++, startDate);
            if (hasEnd) ps.setString(paramIndex, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setInvoiceId(rs.getInt("invoice_id"));
                    inv.setInvoiceCode(rs.getString("invoice_code"));
                    inv.setCustomerId(rs.getInt("customer_id"));
                    inv.setBranchId(rs.getInt("branch_id"));
                    inv.setCashierId(rs.getInt("cashier_id"));
                
                    // Sử dụng getBigDecimal cho độ chính xác tuyệt đối
                    inv.setTotalAmount(rs.getBigDecimal("total_amount"));
                    inv.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    inv.setFinalAmount(rs.getBigDecimal("final_amount"));
                
                    inv.setPaymentMethod(rs.getString("payment_method"));
                    inv.setStatus(rs.getString("status"));
                    inv.setNote(rs.getString("note"));

                    java.sql.Timestamp invDate = rs.getTimestamp("invoice_date");
                    if (invDate != null) inv.setInvoiceDate(invDate.toLocalDateTime());

                    java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) inv.setCreatedAt(createdAt.toLocalDateTime());

                    // Thông tin JOIN
                    inv.setCustomerName(rs.getString("customer_name"));
                    inv.setCashierName(rs.getString("cashier_name"));
                    inv.setBranchName(rs.getString("branch_name"));

                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            System.err.println("InvoiceDAO.getInvoicesForAccounting Error: " + e.getMessage());
        }
        return list;
    }
    
    public Invoice getInvoiceDetail(int invoiceId, Integer currentBranchId) {
        String sql = """
        SELECT i.*, 
               u.full_name AS cashier_name, 
               c.full_name AS customer_name, 
               c.phone AS customer_phone,
               b.branch_name
        FROM Invoice i
        LEFT JOIN [User] u ON i.cashier_id = u.user_id
        LEFT JOIN Customer c ON i.customer_id = c.customer_id
        LEFT JOIN Branch b ON i.branch_id = b.branch_id
        WHERE i.invoice_id = ?
          AND i.branch_id = ?
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, invoiceId);
        ps.setInt(2, currentBranchId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Invoice inv = new Invoice();

                inv.setInvoiceId(rs.getInt("invoice_id"));
                inv.setInvoiceCode(rs.getString("invoice_code"));
                inv.setCustomerId(rs.getInt("customer_id"));
                inv.setBranchId(rs.getInt("branch_id"));
                inv.setCashierId(rs.getInt("cashier_id"));
                inv.setTotalAmount(rs.getBigDecimal("total_amount"));
                inv.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                inv.setFinalAmount(rs.getBigDecimal("final_amount"));
                inv.setPaymentMethod(rs.getString("payment_method"));
                inv.setStatus(rs.getString("status"));
                inv.setNote(rs.getString("note"));

                Timestamp invoiceDate = rs.getTimestamp("invoice_date");
                if (invoiceDate != null)
                    inv.setInvoiceDate(invoiceDate.toLocalDateTime());

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null)
                    inv.setCreatedAt(createdAt.toLocalDateTime());

                    inv.setCustomerName(rs.getString("customer_name"));
                    inv.setCustomerPhone(rs.getString("customer_phone"));
                    inv.setBranchName(rs.getString("branch_name"));
                    inv.setCashierName(rs.getString("cashier_name"));

                    return inv;
                }
            }
        } catch (SQLException e) {
            System.err.println("getInvoiceDetail Error: " + e.getMessage());
        }
        return null;
    }
    
    public List<InvoiceItem> getInvoiceItems(int invoiceId, Integer currentBranchId) {
        List<InvoiceItem> list = new ArrayList<>();

        String sql = """
        SELECT ii.*, 
               v.variant_name,
               v.sku,
               p.imei
        FROM InvoiceItem ii
        LEFT JOIN ProductVariant v ON ii.variant_id = v.variant_id
        LEFT JOIN PhysicalProduct p ON ii.physical_id = p.physical_id
        INNER JOIN Invoice i ON ii.invoice_id = i.invoice_id
        WHERE ii.invoice_id = ?
          AND i.branch_id = ?
    """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, invoiceId);
        ps.setInt(2, currentBranchId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                InvoiceItem item = new InvoiceItem();

                item.setItemId(rs.getInt("item_id"));
                item.setInvoiceId(rs.getInt("invoice_id"));
                item.setPhysicalId(rs.getInt("physical_id"));
                item.setVariantId(rs.getInt("variant_id"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setQuantity(rs.getInt("quantity"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));
                item.setWarrantyMonths(rs.getInt("warranty_months"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null)
                    item.setCreatedAt(createdAt.toLocalDateTime());

                    item.setVariantName(rs.getString("variant_name"));
                    item.setSku(rs.getString("sku"));
                    item.setImei(rs.getString("imei"));

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("getInvoiceItems Error: " + e.getMessage());
        }

        return list;
    }
    
    // --- BỔ SUNG VÀO INVOICEDAO ---
    public List<Invoice> getCustomerInvoices(int customerId, String fromDate, String toDate, String status) {
        List<com.techshop.model.Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Invoice WHERE customer_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(customerId);

        // Lọc theo ngày bắt đầu
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append(" AND CAST(invoice_date AS DATE) >= ? ");
            params.add(fromDate.trim());
        }
        // Lọc theo ngày kết thúc
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append(" AND CAST(invoice_date AS DATE) <= ? ");
            params.add(toDate.trim());
        }
        // Lọc theo trạng thái
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ? ");
            params.add(status.trim());
        }
        
        sql.append(" ORDER BY invoice_date DESC"); // Đơn mới nhất xếp lên đầu

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.techshop.model.Invoice inv = new com.techshop.model.Invoice();
                    inv.setInvoiceId(rs.getInt("invoice_id"));
                    inv.setInvoiceCode(rs.getString("invoice_code"));
                    inv.setTotalAmount(rs.getBigDecimal("total_amount"));
                    inv.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    inv.setFinalAmount(rs.getBigDecimal("final_amount"));
                    inv.setInvoiceDate(rs.getTimestamp("invoice_date").toLocalDateTime());
                    inv.setStatus(rs.getString("status"));
                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
