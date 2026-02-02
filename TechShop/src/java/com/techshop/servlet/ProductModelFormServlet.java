/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.ProductModelDAO;
import com.techshop.model.ProductModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductModelFormServlet", urlPatterns = {"/ProductModel/form"})
public class ProductModelFormServlet extends HttpServlet {

    private final ProductModelDAO dao = new ProductModelDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryId;
        try {
            categoryId = Integer.parseInt(request.getParameter("categoryId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ProductCategory");
            return;
        }

        String idRaw = request.getParameter("id");
        ProductModel model;

        if (idRaw != null && !idRaw.trim().isEmpty()) {
            // EDIT mode
            int id;
            try {
                id = Integer.parseInt(idRaw);
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/ProductModel?categoryId=" + categoryId);
                return;
            }

            model = dao.getModelById(id);
            if (model == null) {
                response.sendRedirect(request.getContextPath() + "/ProductModel?categoryId=" + categoryId);
                return;
            }

            request.setAttribute("mode", "edit");
            request.setAttribute("pageTitle", "Edit Model - TechShop");
        } else {
            // CREATE mode
            model = new ProductModel();
            model.setModelId(0);
            model.setCategoryId(categoryId);
            model.setStatus("ACTIVE"); // default

            request.setAttribute("mode", "create");
            request.setAttribute("pageTitle", "Create Model - TechShop");
        }

        request.setAttribute("model", model);
        request.setAttribute("categoryId", categoryId);

        request.getRequestDispatcher("/views/Admin/adminModelForm.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int modelId = 0;
        int categoryId;

        try {
            categoryId = Integer.parseInt(request.getParameter("categoryId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ProductCategory");
            return;
        }

        try {
            modelId = Integer.parseInt(request.getParameter("modelId"));
        } catch (Exception ignore) {
            modelId = 0;
        }

        String code = request.getParameter("modelCode");
        String name = request.getParameter("modelName");
        String brand = request.getParameter("brand");
        String desc = request.getParameter("description");
        String status = request.getParameter("status");

        if (code != null) code = code.trim();
        if (name != null) name = name.trim();
        if (brand != null) brand = brand.trim();

        ProductModel m = new ProductModel();
        m.setModelId(modelId);
        m.setCategoryId(categoryId);
        m.setModelCode(code);
        m.setModelName(name);
        m.setBrand(brand);
        m.setDescription(desc);
        m.setStatus(status);

        if (modelId > 0) {
            dao.updateModel(m);
        } else {
            dao.insertModel(m);
        }

        response.sendRedirect(request.getContextPath() + "/ProductModel?categoryId=" + categoryId);
    }
}
