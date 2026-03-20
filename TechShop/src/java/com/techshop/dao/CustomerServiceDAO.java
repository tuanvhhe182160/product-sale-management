package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.WarrantyCheckDTO;
import com.techshop.model.WarrantyRequest;
import com.techshop.model.WarrantyStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerServiceDAO extends DBContext {

    // 1 & 2. TÌM KIẾM ĐƠN HÀNG VÀ KIỂM TRA BẢO HÀNH BẰNG IMEI
    // Yêu cầu: "Search Order by IMEI" & "Check Warranty Status"
    public WarrantyCheckDTO checkWarrantyByImei(String imei) {
        // Query kết hợp 5 bảng: PhysicalProduct, InvoiceItem, Invoice, Customer, ProductVariant
        // Tính toán luôn ngày hết hạn bảo hành bằng DATEADD của SQL Server
        String sql = "SELECT " +
                     "  pp.physical_id, pp.imei, " +
                     "  pv.variant_name, " +
                     "  i.invoice_id, i.invoice_code, i.invoice_date, " +
                     "  c.customer_id, c.full_name AS customer_name, c.email, c.phone AS customer_phone, " +
                     "  ii.warranty_months, " +
                     "  DATEADD(month, ii.warranty_months, i.invoice_date) AS warranty_end_date, " +
                     "  CASE " +
                     "    WHEN GETDATE() <= DATEADD(month, ii.warranty_months, i.invoice_date) THEN 'VALID' " +
                     "    ELSE 'EXPIRED' " +
                     "  END AS warranty_status " +
                     "FROM PhysicalProduct pp " +
                     "JOIN InvoiceItem ii ON pp.physical_id = ii.physical_id " +
                     "JOIN Invoice i ON ii.invoice_id = i.invoice_id " +
                     "JOIN ProductVariant pv ON pp.variant_id = pv.variant_id " +
                     "JOIN Customer c ON i.customer_id = c.customer_id " +
                     "WHERE pp.imei = ? AND i.status IN ('COMPLETED', 'PENDING')";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, imei.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WarrantyCheckDTO dto = new WarrantyCheckDTO();
                    dto.setPhysicalId(rs.getInt("physical_id"));
                    dto.setImei(rs.getString("imei"));
                    dto.setVariantName(rs.getString("variant_name"));
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    dto.setInvoiceCode(rs.getString("invoice_code"));
                    dto.setInvoiceDate(rs.getTimestamp("invoice_date"));
                    dto.setCustomerId(rs.getInt("customer_id"));
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setCustomerEmail(rs.getString("email"));
                    dto.setCustomerPhone(rs.getString("customer_phone"));
                    dto.setWarrantyMonths(rs.getInt("warranty_months"));
                    dto.setWarrantyEndDate(rs.getTimestamp("warranty_end_date"));
                    dto.setWarrantyStatus(rs.getString("warranty_status"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy hoặc IMEI chưa bán
    }

    // 3. TẠO YÊU CẦU BẢO HÀNH (Transaction)
    // Yêu cầu: "Create Warranty Request"
    public boolean createWarrantyRequest(int invoiceId, int physicalId, int customerId, 
                                         int csUserId, String issueDescription) {
        String sqlRequest = "INSERT INTO WarrantyRequest (request_code, invoice_id, physical_id, customer_id, " +
                            "issue_description, status, customer_service_id, request_date, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, '" + WarrantyStatus.PENDING.name() + "', ?, GETDATE(), GETDATE())";
                        
        String sqlHistory = "INSERT INTO WarrantyHistory (request_id, status, note, updated_by, updated_at) " +
                            "VALUES (?, '" + WarrantyStatus.PENDING.name() + "', 'Khởi tạo yêu cầu bảo hành', ?, GETDATE())";

        try {
            connection.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Insert Request
            int newRequestId = 0;
            String requestCode = "WR-" + System.currentTimeMillis(); // Generate code
            
            try (PreparedStatement psReq = connection.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                psReq.setString(1, requestCode);
                psReq.setInt(2, invoiceId);
                psReq.setInt(3, physicalId);
                psReq.setInt(4, customerId);
                psReq.setString(5, issueDescription);
                psReq.setInt(6, csUserId);
                psReq.executeUpdate();

                try (ResultSet rs = psReq.getGeneratedKeys()) {
                    if (rs.next()) newRequestId = rs.getInt(1);
                }
            }

            // 2. Insert History
            if (newRequestId > 0) {
                try (PreparedStatement psHist = connection.prepareStatement(sqlHistory)) {
                    psHist.setInt(1, newRequestId);
                    psHist.setInt(2, csUserId);
                    psHist.executeUpdate();
                }
            } else {
                connection.rollback();
                return false;
            }

            connection.commit(); // Thành công cả 2
            return true;

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // 4. CẬP NHẬT TRẠNG THÁI BẢO HÀNH VÀ GHI LOG (Transaction)
    // Yêu cầu: "Update Request Status"
    public boolean updateWarrantyStatus(int requestId, String newStatus, String note, int userId) {
        String updateReq = "UPDATE WarrantyRequest SET status = ?, updated_at = GETDATE() WHERE request_id = ?";
        String insertHist = "INSERT INTO WarrantyHistory (request_id, status, note, updated_by, updated_at) " +
                            "VALUES (?, ?, ?, ?, GETDATE())";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psUpdate = connection.prepareStatement(updateReq)) {
                psUpdate.setString(1, newStatus);
                psUpdate.setInt(2, requestId);
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psHist = connection.prepareStatement(insertHist)) {
                psHist.setInt(1, requestId);
                psHist.setString(2, newStatus);
                psHist.setString(3, note);
                psHist.setInt(4, userId);
                psHist.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    public List<WarrantyRequest> getWarrantyRequestsForCS(int branchId, String keyword, String status, String fromDate, String toDate) {
        List<WarrantyRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT wr.request_id, wr.request_code, wr.request_date, wr.issue_description, wr.status, " +
            "c.full_name, c.phone, pp.imei, pv.variant_name " +
            "FROM WarrantyRequest wr " +
            "JOIN Customer c ON wr.customer_id = c.customer_id " +
            "JOIN PhysicalProduct pp ON wr.physical_id = pp.physical_id " +
            "JOIN ProductVariant pv ON pp.variant_id = pv.variant_id " +
            "JOIN [User] cs ON wr.customer_service_id = cs.user_id " +
            "WHERE cs.branch_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(branchId);
        
        // Lọc theo từ khóa (SĐT, IMEI, Mã phiếu)
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.phone LIKE ? OR pp.imei LIKE ? OR wr.request_code LIKE ?) ");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        // Lọc theo trạng thái
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND wr.status = ? ");
            params.add(status.trim());
        }
        // Lọc theo ngày
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
                    if(rs.getTimestamp("request_date") != null) {
                        req.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
                    }
                    req.setIssueDescription(rs.getString("issue_description"));
                    req.setStatus(rs.getString("status"));
                    req.setCustomerName(rs.getString("full_name"));
                    req.setCustomerPhone(rs.getString("phone"));
                    req.setImei(rs.getString("imei"));
                    req.setVariantName(rs.getString("variant_name"));
                    list.add(req);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    public boolean hasActiveWarrantyRequest(int physicalId) {
        // Nếu status là PENDING hoặc IN_PROGRESS nghĩa là máy vẫn đang nằm viện
        String sql = "SELECT COUNT(*) FROM WarrantyRequest WHERE physical_id = ? AND status IN ('PENDING', 'IN_PROGRESS')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, physicalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu đang có phiếu chưa xong
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}