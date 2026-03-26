/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.VariantAttributeDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@WebServlet("/variant/form")
public class VariantFormServlet extends HttpServlet {

    private VariantDAO variantDAO;
    private VariantAttributeDAO attributeDAO;

    @Override
    public void init() {
        variantDAO = new VariantDAO();
        attributeDAO = new VariantAttributeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String idRaw = request.getParameter("id");

            List<ProductModel> models = variantDAO.getAllActiveModels();
            request.setAttribute("models", models);

            if (idRaw != null && !idRaw.isEmpty()) {

                int id = Integer.parseInt(idRaw);

                ProductVariant variant = variantDAO.getVariantById(id);

                if (variant == null) {
                    response.sendRedirect(request.getContextPath() + "/variant");
                    return;
                }

                request.setAttribute("variant", variant);

                // load attributes
                Map<String, String> attributes =
                        attributeDAO.getAttributesByVariantId(id);

                request.setAttribute("attributes", attributes);
            }

            request.getRequestDispatcher("/views/variant/variant-form.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/variant");
        }
    }
}

