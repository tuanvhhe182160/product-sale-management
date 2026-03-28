package com.techshop.servlet;

import com.techshop.dao.BranchDAO;
import com.techshop.dao.CashierManagementDAO;
import com.techshop.dao.UserDAO;
import com.techshop.model.Branch;
import com.techshop.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Quản lý Cashier cho Admin / Shop Manager.
 * GET  /cashier-mgmt                → danh sách + thống kê
 * GET  /cashier-mgmt?action=toggle  → vô hiệu hóa / kích hoạt
 * POST /cashier-mgmt?action=add     → thêm cashier mới
 */
@WebServlet("/admin/cashier-mgmt")
public class AdminCashierServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (!checkAccess(session, resp)) return;
        String role = (String) session.getAttribute("userRole");

        if ("toggle".equals(req.getParameter("action"))) {
            handleToggle(req, resp, session);
        } else {
            handleList(req, resp, session, role);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (!checkAccess(session, resp)) return;
        String role = (String) session.getAttribute("userRole");

        if ("add".equals(req.getParameter("action"))) {
            handleAdd(req, resp, session, role);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt");
        }
    }

    // ── Danh sách + thống kê ────────────────────────────────────────────
    private void handleList(HttpServletRequest req, HttpServletResponse resp,
                            HttpSession session, String role)
            throws ServletException, IOException {

        CashierManagementDAO dao = new CashierManagementDAO();

        // branchId: Shop Manager → chi nhánh mình, Admin → filter hoặc tất cả
        int viewBranchId = 0;
        if ("Shop Manager".equals(role)) {
            Object bid = session.getAttribute("branchId");
            viewBranchId = (bid instanceof Integer) ? (Integer) bid : 0;
        } else {
            String bp = req.getParameter("branchId");
            if (bp != null && !bp.trim().isEmpty()) {
                try { viewBranchId = Integer.parseInt(bp.trim()); } catch (NumberFormatException ignored) {}
            }
        }

        String search = req.getParameter("search");
        String statusFilter = req.getParameter("status");

        List<User> cashiers = dao.getCashiers(viewBranchId, search, statusFilter);

        // Filter tháng/năm cho thống kê cashier
        int filterMonth = 0, filterYear = 0;
        try {
            String m = req.getParameter("month"); String y = req.getParameter("year");
            if (m != null && !m.trim().isEmpty()) filterMonth = Integer.parseInt(m.trim());
            if (y != null && !y.trim().isEmpty()) filterYear = Integer.parseInt(y.trim());
        } catch (NumberFormatException ignored) {}

        // Stats cashier được chọn
        int selectedCashierId = 0;
        String cp = req.getParameter("cashierId");
        if (cp != null && !cp.trim().isEmpty()) {
            try { selectedCashierId = Integer.parseInt(cp.trim()); } catch (NumberFormatException ignored) {}
        }
        BigDecimal[] selectedStats = selectedCashierId > 0
            ? dao.getCashierStats(selectedCashierId, filterMonth, filterYear) : null;

        // Stats chi nhánh
        BigDecimal[] branchStats = viewBranchId > 0 ? dao.getBranchStats(viewBranchId) : null;

        // Dropdown chi nhánh (Admin)
        List<Branch> branches = "Admin".equals(role) ? new BranchDAO().getAllActive() : null;

        req.setAttribute("cashiers", cashiers);
        req.setAttribute("viewBranchId", viewBranchId);
        req.setAttribute("search", search != null ? search : "");
        req.setAttribute("statusFilter", statusFilter != null ? statusFilter : "ALL");
        req.setAttribute("branches", branches);
        req.setAttribute("selectedCashierId", selectedCashierId);
        req.setAttribute("selectedStats", selectedStats);
        req.setAttribute("branchStats", branchStats);
        req.setAttribute("filterMonth", filterMonth);
        req.setAttribute("filterYear", filterYear);
        req.setAttribute("userRole", role);
        req.setAttribute("pageTitle", "Quản lý Cashier - TechShop");

        req.getRequestDispatcher("/views/Admin/cashier-management.jsp").forward(req, resp);
    }

    // ── Thêm cashier ────────────────────────────────────────────────────
    private void handleAdd(HttpServletRequest req, HttpServletResponse resp,
                           HttpSession session, String role) throws IOException {

        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");

        if (email == null || email.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            session.setAttribute("mgmtError", "Email và họ tên không được để trống.");
            resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt");
            return;
        }

        int targetBranchId;
        if ("Shop Manager".equals(role)) {
            Object bid = session.getAttribute("branchId");
            targetBranchId = (bid instanceof Integer) ? (Integer) bid : 0;
        } else {
            String bp = req.getParameter("addBranchId");
            if (bp == null || bp.trim().isEmpty()) {
                session.setAttribute("mgmtError", "Vui lòng chọn chi nhánh.");
                resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt"); return;
            }
            try { targetBranchId = Integer.parseInt(bp.trim()); }
            catch (NumberFormatException e) {
                session.setAttribute("mgmtError", "Chi nhánh không hợp lệ.");
                resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt"); return;
            }
        }

        if (targetBranchId <= 0) {
            session.setAttribute("mgmtError", "Chi nhánh không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt"); return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.isEmailExist(email.trim())) {
            session.setAttribute("mgmtError", "Email đã tồn tại trong hệ thống.");
            resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt"); return;
        }

        // Dùng UserDAO.insert() — tạo User object với role Cashier
        User newUser = new User();
        newUser.setEmail(email.trim());
        newUser.setFullName(fullName.trim());
        newUser.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        newUser.setRoleId(new CashierManagementDAO().getCashierRoleId());
        newUser.setBranchId(targetBranchId);
        newUser.setStatus("ACTIVE");

        int newUserId = userDAO.insert(newUser);
        boolean ok = newUserId > 0;
        session.setAttribute(ok ? "mgmtSuccess" : "mgmtError",
            ok ? "Đã thêm cashier " + fullName.trim() + " thành công."
               : "Thêm cashier thất bại.");
        resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt");
    }

    // ── Toggle trạng thái (ACTIVE / INACTIVE) ──────────────────────────
    private void handleToggle(HttpServletRequest req, HttpServletResponse resp,
                              HttpSession session) throws IOException {
        String uidStr = req.getParameter("userId");
        String newStatus = req.getParameter("newStatus");

        if (uidStr == null || newStatus == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt");
            return;
        }

        try {
            int userId = Integer.parseInt(uidStr.trim());
            UserDAO userDAO = new UserDAO();
            boolean ok = userDAO.updateStatus(userId, newStatus.trim());
            session.setAttribute(ok ? "mgmtSuccess" : "mgmtError",
                ok ? "Đã cập nhật trạng thái thành công."
                   : "Cập nhật trạng thái thất bại.");
        } catch (NumberFormatException e) {
            session.setAttribute("mgmtError", "User ID không hợp lệ.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/cashier-mgmt");
    }

    // ── Kiểm tra quyền truy cập ────────────────────────────────────────
    private boolean checkAccess(HttpSession session, HttpServletResponse resp) throws IOException {
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login");
            return false;
        }
        String role = (String) session.getAttribute("userRole");
        if (!"Admin".equals(role) && !"Shop Manager".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return false;
        }
        return true;
    }
}
