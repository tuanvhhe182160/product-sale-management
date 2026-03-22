package com.techshop.servlet;

import com.techshop.dao.BranchDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.Branch;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;
import com.techshop.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.techshop.util.NumberUtil.parseIntOrDefault;

@WebServlet("/branch")
public class BranchServlet extends HttpServlet {
    private BranchDAO branchDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() {
        branchDAO = new BranchDAO();
        logDAO = new SystemLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null) {
            listBranches(req, resp);
        } else {
            switch (action) {
                case "add":
                    showFormPage(req, resp);
                    break;

                case "edit":
                    int id = Integer.parseInt(req.getParameter("id"));
                    if (!branchDAO.isBranchIdExist(id)) {
                        resp.sendRedirect("branch");
                        return;
                    }

                    Branch branch = branchDAO.getById(id);

                    showFormPage(req, resp, branch);
                    break;

                default:
                    listBranches(req, resp);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("insert".equals(action)) {
            insertBranch(req, resp);          
        } else if ("update".equals(action)) {
            updateBranch(req, resp);            
        } else {
            resp.sendRedirect("branch");
        }
    }

    private void listBranches(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int PAGE_SIZE = parseIntOrDefault(req.getParameter("pageSize"), 10);
        List<Branch> branchList;

        if(PAGE_SIZE > 0) {

            int page = parseIntOrDefault(req.getParameter("page"), 1);

            if (page < 1) {
                page = 1;
            }

            int totalBranchesCount = branchDAO.countAllBranches();
            int totalPages = (int) Math.ceil((double) totalBranchesCount / PAGE_SIZE);
            if (page > totalPages && totalPages > 0) page = totalPages;

            int offset = (page - 1) * PAGE_SIZE;

            branchList = branchDAO.getAllWithPagination(offset, PAGE_SIZE);

            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("pageSize", PAGE_SIZE);
        } else {
            branchList = branchDAO.getAll();
            req.setAttribute("pageSize", 0);
        }

        req.setAttribute("branchList", branchList);
        req.setAttribute("pageTitle", "Branch Management");

        req.getRequestDispatcher("/views/branch/branchManage.jsp").forward(req, resp);
    }

    private void showFormPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showFormPage(req, resp, null);
    }

    private void showFormPage(HttpServletRequest req, HttpServletResponse resp, Branch branch)
            throws ServletException, IOException {
        if (branch != null) {
            req.setAttribute("pageTitle", "Edit Branch");
            req.setAttribute("branch", branch);
        } else {
            req.setAttribute("pageTitle", "Add New Branch");
        }

        req.getRequestDispatcher("/views/branch/branchForm.jsp").forward(req, resp);
    }

    private void insertBranch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String status = req.getParameter("status");

        List<String> errors = validate(name, code, address, phone, status);

        if (errors.isEmpty() && branchDAO.isBranchCodeExist(code)) {
            errors.add("Branch code already exists!");
        }

        if (errors.isEmpty() && branchDAO.isPhoneNumberUsedInBranch(phone)) {
            errors.add("Phone number is already in use!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errorsList", errors);
            showFormPage(req, resp);
            return;
        }

        Branch branch = new Branch();
        branch.setBranchName(name);
        branch.setBranchCode(code);
        branch.setAddress(address);
        branch.setPhone(phone);
        branch.setStatus(status);

        branchDAO.insert(branch);
        
        //Ghi log action
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer userId = (user != null) ? user.getUserId() : null;

        logDAO.logAction(
            userId, 
            LogAction.CREATE_BRANCH, 
            EntityType.BRANCH, 
            null, 
            req.getRemoteAddr(), 
            "Thêm mới chi nhánh: " + name + " (Mã: " + code + ")"
            );

        resp.sendRedirect("branch");
    }

    private void updateBranch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        int id = Integer.parseInt(req.getParameter("id"));

        if (branchDAO.isBranchIdExist(id)) {

            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String address = req.getParameter("address");
            String phone = req.getParameter("phone");
            String status = req.getParameter("status");

            List<String> errors = validate(name, code, address, phone, status);

            if (errors.isEmpty() && branchDAO.isBranchCodeExistExcludeId(code, id)) {
                errors.add("New branch code already exists!");
            }

            if (errors.isEmpty() && branchDAO.isPhoneNumberUsedInBranch(phone)) {
                errors.add("Phone number is already in use!");
            }

            if (!errors.isEmpty()) {
                Branch branch = branchDAO.getById(id);
                req.setAttribute("errorsList", errors);
                showFormPage(req, resp, branch);
                return;
            }

            Branch branch = new Branch();
            branch.setBranchId(id);
            branch.setBranchName(name);
            branch.setBranchCode(code);
            branch.setAddress(address);
            branch.setPhone(phone);
            branch.setStatus(status);

            branchDAO.update(branch);
            
            // --- GHI LOG ---
            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            Integer userId = (user != null) ? user.getUserId() : null;

            logDAO.logAction(
                userId, 
                LogAction.UPDATE_BRANCH, 
                EntityType.BRANCH, 
                id, 
                req.getRemoteAddr(), 
                "Cập nhật thông tin chi nhánh: " + name
            );
        }

        resp.sendRedirect("branch");
    }

    private List<String> validate(String name, String code, String address, String phone, String status) {
        List<String> errors = new ArrayList<>();

        if (!ValidationUtil.isNotEmpty(name)) {
            errors.add("Branch name is required!");
        }

        if (!ValidationUtil.isNotEmpty(code)) {
            errors.add("Branch code is required!");
        }

        if (address == null || address.trim().isEmpty()) {
            errors.add("Address is required!");
        }

        if (phone == null || !phone.matches("\\d{9,11}")) {
            errors.add("Phone number is required and must be 9–11 digits!");
        }

        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status)) {
            errors.add("Status must be either ACTIVE or INACTIVE!");
        }

        return errors;
    }
}
