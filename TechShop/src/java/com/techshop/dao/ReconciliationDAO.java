package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Invoice;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReconciliationDAO extends DBContext {

    /**
     * Lấy danh sách hóa đơn TRANSFER/MIXED đang chờ đối soát.
     * CHỈ lấy invoice thuộc branchId của Kế toán (bảo mật cross-branch).
     * Hỗ trợ lọc ngày và phân trang.
     */
    public List<Invoice> getPendingBankInvoices(int branchId,
                                                String dateFrom,
                                                String dateTo,
                                                int page,
                                                int pageSize) {
        List<Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.invoice_id, i.invoice_code, i.invoice_date, " +
            "  i.final_amount, i.payment_method, i.status, " +
            "  c.full_name AS customer_name, u.full_name AS cashier_name " +
            "FROM Invoice i " +
            "LEFT JOIN Customer c ON i.customer_id = c.customer_id " +
            "LEFT JOIN [User] u ON i.cashier_id = u.user_id " +
            "WHERE i.branch_id = ? " +
            "  AND i.status = 'PENDING' " +
            "  AND i.payment_method IN ('TRANSFER', 'MIXED') "
        );
        List<Object> params = new ArrayList<>();
        params.add(branchId);
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND CAST(i.invoice_date AS DATE) >= ? ");
            params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND CAST(i.invoice_date AS DATE) <= ? ");
            params.add(dateTo.trim());
        }
        sql.append("ORDER BY i.invoice_date ASC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceId(rs.getInt("invoice_id"));
                inv.setInvoiceCode(rs.getString("invoice_code"));
                inv.setInvoiceDate(rs.getTimestamp("invoice_date").toLocalDateTime());
                inv.setFinalAmount(rs.getBigDecimal("final_amount"));
                inv.setPaymentMethod(rs.getString("payment_method"));
                inv.setStatus(rs.getString("status"));
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setCashierName(rs.getString("cashier_name"));
                list.add(inv);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int countPendingBankInvoices(int branchId, String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Invoice " +
            "WHERE branch_id = ? AND status = 'PENDING' " +
            "  AND payment_method IN ('TRANSFER', 'MIXED') "
        );
        List<Object> params = new ArrayList<>();
        params.add(branchId);
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND CAST(invoice_date AS DATE) >= ? "); params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND CAST(invoice_date AS DATE) <= ? "); params.add(dateTo.trim());
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Xác nhận nhận tiền: chỉ cập nhật invoice thuộc đúng branch + đang PENDING.
     * Tránh kế toán chi nhánh A confirm invoice của chi nhánh B.
     */
    public boolean confirmPayment(int invoiceId, int branchId) {
        String sql = "UPDATE Invoice SET status = 'COMPLETED' " +
                     "WHERE invoice_id = ? AND branch_id = ? " +
                     "  AND status = 'PENDING' AND payment_method IN ('TRANSFER', 'MIXED')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, branchId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}