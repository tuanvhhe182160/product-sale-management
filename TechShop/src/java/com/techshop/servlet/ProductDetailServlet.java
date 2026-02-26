/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.ProductDetailDAO;
import com.techshop.model.CashierSaleItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Đọc physicalId
        String param = request.getParameter("physicalId");
        if (param == null || param.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int physicalId;
        try {
            physicalId = Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        ProductDetailDAO dao = new ProductDetailDAO();

        // 1. Thông tin chính
        CashierSaleItem item = dao.getPhysicalDetail(physicalId);
        if (item == null) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // 2. Thông số kỹ thuật
        Map<String, String> attributes = dao.getVariantAttributes(item.getVariantId());

        // 3. Lịch sử giao dịch kho
        List<Map<String, String>> inventoryHistory = dao.getInventoryHistory(physicalId);

        // 4. Lịch sử bảo hành
        List<Map<String, String>> warrantyHistory = dao.getWarrantyHistory(physicalId);

        // Tham số quay lại
        String backKeyword    = request.getParameter("keyword")    != null ? request.getParameter("keyword")    : "";
        String backCategoryId = request.getParameter("categoryId") != null ? request.getParameter("categoryId") : "";
        String backModelId    = request.getParameter("modelId")    != null ? request.getParameter("modelId")    : "";
        String backSku        = request.getParameter("sku")        != null ? request.getParameter("sku")        : "";
        String backPage       = request.getParameter("page")       != null ? request.getParameter("page")       : "1";

        request.setAttribute("item",             item);
        request.setAttribute("attributes",       attributes);
        request.setAttribute("inventoryHistory", inventoryHistory);
        request.setAttribute("warrantyHistory",  warrantyHistory);
        request.setAttribute("backKeyword",      backKeyword);
        request.setAttribute("backCategoryId",   backCategoryId);
        request.setAttribute("backModelId",      backModelId);
        request.setAttribute("backSku",          backSku);
        request.setAttribute("backPage",         backPage);

        request.getRequestDispatcher("/views/cashier/productDetail.jsp")
                .forward(request, response);
    }
}
