package com.techshop.servlet;

import com.techshop.dao.VariantDAO;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminVariantServlet", urlPatterns = {"/variant"})
public class AdminVariantServlet extends HttpServlet {

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
            // Get search and filter parameters
            String search = request.getParameter("search");
            String modelIdStr = request.getParameter("modelId");
            Integer modelId = null;
            
            if (modelIdStr != null && !modelIdStr.trim().isEmpty()) {
                try {
                    modelId = Integer.parseInt(modelIdStr);
                } catch (NumberFormatException e) {
                    // Invalid modelId, ignore
                }
            }
            
            // Get variants with search and filter
            List<ProductVariant> variants = variantDAO.getAllVariants(search, modelId);
            
            // Get all models for filter dropdown
            List<ProductModel> models = variantDAO.getAllActiveModels();
            
            request.setAttribute("variants", variants);
            request.setAttribute("models", models);
            request.setAttribute("searchValue", search != null ? search : "");
            request.setAttribute("selectedModelId", modelId);
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

