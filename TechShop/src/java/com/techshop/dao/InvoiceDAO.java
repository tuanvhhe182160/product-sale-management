package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.InvoiceCustomerForm;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * DAO xử lý nghiệp vụ tạo hóa đơn bán hàng.
 *
 * Giỏ hàng lưu theo Variant + quantity.
 * Khi tạo hóa đơn, pick random PhysicalProduct IN_STOCK theo variant.
 *
 * Transaction:
 *   1. Tạo/tìm khách hàng
 *   2. INSERT Invoice
 *   3. Với mỗi variant trong giỏ:
 *      a. Pick N PhysicalProduct IN_STOCK (N = quantity)
 *      b. INSERT InvoiceItem × N
 *      c. UPDATE PhysicalProduct.status = 'SOLD' × N
 *      d. INSERT InventoryTransaction × N
 *   4. Commit hoặc rollback
 */
public class InvoiceDAO extends DBContext {

    public int createInvoice(InvoiceCustomerForm form,
                             List<CashierSaleItem> items,
                             int branchId,
                             int cashierId,
                             boolean saveCustomer) {
        if (items == null || items.isEmpty()) return -1;

        try {
            connection.setAutoCommit(false);

            // Bước 1: Xác định customerId
            int customerId = resolveCustomerId(form, saveCustomer);
            if (customerId <= 0) { connection.rollback(); return -1; }

            // Bước 2: Tính tiền
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CashierSaleItem item : items) {
                totalAmount = totalAmount.add(item.getSubtotal());
            }
            BigDecimal discountAmount = form.getDiscountAmount() != null
                    ? form.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal finalAmount = totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);

            // Bước 3: Tạo invoice_code
            String invoiceCode = generateInvoiceCode(connection);

            // Bước 4: INSERT Invoice
            int invoiceId = insertInvoice(
                    invoiceCode, customerId, branchId, cashierId,
                    totalAmount, discountAmount, finalAmount,
                    form.getPaymentMethod(), form.getNote());
            if (invoiceId <= 0) { connection.rollback(); return -1; }

            // Bước 5: Với mỗi variant, pick PhysicalProduct và tạo InvoiceItem
            for (CashierSaleItem item : items) {
                List<Integer> physicalIds = pickPhysicalProducts(
                        item.getVariantId(), branchId, item.getQuantity());

                if (physicalIds.size() < item.getQuantity()) {
                    // Không đủ tồn kho
                    System.err.println("[InvoiceDAO] Not enough stock for variant="
                        + item.getVariantId() + " need=" + item.getQuantity()
                        + " got=" + physicalIds.size());
                    connection.rollback();
                    return -1;
                }

                for (int physicalId : physicalIds) {
                    insertInvoiceItem(invoiceId, physicalId, item);
                    markAsSold(physicalId);
                    insertInventoryTransaction(physicalId, branchId, invoiceId, cashierId);
                }
            }

