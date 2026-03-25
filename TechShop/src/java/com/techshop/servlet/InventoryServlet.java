package com.techshop.servlet;

import com.techshop.dao.VariantDAO;
import com.techshop.model.ProductVariant;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "InventoryServlet", urlPatterns = {"/inventory/import"})
public class InventoryServlet extends HttpServlet {

    private VariantDAO variantDAO;

    @Override
    public void init() throws ServletException {
        variantDAO = new VariantDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            request.setAttribute("error", "Your account is not assigned to any branch.");
            request.setAttribute("variants", Collections.emptyList());
        } else {
            List<ProductVariant> variants = variantDAO.getAllActive();
            request.setAttribute("variants", variants);
        }

        request.setAttribute("pageTitle", "Import Products");
        request.getRequestDispatcher("/views/inventory/import-step1.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        String variantIdParam = request.getParameter("variantId");
        String quantityParam  = request.getParameter("quantity");

        // Validate variantId
        int variantId;
        try {
            variantId = Integer.parseInt(variantIdParam);
            if (variantId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please select a valid product variant.");
            request.setAttribute("variants", variantDAO.getAllActive());
            request.setAttribute("pageTitle", "Import Products");
            request.getRequestDispatcher("/views/inventory/import-step1.jsp").forward(request, response);
            return;
        }

        // Validate quantity
        int quantity;
        try {
            quantity = Integer.parseInt(quantityParam);
            if (quantity < 1 || quantity > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Quantity must be between 1 and 100.");
            request.setAttribute("variants", variantDAO.getAllActive());
            request.setAttribute("selectedVariantId", variantId);
            request.setAttribute("pageTitle", "Import Products");
            request.getRequestDispatcher("/views/inventory/import-step1.jsp").forward(request, response);
            return;
        }

        // Validate variant exists and is active
        ProductVariant variant = variantDAO.getVariantById(variantId);
        if (variant == null || !"ACTIVE".equals(variant.getStatus())) {
            request.setAttribute("error", "Selected variant is not valid.");
            request.setAttribute("variants", variantDAO.getAllActive());
            request.setAttribute("pageTitle", "Import Products");
            request.getRequestDispatcher("/views/inventory/import-step1.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/inventory/import/imei?variantId=" + variantId + "&quantity=" + quantity);
    }
}
