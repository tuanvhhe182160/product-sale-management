package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.LoyaltyAccount;
import com.techshop.model.LoyaltyTransaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý tích điểm thưởng (Loyalty Points).
 *
 * Quy tắc:
 *   - EARN:   Mỗi 10,000đ final_amount = 1 điểm
 *   - REDEEM: 1 điểm = 1,000đ giảm giá
 */
public class LoyaltyDAO extends DBContext {

    private static final BigDecimal EARN_RATE   = new BigDecimal("10000"); // 10,000đ = 1 điểm
    private static final BigDecimal REDEEM_RATE = new BigDecimal("1000");  // 1 điểm = 1,000đ

    /**
     * Tính số điểm được cộng từ giá trị hóa đơn.
     */
    public static int calculateEarnPoints(BigDecimal finalAmount) {
        if (finalAmount == null || finalAmount.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return finalAmount.divideToIntegralValue(EARN_RATE).intValue();
    }

    /**
     * Tính số tiền giảm giá khi đổi điểm.
     */
    public static BigDecimal calculateRedeemDiscount(int points) {
        return REDEEM_RATE.multiply(new BigDecimal(points));
    }

    /**
     * Lấy LoyaltyAccount theo customerId. Tự tạo nếu chưa có.
     */
    public LoyaltyAccount getOrCreateAccount(int customerId) {
        LoyaltyAccount acc = getAccountByCustomerId(customerId);
        if (acc == null) {
            String sql = "INSERT INTO LoyaltyAccount (customer_id, total_points, current_points, created_at) " +
                         "VALUES (?, 0, 0, GETDATE())";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
            acc = getAccountByCustomerId(customerId);
        }
        return acc;
    }

    /**
     * Lấy LoyaltyAccount theo customerId.
     */
    public LoyaltyAccount getAccountByCustomerId(int customerId) {
        String sql = "SELECT la.*, c.full_name AS customerName, c.phone AS customerPhone " +
                     "FROM LoyaltyAccount la JOIN Customer c ON la.customer_id = c.customer_id " +
                     "WHERE la.customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccount(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Cộng điểm cho khách sau khi mua hàng (EARN).
     * Gọi trong cùng transaction với createInvoice.
     */
    public boolean earnPoints(Connection conn, int customerId, int invoiceId, BigDecimal finalAmount) {
        int points = calculateEarnPoints(finalAmount);
        if (points <= 0) return true;

        try {
            // Đảm bảo có account
            int accountId = getOrCreateAccountId(conn, customerId);
            if (accountId <= 0) return false;

            // Cập nhật điểm
            String sqlUpdate = "UPDATE LoyaltyAccount SET total_points = total_points + ?, " +
                               "current_points = current_points + ?, updated_at = GETDATE() " +
                               "WHERE account_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setInt(1, points);
                ps.setInt(2, points);
                ps.setInt(3, accountId);
                ps.executeUpdate();
            }

            // Ghi log transaction
            insertTransaction(conn, accountId, "EARN", points, invoiceId,
                    "Tích " + points + " điểm từ hóa đơn #" + invoiceId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Trừ điểm khi khách đổi điểm lấy giảm giá (REDEEM).
     * Gọi trong cùng transaction với createInvoice.
     */
    public boolean redeemPoints(Connection conn, int customerId, int invoiceId, int points) {
        if (points <= 0) return true;

        try {
            int accountId = getOrCreateAccountId(conn, customerId);
            if (accountId <= 0) return false;

            // Kiểm tra đủ điểm
            int currentPoints = getCurrentPoints(conn, accountId);
            if (currentPoints < points) return false;

            String sqlUpdate = "UPDATE LoyaltyAccount SET current_points = current_points - ?, " +
                               "updated_at = GETDATE() WHERE account_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setInt(1, points);
                ps.setInt(2, accountId);
                ps.executeUpdate();
            }

            insertTransaction(conn, accountId, "REDEEM", points, invoiceId,
                    "Đổi " + points + " điểm giảm giá hóa đơn #" + invoiceId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy điểm hiện tại của khách hàng.
     */
    public int getCustomerPoints(int customerId) {
        String sql = "SELECT current_points FROM LoyaltyAccount WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("current_points");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Lấy lịch sử giao dịch điểm của khách.
     */
    public List<LoyaltyTransaction> getTransactionHistory(int customerId) {
        List<LoyaltyTransaction> list = new ArrayList<>();
        String sql = "SELECT lt.* FROM LoyaltyTransaction lt " +
                     "JOIN LoyaltyAccount la ON lt.account_id = la.account_id " +
                     "WHERE la.customer_id = ? ORDER BY lt.transaction_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoyaltyTransaction t = new LoyaltyTransaction();
                    t.setTransactionId(rs.getInt("transaction_id"));
                    t.setAccountId(rs.getInt("account_id"));
                    t.setTransactionType(rs.getString("transaction_type"));
                    t.setPoints(rs.getInt("points"));
                    t.setReferenceId(rs.getObject("reference_id") != null ? rs.getInt("reference_id") : null);
                    t.setDescription(rs.getString("description"));
                    t.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                    list.add(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Private helpers ──

    private int getOrCreateAccountId(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT account_id FROM LoyaltyAccount WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("account_id");
            }
        }
        // Tạo mới
        String sqlInsert = "INSERT INTO LoyaltyAccount (customer_id, total_points, current_points, created_at) " +
                           "OUTPUT INSERTED.account_id VALUES (?, 0, 0, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private int getCurrentPoints(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT current_points FROM LoyaltyAccount WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("current_points");
            }
        }
        return 0;
    }

    private void insertTransaction(Connection conn, int accountId, String type, int points,
                                   int invoiceId, String description) throws SQLException {
        String sql = "INSERT INTO LoyaltyTransaction (account_id, transaction_type, points, " +
                     "reference_id, description, transaction_date) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setInt(3, points);
            ps.setInt(4, invoiceId);
            ps.setString(5, description);
            ps.executeUpdate();
        }
    }

    private LoyaltyAccount mapAccount(ResultSet rs) throws SQLException {
        LoyaltyAccount a = new LoyaltyAccount();
        a.setAccountId(rs.getInt("account_id"));
        a.setCustomerId(rs.getInt("customer_id"));
        a.setTotalPoints(rs.getInt("total_points"));
        a.setCurrentPoints(rs.getInt("current_points"));
        a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        Timestamp upd = rs.getTimestamp("updated_at");
        if (upd != null) a.setUpdatedAt(upd.toLocalDateTime());
        a.setCustomerName(rs.getString("customerName"));
        a.setCustomerPhone(rs.getString("customerPhone"));
        return a;
    }

    public static BigDecimal getEarnRate() { return EARN_RATE; }
    public static BigDecimal getRedeemRate() { return REDEEM_RATE; }
}
