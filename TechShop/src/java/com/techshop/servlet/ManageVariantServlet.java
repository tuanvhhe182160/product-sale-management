package com.techshop.servlet;

import com.techshop.dao.VariantDAO;
import com.techshop.model.ProductVariant;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageVariantServlet", urlPatterns = {"/variant"})
public class ManageVariantServlet extends HttpServlet {

    private VariantDAO variantDAO;

    @Override
    public void init() throws ServletException {
        variantDAO = new VariantDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            List<ProductVariant> variants = variantDAO.getAllVariants();
            request.setAttribute("variants", variants);
            request.setAttribute("pageTitle", "Quản lý Variant");
            request.getRequestDispatcher("/views/variant/ManageVariant.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/views/variant/ManageVariant.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Manage Variant Servlet - List all variants";
    }
}

