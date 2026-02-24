package com.techshop.servlet;

import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.ProductModelDAO;
import com.techshop.dao.ProductSearchDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.ProductCategory;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/cashier")
public class CashierServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Lấy branchId từ session (tạm hardcode = 1 khi chưa có login)
        HttpSession session = request.getSession(false);
        int branchId = 1;
        if (session != null && session.getAttribute("branchId") != null) {
            branchId = (int) session.getAttribute("branchId");
        }

        // Đọc tham số tìm kiếm
        String keyword    = emptyIfNull(request.getParameter("keyword"));
        String categoryId = emptyIfNull(request.getParameter("categoryId"));
        String modelId    = emptyIfNull(request.getParameter("modelId"));
        String sku        = emptyIfNull(request.getParameter("sku"));

        int page = 1;
        try {
            String p = request.getParameter("page");
            if (p != null && !p.isEmpty()) page = Integer.parseInt(p);
            if (page < 1) page = 1;
        } catch (NumberFormatException ignored) { }

        // Dữ liệu dropdown categories
        ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
        List<ProductCategory> categories = categoryDAO.getActiveCategories();

        // Dropdown models: lọc theo category nếu có, không thì lấy hết
        ProductModelDAO modelDAO = new ProductModelDAO();
        List<ProductModel> models;

        if (!categoryId.isEmpty()) {
            models = modelDAO.getActiveModelsByCategoryId(Integer.parseInt(categoryId));

            // Validate modelId có thuộc category này không, nếu không thì reset
            if (!modelId.isEmpty()) {
                boolean belongs = false;
                for (ProductModel m : models) {
                    if (String.valueOf(m.getModelId()).equals(modelId)) {
                        belongs = true;
                        break;
                    }
                }
                if (!belongs) {
                    modelId = "";
                }
            }
        } else {
            VariantDAO variantDAO = new VariantDAO();
            models = variantDAO.getAllActiveModels();
        }

        // Tìm kiếm & phân trang (dùng modelId đã được validate)
        ProductSearchDAO searchDAO = new ProductSearchDAO();

        List<ProductVariant> list = searchDAO.searchForCashier(
                keyword, categoryId, modelId, sku, branchId, page, PAGE_SIZE);

        int totalItems = searchDAO.countForCashier(
                keyword, categoryId, modelId, sku, branchId);

        int totalPages = (int) Math.ceil(totalItems * 1.0 / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // Pass sang JSP
        request.setAttribute("list",        list);
        request.setAttribute("totalPages",  totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalItems",  totalItems);
        request.setAttribute("keyword",     keyword);
        request.setAttribute("categoryId",  categoryId);
        request.setAttribute("modelId",     modelId);
        request.setAttribute("sku",         sku);
        request.setAttribute("categories",  categories);
        request.setAttribute("models",      models);

        request.getRequestDispatcher("/views/cashier/cashier.jsp")
                .forward(request, response);
    }

    private String emptyIfNull(String s) {
        return s == null ? "" : s.trim();
    }
}