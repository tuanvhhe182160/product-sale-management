package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.WarrantyHistory;
import com.techshop.model.WarrantyRequest;
import com.techshop.model.WarrantyStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicianDAO extends DBContext {

    // Hàm "All-in-one" xử lý mọi cập nhật từ Technician
    public boolean processWarrantyRequest(int requestId, int techId, WarrantyStatus newStatus, String note, String resolution) {
        String updateReq = "UPDATE WarrantyRequest SET status = ?, resolution = ISNULL(?, resolution), " +
                           "technician_id = ISNULL(technician_id, ?), updated_at = GETDATE()";
        
        // So sánh trực tiếp bằng Enum thay vì chuỗi
        if (newStatus == WarrantyStatus.COMPLETED || newStatus == WarrantyStatus.REJECTED) {
            updateReq += ", completion_date = GETDATE() ";
        }
        updateReq += " WHERE request_id = ?";

        String insertHist = "INSERT INTO WarrantyHistory (request_id, status, note, updated_by, updated_at) " +
                            "VALUES (?, ?, ?, ?, GETDATE())";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psReq = connection.prepareStatement(updateReq)) {
                psReq.setString(1, newStatus.name()); // Lấy tên chuỗi của Enum (VD: "COMPLETED")
                psReq.setString(2, resolution);
                psReq.setInt(3, techId);
                psReq.setInt(4, requestId);
                psReq.executeUpdate();
            }

            try (PreparedStatement psHist = connection.prepareStatement(insertHist)) {
                psHist.setInt(1, requestId);
                psHist.setString(2, newStatus.name()); // Lấy tên chuỗi của Enum
                psHist.setString(3, note != null ? note : resolution);
                psHist.setInt(4, techId);
                psHist.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            // ... (phần catch/finally giữ nguyên như cũ)
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    // 1. Lấy danh sách yêu cầu bảo hành (Có bộ lọc)
    public List<WarrantyRequest> getWarrantyRequests(String status, String fromDate, String toDate) {
        List<WarrantyRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT wr.request_id, wr.request_code, wr.request_date, wr.issue_description, wr.status, c.full_name AS customer_name " +
            "FROM WarrantyRequest wr JOIN Customer c ON wr.customer_id = c.customer_id WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND wr.status = ? ");
            params.add(status.trim());
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append(" AND CAST(wr.request_date AS DATE) >= ? ");
            params.add(fromDate.trim());
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append(" AND CAST(wr.request_date AS DATE) <= ? ");
            params.add(toDate.trim());
        }
        sql.append(" ORDER BY wr.request_date DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WarrantyRequest req = new WarrantyRequest();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
                    req.setIssueDescription(rs.getString("issue_description"));
                    req.setStatus(rs.getString("status")); 
                    req.setCustomerName(rs.getString("customer_name"));
                    list.add(req);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lấy chi tiết 1 yêu cầu bảo hành (Gom thông tin từ 5 bảng)
    public WarrantyRequest getWarrantyDetail(int requestId) {
        String sql = "SELECT wr.*, c.full_name, c.email, c.phone, pp.imei, pv.variant_name, i.invoice_code, i.invoice_date " +
                     "FROM WarrantyRequest wr " +
                     "JOIN Customer c ON wr.customer_id = c.customer_id " +
                     "JOIN PhysicalProduct pp ON wr.physical_id = pp.physical_id " +
                     "JOIN ProductVariant pv ON pp.variant_id = pv.variant_id " +
                     "JOIN Invoice i ON wr.invoice_id = i.invoice_id " +
                     "WHERE wr.request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WarrantyRequest req = new WarrantyRequest();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setStatus(rs.getString("status"));
                    req.setIssueDescription(rs.getString("issue_description"));
                    req.setResolution(rs.getString("resolution"));
                    req.setCustomerName(rs.getString("full_name"));
                    req.setCustomerEmail(rs.getString("email"));
                    req.setCustomerPhone(rs.getString("phone"));
                    req.setImei(rs.getString("imei"));
                    req.setVariantName(rs.getString("variant_name"));
                    req.setInvoiceCode(rs.getString("invoice_code"));
                    req.setInvoiceDate(rs.getTimestamp("invoice_date").toLocalDateTime());
                    return req;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 3. Lấy lịch sử cập nhật của phiếu bảo hành
    // Lưu ý: Bạn cần tạo class WarrantyHistory (gồm updatedAt, status, updatedByName, note) trong model nhé.
    public List<WarrantyHistory> getWarrantyHistory(int requestId) {
        List<WarrantyHistory> list = new ArrayList<>();
        String sql = "SELECT wh.*, u.full_name AS updated_by_name " +
                     "FROM WarrantyHistory wh JOIN [User] u ON wh.updated_by = u.user_id " +
                     "WHERE wh.request_id = ? ORDER BY wh.updated_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WarrantyHistory h = new WarrantyHistory();
                    h.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    h.setStatus(rs.getString("status"));
                    h.setUpdatedByName(rs.getString("updated_by_name"));
                    h.setNote(rs.getString("note"));
                    list.add(h);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}