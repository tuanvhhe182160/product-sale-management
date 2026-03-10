/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.InvoiceDAOTest;
import com.techshop.model.Invoice;
import com.techshop.model.InvoiceItem;
import com.techshop.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author justi
 */
@WebServlet(name="InvoiceDetailServlet", urlPatterns={"/accounting/invoice-detail"})
public class InvoiceDetailServlet extends HttpServlet {
   private InvoiceDAOTest invoiceDAO = new InvoiceDAOTest();
   @Override
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response)
            throws ServletException, IOException {

        int invoiceId = Integer.parseInt(request.getParameter("id"));

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        Invoice invoice = invoiceDAO.getInvoiceDetail(invoiceId, user.getBranchId());
        List<InvoiceItem> items = invoiceDAO.getInvoiceItems(invoiceId, user.getBranchId());

        if (invoice == null) {
            response.sendRedirect(request.getContextPath() + "/accounting/invoices");
            return;
        }

        request.setAttribute("invoice", invoice);
        request.setAttribute("items", items);

        request.getRequestDispatcher("/views/accounting/invoice-detail.jsp")
               .forward(request, response);
    }
}
