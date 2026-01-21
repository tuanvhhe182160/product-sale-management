/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.ProductModelDAO;
import com.techshop.model.ProductModel;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */


@WebServlet(name = "ProductModelEditServlet", urlPatterns = "/model/edit")
public class ProductModelEditServlet extends HttpServlet {

    ProductModelDAO dao = new ProductModelDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/category");
            return;
        }

        ProductModel model = dao.getModelById(id);
        if (model == null) {
            response.sendRedirect(request.getContextPath() + "/category");
            return;
        }

        request.setAttribute("model", model);
        request.getRequestDispatcher("/views/Admin/adminEditModel.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int modelId = Integer.parseInt(request.getParameter("modelId"));

        String code = request.getParameter("modelCode").trim();
        String name = request.getParameter("modelName").trim();
        String brand = request.getParameter("brand");
        String desc = request.getParameter("description");
        String status = request.getParameter("status");

        ProductModel m = new ProductModel();
        m.setModelId(modelId);
        m.setModelCode(code);
        m.setModelName(name);
        m.setBrand(brand);
        m.setDescription(desc);
        m.setStatus(status);

        dao.updateModel(m);

        response.sendRedirect(request.getContextPath() + "/category?categoryId="
                + request.getParameter("categoryId"));
    }
}