            connection.commit();
            return invoiceId;

        } catch (Exception e) {
            System.err.println("[InvoiceDAO] createInvoice FAILED: " + e.getMessage());
            e.printStackTrace();
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ── Pick N PhysicalProduct IN_STOCK ngẫu nhiên (trong transaction) ──
    private List<Integer> pickPhysicalProducts(int variantId, int branchId, int quantity)
            throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT TOP (?) physical_id FROM PhysicalProduct " +
                     "WHERE variant_id = ? AND branch_id = ? AND status = 'IN_STOCK' " +
                     "ORDER BY NEWID()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, variantId);
            ps.setInt(3, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("physical_id"));
            }
        }
        return ids;
    }

    // ── Xác định customerId ─────────────────────────────────────────────
    private int resolveCustomerId(InvoiceCustomerForm form, boolean saveCustomer)
            throws SQLException {
        if (form.getCustomerId() != null && !form.getCustomerId().trim().isEmpty()) {
            try { return Integer.parseInt(form.getCustomerId().trim()); }
            catch (NumberFormatException ignored) {}
        }
        if (form.getPhone() != null && !form.getPhone().trim().isEmpty()) {
            int existingId = findCustomerByPhone(form.getPhone().trim());
            if (existingId > 0) return existingId;
        }
        if (saveCustomer) {
            return createCustomer(form.getPhone(), form.getFullName(),
                                  form.getEmail(), form.getAddress());
        }
        return findOrCreateWalkInCustomer();
    }

    private int findCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT customer_id FROM Customer WHERE phone = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private int findOrCreateWalkInCustomer() throws SQLException {
        String sql = "SELECT customer_id FROM Customer WHERE phone = '0000000000'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return createCustomer("0000000000", "Khách vãng lai", null, null);
    }

    private int createCustomer(String phone, String fullName, String email, String address)
            throws SQLException {
        if (fullName == null || fullName.trim().isEmpty()) fullName = "Khách " + phone;
        String sql = "INSERT INTO Customer (phone, full_name, email, address) " +
                     "OUTPUT INSERTED.customer_id VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            ps.setString(2, fullName.trim());
            ps.setString(3, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
            ps.setString(4, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private int insertInvoice(String invoiceCode, int customerId, int branchId,
                              int cashierId, BigDecimal totalAmount,
                              BigDecimal discountAmount, BigDecimal finalAmount,
                              String paymentMethod, String note)
            throws SQLException {
        // TRANSFER và MIXED cần kế toán đối soát trước khi xác nhận doanh thu
        String status = ("TRANSFER".equalsIgnoreCase(paymentMethod) ||
                         "MIXED".equalsIgnoreCase(paymentMethod))
                        ? "PENDING" : "COMPLETED";
        String sql =
            "INSERT INTO Invoice " +
            "  (invoice_code, customer_id, branch_id, cashier_id, " +
            "   total_amount, discount_amount, final_amount, " +
            "   payment_method, status, note) " +
            "OUTPUT INSERTED.invoice_id " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, invoiceCode);
            ps.setInt(2, customerId);
            ps.setInt(3, branchId);
            ps.setInt(4, cashierId);
            ps.setBigDecimal(5, totalAmount);
            ps.setBigDecimal(6, discountAmount);
            ps.setBigDecimal(7, finalAmount);
            ps.setString(8, paymentMethod != null ? paymentMethod : "CASH");
            ps.setString(9, status);
            ps.setString(10, (note != null && !note.trim().isEmpty()) ? note.trim() : null);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private void insertInvoiceItem(int invoiceId, int physicalId, CashierSaleItem item)
            throws SQLException {
        String sql =
            "INSERT INTO InvoiceItem " +
            "  (invoice_id, physical_id, variant_id, unit_price, quantity, subtotal, warranty_months) " +
            "VALUES (?, ?, ?, ?, 1, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, physicalId);
            ps.setInt(3, item.getVariantId());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setBigDecimal(5, item.getUnitPrice());
            ps.setInt(6, item.getWarrantyMonths() > 0 ? item.getWarrantyMonths() : 12);
            ps.executeUpdate();
        }
    }

    private void markAsSold(int physicalId) throws SQLException {
        String sql =
            "UPDATE PhysicalProduct " +
            "SET status = 'SOLD', sale_date = GETDATE(), updated_at = GETDATE() " +
            "WHERE physical_id = ? AND status = 'IN_STOCK'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException(
                    "PhysicalProduct " + physicalId + " không còn IN_STOCK.");
            }
        }
    }

    private void insertInventoryTransaction(int physicalId, int branchId,
                                            int invoiceId, int cashierId)
            throws SQLException {
        String sql =
            "INSERT INTO InventoryTransaction " +
            "  (transaction_type, physical_id, from_branch_id, quantity, reference_id, performed_by, note) " +
            "VALUES ('SALE', ?, ?, 1, ?, ?, N'Bán hàng tại quầy')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            ps.setInt(2, branchId);
            ps.setInt(3, invoiceId);
            ps.setInt(4, cashierId);
            ps.executeUpdate();
        }
    }

    private String generateInvoiceCode(Connection conn) throws SQLException {
        String sql =
            "SELECT COUNT(*) FROM Invoice " +
            "WHERE CAST(invoice_date AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = rs.next() ? rs.getInt(1) : 0;
            String datePart = new SimpleDateFormat("yyyyMMdd")
                    .format(new Date());
            return String.format("INV-%s-%06d", datePart, count + 1);
        }
    }
}