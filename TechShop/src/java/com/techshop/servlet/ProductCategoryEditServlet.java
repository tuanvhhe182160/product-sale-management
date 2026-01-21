/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.ProductCategoryDAO;
import com.techshop.model.ProductCategory;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name="ProductCategoryEditServlet", urlPatterns="/ProductCategory/edit")

public class ProductCategoryEditServlet extends HttpServlet {

    ProductCategoryDAO dao = new ProductCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id;
        try {
            id = Integer.parseInt(request.getParameter("categoryId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ProductCategory");
            return;
        }

        ProductCategory category = dao.getById(id);
        if (category == null) {
            response.sendRedirect(request.getContextPath() + "/ProductCategory");
            return;
        }

        request.setAttribute("category", category);
        request.getRequestDispatcher("/views/Admin/categoryEdit.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int categoryId;
        try {
            categoryId = Integer.parseInt(request.getParameter("categoryId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ProductCategory");
            return;
        }

        String code = request.getParameter("categoryCode").trim();
        String name = request.getParameter("categoryName").trim();
        String desc = request.getParameter("description");
        String status = request.getParameter("status");

        ProductCategory c = new ProductCategory();
        c.setCategoryId(categoryId);
        c.setCategoryCode(code);
        c.setCategoryName(name);
        c.setDescription(desc);
        c.setStatus(status); // ACTIVE/INACTIVE

        dao.updateCategory(c);

        response.sendRedirect(request.getContextPath() + "/ProductCategory");
    }
}
