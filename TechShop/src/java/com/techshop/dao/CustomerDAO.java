/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
