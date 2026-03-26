package com.techshop.servlet;

import com.techshop.dao.PhysicalProductDAO;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "PhysicalProductDetailServlet", urlPatterns = {"/inventory/detail"})
public class PhysicalProductDetailServlet extends HttpServlet {

    private PhysicalProductDAO physicalProductDAO;

    @Override
    public void init() throws ServletException {
        physicalProductDAO = new PhysicalProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        int physicalId;
        try {
            physicalId = Integer.parseInt(idParam);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }

        PhysicalProduct product = physicalProductDAO.getById(physicalId);

        if (product == null || user.getBranchId() == null || product.getBranchId() != user.getBranchId()) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }

        request.setAttribute("product", product);
        request.setAttribute("pageTitle", "Physical Product Detail");
        request.getRequestDispatcher("/views/inventory/physical-product-detail.jsp").forward(request, response);
    }
}
