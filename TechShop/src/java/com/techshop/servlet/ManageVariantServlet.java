package com.techshop.servlet;

import com.techshop.dao.VariantDAO;
import com.techshop.dao.ProductCategoryDAO;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;
import com.techshop.model.ProductCategory;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "ManageVariantServlet", urlPatterns = {"/variant"})
public class ManageVariantServlet extends HttpServlet {

    private VariantDAO variantDAO;
    private ProductCategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        variantDAO = new VariantDAO();
        categoryDAO = new ProductCategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {

            /*
            =========================
            PARAMETERS
            =========================
            */
            String search = request.getParameter("search");

            String categoryStr = request.getParameter("categoryId");
            String modelIdStr = request.getParameter("modelId");

            Integer categoryId = null;
            Integer modelId = null;

            if (categoryStr != null && !categoryStr.isBlank()) {
                try {
                    categoryId = Integer.parseInt(categoryStr);
                } catch (NumberFormatException ignored) {}
            }

            if (modelIdStr != null && !modelIdStr.isBlank()) {
                try {
                    modelId = Integer.parseInt(modelIdStr);
                } catch (NumberFormatException ignored) {}
            }

            /*
            =========================
            DATA
            =========================
            */

            // variants list
            List<ProductVariant> variants =
                    variantDAO.getAllVariants(search, categoryId, modelId);

            // models for filter
            List<ProductModel> models =
                    variantDAO.getAllActiveModels();

            // categories for filter
            List<ProductCategory> categories =
                    categoryDAO.getActiveCategories();

            /*
            =========================
            ATTRIBUTES
            =========================
            */

            request.setAttribute("variants", variants);
            request.setAttribute("models", models);
            request.setAttribute("categories", categories);

            request.setAttribute("searchValue", search != null ? search : "");
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("selectedModelId", modelId);

            request.setAttribute("pageTitle", "Quản lý Variant");

            request.getRequestDispatcher("/views/variant/ManageVariant.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error",
                    "Có lỗi xảy ra: " + e.getMessage());

            request.getRequestDispatcher("/views/variant/ManageVariant.jsp")
                    .forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Manage Variant Servlet - List all variants";
    }
}