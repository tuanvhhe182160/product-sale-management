package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.InventoryTransaction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class InventoryTransactionDAO extends DBContext {

    public boolean insert(InventoryTransaction tx) {
        String sql = "INSERT INTO InventoryTransaction "
                + "(transaction_type, physical_id, from_branch_id, to_branch_id, "
                + "quantity, reference_id, performed_by, note, transaction_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tx.getTransactionType());
            ps.setInt(2, tx.getPhysicalId());

            if (tx.getFromBranchId() != null) {
                ps.setInt(3, tx.getFromBranchId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            if (tx.getToBranchId() != null) {
                ps.setInt(4, tx.getToBranchId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, tx.getQuantity());

            if (tx.getReferenceId() != null) {
                ps.setInt(6, tx.getReferenceId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, tx.getPerformedBy());
            ps.setString(8, tx.getNote());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("InventoryTransactionDAO.insert() Error: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
