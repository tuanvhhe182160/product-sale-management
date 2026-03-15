package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Invoice;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho trang lịch sử bán hàng của Cashier.
 */
public class SalesHistoryDAO extends DBContext {

    /**
     * Lấy danh sách hóa đơn theo cashier, có lọc và phân trang.
     */
    public List<Invoice> getInvoices(int cashierId, String search,
                                     String dateFrom, String dateTo,
                                     String status, int page, int pageSize) {
        List<Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT i.invoice_id, i.invoice_code, i.invoice_date, ");
        sql.append("  i.total_amount, i.discount_amount, i.final_amount, ");
        sql.append("  i.payment_method, i.status, i.note, ");
        sql.append("  c.full_name AS customer_name, c.phone AS customer_phone, ");
        sql.append("  (SELECT COUNT(*) FROM InvoiceItem ii WHERE ii.invoice_id = i.invoice_id) AS item_count ");
        sql.append("FROM Invoice i ");
        sql.append("JOIN Customer c ON i.customer_id = c.customer_id ");
        sql.append("WHERE i.cashier_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(cashierId);

        appendFilters(sql, params, search, dateFrom, dateTo, status);

        sql.append("ORDER BY i.invoice_date DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapInvoice(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số hóa đơn (cho phân trang).
     */
    public int countInvoices(int cashierId, String search,
                             String dateFrom, String dateTo, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM Invoice i ");
        sql.append("JOIN Customer c ON i.customer_id = c.customer_id ");
        sql.append("WHERE i.cashier_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(cashierId);
        appendFilters(sql, params, search, dateFrom, dateTo, status);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Thống kê: số hóa đơn hôm nay, doanh thu hôm nay, doanh thu tháng này.
     * Trả về mảng BigDecimal[3]: [todayCount, todayRevenue, monthRevenue]
     */
    public BigDecimal[] getStats(int cashierId) {
        BigDecimal[] stats = { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };

        String sql =
            "SELECT " +
            "  SUM(CASE WHEN CAST(invoice_date AS DATE) = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS today_count, " +
            "  SUM(CASE WHEN CAST(invoice_date AS DATE) = CAST(GETDATE() AS DATE) THEN final_amount ELSE 0 END) AS today_revenue, " +
            "  SUM(CASE WHEN YEAR(invoice_date) = YEAR(GETDATE()) AND MONTH(invoice_date) = MONTH(GETDATE()) THEN final_amount ELSE 0 END) AS month_revenue " +
            "FROM Invoice WHERE cashier_id = ? AND status = 'COMPLETED'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cashierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats[0] = rs.getBigDecimal("today_count")   != null ? rs.getBigDecimal("today_count")   : BigDecimal.ZERO;
                    stats[1] = rs.getBigDecimal("today_revenue") != null ? rs.getBigDecimal("today_revenue") : BigDecimal.ZERO;
                    stats[2] = rs.getBigDecimal("month_revenue") != null ? rs.getBigDecimal("month_revenue") : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Đếm số PhysicalProduct IN_STOCK tại chi nhánh.
     */
    public int countInStockByBranch(int branchId) {
        String sql = "SELECT COUNT(*) FROM PhysicalProduct WHERE branch_id = ? AND status = 'IN_STOCK'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm tổng số hóa đơn COMPLETED của cashier.
     */
    public int countTotalCompleted(int cashierId) {
        String sql = "SELECT COUNT(*) FROM Invoice WHERE cashier_id = ? AND status = 'COMPLETED'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cashierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy TẤT CẢ hóa đơn theo filter (không phân trang) — dùng cho xuất Excel.
     */
    public List<Invoice> getAllInvoices(int cashierId, String search,
                                       String dateFrom, String dateTo, String status) {
        List<Invoice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT i.invoice_id, i.invoice_code, i.invoice_date, ");
        sql.append("  i.total_amount, i.discount_amount, i.final_amount, ");
        sql.append("  i.payment_method, i.status, i.note, ");
        sql.append("  c.full_name AS customer_name, c.phone AS customer_phone, ");
        sql.append("  (SELECT COUNT(*) FROM InvoiceItem ii WHERE ii.invoice_id = i.invoice_id) AS item_count ");
        sql.append("FROM Invoice i ");
        sql.append("JOIN Customer c ON i.customer_id = c.customer_id ");
        sql.append("WHERE i.cashier_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(cashierId);
        appendFilters(sql, params, search, dateFrom, dateTo, status);
        sql.append("ORDER BY i.invoice_date DESC ");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapInvoice(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String search, String dateFrom, String dateTo, String status) {
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (i.invoice_code LIKE ? OR c.full_name LIKE ? OR c.phone LIKE ?) ");
            String like = "%" + search.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND CAST(i.invoice_date AS DATE) >= ? ");
            params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND CAST(i.invoice_date AS DATE) <= ? ");
            params.add(dateTo.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
            params.add(status.trim());
        }
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object val = params.get(i);
            if (val instanceof Integer) {
                ps.setInt(i + 1, (Integer) val);
            } else {
                ps.setString(i + 1, val.toString());
            }
        }
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setInvoiceCode(rs.getString("invoice_code"));
        if (rs.getTimestamp("invoice_date") != null)
            inv.setInvoiceDate(rs.getTimestamp("invoice_date").toLocalDateTime());
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        inv.setFinalAmount(rs.getBigDecimal("final_amount"));
        inv.setPaymentMethod(rs.getString("payment_method"));
        inv.setStatus(rs.getString("status"));
        inv.setCustomerName(rs.getString("customer_name"));
        inv.setCustomerPhone(rs.getString("customer_phone"));
        inv.setItemCount(rs.getInt("item_count"));
        return inv;
    }

    // ── Báo cáo doanh thu cho Admin ─────────────────────────────────────

    /**
     * Doanh thu theo chi nhánh, filter ngày/tháng.
     * Trả về list Object[]: [branchId, branchName, invoiceCount, totalRevenue]
     */
    public List<Object[]> getRevenueByBranch(String dateFrom, String dateTo) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder onClause = new StringBuilder();
        onClause.append("b.branch_id = i.branch_id AND i.status='COMPLETED' ");

        List<Object> params = new ArrayList<>();
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            onClause.append("AND CAST(i.invoice_date AS DATE) >= ? ");
            params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            onClause.append("AND CAST(i.invoice_date AS DATE) <= ? ");
            params.add(dateTo.trim());
        }

        String sql = "SELECT b.branch_id, b.branch_name, COUNT(i.invoice_id) AS cnt, "
                   + "ISNULL(SUM(i.final_amount),0) AS revenue "
                   + "FROM Branch b "
                   + "LEFT JOIN Invoice i ON " + onClause
                   + "WHERE b.status='ACTIVE' "
                   + "GROUP BY b.branch_id, b.branch_name ORDER BY revenue DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("branch_id"), rs.getString("branch_name"),
                        rs.getInt("cnt"), rs.getBigDecimal("revenue")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Doanh thu theo ngày trong khoảng, filter chi nhánh.
     * Trả về list Object[]: [date(String), invoiceCount, totalRevenue]
     */
    public List<Object[]> getDailyRevenue(int branchId, String dateFrom, String dateTo) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CAST(i.invoice_date AS DATE) AS d, COUNT(*) AS cnt, ");
        sql.append("  SUM(i.final_amount) AS revenue ");
        sql.append("FROM Invoice i WHERE i.status='COMPLETED' ");

        List<Object> params = new ArrayList<>();
        if (branchId > 0) {
            sql.append("AND i.branch_id = ? ");
            params.add(branchId);
        }
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND CAST(i.invoice_date AS DATE) >= ? ");
            params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND CAST(i.invoice_date AS DATE) <= ? ");
            params.add(dateTo.trim());
        }
        sql.append("GROUP BY CAST(i.invoice_date AS DATE) ORDER BY d DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("d"), rs.getInt("cnt"), rs.getBigDecimal("revenue")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Tổng doanh thu + số HĐ toàn hệ thống (hoặc theo chi nhánh), filter ngày.
     * Trả về BigDecimal[3]: [totalInvoices, totalRevenue, avgPerInvoice]
     */
    public BigDecimal[] getOverallStats(int branchId, String dateFrom, String dateTo) {
        BigDecimal[] s = { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) AS cnt, ISNULL(SUM(final_amount),0) AS rev ");
        sql.append("FROM Invoice WHERE status='COMPLETED' ");

        List<Object> params = new ArrayList<>();
        if (branchId > 0) { sql.append("AND branch_id=? "); params.add(branchId); }
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND CAST(invoice_date AS DATE)>=? "); params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND CAST(invoice_date AS DATE)<=? "); params.add(dateTo.trim());
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s[0] = BigDecimal.valueOf(rs.getInt("cnt"));
                    s[1] = rs.getBigDecimal("rev") != null ? rs.getBigDecimal("rev") : BigDecimal.ZERO;
                    if (s[0].intValue() > 0) s[2] = s[1].divide(s[0], 0, java.math.RoundingMode.HALF_UP);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return s;
    }
}
