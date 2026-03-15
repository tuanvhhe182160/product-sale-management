package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.AccountingPeriod;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountingPeriodDAO extends DBContext {

    // 1️⃣ Kiểm tra kỳ đã chốt chưa
    public boolean isPeriodClosed(int branchId, int month, int year) {

        String sql = "SELECT COUNT(*) " +
                     "FROM AccountingPeriod " +
                     "WHERE branch_id = ? AND period_month = ? AND period_year = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                rs.close();
                ps.close();
                return exists;
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.isPeriodClosed error: " + e.getMessage());
        }

        return false;
    }

    // 2️⃣ Tính tổng dữ liệu để chốt kỳ
    public AccountingPeriod calculatePeriod(int branchId, int month, int year) {

        AccountingPeriod period = new AccountingPeriod();

        String sql = "SELECT " +
                     " COUNT(DISTINCT i.invoice_id) AS total_invoices, " +
                     " SUM(ii.subtotal) AS total_revenue, " +
                     " SUM(ii.subtotal) - SUM(ii.quantity * pv.cost_price) AS total_profit " +
                     "FROM Invoice i " +
                     "JOIN InvoiceItem ii ON i.invoice_id = ii.invoice_id " +
                     "JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
                     "WHERE i.status = 'COMPLETED' " +
                     "AND i.branch_id = ? " +
                     "AND MONTH(i.invoice_date) = ? " +
                     "AND YEAR(i.invoice_date) = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                period.setBranchId(branchId);
                period.setPeriodMonth(month);
                period.setPeriodYear(year);
                period.setTotalInvoices(rs.getInt("total_invoices"));

                BigDecimal revenue = rs.getBigDecimal("total_revenue");
                BigDecimal profit = rs.getBigDecimal("total_profit");

                period.setTotalRevenue(revenue == null ? BigDecimal.ZERO : revenue);
                period.setTotalProfit(profit == null ? BigDecimal.ZERO : profit);

                period.setStatus("CLOSED");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.calculatePeriod error: " + e.getMessage());
        }

        return period;
    }

    // 3️⃣ Insert kỳ kế toán
    public boolean closePeriod(AccountingPeriod period) {

        String sql = "INSERT INTO AccountingPeriod " +
                     "(branch_id, period_month, period_year, total_revenue, total_profit, total_invoices, closed_by, closed_at, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, period.getBranchId());
            ps.setInt(2, period.getPeriodMonth());
            ps.setInt(3, period.getPeriodYear());
            ps.setBigDecimal(4, period.getTotalRevenue());
            ps.setBigDecimal(5, period.getTotalProfit());
            ps.setInt(6, period.getTotalInvoices());

            if (period.getClosedBy() != null) {
                ps.setInt(7, period.getClosedBy());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setString(8, period.getStatus());

            int rows = ps.executeUpdate();
            ps.close();

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.closePeriod error: " + e.getMessage());
        }

        return false;
    }

    // 4️⃣ Lấy danh sách kỳ đã chốt
    public List<AccountingPeriod> getClosedPeriods(int branchId) {

        List<AccountingPeriod> list = new ArrayList<>();

        String sql = "SELECT * FROM AccountingPeriod " +
                     "WHERE branch_id = ? " +
                     "ORDER BY period_year DESC, period_month DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, branchId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                AccountingPeriod p = new AccountingPeriod();

                p.setPeriodId(rs.getInt("period_id"));
                p.setBranchId(rs.getInt("branch_id"));
                p.setPeriodMonth(rs.getInt("period_month"));
                p.setPeriodYear(rs.getInt("period_year"));
                p.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                p.setTotalProfit(rs.getBigDecimal("total_profit"));
                p.setTotalInvoices(rs.getInt("total_invoices"));

                Timestamp ts = rs.getTimestamp("closed_at");
                if (ts != null) {
                    p.setClosedAt(ts.toLocalDateTime());
                }

                p.setClosedBy(rs.getObject("closed_by") != null 
                              ? rs.getInt("closed_by") 
                              : null);

                p.setStatus(rs.getString("status"));

                list.add(p);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.getClosedPeriods error: " + e.getMessage());
        }

        return list;
    }
}