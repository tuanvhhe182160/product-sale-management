package com.techshop.servlet;

import com.techshop.dao.StockCheckDAO;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * URL: /stock-check?variantId=5
 * Tránh conflict với /cashier servlet khi dùng sub-path
 */
@WebServlet("/stock-check")
public class StockCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Lấy branchId từ session
        HttpSession session = request.getSession(false);
        int branchId = 1;
        if (session != null && session.getAttribute("branchId") != null) {
            branchId = (int) session.getAttribute("branchId");
        }

        // Đọc variantId
        String variantIdParam = request.getParameter("variantId");
        if (variantIdParam == null || variantIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int variantId;
        try {
            variantId = Integer.parseInt(variantIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // Filter status: IN_STOCK / SOLD / RESERVED / DEFECTIVE / ALL
        String statusFilter = request.getParameter("statusFilter");
        if (statusFilter == null || statusFilter.trim().isEmpty()) {
            statusFilter = "ALL";
        }

        StockCheckDAO dao = new StockCheckDAO();

        ProductVariant variant = dao.getVariantDetail(variantId, branchId);
        if (variant == null) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        List<PhysicalProduct> physicalList = dao.getPhysicalList(variantId, branchId, statusFilter);

        int cntInStock   = dao.countByStatus(variantId, branchId, "IN_STOCK");
        int cntSold      = dao.countByStatus(variantId, branchId, "SOLD");
        int cntReserved  = dao.countByStatus(variantId, branchId, "RESERVED");
        int cntDefective = dao.countByStatus(variantId, branchId, "DEFECTIVE");

        // Giữ params để nút Quay lại trả về đúng trang search cũ
        String backKeyword    = request.getParameter("keyword")    != null ? request.getParameter("keyword")    : "";
        String backCategoryId = request.getParameter("categoryId") != null ? request.getParameter("categoryId") : "";
        String backModelId    = request.getParameter("modelId")    != null ? request.getParameter("modelId")    : "";
        String backSku        = request.getParameter("sku")        != null ? request.getParameter("sku")        : "";
        String backPage       = request.getParameter("page")       != null ? request.getParameter("page")       : "1";

        request.setAttribute("variant",       variant);
        request.setAttribute("physicalList",  physicalList);
        request.setAttribute("statusFilter",  statusFilter);
        request.setAttribute("cntInStock",    cntInStock);
        request.setAttribute("cntSold",       cntSold);
        request.setAttribute("cntReserved",   cntReserved);
        request.setAttribute("cntDefective",  cntDefective);
        request.setAttribute("backKeyword",    backKeyword);
        request.setAttribute("backCategoryId", backCategoryId);
        request.setAttribute("backModelId",    backModelId);
        request.setAttribute("backSku",        backSku);
        request.setAttribute("backPage",       backPage);

        request.getRequestDispatcher("/views/cashier/stockCheck.jsp")
                .forward(request, response);
    }
}