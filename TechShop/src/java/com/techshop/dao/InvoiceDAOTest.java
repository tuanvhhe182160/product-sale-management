/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Invoice;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 *
 * @author justi
 */
public class InvoiceDAOTest extends DBContext{
    public List<Invoice> getInvoicesForAccounting(String startDate, String endDate) {
        List<Invoice> list = new ArrayList<>();
        // JOIN để lấy tên thu ngân, tên khách hàng và tên chi nhánh
        String sql = "SELECT i.*, u.full_name AS cashier_name, c.full_name AS customer_name, b.branch_name " +
                 "FROM Invoice i " +
                 "LEFT JOIN [User] u ON i.cashier_id = u.user_id " +
                 "LEFT JOIN Customer c ON i.customer_id = c.customer_id " +
                 "LEFT JOIN Branch b ON i.branch_id = b.branch_id " +
                 "WHERE 1=1 ";

        boolean hasStart = (startDate != null && !startDate.trim().isEmpty());
        boolean hasEnd = (endDate != null && !endDate.trim().isEmpty());

        if (hasStart) sql += " AND CAST(i.invoice_date AS DATE) >= ? ";
        if (hasEnd) sql += " AND CAST(i.invoice_date AS DATE) <= ? ";

        sql += " ORDER BY i.invoice_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
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
}
