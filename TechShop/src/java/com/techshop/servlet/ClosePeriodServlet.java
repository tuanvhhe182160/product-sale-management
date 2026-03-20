package com.techshop.servlet;

import com.google.gson.Gson;
import com.techshop.dao.AccountingPeriodDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.AccountingPeriod;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Chốt kỳ kế toán theo tháng/năm cho chi nhánh của Kế toán viên.
 *
 * URL: /accounting/close-period
 *
 * GET  (no action)          → trang chính: form chọn kỳ + lịch sử
 * GET  ?action=preview-json → AJAX: trả JSON dữ liệu preview kỳ được chọn
 * POST ?action=close        → chốt kỳ
 *
 * branchId LUÔN lấy từ session, KHÔNG lấy từ URL param (bảo mật).
 */
@WebServlet(name = "ClosePeriodServlet", urlPatterns = {"/accounting/close-period"})
public class ClosePeriodServlet extends HttpServlet {

    private AccountingPeriodDAO periodDAO;
    private SystemLogDAO        logDAO;
    private final Gson          gson = new Gson();

    @Override
    public void init() {
        periodDAO = new AccountingPeriodDAO();
        logDAO    = new SystemLogDAO();
    }

    // ───────────────────────────────────────────────────────
    //  GET
    // ───────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        // branchId từ session — không bao giờ lấy từ URL
        Integer branchIdObj = user.getBranchId();
        if (branchIdObj == null) {
            // Admin không có chi nhánh → không dùng chức năng này
            resp.sendRedirect(req.getContextPath() +
                "/dashboard?error=close-period-not-for-admin");
            return;
        }
        int branchId = branchIdObj;

        String action = emptyToNull(req.getParameter("action"));

        // ── AJAX: trả JSON preview ──────────────────────────────────────
        if ("preview-json".equals(action)) {
            handlePreviewJson(req, resp, branchId);
            return;
        }

        // ── Trang chính ─────────────────────────────────────────────────
        List<AccountingPeriod> history = periodDAO.getClosedPeriods(branchId);

        // Default: tháng trước
        LocalDate prev = LocalDate.now().minusMonths(1);

        req.setAttribute("history",      history);
        req.setAttribute("branchName",   user.getBranchName());
        req.setAttribute("defaultMonth", prev.getMonthValue());
        req.setAttribute("defaultYear",  prev.getYear());
        req.setAttribute("currentMonth", LocalDate.now().getMonthValue());
        req.setAttribute("currentYear",  LocalDate.now().getYear());

        req.getRequestDispatcher("/views/accounting/close-period.jsp")
           .forward(req, resp);
    }

    // ── AJAX preview-json ──────────────────────────────────────────────
    private void handlePreviewJson(HttpServletRequest req, HttpServletResponse resp,
                                   int branchId) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        int month, year;
        try {
            month = Integer.parseInt(req.getParameter("month"));
            year  = Integer.parseInt(req.getParameter("year"));
        } catch (NumberFormatException e) {
            resp.getWriter().print("{\"error\":\"Tháng hoặc năm không hợp lệ\"}");
            return;
        }

        // Validation: không cho chốt tháng hiện tại hoặc tương lai
        LocalDate now   = LocalDate.now();
        LocalDate chosen = LocalDate.of(year, month, 1);
        if (!chosen.isBefore(LocalDate.of(now.getYear(), now.getMonthValue(), 1))) {
            resp.getWriter().print(
                "{\"error\":\"Chỉ được chốt các tháng đã qua, không chốt tháng hiện tại hoặc tương lai.\"}");
            return;
        }

        boolean alreadyClosed = periodDAO.isPeriodClosed(branchId, month, year);
        AccountingPeriod preview = periodDAO.calculatePeriod(branchId, month, year);

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("alreadyClosed",  alreadyClosed);
        json.put("month",          month);
        json.put("year",           year);
        json.put("totalInvoices",  preview.getTotalInvoices());
        json.put("totalRevenue",   preview.getTotalRevenue());
        json.put("totalProfit",    preview.getTotalProfit());

        resp.getWriter().print(gson.toJson(json));
    }

    // ───────────────────────────────────────────────────────
    //  POST: chốt kỳ
    // ───────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        User user = currentUser(req);
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        Integer branchIdObj = user.getBranchId();
        if (branchIdObj == null) {
            resp.sendRedirect(req.getContextPath() + "/accounting/close-period?error=no_branch");
            return;
        }
        int branchId = branchIdObj;

        if (!"close".equals(req.getParameter("action"))) {
            resp.sendRedirect(req.getContextPath() + "/accounting/close-period");
            return;
        }

        int month, year;
        try {
            month = Integer.parseInt(req.getParameter("month"));
            year  = Integer.parseInt(req.getParameter("year"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() +
                "/accounting/close-period?error=invalid_params");
            return;
        }

        // Validate: chỉ tháng đã qua
        LocalDate now    = LocalDate.now();
        LocalDate chosen = LocalDate.of(year, month, 1);
        if (!chosen.isBefore(LocalDate.of(now.getYear(), now.getMonthValue(), 1))) {
            resp.sendRedirect(req.getContextPath() +
                "/accounting/close-period?error=future_month");
            return;
        }

        // Kiểm tra đã chốt chưa
        if (periodDAO.isPeriodClosed(branchId, month, year)) {
            resp.sendRedirect(req.getContextPath() +
                "/accounting/close-period?error=already_closed&month=" + month + "&year=" + year);
            return;
        }

        // Tính + lưu
        AccountingPeriod period = periodDAO.calculatePeriod(branchId, month, year);
        period.setClosedBy(user.getUserId());

        boolean ok = periodDAO.closePeriod(period);

        if (ok) {
            logDAO.logAction(
                user.getUserId(),
                LogAction.CLOSE_ACCOUNTING_PERIOD,
                EntityType.ACCOUNTING,
                null,
                req.getRemoteAddr(),
                "Chốt sổ kế toán tháng " + month + "/" + year +
                " (Chi nhánh: " + user.getBranchName() + ")"
            );
            resp.sendRedirect(req.getContextPath() +
                "/accounting/close-period?success=closed&month=" + month + "&year=" + year);
        } else {
            resp.sendRedirect(req.getContextPath() +
                "/accounting/close-period?error=db_failed");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? (User) s.getAttribute("user") : null;
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}