package com.techshop.servlet;

import com.techshop.dao.ProductCategoryDAO;
import com.techshop.model.ProductCategory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/category/create")
public class CategoryCreateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/Admin/adminCreateCategory.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String code = request.getParameter("categoryCode");
        String name = request.getParameter("categoryName");
        String desc = request.getParameter("description");

        code = (code == null) ? "" : code.trim();
        name = (name == null) ? "" : name.trim();
        desc = (desc == null) ? null : desc.trim();
        if (desc != null && desc.isEmpty()) {
            desc = null;
        }

        String error = null;

        if (code.isEmpty()) {
            error = "Category code is required.";
        } else if (code.length() > 20) {
            error = "Category code must be <= 20 characters.";
        } else if (name.isEmpty()) {
            error = "Category name is required.";
        } else if (name.length() > 100) {
            error = "Category name must be <= 100 characters.";
        } else if (desc != null && desc.length() > 255) {
            error = "Description must be <= 255 characters.";
        }

        ProductCategoryDAO dao = new ProductCategoryDAO();

        if (error == null && dao.existsByCode(code)) {
            error = "Category code already exists.";
        }
        List<ProductCategory> list = dao.getAllCategories();
        request.setAttribute("categories", list);   
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("categoryCode", code);
            request.setAttribute("categoryName", name);
            request.setAttribute("description", desc);

            request.getRequestDispatcher("/views/Admin/adminListCategory.jsp")
                    .forward(request, response);
            return;
        }

        ProductCategory c = new ProductCategory();
        c.setCategoryCode(code);
        c.setCategoryName(name);
        c.setDescription(desc);

        dao.createCategory(c);

        response.sendRedirect(request.getContextPath() + "/ProductCategory");
    }
}
