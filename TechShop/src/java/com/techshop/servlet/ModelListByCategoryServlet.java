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
@WebServlet(name = "ModelListByCategoryServlet", urlPatterns = "/model")
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

        // lấy categoryId 
        String raw = request.getParameter("categoryId");
        List<ProductModel> models;
        
        if(raw == null || raw.isEmpty()){
            models = dao.getAll();
            request.setAttribute("showCategory", true);
        }
        else{
            try{
                int categoryId = Integer.parseInt(raw);
                models = dao.getModelsByCategoryId(categoryId);
                request.setAttribute("categoryId", categoryId);
                request.setAttribute("showCategory", false);
            }
            catch (NumberFormatException e){
                response.sendRedirect(request.getContextPath() + "/category");
                return;
            }
        }

        request.setAttribute("models", models);

        // chuyển sang trang list model
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
