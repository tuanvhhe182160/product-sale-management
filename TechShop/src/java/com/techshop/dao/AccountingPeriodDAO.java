package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.AccountingPeriod;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountingPeriodDAO extends DBContext {

    /**
     * Đếm số hóa đơn TRANSFER/MIXED chưa đối soát (PENDING) trong kỳ cần chốt.
     * Nếu > 0 thì không được phép chốt kỳ.
     */
    public int countPendingInPeriod(int branchId, int month, int year) {
        String sql =
            "SELECT COUNT(*) FROM Invoice " +
            "WHERE branch_id = ? " +
            "  AND status = 'PENDING' " +
            "  AND payment_method IN ('TRANSFER', 'MIXED') " +
            "  AND MONTH(invoice_date) = ? " +
            "  AND YEAR(invoice_date)  = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.countPendingInPeriod: " + e.getMessage());
        }
        return 0;
    }

    /** Kiểm tra kỳ đã chốt chưa. */
    public boolean isPeriodClosed(int branchId, int month, int year) {        
        String sql = "SELECT COUNT(*) FROM AccountingPeriod " +
                     "WHERE branch_id = ? AND period_month = ? AND period_year = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.isPeriodClosed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Tính dữ liệu preview cho một kỳ (chưa lưu vào DB).
     * Chỉ đếm invoice COMPLETED thuộc chi nhánh + tháng + năm chỉ định.
     * COGS = SUM(quantity × cost_price) per InvoiceItem.
     */
    public AccountingPeriod calculatePeriod(int branchId, int month, int year) {
        AccountingPeriod period = new AccountingPeriod();
        period.setBranchId(branchId);
        period.setPeriodMonth(month);
        period.setPeriodYear(year);
        period.setStatus("CLOSED");

        String sql =
            "SELECT " +
            "  COUNT(DISTINCT i.invoice_id)        AS total_invoices, " +
            "  ISNULL(SUM(i.final_amount), 0)       AS total_revenue, " +
            "  ISNULL(SUM(cost.TotalCost), 0)       AS total_cost " +
            "FROM Invoice i " +
            "LEFT JOIN ( " +
            "    SELECT ii.invoice_id, " +
            "           SUM(ii.quantity * pv.cost_price) AS TotalCost " +
            "    FROM InvoiceItem ii " +
            "    JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
            "    GROUP BY ii.invoice_id " +
            ") cost ON i.invoice_id = cost.invoice_id " +
            "WHERE i.status = 'COMPLETED' " +
            "  AND i.branch_id = ? " +
            "  AND MONTH(i.invoice_date) = ? " +
            "  AND YEAR(i.invoice_date)  = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal revenue = rs.getBigDecimal("total_revenue");
                    BigDecimal cost    = rs.getBigDecimal("total_cost");
                    if (revenue == null) revenue = BigDecimal.ZERO;
                    if (cost    == null) cost    = BigDecimal.ZERO;
                    period.setTotalInvoices(rs.getInt("total_invoices"));
                    period.setTotalRevenue(revenue);
                    period.setTotalProfit(revenue.subtract(cost));
                } else {
                    period.setTotalInvoices(0);
                    period.setTotalRevenue(BigDecimal.ZERO);
                    period.setTotalProfit(BigDecimal.ZERO);
                }
            }
        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.calculatePeriod: " + e.getMessage());
        }
        return period;
    }

    /** Lưu kỳ kế toán đã chốt. */
    public boolean closePeriod(AccountingPeriod period) {
        String sql =
            "INSERT INTO AccountingPeriod " +
            "  (branch_id, period_month, period_year, " +
            "   total_revenue, total_profit, total_invoices, " +
            "   closed_by, closed_at, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, period.getBranchId());
            ps.setInt(2, period.getPeriodMonth());
            ps.setInt(3, period.getPeriodYear());
            ps.setBigDecimal(4, period.getTotalRevenue());
            ps.setBigDecimal(5, period.getTotalProfit());
            ps.setInt(6, period.getTotalInvoices());
            if (period.getClosedBy() != null)
                ps.setInt(7, period.getClosedBy());
            else
                ps.setNull(7, Types.INTEGER);
            ps.setString(8, "CLOSED");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.closePeriod: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy danh sách kỳ đã chốt của chi nhánh, JOIN User để lấy tên người chốt.
     */
    public List<AccountingPeriod> getClosedPeriods(int branchId) {
        List<AccountingPeriod> list = new ArrayList<>();
        String sql =
            "SELECT ap.*, u.full_name AS closed_by_name " +
            "FROM AccountingPeriod ap " +
            "LEFT JOIN [User] u ON ap.closed_by = u.user_id " +
            "WHERE ap.branch_id = ? " +
            "ORDER BY ap.period_year DESC, ap.period_month DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AccountingPeriod p = new AccountingPeriod();
                    p.setPeriodId(rs.getInt("period_id"));
                    p.setBranchId(rs.getInt("branch_id"));
                    p.setPeriodMonth(rs.getInt("period_month"));
                    p.setPeriodYear(rs.getInt("period_year"));
                    p.setTotalRevenue(rs.getBigDecimal("total_revenue"));
                    p.setTotalProfit(rs.getBigDecimal("total_profit"));
                    p.setTotalInvoices(rs.getInt("total_invoices"));
                    p.setStatus(rs.getString("status"));
                    p.setClosedByName(rs.getString("closed_by_name"));
                    Timestamp ts = rs.getTimestamp("closed_at");
                    if (ts != null) p.setClosedAt(ts.toLocalDateTime());
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("AccountingPeriodDAO.getClosedPeriods: " + e.getMessage());
        }
        return list;
    }
}