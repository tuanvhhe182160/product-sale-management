package com.techshop.servlet;

import com.techshop.dao.AdminReportDAO;
import com.techshop.dao.BranchDAO;
import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.UserDAO;
import com.techshop.model.Branch;
import com.techshop.model.ProductCategory;
import com.techshop.model.User;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/sales-report")
public class AdminProductSalesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String reportType = req.getParameter("reportType");
        String format = req.getParameter("format");
        String action = req.getParameter("action");

        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (reportType == null || reportType.isEmpty()) {
            reportType = "product";
        }

        AdminReportDAO dao = new AdminReportDAO();

        // JSON format for dashboard chart
        if ("json".equals(format)) {
            resp.setContentType("application/json;charset=UTF-8");
            List<Map<String, Object>> topProducts = dao.getTopProductChart(startDate, endDate);
            PrintWriter out = resp.getWriter();
            out.print(new Gson().toJson(topProducts));
            out.flush();
            return;
        }

        List<Map<String, Object>> reportData;

        if ("branch".equals(reportType)) {
            String branchIdStr = req.getParameter("branchId");
            String employeeIdStr = req.getParameter("employeeId");
            Integer branchId = (branchIdStr != null && !branchIdStr.isEmpty()) ? Integer.parseInt(branchIdStr) : null;
            Integer employeeId = (employeeIdStr != null && !employeeIdStr.isEmpty()) ? Integer.parseInt(employeeIdStr) : null;

            reportData = dao.getSalesByBranch(startDate, endDate, branchId, employeeId);

            List<Branch> branchList = new BranchDAO().getAllActive();
            req.setAttribute("branchList", branchList);
            req.setAttribute("selectedBranchId", branchId);
            req.setAttribute("selectedEmployeeId", employeeId);

            if (branchId != null) {
                List<User> employees = new UserDAO().getAllByBranch(branchId);
                req.setAttribute("employeeList", employees);
            }
        } else {
            String categoryIdStr = req.getParameter("categoryId");
            String modelIdStr = req.getParameter("modelId");
            String variantIdStr = req.getParameter("variantId");
            Integer categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty()) ? Integer.parseInt(categoryIdStr) : null;
            Integer modelId = (modelIdStr != null && !modelIdStr.isEmpty()) ? Integer.parseInt(modelIdStr) : null;
            Integer variantId = (variantIdStr != null && !variantIdStr.isEmpty()) ? Integer.parseInt(variantIdStr) : null;

            reportData = dao.getSalesByProduct(startDate, endDate, categoryId, modelId, variantId);

            List<ProductCategory> categoryList = new ProductCategoryDAO().getAll();
            req.setAttribute("categoryList", categoryList);
            req.setAttribute("selectedCategoryId", categoryId);
            req.setAttribute("selectedModelId", modelId);
            req.setAttribute("selectedVariantId", variantId);
        }

        // CSV export
        if ("export".equals(action)) {
            exportCsv(resp, reportData, reportType);
            return;
        }

        req.setAttribute("reportData", reportData);
        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.setAttribute("reportType", reportType);
        req.setAttribute("pageTitle", "Báo cáo doanh số - Admin");

        req.getRequestDispatcher("/views/Admin/admin_product_sales.jsp").forward(req, resp);
    }

    private void exportCsv(HttpServletResponse resp, List<Map<String, Object>> data, String type) throws IOException {
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=sales_report.csv");
        PrintWriter out = resp.getWriter();
        out.write('\ufeff'); // BOM for Excel

        if ("branch".equals(type)) {
            out.println("Branch/Employee,Orders,Revenue");
            for (Map<String, Object> row : data) {
                String name = row.containsKey("branch_name") ? String.valueOf(row.get("branch_name")) : String.valueOf(row.get("employee_name"));
                out.println(name + "," + row.get("total_orders") + "," + row.get("total_sales"));
            }
        } else {
            out.println("Category,Model,Variant,Quantity,Revenue");
            for (Map<String, Object> row : data) {
                out.println(row.get("category_name") + "," + row.get("model_name") + "," +
                        row.get("variant_name") + "," + row.get("total_qty") + "," + row.get("total_sales"));
            }
        }
        out.flush();
    }
}
