/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.ProductCategory;
import com.techshop.model.User;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name="ProductCategoryEditServlet", urlPatterns="/category/edit")

public class ProductCategoryEditServlet extends HttpServlet {

    ProductCategoryDAO dao = new ProductCategoryDAO();
    SystemLogDAO logDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id;
        try {
            id = Integer.parseInt(request.getParameter("categoryId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/category");
            return;
        }

        ProductCategory category = dao.getById(id);
        if (category == null) {
            response.sendRedirect(request.getContextPath() + "/category");
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
            response.sendRedirect(request.getContextPath() + "/category");
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
        // --- GHI LOG ---
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer userId = (user != null) ? user.getUserId() : null;

        logDAO.logAction(
            userId, 
            LogAction.UPDATE_CATEGORY,
            EntityType.CATEGORY, 
            categoryId, 
            request.getRemoteAddr(), 
            "Cập nhật thông tin danh mục: " + name + " (Mã: " + code + ")"
        );
        // -----------------------

        response.sendRedirect(request.getContextPath() + "/category");
    }
}
