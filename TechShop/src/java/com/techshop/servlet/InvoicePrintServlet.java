package com.techshop.servlet;

import com.techshop.dao.InvoicePrintDAO;
import com.techshop.model.InvoicePrintData;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Lấy dữ liệu hóa đơn từ DB và forward sang invoice_print.jsp để in.
 */
@WebServlet("/invoice/print")
public class InvoicePrintServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int invoiceId;
        try {
            invoiceId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        InvoicePrintDAO dao = new InvoicePrintDAO();
        InvoicePrintData data = dao.getInvoicePrintData(invoiceId);

        if (data == null) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        request.setAttribute("invoice", data);
        request.getRequestDispatcher("/views/cashier/invoice_print.jsp")
               .forward(request, response);
    }
}