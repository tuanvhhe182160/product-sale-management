package com.techshop.servlet;

import com.techshop.dao.InvoicePrintDAO;
import com.techshop.model.InvoicePrintData;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lấy dữ liệu hóa đơn từ DB và forward sang invoice_print.jsp để in.
 * Hỗ trợ in nhiều hóa đơn: /invoice/print?id=1&id=2
 */
@WebServlet("/invoice/print")
public class CashierInvoicePrintServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] idParams = request.getParameterValues("id");
        if (idParams == null || idParams.length == 0) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        InvoicePrintDAO dao = new InvoicePrintDAO();
        List<InvoicePrintData> invoices = new ArrayList<>();

        for (String idParam : idParams) {
            try {
                int invoiceId = Integer.parseInt(idParam.trim());
                InvoicePrintData data = dao.getInvoicePrintData(invoiceId);
                if (data != null) {
                    invoices.add(data);
                }
            } catch (NumberFormatException ignored) {}
        }

        if (invoices.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // Tương thích ngược: nếu chỉ 1 hóa đơn, set "invoice" cho JSP cũ
        request.setAttribute("invoice", invoices.get(0));
        // Luôn set list để JSP mới dùng
        request.setAttribute("invoices", invoices);

        request.getRequestDispatcher("/views/cashier/invoice_print.jsp")
               .forward(request, response);
    }
}
