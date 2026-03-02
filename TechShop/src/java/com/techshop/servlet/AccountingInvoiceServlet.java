package com.techshop.servlet;

import com.techshop.dao.InvoiceDAOTest;
import com.techshop.model.Invoice;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AccountingInvoiceServlet", urlPatterns = {"/accounting/invoices"})
public class AccountingInvoiceServlet extends HttpServlet {

    private InvoiceDAOTest invoiceDAO = new InvoiceDAOTest();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy tham số ngày (mặc định là tháng hiện tại)
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().withDayOfMonth(1).toString(); // Ngày đầu tháng
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().toString(); // Hôm nay
        }

        // 2. Lấy dữ liệu
        List<Invoice> invoices = invoiceDAO.getInvoicesForAccounting(startDate, endDate);

        // 3. Xử lý Export CSV
        String action = request.getParameter("action");
        if ("export".equals(action)) {
            exportInvoiceCSV(response, invoices, startDate, endDate);
            return; // Ngừng thực thi để tải file
        }

        // 4. Đẩy sang JSP
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("invoices", invoices);
        request.getRequestDispatcher("/views/accounting/invoice-list.jsp").forward(request, response);
    }

    // Đặt hàm này bên trong AccountingInvoiceServlet.java
    private void exportInvoiceCSV(HttpServletResponse response, List<Invoice> invoices, String start, String end) 
            throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Invoices_" + start + "_to_" + end + ".csv\"");
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        try (PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(response.getOutputStream(), "UTF-8"))) {
            // Cập nhật Header
            out.println("Mã Hóa Đơn,Ngày Giao Dịch,Khách Hàng,Thu Ngân,Chi Nhánh,Thực Thu (VNĐ),PT Thanh Toán,Trạng Thái");
        
            DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
            for (Invoice inv : invoices) {
                String dateStr = inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(formatter) : "";
                String customer = inv.getCustomerName() != null ? inv.getCustomerName() : "Khách lẻ";
                String cashier = inv.getCashierName() != null ? inv.getCashierName() : "N/A";
                String branch = inv.getBranchName() != null ? inv.getBranchName() : "N/A";
            
                // Xóa dấu phẩy trong chuỗi để tránh làm hỏng định dạng CSV
                out.printf("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    inv.getInvoiceCode(),
                    dateStr,
                    customer.replace(",", " "),
                    cashier.replace(",", " "),
                    branch.replace(",", " "),
                    inv.getFinalAmount(), // Gọi trực tiếp BigDecimal
                    inv.getPaymentMethod(),
                    inv.getStatus());
            }
            out.flush();
        }
    }
}