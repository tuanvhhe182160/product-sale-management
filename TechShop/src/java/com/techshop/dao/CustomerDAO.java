/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý nghiệp vụ khách hàng tại màn hình bán hàng (Cashier).
 *
 * Luồng chính:
 *   1. Nhân viên nhập SĐT → gọi findByPhone()
 *   2. Nếu tìm thấy → điền sẵn thông tin vào form
 *   3. Nếu không tìm thấy → khi xác nhận thanh toán sẽ gọi createCustomer()
 */
public class CustomerDAO extends DBContext {

    /**
     * Tìm khách hàng theo số điện thoại.
     * Trả về null nếu chưa có trong hệ thống.
     */
    public Customer findByPhone(String phone) {
        String sql = "SELECT customer_id, phone, full_name, email, address " +
                     "FROM Customer WHERE phone = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerId(rs.getInt("customer_id"));
                    c.setPhone(rs.getString("phone"));
                    c.setFullName(rs.getString("full_name"));
                    c.setEmail(rs.getString("email"));
                    c.setAddress(rs.getString("address"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Customer getCustomerByPhone(String phone) {
        String sql = "SELECT * FROM Customer WHERE phone = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo khách hàng mới và trả về customer_id vừa tạo.
     * Trả về -1 nếu thất bại.
     *
     * full_name bắt buộc — nếu người dùng không nhập thì dùng SĐT làm tên tạm.
     */
    public int createCustomer(String phone, String fullName, String email, String address) {
        // Nếu không có tên thì dùng SĐT làm tên tạm
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = "Khách " + phone;
        }

        String sql = "INSERT INTO Customer (phone, full_name, email, address) " +
                     "OUTPUT INSERTED.customer_id " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            ps.setString(2, fullName.trim());
            ps.setString(3, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
            ps.setString(4, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Cập nhật thông tin khách hàng đã có.
     * Dùng khi khách hàng cũ thay đổi địa chỉ hoặc email.
     */
    public boolean updateCustomer(int customerId, String fullName, String email, String address) {
        String sql = "UPDATE Customer SET full_name = ?, email = ?, address = ?, " +
                     "updated_at = GETDATE() WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fullName != null ? fullName.trim() : "");
            ps.setString(2, (email != null && !email.trim().isEmpty()) ? email.trim() : null);
            ps.setString(3, (address != null && !address.trim().isEmpty()) ? address.trim() : null);
            ps.setInt(4, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE phone LIKE ? OR full_name LIKE ? OR email LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCustomer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM Customer WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int getCustomerLoyaltyPoints(int customerId) {
        String sql = "SELECT current_points FROM LoyaltyAccount WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("current_points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE Customer SET full_name=?, email=?, address=?, date_of_birth=?, updated_at=GETDATE() WHERE customer_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getAddress());
            if (c.getDateOfBirth() != null) {
                ps.setDate(4, Date.valueOf(c.getDateOfBirth()));
            }
            else ps.setNull(4, Types.DATE);
            ps.setInt(5, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int createCustomer(Customer c) {
        String sqlCustomer = "INSERT INTO Customer (phone, full_name, email, address, date_of_birth, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, GETDATE())";
        String sqlLoyalty = "INSERT INTO LoyaltyAccount (customer_id, total_points, current_points, created_at) " +
                            "VALUES (?, 0, 0, GETDATE())";
        try {
            connection.setAutoCommit(false);
            int newId = 0;
            
            try (PreparedStatement ps = connection.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getPhone());
                ps.setString(2, c.getFullName());
                ps.setString(3, c.getEmail());
                ps.setString(4, c.getAddress());
                if (c.getDateOfBirth() != null) ps.setDate(5, Date.valueOf(c.getDateOfBirth()));
                else ps.setNull(5, Types.DATE);
                
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) newId = rs.getInt(1);
                }
            }

            // Tạo luôn ví điểm thưởng
            if (newId > 0) {
                try (PreparedStatement psLoyalty = connection.prepareStatement(sqlLoyalty)) {
                    psLoyalty.setInt(1, newId);
                    psLoyalty.executeUpdate();
                }
                connection.commit();
                return newId;
            }
            connection.rollback();
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return 0;
    }
    
    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setPhone(rs.getString("phone"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        Date dob = rs.getDate("date_of_birth");
        c.setDateOfBirth(dob != null ? dob.toLocalDate() : null);
        return c;
    }
}
