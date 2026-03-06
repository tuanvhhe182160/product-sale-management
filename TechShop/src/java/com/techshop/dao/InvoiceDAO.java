package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.InvoiceCustomerForm;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

/**
 * DAO xử lý nghiệp vụ tạo hóa đơn bán hàng.
 *
 * Toàn bộ logic được thực hiện trong 1 DB transaction:
 *   1. Tạo khách hàng mới (nếu cần)
 *   2. INSERT Invoice
 *   3. INSERT InvoiceItem × n sản phẩm
 *   4. UPDATE PhysicalProduct.status = 'SOLD', sale_date = NOW()
 *   5. INSERT InventoryTransaction type='SALE' × n sản phẩm
 *
 * Nếu bất kỳ bước nào thất bại → rollback toàn bộ.
 */
public class InvoiceDAO extends DBContext {

    /**
     * Tạo hóa đơn hoàn chỉnh.
     *
     * @param form       Thông tin khách hàng + thanh toán từ form
     * @param items      Danh sách IMEI trong giỏ hàng
     * @param branchId   Chi nhánh của cashier
     * @param cashierId  ID nhân viên đang đăng nhập
     * @param saveCustomer true = lưu khách mới vào DB
     * @return invoice_id vừa tạo, hoặc -1 nếu thất bại
     */
    public int createInvoice(InvoiceCustomerForm form,
                             List<CashierSaleItem> items,
                             int branchId,
                             int cashierId,
                             boolean saveCustomer) {
        if (items == null || items.isEmpty()) {
            System.err.println("[InvoiceDAO] createInvoice: items is null or empty!");
            return -1;
        }

        try {
            connection.setAutoCommit(false);
            System.out.println("[InvoiceDAO] === START createInvoice === items=" + items.size()
                + " branchId=" + branchId + " cashierId=" + cashierId + " saveCustomer=" + saveCustomer);

            // ── Bước 1: Xác định customerId ────────────────────────────────
            int customerId = resolveCustomerId(form, saveCustomer);
            System.out.println("[InvoiceDAO] Step1 resolveCustomerId => " + customerId);
            if (customerId <= 0) {
                System.err.println("[InvoiceDAO] FAILED at Step1: customerId=" + customerId);
                connection.rollback();
                return -1;
            }

            // ── Bước 2: Tính tiền ──────────────────────────────────────────
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CashierSaleItem item : items) {
                totalAmount = totalAmount.add(item.getUnitPrice());
            }
            BigDecimal discountAmount = form.getDiscountAmount() != null
                    ? form.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal finalAmount = totalAmount.subtract(discountAmount)
                    .max(BigDecimal.ZERO);
            System.out.println("[InvoiceDAO] Step2 total=" + totalAmount + " discount=" + discountAmount + " final=" + finalAmount);

            // ── Bước 3: Tạo invoice_code duy nhất (INV-YYYYMMDD-XXXXXX) ───
            String invoiceCode = generateInvoiceCode(connection);
            System.out.println("[InvoiceDAO] Step3 invoiceCode=" + invoiceCode);

            // ── Bước 4: INSERT Invoice ─────────────────────────────────────
            int invoiceId = insertInvoice(
                    invoiceCode, customerId, branchId, cashierId,
                    totalAmount, discountAmount, finalAmount,
                    form.getPaymentMethod(), form.getNote());
            System.out.println("[InvoiceDAO] Step4 insertInvoice => invoiceId=" + invoiceId);
            if (invoiceId <= 0) {
                System.err.println("[InvoiceDAO] FAILED at Step4: invoiceId=" + invoiceId);
                connection.rollback();
                return -1;
            }

            // ── Bước 5: INSERT InvoiceItem + UPDATE PhysicalProduct + INSERT InventoryTransaction ──
            for (CashierSaleItem item : items) {
                System.out.println("[InvoiceDAO] Step5 processing physicalId=" + item.getPhysicalId());
                // InvoiceItem
                insertInvoiceItem(invoiceId, item);

                // PhysicalProduct → SOLD
                markAsSold(item.getPhysicalId());

                // InventoryTransaction → SALE
                insertInventoryTransaction(item.getPhysicalId(), branchId, invoiceId, cashierId);
            }

            connection.commit();
            System.out.println("[InvoiceDAO] === SUCCESS createInvoice === invoiceId=" + invoiceId);
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

    // ── Xác định customerId: dùng khách cũ hoặc tạo mới ───────────────────
    private int resolveCustomerId(InvoiceCustomerForm form, boolean saveCustomer)
            throws SQLException {

        // Khách cũ — đã có customerId từ lookup
        if (form.getCustomerId() != null && !form.getCustomerId().trim().isEmpty()) {
            try {
                int id = Integer.parseInt(form.getCustomerId().trim());
                System.out.println("[InvoiceDAO] resolveCustomerId: existing customer id=" + id);
                return id;
            } catch (NumberFormatException ignored) {}
        }

        // Khách mới — kiểm tra xem SĐT đã tồn tại chưa (tránh lỗi UNIQUE constraint)
        if (form.getPhone() != null && !form.getPhone().trim().isEmpty()) {
            int existingId = findCustomerByPhone(form.getPhone().trim());
            if (existingId > 0) {
                System.out.println("[InvoiceDAO] resolveCustomerId: found existing customer by phone="
                    + form.getPhone() + " id=" + existingId);
                return existingId;
            }
        }

        // Khách mới muốn lưu vào DB
        if (saveCustomer) {
            return createCustomer(form.getPhone(), form.getFullName(),
                                  form.getEmail(), form.getAddress());
        }

        // Khách mới không lưu → dùng tài khoản khách vãng lai của chi nhánh
        // Quy ước: phone = "0000000000" là khách vãng lai
        return findOrCreateWalkInCustomer();
    }

    // ── Tìm customer theo SĐT, trả về customer_id hoặc -1 ────────────────
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

    // ── Tìm hoặc tạo khách vãng lai ────────────────────────────────────────
    private int findOrCreateWalkInCustomer() throws SQLException {
        String selectSql = "SELECT customer_id FROM Customer WHERE phone = '0000000000'";
        try (PreparedStatement ps = connection.prepareStatement(selectSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        // Chưa có → tạo mới
        return createCustomer("0000000000", "Khách vãng lai", null, null);
    }

    // ── INSERT Customer mới, trả về customer_id ────────────────────────────
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
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("[InvoiceDAO] Created new customer id=" + id + " phone=" + phone);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("[InvoiceDAO] createCustomer FAILED for phone=" + phone + ": " + e.getMessage());
            throw e; // Re-throw để transaction rollback
        }
        return -1;
    }

    // ── INSERT Invoice, trả về invoice_id ──────────────────────────────────
    private int insertInvoice(String invoiceCode, int customerId, int branchId,
                              int cashierId, BigDecimal totalAmount,
                              BigDecimal discountAmount, BigDecimal finalAmount,
                              String paymentMethod, String note)
            throws SQLException {
        String sql =
            "INSERT INTO Invoice " +
            "  (invoice_code, customer_id, branch_id, cashier_id, " +
            "   total_amount, discount_amount, final_amount, " +
            "   payment_method, status, note) " +
            "OUTPUT INSERTED.invoice_id " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'COMPLETED', ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, invoiceCode);
            ps.setInt(2, customerId);
            ps.setInt(3, branchId);
            ps.setInt(4, cashierId);
            ps.setBigDecimal(5, totalAmount);
            ps.setBigDecimal(6, discountAmount);
            ps.setBigDecimal(7, finalAmount);
            ps.setString(8, paymentMethod != null ? paymentMethod : "CASH");
            ps.setString(9, (note != null && !note.trim().isEmpty()) ? note.trim() : null);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ── INSERT InvoiceItem ─────────────────────────────────────────────────
    private void insertInvoiceItem(int invoiceId, CashierSaleItem item)
            throws SQLException {
        String sql =
            "INSERT INTO InvoiceItem " +
            "  (invoice_id, physical_id, variant_id, unit_price, quantity, subtotal, warranty_months) " +
            "VALUES (?, ?, ?, ?, 1, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, item.getPhysicalId());
            ps.setInt(3, item.getVariantId());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setBigDecimal(5, item.getUnitPrice()); // quantity=1 nên subtotal = unitPrice
            ps.setInt(6, item.getWarrantyMonths() > 0 ? item.getWarrantyMonths() : 12);
            ps.executeUpdate();
        }
    }

