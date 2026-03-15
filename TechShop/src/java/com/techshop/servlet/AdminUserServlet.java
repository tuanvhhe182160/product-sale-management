package com.techshop.servlet;

import com.techshop.dao.BranchDAO;
import com.techshop.dao.RoleDAO;
import com.techshop.dao.UserDAO;
import com.techshop.model.Branch;
import com.techshop.model.Role;
import com.techshop.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/user")
public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if ("toggle".equals(req.getParameter("action"))) {
            handleToggle(req, resp);
        } else {
            handleList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            handleAdd(req, resp);
        } else if ("edit".equals(action)) {
            handleEdit(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/user");
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        String search = req.getParameter("search");
        String roleFilter = req.getParameter("roleId");
        String statusFilter = req.getParameter("status");
        String branchFilter = req.getParameter("branchId");

        List<User> users = userDAO.getAll();

        // Filter in Java (simple approach, data set is small)
        if (search != null && !search.trim().isEmpty()) {
            String kw = search.trim().toLowerCase();
            users = users.stream().filter(u ->
                (u.getFullName() != null && u.getFullName().toLowerCase().contains(kw)) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(kw)) ||
                (u.getPhone() != null && u.getPhone().toLowerCase().contains(kw))
            ).collect(Collectors.toList());
        }
        if (roleFilter != null && !roleFilter.isEmpty()) {
            int rid = Integer.parseInt(roleFilter);
            users = users.stream().filter(u -> u.getRoleId() == rid).collect(Collectors.toList());
        }
        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equals(statusFilter)) {
            users = users.stream().filter(u -> statusFilter.equals(u.getStatus())).collect(Collectors.toList());
        }
        if (branchFilter != null && !branchFilter.isEmpty()) {
            int bid = Integer.parseInt(branchFilter);
            users = users.stream().filter(u -> u.getBranchId() != null && u.getBranchId() == bid).collect(Collectors.toList());
        }

        req.setAttribute("users", users);
        req.setAttribute("roles", new RoleDAO().getAll());
        req.setAttribute("branches", new BranchDAO().getAllActive());
        req.setAttribute("search", search != null ? search : "");
        req.setAttribute("roleFilter", roleFilter != null ? roleFilter : "");
        req.setAttribute("statusFilter", statusFilter != null ? statusFilter : "ALL");
        req.setAttribute("branchFilter", branchFilter != null ? branchFilter : "");
        req.setAttribute("pageTitle", "Quản lý Nhân viên - TechShop");

        req.getRequestDispatcher("/views/Admin/user_management.jsp").forward(req, resp);
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String roleIdStr = req.getParameter("roleId");
        String branchIdStr = req.getParameter("branchId");

        if (email == null || email.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            session.setAttribute("userMgmtError", "Email và họ tên không được để trống.");
            resp.sendRedirect(req.getContextPath() + "/user"); return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.isEmailExist(email.trim())) {
            session.setAttribute("userMgmtError", "Email đã tồn tại trong hệ thống.");
            resp.sendRedirect(req.getContextPath() + "/user"); return;
        }

        User u = new User();
        u.setEmail(email.trim());
        u.setFullName(fullName.trim());
        u.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        u.setRoleId(Integer.parseInt(roleIdStr));
        u.setBranchId(branchIdStr != null && !branchIdStr.isEmpty() ? Integer.parseInt(branchIdStr) : null);
        u.setStatus("ACTIVE");

        boolean ok = userDAO.insert(u);
        session.setAttribute(ok ? "userMgmtSuccess" : "userMgmtError",
            ok ? "Đã thêm nhân viên " + fullName.trim() + " thành công." : "Thêm nhân viên thất bại.");
        resp.sendRedirect(req.getContextPath() + "/user");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = Integer.parseInt(req.getParameter("userId"));
        String email = req.getParameter("email");
        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String roleIdStr = req.getParameter("roleId");
        String branchIdStr = req.getParameter("branchId");
        String status = req.getParameter("status");

        UserDAO userDAO = new UserDAO();
        User u = userDAO.getById(userId);
        if (u == null) {
            session.setAttribute("userMgmtError", "Không tìm thấy nhân viên.");
            resp.sendRedirect(req.getContextPath() + "/user"); return;
        }

        u.setEmail(email.trim());
        u.setFullName(fullName.trim());
        u.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        u.setRoleId(Integer.parseInt(roleIdStr));
        u.setBranchId(branchIdStr != null && !branchIdStr.isEmpty() ? Integer.parseInt(branchIdStr) : null);
        u.setStatus(status);

        boolean ok = userDAO.update(u);
        session.setAttribute(ok ? "userMgmtSuccess" : "userMgmtError",
            ok ? "Đã cập nhật nhân viên thành công." : "Cập nhật thất bại.");
        resp.sendRedirect(req.getContextPath() + "/user");
    }

    private void handleToggle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = Integer.parseInt(req.getParameter("userId"));
        String newStatus = req.getParameter("newStatus");
        boolean ok = new UserDAO().updateStatus(userId, newStatus);
        session.setAttribute(ok ? "userMgmtSuccess" : "userMgmtError",
            ok ? "Đã cập nhật trạng thái." : "Cập nhật thất bại.");
        resp.sendRedirect(req.getContextPath() + "/user");
    }
}
