package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.InvoicePrintData;
import com.techshop.model.InvoicePrintData.InvoicePrintItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO lấy toàn bộ thông tin hóa đơn để render trang in.
 */
public class InvoicePrintDAO extends DBContext {

    public InvoicePrintData getInvoicePrintData(int invoiceId) {

        // ── Query 1: Thông tin hóa đơn + khách hàng + cashier + chi nhánh ──
        String invoiceSql =
            "SELECT " +
            "  i.invoice_id, i.invoice_code, i.invoice_date, " +
            "  i.total_amount, i.discount_amount, i.final_amount, " +
            "  i.payment_method, i.note, " +
            "  c.full_name  AS customer_name,  c.phone AS customer_phone, " +
            "  c.email      AS customer_email, c.address AS customer_address, " +
            "  u.full_name  AS cashier_name, " +
            "  b.branch_name, b.address AS branch_address, b.phone AS branch_phone " +
            "FROM Invoice i " +
            "JOIN Customer c ON i.customer_id = c.customer_id " +
            "JOIN [User]   u ON i.cashier_id  = u.user_id " +
            "JOIN Branch   b ON i.branch_id   = b.branch_id " +
            "WHERE i.invoice_id = ?";

        // ── Query 2: Danh sách sản phẩm trong hóa đơn ──
        String itemsSql =
            "SELECT " +
            "  v.variant_name, p.imei, v.sku, " +
            "  ii.unit_price, ii.warranty_months " +
            "FROM InvoiceItem ii " +
            "JOIN PhysicalProduct p  ON ii.physical_id = p.physical_id " +
            "JOIN ProductVariant  v  ON ii.variant_id  = v.variant_id " +
            "WHERE ii.invoice_id = ? " +
            "ORDER BY ii.item_id";

        try {
            InvoicePrintData data = new InvoicePrintData();

            // Query 1
            try (PreparedStatement ps = connection.prepareStatement(invoiceSql)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    data.setInvoiceId(rs.getInt("invoice_id"));
                    data.setInvoiceCode(rs.getString("invoice_code"));
                    if (rs.getTimestamp("invoice_date") != null)
                        data.setInvoiceDate(rs.getTimestamp("invoice_date").toLocalDateTime());
                    data.setTotalAmount(rs.getBigDecimal("total_amount"));
                    data.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    data.setFinalAmount(rs.getBigDecimal("final_amount"));
                    data.setPaymentMethod(rs.getString("payment_method"));
                    data.setNote(rs.getString("note"));
                    data.setCustomerName(rs.getString("customer_name"));
                    data.setCustomerPhone(rs.getString("customer_phone"));
                    data.setCustomerEmail(rs.getString("customer_email"));
                    data.setCustomerAddress(rs.getString("customer_address"));
                    data.setCashierName(rs.getString("cashier_name"));
                    data.setBranchName(rs.getString("branch_name"));
                    data.setBranchAddress(rs.getString("branch_address"));
                    data.setBranchPhone(rs.getString("branch_phone"));
                }
            }

            // Query 2
            List<InvoicePrintItem> items = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(itemsSql)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        InvoicePrintItem item = new InvoicePrintItem();
                        item.setVariantName(rs.getString("variant_name"));
                        item.setImei(rs.getString("imei"));
                        item.setSku(rs.getString("sku"));
                        item.setUnitPrice(rs.getBigDecimal("unit_price"));
                        item.setWarrantyMonths(rs.getInt("warranty_months"));
                        items.add(item);
                    }
                }
            }
            data.setItems(items);
            return data;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}