package com.techshop.servlet;

import com.techshop.dao.PhysicalProductDAO;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "PhysicalProductListServlet", urlPatterns = {"/inventory/list"})
public class PhysicalProductListServlet extends HttpServlet {

    private PhysicalProductDAO physicalProductDAO;

    @Override
    public void init() throws ServletException {
        physicalProductDAO = new PhysicalProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        Integer branchId = user.getBranchId();
        if (branchId == null) {
            request.setAttribute("error", "Your account is not assigned to any branch.");
            request.setAttribute("physicalProducts", Collections.emptyList());
            request.setAttribute("statusOptions", Collections.emptyList());
            request.setAttribute("variantOptions", Collections.emptyList());
        } else {
            String imei = trimToNull(request.getParameter("imei"));
            String status = trimToNull(request.getParameter("status"));
            Integer variantId = parseIntOrNull(request.getParameter("variantId"));
            LocalDate importDate = parseDateOrNull(request.getParameter("importDate"));
            final int PAGE_SIZE = 20;
            int page = parseIntOrDefault(request.getParameter("page"), 1);
            if (page < 1) page = 1;

            int totalCount = physicalProductDAO.countByBranchWithFilters(branchId, imei, status, variantId, importDate);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            if (page > totalPages && totalPages > 0) page = totalPages;

            int offset = (page - 1) * PAGE_SIZE;

            List<PhysicalProduct> physicalProducts = physicalProductDAO.getByBranchWithFilters(
                    branchId, imei, status, variantId, importDate, offset, PAGE_SIZE
            );
            List<String> statusOptions = physicalProductDAO.getDistinctStatusesByBranch(branchId);
            List<ProductVariant> variantOptions = physicalProductDAO.getVariantsByBranch(branchId);

            request.setAttribute("physicalProducts", physicalProducts);
            request.setAttribute("statusOptions", statusOptions);
            request.setAttribute("variantOptions", variantOptions);
            request.setAttribute("imei", imei != null ? imei : "");
            request.setAttribute("status", status != null ? status : "");
            request.setAttribute("selectedVariantId", variantId);
            request.setAttribute("importDate", importDate != null ? importDate.toString() : "");
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("pageSize", PAGE_SIZE);
        }

        request.setAttribute("pageTitle", "Physical Product List");
        request.getRequestDispatcher("/views/inventory/physicalProductList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

