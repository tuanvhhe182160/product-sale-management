package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Invoice;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReconciliationDAO extends DBContext {

    // 1. Lấy danh sách hóa đơn Chuyển khoản/Thẻ đang chờ duyệt (PENDING)
    public List<Invoice> getPendingBankInvoices(int branchId) {
        List<Invoice> list = new ArrayList<>();
        // Lấy thêm tên khách hàng và thu ngân để hiển thị cho Kế toán dễ tra cứu
        String sql = "SELECT i.*, c.full_name as customer_name, u.full_name as cashier_name " +
                     "FROM Invoice i " +
                     "LEFT JOIN Customer c ON i.customer_id = c.customer_id " +
                     "LEFT JOIN [User] u ON i.cashier_id = u.user_id " +
                     "WHERE i.branch_id = ? AND i.status = 'PENDING' " +
                     "AND i.payment_method IN ('TRANSFER', 'CARD', 'MIXED') " +
                     "ORDER BY i.invoice_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, branchId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setInvoiceId(rs.getInt("invoice_id"));
                inv.setInvoiceCode(rs.getString("invoice_code"));
                inv.setFinalAmount(rs.getBigDecimal("final_amount"));
                inv.setPaymentMethod(rs.getString("payment_method"));
                inv.setInvoiceDate(rs.getTimestamp("invoice_date").toLocalDateTime());
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setCashierName(rs.getString("cashier_name"));
                inv.setStatus(rs.getString("status"));
                list.add(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Xác nhận đã nhận tiền -> Chuyển status sang COMPLETED
    public boolean confirmPayment(int invoiceId) {
        String sql = "UPDATE Invoice SET status = 'COMPLETED' WHERE invoice_id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, invoiceId);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}