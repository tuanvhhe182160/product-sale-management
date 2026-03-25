package com.techshop.dao;

import com.techshop.dal.DBContext;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * DAO chuyên biệt cho Admin Dashboard.
 * Tất cả query là system-wide (không filter branch_id).
 */
public class AdminDashboardDAO extends DBContext {

    // ── 1. KPI hôm nay: [revenue, invoiceCount] ─────────────────────────
    public BigDecimal[] getTodayStats() {
        BigDecimal[] r = { BigDecimal.ZERO, BigDecimal.ZERO };
        String sql =
            "SELECT COUNT(*) AS cnt, ISNULL(SUM(final_amount),0) AS rev " +
            "FROM Invoice " +
            "WHERE status='COMPLETED' " +
            "  AND CAST(invoice_date AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                r[0] = orZero(rs.getBigDecimal("rev"));
                r[1] = BigDecimal.valueOf(rs.getInt("cnt"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    // ── 2. KPI tháng này: [revenue, invoiceCount, profit] ───────────────
    public BigDecimal[] getThisMonthStats() {
        BigDecimal[] r = { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        String sql =
            "SELECT COUNT(i.invoice_id) AS cnt, " +
            "  ISNULL(SUM(i.final_amount),0) AS rev, " +
            "  ISNULL(SUM(i.final_amount),0) - ISNULL(SUM(c.cost),0) AS profit " +
            "FROM Invoice i " +
            "LEFT JOIN ( " +
            "  SELECT ii.invoice_id, SUM(ii.quantity * pv.cost_price) AS cost " +
            "  FROM InvoiceItem ii " +
            "  JOIN ProductVariant pv ON ii.variant_id = pv.variant_id " +
            "  GROUP BY ii.invoice_id " +
            ") c ON i.invoice_id = c.invoice_id " +
            "WHERE i.status='COMPLETED' " +
            "  AND MONTH(i.invoice_date)=MONTH(GETDATE()) " +
            "  AND YEAR(i.invoice_date)=YEAR(GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                r[0] = orZero(rs.getBigDecimal("rev"));
                r[1] = BigDecimal.valueOf(rs.getInt("cnt"));
                r[2] = orZero(rs.getBigDecimal("profit"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    // ── 3. Tổng sản phẩm IN_STOCK toàn hệ thống ────────────────────────
    public int getTotalInStock() {
        String sql = "SELECT COUNT(*) FROM PhysicalProduct WHERE status='IN_STOCK'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ── 4. Số hóa đơn PENDING toàn hệ thống (chờ đối soát) ─────────────
    public int getTotalPendingInvoices() {
        String sql =
            "SELECT COUNT(*) FROM Invoice " +
            "WHERE status='PENDING' AND payment_method IN ('TRANSFER','MIXED')";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ── 5. Số warranty request đang chờ xử lý ───────────────────────────
    public int getTotalPendingWarranty() {
        String sql =
            "SELECT COUNT(*) FROM WarrantyRequest " +
            "WHERE status IN ('PENDING','IN_PROGRESS')";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ── 6. Doanh thu tháng này theo từng chi nhánh (cho bar chart) ───────
    // Returns list of [branchName, revenue, orderCount]
    public List<Object[]> getRevenuByBranchThisMonth() {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT b.branch_name, " +
            "  ISNULL(SUM(i.final_amount),0) AS revenue, " +
            "  COUNT(i.invoice_id) AS cnt " +
            "FROM Branch b " +
            "LEFT JOIN Invoice i ON b.branch_id=i.branch_id " +
            "  AND i.status='COMPLETED' " +
            "  AND MONTH(i.invoice_date)=MONTH(GETDATE()) " +
            "  AND YEAR(i.invoice_date)=YEAR(GETDATE()) " +
            "WHERE b.status='ACTIVE' " +
            "GROUP BY b.branch_id, b.branch_name " +
            "ORDER BY revenue DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("branch_name"),
                    rs.getBigDecimal("revenue"),
                    rs.getInt("cnt")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── 7. Doanh thu 30 ngày gần nhất (toàn hệ thống, cho line chart) ───
    // Returns list of [dateStr, revenue]
    public List<Object[]> getLast30DaysRevenue() {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT CAST(invoice_date AS DATE) AS d, " +
            "  ISNULL(SUM(final_amount),0) AS rev " +
            "FROM Invoice " +
            "WHERE status='COMPLETED' " +
            "  AND CAST(invoice_date AS DATE) >= CAST(DATEADD(DAY,-29,GETDATE()) AS DATE) " +
            "GROUP BY CAST(invoice_date AS DATE) " +
            "ORDER BY d ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{ rs.getString("d"), rs.getBigDecimal("rev") });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── 8. Top 5 sản phẩm bán chạy tháng này (cho donut chart) ──────────
    // Returns list of [variantName, totalQty, totalSales]
    public List<Object[]> getTop5ProductsThisMonth() {
        List<Object[]> list = new ArrayList<>();
        // FIX: revenue subquery to avoid SUM(final_amount) * item_count inflation
        String sql =
            "SELECT TOP 5 pv.variant_name, " +
            "  SUM(ii.quantity) AS qty, " +
            "  SUM(rev.final_amount) AS sales " +
            "FROM InvoiceItem ii " +
            "JOIN ProductVariant pv ON ii.variant_id=pv.variant_id " +
            "JOIN ( " +
            "    SELECT invoice_id, final_amount FROM Invoice " +
            "    WHERE status='COMPLETED' " +
            "      AND MONTH(invoice_date)=MONTH(GETDATE()) " +
            "      AND YEAR(invoice_date)=YEAR(GETDATE()) " +
            ") rev ON ii.invoice_id=rev.invoice_id " +
            "GROUP BY pv.variant_id, pv.variant_name " +
            "ORDER BY qty DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("variant_name"),
                    rs.getInt("qty"),
                    rs.getBigDecimal("sales")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── 9. Số user theo role (để hiển thị breakdown) ─────────────────────
    // Returns list of [roleName, userCount]
    public List<Object[]> getUserCountByRole() {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT r.role_name, COUNT(u.user_id) AS cnt " +
            "FROM Role r " +
            "LEFT JOIN [User] u ON r.role_id=u.role_id AND u.status='ACTIVE' " +
            "GROUP BY r.role_id, r.role_name " +
            "ORDER BY cnt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{ rs.getString("role_name"), rs.getInt("cnt") });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private static BigDecimal orZero(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}