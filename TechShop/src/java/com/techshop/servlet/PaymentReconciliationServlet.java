package com.techshop.servlet;

import com.techshop.dao.ReconciliationDAO;
import com.techshop.dao.SystemLogDAO;
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
import java.util.List;

@WebServlet(name = "PaymentReconciliationServlet", urlPatterns = {"/accounting/reconciliation"})
public class PaymentReconciliationServlet extends HttpServlet {

    private ReconciliationDAO reconDAO = new ReconciliationDAO();
    private SystemLogDAO logDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        // Lấy danh sách hóa đơn đang chờ đối soát theo chi nhánh của Kế toán
        List<Invoice> pendingInvoices = reconDAO.getPendingBankInvoices(user.getBranchId());

        request.setAttribute("pendingInvoices", pendingInvoices);
        request.getRequestDispatcher("/views/accounting/reconciliation-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if ("confirm".equals(action)) {
            int invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
            String invoiceCode = request.getParameter("invoiceCode");

            boolean isSuccess = reconDAO.confirmPayment(invoiceId);

            if (isSuccess) {
                // --- GHI LOG HỆ THỐNG ---
                String details = "Kế toán xác nhận đã nhận tiền thanh toán cho hóa đơn: " + invoiceCode;
                logDAO.logAction(
                    user.getUserId(), 
                    LogAction.APPROVE_PAYMENT,
                    EntityType.INVOICE, 
                    invoiceId, 
                    request.getRemoteAddr(), 
                    details
                );
                
                response.sendRedirect(request.getContextPath() + "/accounting/reconciliation?success=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/accounting/reconciliation?error=confirm_failed");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/accounting/reconciliation");
        }
    }
}