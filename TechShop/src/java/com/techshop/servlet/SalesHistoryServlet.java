package com.techshop.servlet;

import com.techshop.dao.SalesHistoryDAO;
import com.techshop.model.Invoice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servlet xử lý lịch sử bán hàng + xuất Excel cho Cashier.
 * URL: /invoice (GET) — hiển thị trang lịch sử
 * URL: /invoice?action=export — xuất file CSV
 */
@WebServlet("/invoice")
public class SalesHistoryServlet extends HttpServlet {

    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        int cashierId = 0;
        String cashierName = "Cashier";
        if (session != null) {
            if (session.getAttribute("userId") != null)
                cashierId = (int) session.getAttribute("userId");
            if (session.getAttribute("userName") != null)
                cashierName = (String) session.getAttribute("userName");
        }
        if (cashierId <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Đọc filter params
        String search   = emptyToNull(request.getParameter("search"));
        String dateFrom = emptyToNull(request.getParameter("dateFrom"));
        String dateTo   = emptyToNull(request.getParameter("dateTo"));
        String status   = emptyToNull(request.getParameter("status"));

        // Phân luồng: export CSV hoặc hiển thị trang
        String action = request.getParameter("action");
        if ("export".equals(action)) {
            handleExport(response, cashierId, cashierName, search, dateFrom, dateTo, status);
        } else {
            handleList(request, response, cashierId, search, dateFrom, dateTo, status);
        }
    }

    // ── Hiển thị trang lịch sử bán hàng ─────────────────────────────────
    private void handleList(HttpServletRequest request, HttpServletResponse response,
                            int cashierId, String search, String dateFrom,
                            String dateTo, String status)
            throws ServletException, IOException {

        int page = 1;
        try {
            String p = request.getParameter("page");
            if (p != null && !p.isEmpty()) page = Integer.parseInt(p);
            if (page < 1) page = 1;
        } catch (NumberFormatException ignored) {}

        SalesHistoryDAO dao = new SalesHistoryDAO();

        List<Invoice> invoices = dao.getInvoices(cashierId, search, dateFrom, dateTo, status, page, PAGE_SIZE);
        int totalItems = dao.countInvoices(cashierId, search, dateFrom, dateTo, status);
        int totalPages = (int) Math.ceil(totalItems * 1.0 / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page > totalPages) page = totalPages;

        BigDecimal[] stats = dao.getStats(cashierId);

        request.setAttribute("invoices", invoices);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("search", search != null ? search : "");
        request.setAttribute("dateFrom", dateFrom != null ? dateFrom : "");
        request.setAttribute("dateTo", dateTo != null ? dateTo : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("todayCount", stats[0].intValue());
        request.setAttribute("todayRevenue", stats[1]);
        request.setAttribute("monthRevenue", stats[2]);
        request.setAttribute("pageTitle", "Lịch sử bán hàng");

        request.getRequestDispatcher("/views/cashier/sales-history.jsp")
                .forward(request, response);
    }

    // ── Xuất file CSV (Excel) ───────────────────────────────────────────
    private void handleExport(HttpServletResponse response,
                              int cashierId, String cashierName,
                              String search, String dateFrom,
                              String dateTo, String status)
            throws IOException {

        SalesHistoryDAO dao = new SalesHistoryDAO();
        List<Invoice> invoices = dao.getAllInvoices(cashierId, search, dateFrom, dateTo, status);
        BigDecimal[] stats = dao.getStats(cashierId);

        // Tên file
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String safeName = cashierName.replaceAll("[^a-zA-Z0-9\\p{L}]", "_");
        String fileName = "BaoCao_" + safeName + "_" + dateStr + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + fileName);

        // UTF-8 BOM để Excel nhận diện encoding
        var out = response.getOutputStream();
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);

        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);

        // Thông tin nhân viên
        pw.println("THỐNG KÊ BÁN HÀNG");
        pw.println("Nhân viên:," + escapeCsv(cashierName));
        pw.println("Ngày xuất:," + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (dateFrom != null || dateTo != null) {
            String range = (dateFrom != null ? dateFrom : "...") + " đến " + (dateTo != null ? dateTo : "...");
            pw.println("Khoảng thời gian:," + range);
        }
        pw.println();

        // Thống kê tổng quan
        pw.println("TỔNG QUAN");
        pw.println("Hóa đơn hôm nay:," + stats[0].intValue());
        pw.println("Doanh thu hôm nay:," + stats[1].setScale(0).toPlainString());
        pw.println("Doanh thu tháng này:," + stats[2].setScale(0).toPlainString());
        pw.println("Tổng hóa đơn (theo bộ lọc):," + invoices.size());

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        int totalItems = 0;
        for (Invoice inv : invoices) {
            if (inv.getFinalAmount() != null) totalRevenue = totalRevenue.add(inv.getFinalAmount());
            if (inv.getDiscountAmount() != null) totalDiscount = totalDiscount.add(inv.getDiscountAmount());
            totalItems += inv.getItemCount();
        }
        pw.println("Tổng doanh thu (theo bộ lọc):," + totalRevenue.setScale(0).toPlainString());
        pw.println("Tổng giảm giá:," + totalDiscount.setScale(0).toPlainString());
        pw.println("Tổng sản phẩm đã bán:," + totalItems);
        pw.println();

        // Bảng chi tiết
        pw.println("CHI TIẾT HÓA ĐƠN");
        pw.println("STT,Mã hóa đơn,Thời gian,Khách hàng,SĐT khách,Số SP,Tổng tiền hàng,Giảm giá,Thành tiền,Thanh toán,Trạng thái,Ghi chú");

        int stt = 0;
        for (Invoice inv : invoices) {
            stt++;
            pw.println(
                stt + "," +
                escapeCsv(inv.getInvoiceCode()) + "," +
                escapeCsv(inv.getInvoiceDateFormatted()) + "," +
                escapeCsv(inv.getCustomerName()) + "," +
                escapeCsv(inv.getCustomerPhone()) + "," +
                inv.getItemCount() + "," +
                (inv.getTotalAmount() != null ? inv.getTotalAmount().setScale(0).toPlainString() : "0") + "," +
                (inv.getDiscountAmount() != null ? inv.getDiscountAmount().setScale(0).toPlainString() : "0") + "," +
                (inv.getFinalAmount() != null ? inv.getFinalAmount().setScale(0).toPlainString() : "0") + "," +
                paymentLabel(inv.getPaymentMethod()) + "," +
                statusLabel(inv.getStatus()) + "," +
                escapeCsv(inv.getNote() != null ? inv.getNote() : "")
            );
        }

        pw.flush();
        pw.close();
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private String paymentLabel(String method) {
        if (method == null) return "";
        switch (method) {
            case "CASH": return "Tiền mặt";
            case "CARD": return "Thẻ";
            case "TRANSFER": return "Chuyển khoản";
            default: return method;
        }
    }

    private String statusLabel(String s) {
        if (s == null) return "";
        switch (s) {
            case "COMPLETED": return "Hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return s;
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
