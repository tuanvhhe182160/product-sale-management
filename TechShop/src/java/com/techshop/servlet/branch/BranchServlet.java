package com.techshop.servlet.branch;

import com.techshop.dao.BranchDAO;
import com.techshop.model.Branch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "BranchServlet", urlPatterns = "/branch")
public class BranchServlet extends HttpServlet {
    BranchDAO dao = new BranchDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            request.setAttribute("list", dao.getAll());
            request.getRequestDispatcher("/views/Admin/branch/branchList.jsp").forward(request, response);
        } else if (action.equals("add")) {
            request.getRequestDispatcher("/views/Admin/branch/branchForm.jsp").forward(request, response);
        } else if (action.equals("edit")) {
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("branch", dao.getById(id));
            request.getRequestDispatcher("/views/Admin/branch/branchForm.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("branchId");
        String name = request.getParameter("branchName");
        String code = request.getParameter("branchCode");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        Branch branch = new Branch();

        branch.setBranchName(name);
        branch.setBranchCode(code);
        branch.setPhone(phone);
        branch.setAddress(address);

        if (id == null || id.isEmpty()) {
            dao.insert(branch);
        } else {
            branch.setBranchId(Integer.parseInt(id));
            dao.update(branch);
        }

        response.sendRedirect("branch");
    }
}