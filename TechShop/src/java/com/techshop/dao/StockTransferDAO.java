package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.StockTransfer;
import com.techshop.model.StockTransferItem;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StockTransferDAO extends DBContext {

    private static final String BASE_SELECT =
            "SELECT st.transfer_id, st.transfer_code, st.from_branch_id, st.to_branch_id, "
                    + "st.variant_id, st.requested_quantity, "
                    + "st.requested_by, st.approved_by, st.received_by, "
                    + "st.status, st.note, st.request_date, st.approval_date, st.completion_date, "
                    + "st.created_at, st.updated_at, "
                    + "fb.branch_name AS from_branch_name, "
                    + "tb.branch_name AS to_branch_name, "
                    + "ru.full_name AS requested_by_name, "
                    + "au.full_name AS approved_by_name, "
                    + "v.variant_name, v.sku, "
                    + "(SELECT COUNT(*) FROM StockTransferItem sti WHERE sti.transfer_id = st.transfer_id) AS item_count "
                    + "FROM StockTransfer st "
                    + "JOIN Branch fb ON st.from_branch_id = fb.branch_id "
                    + "JOIN Branch tb ON st.to_branch_id = tb.branch_id "
                    + "JOIN [User] ru ON st.requested_by = ru.user_id "
                    + "LEFT JOIN [User] au ON st.approved_by = au.user_id "
                    + "JOIN ProductVariant v ON st.variant_id = v.variant_id ";

    /**
     * Create a new transfer request. Returns the generated transfer_id, or 0 on failure.
     */
    public int create(StockTransfer transfer) {
        String transferCode = generateTransferCode();
        String sql = "INSERT INTO StockTransfer "
                + "(transfer_code, from_branch_id, to_branch_id, variant_id, requested_quantity, "
                + "requested_by, status, note, request_date, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?, GETDATE(), GETDATE(), GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transferCode);
            ps.setInt(2, transfer.getFromBranchId());
            ps.setInt(3, transfer.getToBranchId());
            ps.setInt(4, transfer.getVariantId());
            ps.setInt(5, transfer.getRequestedQuantity());
            ps.setInt(6, transfer.getRequestedBy());
            ps.setString(7, transfer.getNote());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.create() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get a single transfer by ID with all join fields.
     */
    public StockTransfer getById(int transferId) {
        String sql = BASE_SELECT + "WHERE st.transfer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTransfer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.getById() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get transfers where this branch is the SOURCE (others are requesting from us).
     * We need to approve/reject these.
     */
    public List<StockTransfer> getOutgoing(int fromBranchId) {
        List<StockTransfer> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE st.from_branch_id = ? ORDER BY st.request_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, fromBranchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransfer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.getOutgoing() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get transfers where this branch is the DESTINATION (we requested stock).
     * We need to receive these when approved.
     */
    public List<StockTransfer> getIncoming(int toBranchId) {
        List<StockTransfer> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE st.to_branch_id = ? ORDER BY st.request_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, toBranchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransfer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.getIncoming() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Update transfer status, approvedBy, and/or receivedBy.
     * Timestamps (approval_date, completion_date) are set automatically based on status.
     */
    public boolean updateStatus(int transferId, String status, Integer approvedBy, Integer receivedBy) {
        String sql = "UPDATE StockTransfer SET status = ?, "
                + "approved_by = CASE WHEN ? IS NOT NULL THEN ? ELSE approved_by END, "
                + "received_by = CASE WHEN ? IS NOT NULL THEN ? ELSE received_by END, "
                + "approval_date = CASE WHEN ? IN ('APPROVED', 'REJECTED') THEN GETDATE() ELSE approval_date END, "
                + "completion_date = CASE WHEN ? = 'COMPLETED' THEN GETDATE() ELSE completion_date END, "
                + "updated_at = GETDATE() "
                + "WHERE transfer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);

            // approved_by CASE
            if (approvedBy != null) {
                ps.setInt(2, approvedBy);
                ps.setInt(3, approvedBy);
            } else {
                ps.setNull(2, Types.INTEGER);
                ps.setNull(3, Types.INTEGER);
            }

            // received_by CASE
            if (receivedBy != null) {
                ps.setInt(4, receivedBy);
                ps.setInt(5, receivedBy);
            } else {
                ps.setNull(4, Types.INTEGER);
                ps.setNull(5, Types.INTEGER);
            }

            ps.setString(6, status); // approval_date CASE
            ps.setString(7, status); // completion_date CASE
            ps.setInt(8, transferId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.updateStatus() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get all items belonging to a transfer, with IMEI and variant details.
     */
    public List<StockTransferItem> getItemsByTransferId(int transferId) {
        List<StockTransferItem> list = new ArrayList<>();
        String sql = "SELECT sti.item_id, sti.transfer_id, sti.physical_id, sti.created_at, "
                + "p.imei, p.serial_number, v.sku, v.variant_name "
                + "FROM StockTransferItem sti "
                + "JOIN PhysicalProduct p ON sti.physical_id = p.physical_id "
                + "JOIN ProductVariant v ON p.variant_id = v.variant_id "
                + "WHERE sti.transfer_id = ? "
                + "ORDER BY sti.item_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.getItemsByTransferId() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Add a single physical product to a transfer.
     */
    public boolean addItem(int transferId, int physicalId) {
        String sql = "INSERT INTO StockTransferItem (transfer_id, physical_id, created_at) VALUES (?, ?, GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, transferId);
            ps.setInt(2, physicalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.addItem() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private String generateTransferCode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "TRF-" + dateStr + "-";
        String sql = "SELECT COUNT(*) FROM StockTransfer WHERE transfer_code LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return prefix + String.format("%04d", rs.getInt(1) + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("StockTransferDAO.generateTransferCode() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return prefix + "0001";
    }

    private StockTransfer mapTransfer(ResultSet rs) throws SQLException {
        StockTransfer t = new StockTransfer();
        t.setTransferId(rs.getInt("transfer_id"));
        t.setTransferCode(rs.getString("transfer_code"));
        t.setFromBranchId(rs.getInt("from_branch_id"));
        t.setToBranchId(rs.getInt("to_branch_id"));
        t.setVariantId(rs.getInt("variant_id"));
        t.setRequestedQuantity(rs.getInt("requested_quantity"));
        t.setRequestedBy(rs.getInt("requested_by"));

        int approvedBy = rs.getInt("approved_by");
        t.setApprovedBy(rs.wasNull() ? null : approvedBy);

        int receivedBy = rs.getInt("received_by");
        t.setReceivedBy(rs.wasNull() ? null : receivedBy);

        t.setStatus(rs.getString("status"));
        t.setNote(rs.getString("note"));

        Timestamp requestTs = rs.getTimestamp("request_date");
        if (requestTs != null) t.setRequestDate(requestTs.toLocalDateTime());

        Timestamp approvalTs = rs.getTimestamp("approval_date");
        if (approvalTs != null) t.setApprovalDate(approvalTs.toLocalDateTime());

        Timestamp completionTs = rs.getTimestamp("completion_date");
        if (completionTs != null) t.setCompletionDate(completionTs.toLocalDateTime());

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) t.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) t.setUpdatedAt(updatedTs.toLocalDateTime());

        t.setFromBranchName(rs.getString("from_branch_name"));
        t.setToBranchName(rs.getString("to_branch_name"));
        t.setRequestedByName(rs.getString("requested_by_name"));
        t.setApprovedByName(rs.getString("approved_by_name"));
        t.setVariantName(rs.getString("variant_name"));
        t.setSku(rs.getString("sku"));
        t.setItemCount(rs.getInt("item_count"));

        return t;
    }

    private StockTransferItem mapItem(ResultSet rs) throws SQLException {
        StockTransferItem item = new StockTransferItem();
        item.setItemId(rs.getInt("item_id"));
        item.setTransferId(rs.getInt("transfer_id"));
        item.setPhysicalId(rs.getInt("physical_id"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) item.setCreatedAt(createdTs.toLocalDateTime());

        item.setImei(rs.getString("imei"));
        item.setSerialNumber(rs.getString("serial_number"));
        item.setSku(rs.getString("sku"));
        item.setVariantName(rs.getString("variant_name"));

        return item;
    }
}
