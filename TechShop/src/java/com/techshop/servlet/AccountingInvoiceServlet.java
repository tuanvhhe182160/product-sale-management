package com.techshop.servlet;

import com.techshop.dao.InvoiceDAOForAccounting;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.Invoice;
import com.techshop.model.LogAction;
import com.techshop.model.User;
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
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AccountingInvoiceServlet", urlPatterns = {"/accounting/invoices"})
public class AccountingInvoiceServlet extends HttpServlet {

    private InvoiceDAOForAccounting invoiceDAO = new InvoiceDAOForAccounting();
    private SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy tham số ngày (mặc định là tháng hiện tại)
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String status = request.getParameter("status");
        String pm = request.getParameter("payment_method");
        String searchKeyword = request.getParameter("searchKeyword");
        
        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().withDayOfMonth(1).toString(); // Ngày đầu tháng
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().toString(); // Hôm nay
        }
        
        if (status == null) {
            status = "ALL";
        }
        
        if (pm == null) {
            pm = "ALL";
        }
        
        int page = 1;
        int pageSize = 15;
        
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) page = Integer.parseInt(pageStr);
        } catch (Exception ignored) {}

        // 2. Lấy dữ liệu
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        // Fix: Admin có branchId = null → 0 = tất cả chi nhánh
        int branchId = (user.getBranchId() != null) ? user.getBranchId() : 0;

        // 3. Xử lý Export CSV
        String action = request.getParameter("action");
        if ("export".equals(action)) {
            List<Invoice> exportList = invoiceDAO.getInvoicesForAccounting(startDate, endDate, branchId, status, pm, searchKeyword, 1, 0);
            exportInvoiceCSV(response, exportList, startDate, endDate);
            //Ghi Log
            String details = "Xuất danh sách hóa đơn kế toán từ " + startDate + " đến " + endDate + " (CN: " + branchId + ")";
            systemLogDAO.logAction(
                user.getUserId(), 
                LogAction.EXPORT_INVOICE, 
                EntityType.INVOICE, 
                null, 
                request.getRemoteAddr(), 
                details
            );
            return; // Ngừng thực thi để tải file
        }
        
        List<Invoice> invoices = invoiceDAO.getInvoicesForAccounting(startDate, endDate, branchId, status, pm, searchKeyword, page, pageSize);
        int totalItems = invoiceDAO.countInvoicesForAccounting(startDate, endDate, branchId, status, pm, searchKeyword);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // 4. Đẩy sang JSP
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("status", status);
        request.setAttribute("pm", pm);
        request.setAttribute("invoices", invoices);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
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