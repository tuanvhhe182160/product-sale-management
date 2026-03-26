package com.techshop.servlet;

import com.techshop.dao.CashierCartDAO;
import com.techshop.dao.ProductDetailDAO;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.PhysicalProduct;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Trang chi tiết sản phẩm (Variant).
 * Hiển thị: thông tin variant, model, category, attributes,
 *           danh sách PhysicalProduct (IMEI) thuộc variant tại chi nhánh.
 * URL: /product-detail?variantId=5
 */
@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String param = request.getParameter("variantId");
        if (param == null || param.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int variantId;
        try {
            variantId = Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // Lấy branchId từ session
        HttpSession session = request.getSession(false);
        int branchId = 1;
        if (session != null && session.getAttribute("branchId") != null) {
            branchId = (int) session.getAttribute("branchId");
        }

        // 1. Thông tin variant (kèm model, category, brand) — dùng chung CashierCartDAO
        CashierSaleItem variant = new CashierCartDAO().getVariantForCart(variantId, branchId);
        if (variant == null) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        ProductDetailDAO dao = new ProductDetailDAO();

        // 2. Thông số kỹ thuật (VariantAttribute)
        Map<String, String> attributes = dao.getVariantAttributes(variantId);

        // 3. Danh sách PhysicalProduct thuộc variant tại chi nhánh
        List<PhysicalProduct> physicalProducts = dao.getPhysicalProductsByVariant(variantId, branchId);

        // Tham số quay lại
        String backKeyword    = request.getParameter("keyword")    != null ? request.getParameter("keyword")    : "";
        String backCategoryId = request.getParameter("categoryId") != null ? request.getParameter("categoryId") : "";
        String backModelId    = request.getParameter("modelId")    != null ? request.getParameter("modelId")    : "";
        String backSku        = request.getParameter("sku")        != null ? request.getParameter("sku")        : "";
        String backPage       = request.getParameter("page")       != null ? request.getParameter("page")       : "1";

        request.setAttribute("variant",          variant);
        request.setAttribute("attributes",       attributes);
        request.setAttribute("physicalProducts", physicalProducts);
        request.setAttribute("backKeyword",      backKeyword);
        request.setAttribute("backCategoryId",   backCategoryId);
        request.setAttribute("backModelId",      backModelId);
        request.setAttribute("backSku",          backSku);
        request.setAttribute("backPage",         backPage);

        request.getRequestDispatcher("/views/cashier/product-detail.jsp")
                .forward(request, response);
    }
}
