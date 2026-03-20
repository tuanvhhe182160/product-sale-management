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
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "PaymentReconciliationServlet", urlPatterns = {"/accounting/reconciliation"})
public class PaymentReconciliationServlet extends HttpServlet {

    private static final int PAGE_SIZE = 20;
    private ReconciliationDAO reconDAO = new ReconciliationDAO();
    private SystemLogDAO logDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        // Fix: Admin có branchId = null → truyền 0 (DAO sẽ lấy tất cả chi nhánh nếu cần,
        // nhưng Accounting Staff không phải Admin nên branchId luôn có giá trị)
        Integer branchIdObj = user.getBranchId();
        if (branchIdObj == null) {
            // Admin không có workflow reconciliation — redirect về dashboard với thông báo
            response.sendRedirect(request.getContextPath() +
                "/dashboard?error=Chức năng đối soát chỉ dành cho Kế toán chi nhánh.");
            return;
        }
        int branchId = branchIdObj;

        // Lọc ngày (mặc định tháng hiện tại)
        String dateFrom = request.getParameter("dateFrom");
        String dateTo   = request.getParameter("dateTo");
        if (dateFrom == null || dateFrom.trim().isEmpty()) {
            dateFrom = LocalDate.now().withDayOfMonth(1).toString();
        }
        if (dateTo == null || dateTo.trim().isEmpty()) {
            dateTo = LocalDate.now().toString();
        }

        // Phân trang
        int page = 1;
        try {
            String p = request.getParameter("page");
            if (p != null && !p.trim().isEmpty()) page = Integer.parseInt(p.trim());
            if (page < 1) page = 1;
        } catch (NumberFormatException ignored) {}

        int total = reconDAO.countPendingBankInvoices(branchId, dateFrom, dateTo);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Invoice> pendingInvoices = reconDAO.getPendingBankInvoices(
                branchId, dateFrom, dateTo, page, PAGE_SIZE);

        request.setAttribute("pendingInvoices", pendingInvoices);
        request.setAttribute("dateFrom",   dateFrom);
        request.setAttribute("dateTo",     dateTo);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", total);

        // Lấy flash messages từ redirect
        String success = request.getParameter("success");
        String error   = request.getParameter("error");
        if ("true".equals(success))             request.setAttribute("successMsg", "Đã xác nhận thanh toán thành công!");
        if ("confirm_failed".equals(error))     request.setAttribute("errorMsg",   "Xác nhận thất bại. Hóa đơn không còn ở trạng thái chờ.");
        if ("invalid_invoice".equals(error))    request.setAttribute("errorMsg",   "Mã hóa đơn không hợp lệ.");
        if ("unauthorized".equals(error))       request.setAttribute("errorMsg",   "Bạn không có quyền xác nhận hóa đơn của chi nhánh khác.");

        request.getRequestDispatcher("/views/accounting/reconciliation-list.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        Integer branchIdObj = user.getBranchId();
        if (branchIdObj == null) {
            response.sendRedirect(request.getContextPath() +
                "/accounting/reconciliation?error=unauthorized");
            return;
        }
        int branchId = branchIdObj;

        String action = request.getParameter("action");
        if ("confirm".equals(action)) {
            String invoiceIdStr  = request.getParameter("invoiceId");
            String invoiceCode   = request.getParameter("invoiceCode");

            int invoiceId;
            try {
                invoiceId = Integer.parseInt(invoiceIdStr);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() +
                    "/accounting/reconciliation?error=invalid_invoice");
                return;
            }

            // confirmPayment kiểm tra branch_id bên trong — bảo mật cross-branch
            boolean isSuccess = reconDAO.confirmPayment(invoiceId, branchId);

            if (isSuccess) {
                logDAO.logAction(
                    user.getUserId(),
                    LogAction.APPROVE_PAYMENT,
                    EntityType.INVOICE,
                    invoiceId,
                    request.getRemoteAddr(),
                    "Kế toán xác nhận đã nhận tiền cho hóa đơn: " + invoiceCode
                );
                response.sendRedirect(request.getContextPath() +
                    "/accounting/reconciliation?success=true");
            } else {
                response.sendRedirect(request.getContextPath() +
                    "/accounting/reconciliation?error=confirm_failed");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/accounting/reconciliation");
        }
    }
}