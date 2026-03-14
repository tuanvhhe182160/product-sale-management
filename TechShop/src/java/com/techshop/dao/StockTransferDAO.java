package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.StockTransfer;

import java.sql.*;
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
}
