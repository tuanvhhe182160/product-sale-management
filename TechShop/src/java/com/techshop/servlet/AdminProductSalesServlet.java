package com.techshop.servlet;

import com.techshop.dao.AdminReportDAO;
import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.ProductModelDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;

import com.google.gson.Gson;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Báo cáo doanh số theo Sản phẩm dành cho Admin.
 * URL: /admin/product-report
 *
 * Tách hoàn toàn khỏi AdminSaleReportServlet để có filter chuyên biệt:
 * Category → Model → Variant cascade.
 */
@WebServlet(name = "AdminProductSalesServlet", urlPatterns = {"/admin/product-report"})
public class AdminProductSalesServlet extends HttpServlet {

    private final AdminReportDAO    reportDAO    = new AdminReportDAO();
    private final ProductCategoryDAO catDAO      = new ProductCategoryDAO();
    private final ProductModelDAO   modelDAO     = new ProductModelDAO();
    private final VariantDAO        variantDAO   = new VariantDAO();
    private final SystemLogDAO      logDAO       = new SystemLogDAO();
    private final Gson              gson         = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Date range (default: last 30 days)
        String startDate = request.getParameter("startDate");
        String endDate   = request.getParameter("endDate");
        if (startDate == null || startDate.trim().isEmpty()) {
            startDate = LocalDate.now().minusDays(30).toString();
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        // Cascade filters
        Integer categoryId = parseIntOrNull(request.getParameter("categoryId"));
        Integer modelId    = parseIntOrNull(request.getParameter("modelId"));
        Integer variantId  = parseIntOrNull(request.getParameter("variantId"));

        String action = request.getParameter("action");

        // JSON API for dynamic model/variant dropdowns (AJAX)
        if ("models".equals(action) && categoryId != null) {
            response.setContentType("application/json;charset=UTF-8");
            List<ProductModel> models = modelDAO.getActiveModelsByCategoryId(categoryId);
            List<Map<String, Object>> jsonList = new ArrayList<>();
            for (var m : models) {
                Map<String, Object> map = new HashMap<>();
                map.put("modelId", m.getModelId());    
                map.put("modelName", m.getModelName());
                jsonList.add(map);
            }
            response.getWriter().print(gson.toJson(jsonList));
            return;
        }
        if ("variants".equals(action) && modelId != null) {
            response.setContentType("application/json;charset=UTF-8");
            List<ProductVariant> variants = variantDAO.getVariantsByModelId(modelId);
            List<Map<String, Object>> jsonList = new ArrayList<>();
            for (var v : variants) {
                Map<String, Object> map = new HashMap<>();
                map.put("variantId", v.getVariantId());
                map.put("variantName", v.getVariantName());
                jsonList.add(map);
            }           
            response.getWriter().print(gson.toJson(jsonList));
            return;
        }

        // Report data
        List<Map<String, Object>> data = reportDAO.getSalesByProduct(
                startDate, endDate, categoryId, modelId, variantId);

        // Export CSV
        if ("export".equals(action)) {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");
            logDAO.logAction(
                user.getUserId(), LogAction.EXPORT_FINANCIAL_REPORT,
                EntityType.REPORT, null, request.getRemoteAddr(),
                "Xuất báo cáo doanh số sản phẩm từ " + startDate + " đến " + endDate
            );
            exportCSV(response, data, startDate, endDate);
            return;
        }

        // Populate dropdown lists
        request.setAttribute("categories",   catDAO.getActiveCategories());
        request.setAttribute("models",       categoryId != null
            ? modelDAO.getActiveModelsByCategoryId(categoryId)
            : modelDAO.getAll());
        request.setAttribute("variants",     modelId != null
            ? variantDAO.getVariantsByModelId(modelId)
            : List.of());

        // Selected filter values (for keeping form state)
        request.setAttribute("startDate",        startDate);
        request.setAttribute("endDate",          endDate);
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("selectedModel",    modelId);
        request.setAttribute("selectedVariant",  variantId);
        request.setAttribute("reportData",       data);
        request.setAttribute("pageTitle",        "Báo cáo Doanh số Sản phẩm - Admin");

        request.getRequestDispatcher("/views/Admin/admin_product_sales.jsp")
               .forward(request, response);
    }

    private void exportCSV(HttpServletResponse response,
                           List<Map<String, Object>> data,
                           String start, String end) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"ProductSales_" + start + "_" + end + ".csv\"");
        response.getOutputStream().write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});

        try (PrintWriter pw = new PrintWriter(
                new java.io.OutputStreamWriter(response.getOutputStream(), "UTF-8"))) {
            pw.println("Danh mục,Model,Biến thể,Số lượng,Doanh thu");
            for (Map<String, Object> row : data) {
                String line = esc(row.get("category_name")) + "," +
                              esc(row.get("model_name"))    + "," +
                              esc(row.get("variant_name"))  + "," +
                              row.get("total_qty")          + "," +
                              row.get("total_sales");
                pw.println(line);
            }
            pw.flush();
        }
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    private static String esc(Object v) {
        if (v == null) return "";
        String s = v.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"","\"\"") + "\"";
        return s;
    }
}