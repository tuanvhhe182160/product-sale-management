package com.techshop.dao;

import com.techshop.dal.DBContext;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * DAO chuyên biệt cho Dashboard của Accounting Staff.
 * Tất cả query đều scoped theo branchId để đảm bảo kế toán
 * không nhìn thấy dữ liệu chi nhánh khác.
 */
public class AccountingDashboardDAO extends DBContext {

    /**
     * KPI tháng hiện tại: doanh thu, lợi nhuận, số hóa đơn COMPLETED.
     * Returns: [revenue, profit, invoiceCount]
     */
    public BigDecimal[] getThisMonthKpi(int branchId) {
        BigDecimal[] r = { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        String sql =
            "SELECT " +
            "  COUNT(DISTINCT i.invoice_id)         AS inv_count, " +
            "  ISNULL(SUM(i.final_amount), 0)        AS revenue, " +
            "  ISNULL(SUM(i.final_amount), 0) - ISNULL(SUM(c.TotalCost), 0) AS profit " +
            "FROM Invoice i " +
            "LEFT JOIN ( " +
            "    SELECT ii.invoice_id, SUM(ii.quantity * pv.cost_price) AS TotalCost " +
            "    FROM InvoiceItem ii " +
            "    JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
            "    GROUP BY ii.invoice_id " +
            ") c ON i.invoice_id = c.invoice_id " +
            "WHERE i.status = 'COMPLETED' " +
            "  AND i.branch_id = ? " +
            "  AND MONTH(i.invoice_date) = MONTH(GETDATE()) " +
            "  AND YEAR(i.invoice_date)  = YEAR(GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r[0] = orZero(rs.getBigDecimal("revenue"));
                    r[1] = orZero(rs.getBigDecimal("profit"));
                    r[2] = BigDecimal.valueOf(rs.getInt("inv_count"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    /**
     * KPI hôm nay: doanh thu và số hóa đơn COMPLETED.
     * Returns: [revenue, invoiceCount]
     */
    public BigDecimal[] getTodayKpi(int branchId) {
        BigDecimal[] r = { BigDecimal.ZERO, BigDecimal.ZERO };
        String sql =
            "SELECT COUNT(*) AS cnt, ISNULL(SUM(final_amount), 0) AS rev " +
            "FROM Invoice " +
            "WHERE status = 'COMPLETED' " +
            "  AND branch_id = ? " +
            "  AND CAST(invoice_date AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r[0] = orZero(rs.getBigDecimal("rev"));
                    r[1] = BigDecimal.valueOf(rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    /**
     * Số hóa đơn TRANSFER/MIXED đang chờ đối soát (PENDING).
     */
    public int getPendingReconciliationCount(int branchId) {
        String sql =
            "SELECT COUNT(*) FROM Invoice " +
            "WHERE branch_id = ? AND status = 'PENDING' " +
            "  AND payment_method IN ('TRANSFER', 'MIXED')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Kỳ kế toán gần nhất đã chốt (để hiển thị "Kỳ cuối đã chốt").
     */
    public int[] getLastClosedPeriod(int branchId) {
        // Returns [month, year] or null if none
        String sql =
            "SELECT TOP 1 period_month, period_year " +
            "FROM AccountingPeriod WHERE branch_id = ? " +
            "ORDER BY period_year DESC, period_month DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new int[]{rs.getInt("period_month"), rs.getInt("period_year")};
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Doanh thu 7 ngày gần nhất (theo ngày) — dữ liệu vẽ mini chart.
     * Returns list of [dateStr, revenue] — 7 phần tử, ngày cũ → mới.
     */
    public List<Object[]> getLast7DaysRevenue(int branchId) {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT CAST(invoice_date AS DATE) AS d, " +
            "       ISNULL(SUM(final_amount), 0) AS rev " +
            "FROM Invoice " +
            "WHERE status = 'COMPLETED' AND branch_id = ? " +
            "  AND CAST(invoice_date AS DATE) >= CAST(DATEADD(DAY, -6, GETDATE()) AS DATE) " +
            "GROUP BY CAST(invoice_date AS DATE) " +
            "ORDER BY d ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{ rs.getString("d"), rs.getBigDecimal("rev") });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Phân bổ theo phương thức thanh toán tháng này (COMPLETED).
     * Returns: Map<paymentMethod, total_amount>
     */
    public Map<String, BigDecimal> getPaymentMethodBreakdown(int branchId) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        String sql =
            "SELECT payment_method, ISNULL(SUM(final_amount), 0) AS total " +
            "FROM Invoice " +
            "WHERE status = 'COMPLETED' AND branch_id = ? " +
            "  AND MONTH(invoice_date) = MONTH(GETDATE()) " +
            "  AND YEAR(invoice_date)  = YEAR(GETDATE()) " +
            "GROUP BY payment_method " +
            "ORDER BY total DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("payment_method"), orZero(rs.getBigDecimal("total")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    /**
     * 5 hóa đơn lớn nhất hôm nay (bao gồm cả PENDING để kế toán thấy
     * chuyển khoản chưa đối soát).
     */
    public List<Map<String, Object>> getTodayTopInvoices(int branchId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql =
            "SELECT TOP 5 i.invoice_code, i.final_amount, i.payment_method, " +
            "  i.status, c.full_name AS customer_name " +
            "FROM Invoice i " +
            "LEFT JOIN Customer c ON i.customer_id = c.customer_id " +
            "WHERE i.branch_id = ? " +
            "  AND CAST(i.invoice_date AS DATE) = CAST(GETDATE() AS DATE) " +
            "ORDER BY i.final_amount DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("invoiceCode",   rs.getString("invoice_code"));
                    row.put("finalAmount",   rs.getBigDecimal("final_amount"));
                    row.put("paymentMethod", rs.getString("payment_method"));
                    row.put("status",        rs.getString("status"));
                    row.put("customerName",  rs.getString("customer_name"));
                    list.add(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private static BigDecimal orZero(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}