    // ── UPDATE PhysicalProduct: IN_STOCK → SOLD ────────────────────────────
    private void markAsSold(int physicalId) throws SQLException {
        String sql =
            "UPDATE PhysicalProduct " +
            "SET status = 'SOLD', sale_date = GETDATE(), updated_at = GETDATE() " +
            "WHERE physical_id = ? AND status = 'IN_STOCK'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                // Sản phẩm không còn IN_STOCK → có thể đã được bán bởi người khác
                throw new SQLException(
                    "PhysicalProduct " + physicalId + " không còn IN_STOCK — hóa đơn bị hủy.");
            }
        }
    }

    // ── INSERT InventoryTransaction type=SALE ──────────────────────────────
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

    // ── Tạo invoice_code duy nhất: INV-YYYYMMDD-XXXXXX ────────────────────
    // Đếm số hóa đơn trong ngày hôm nay rồi tăng thêm 1
    private String generateInvoiceCode(Connection conn) throws SQLException {
        String sql =
            "SELECT COUNT(*) FROM Invoice " +
            "WHERE CAST(invoice_date AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = rs.next() ? rs.getInt(1) : 0;
            String datePart = new java.text.SimpleDateFormat("yyyyMMdd")
                    .format(new java.util.Date());
            return String.format("INV-%s-%06d", datePart, count + 1);
        }
    }
}