/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.CustomerDAO;
import com.techshop.dao.LoyaltyDAO;
import com.techshop.model.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
@WebServlet("/customer/lookup")
public class CustomerLookupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        // Cho phép gọi từ cùng origin (cùng domain)
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter out = response.getWriter();

        String phone = request.getParameter("phone");
        if (phone == null || phone.trim().isEmpty()) {
            out.print("{\"found\":false,\"error\":\"Thiếu số điện thoại\"}");
            return;
        }

        phone = phone.trim();

        // Kiểm tra định dạng SĐT cơ bản (9-11 chữ số)
        if (!phone.matches("[0-9]{9,11}")) {
            out.print("{\"found\":false,\"error\":\"Số điện thoại không hợp lệ\"}");
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        Customer customer = dao.findByPhone(phone);

        if (customer != null) {
            // Lấy điểm thưởng
            LoyaltyDAO loyaltyDAO = new LoyaltyDAO();
            int loyaltyPoints = loyaltyDAO.getCustomerPoints(customer.getCustomerId());

            // Tìm thấy — trả về thông tin để điền vào form
            out.print("{" +
                "\"found\":true," +
                "\"customerId\":" + customer.getCustomerId() + "," +
                "\"phone\":" + jsonString(customer.getPhone()) + "," +
                "\"fullName\":" + jsonString(customer.getFullName()) + "," +
                "\"email\":" + jsonString(customer.getEmail()) + "," +
                "\"address\":" + jsonString(customer.getAddress()) + "," +
                "\"loyaltyPoints\":" + loyaltyPoints + "," +
                "\"redeemRate\":1000" +
            "}");
        } else {
            // Không tìm thấy — khách hàng mới
            out.print("{\"found\":false}");
        }
    }

    /**
     * Chuyển String thành JSON string an toàn (escape ký tự đặc biệt).
     * Trả về "null" nếu giá trị rỗng.
     */
    private String jsonString(String value) {
        if (value == null || value.trim().isEmpty()) return "null";
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                + "\"";
    }
}