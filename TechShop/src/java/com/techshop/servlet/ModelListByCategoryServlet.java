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
import java.util.List;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ModelListByCategoryServlet", urlPatterns = "/ProductModel")
public class ModelListByCategoryServlet extends HttpServlet {

    ProductModelDAO dao = new ProductModelDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ModelListByCategoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ModelListByCategoryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String rawCategoryId = request.getParameter("categoryId");
        int categoryId;

        try {
            categoryId = Integer.parseInt(rawCategoryId);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ProductCategory");
            return;
        }

        String q = request.getParameter("q");             
        String status = request.getParameter("status");   

        if (q != null) {
            q = q.trim();
        }
        if (status == null || status.trim().isEmpty()) {
            status = "ALL";
        }
        status = status.trim().toUpperCase();

        List<ProductModel> models = dao.searchModels(categoryId, q, status);

        int active = 0, inactive = 0;
        java.util.Set<String> brands = new java.util.HashSet<>();

        for (ProductModel m : models) {
            if ("ACTIVE".equalsIgnoreCase(m.getStatus())) {
                active++;
            }
            if ("INACTIVE".equalsIgnoreCase(m.getStatus())) {
                inactive++;
            }
            if (m.getBrand() != null && !m.getBrand().trim().isEmpty()) {
                brands.add(m.getBrand().trim());
            }
        }

        request.setAttribute("models", models);
        request.setAttribute("categoryId", categoryId);

        request.setAttribute("activeCount", active);
        request.setAttribute("inactiveCount", inactive);
        request.setAttribute("brandCount", brands.size());

        request.setAttribute("q", q);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/views/Admin/adminListModel.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "List all ProductModel by categoryId";
    }
}
