package com.techshop.servlet;

import com.techshop.dao.CustomerDAO;
import com.techshop.dao.InvoiceDAOTest;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.Customer;
import com.techshop.model.EntityType;
import com.techshop.model.Invoice;
import com.techshop.model.LogAction;
import com.techshop.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(name = "CustomerServlet", urlPatterns = {"/customer", "/customer/save"})
public class CustomerServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();
    private final InvoiceDAOTest invoiceDAO = new InvoiceDAOTest();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String action = request.getParameter("action");

        // Use Case: Customer List & Search
        if ("/customer".equals(path) && (action == null || "list".equals(action))) {
            String keyword = request.getParameter("search");
            List<Customer> customers = customerDAO.searchCustomers(keyword);
            
            request.setAttribute("customers", customers);
            request.setAttribute("searchKeyword", keyword);
            request.getRequestDispatcher("/views/cs/customer-list.jsp").forward(request, response);
            return;
        }

        // Use Case: Customer Detail (View Purchase History & Loyalty Points)
        if ("/customer".equals(path) && "detail".equals(action)) {
            try {
                int customerId = Integer.parseInt(request.getParameter("id"));
                Customer customer = customerDAO.getCustomerById(customerId);
                
                if (customer != null) {
                    // Lấy điểm Loyalty
                    int loyaltyPoints = customerDAO.getCustomerLoyaltyPoints(customerId);
                    
                    String fromDate = request.getParameter("fromDate");
                    String toDate = request.getParameter("toDate");
                    String status = request.getParameter("status");
                    
                    List<Invoice> purchaseHistory = invoiceDAO.getCustomerInvoices(customerId, fromDate, toDate, status);
                    
                    request.setAttribute("purchaseHistory", purchaseHistory);
                    request.setAttribute("fromDate", fromDate);
                    request.setAttribute("toDate", toDate);
                    request.setAttribute("status", status);
                    // -------------------------------------------------------------

                    request.setAttribute("customer", customer);
                    request.setAttribute("loyaltyPoints", loyaltyPoints);
                    request.getRequestDispatcher("/views/cs/customer-detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer?error=Không tìm thấy khách hàng");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/customer");
            }
        }
    }

    // Use Case: Customer Form (Create/Edit)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String idRaw = request.getParameter("customerId");
        String phone = request.getParameter("phone");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String dobRaw = request.getParameter("dateOfBirth"); // Format: yyyy-MM-dd

        Customer c = new Customer();
        c.setPhone(phone != null ? phone.trim() : "");
        c.setFullName(fullName != null ? fullName.trim() : "");
        c.setEmail(email != null ? email.trim() : "");
        c.setAddress(address != null ? address.trim() : "");
        
        try {
            if (dobRaw != null && !dobRaw.trim().isEmpty()) {
                c.setDateOfBirth(LocalDate.parse(dobRaw.trim()));
            }
        } catch (DateTimeParseException ignored) {}

        boolean isEdit = (idRaw != null && !idRaw.trim().isEmpty());

        // --- CHUẨN BỊ GHI LOG ---
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer userId = (user != null) ? user.getUserId() : null;
        // -------------------------

        if (!isEdit) {
            // Kiểm tra trùng SĐT trước khi tạo
            if (customerDAO.getCustomerByPhone(c.getPhone()) != null) {
                response.sendRedirect(request.getContextPath() + "/customer?error=Số điện thoại đã tồn tại!");
                return;
            }
            
            int newId = customerDAO.createCustomer(c);
            if (newId > 0) {
                // --- GHI LOG ---
                logDAO.logAction(
                    userId, 
                    LogAction.CREATE_CUSTOMER, 
                    EntityType.CUSTOMER,       
                    newId, 
                    request.getRemoteAddr(), 
                    "Thêm mới khách hàng: " + c.getFullName() + " (SĐT: " + c.getPhone() + ")"
                );
                
                response.sendRedirect(request.getContextPath() + "/customer?message=Thêm khách hàng thành công!");
            } else {
                response.sendRedirect(request.getContextPath() + "/customer?error=Lỗi khi thêm khách hàng.");
            }
        } else {
            try {
                c.setCustomerId(Integer.parseInt(idRaw));
                boolean success = customerDAO.updateCustomer(c);
                if (success) {
                    // --- GHI LOG ---
                    logDAO.logAction(
                        userId, 
                        LogAction.UPDATE_CUSTOMER, 
                        EntityType.CUSTOMER, 
                        c.getCustomerId(), 
                        request.getRemoteAddr(), 
                        "Cập nhật hồ sơ khách hàng: " + c.getFullName() + " (SĐT: " + c.getPhone() + ")"
                    );
                    
                    response.sendRedirect(request.getContextPath() + "/customer?action=detail&id=" + c.getCustomerId() + "&message=Cập nhật thành công!");
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer?error=Lỗi khi cập nhật.");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/customer?error=ID không hợp lệ.");
            }
        }
    }
}