package com.techshop.servlet;

import com.google.gson.Gson;
import com.techshop.dao.AdminReportDAO;
import com.techshop.dao.BranchDAO;
import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.ProductModelDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.UserDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 *
 * @author justi
 */
@WebServlet(name="AdminSaleReportServlet", urlPatterns={"/admin/sales-report"})
public class AdminSaleReportServlet extends HttpServlet {
   private final AdminReportDAO reportDAO = new AdminReportDAO();
   private final ProductCategoryDAO productCategoryDAO = new ProductCategoryDAO();
   private final ProductModelDAO productModelDAO = new ProductModelDAO();
   private final VariantDAO variantDAO = new VariantDAO();
   private final BranchDAO branchDAO = new BranchDAO();
   private final UserDAO userDAO = new UserDAO();
   private final SystemLogDAO systemLogDAO = new SystemLogDAO();
   private final Gson gson = new Gson();
   
   protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String reportType = request.getParameter("reportType"); // "product" hoặc "branch"
        String action = request.getParameter("action"); // "export" hoặc "view"

        // Mặc định 30 ngày gần nhất
        if (startDate == null || endDate == null) {
            startDate = java.time.LocalDate.now().minusDays(30).toString();
            endDate = java.time.LocalDate.now().toString();
        }

        // 1. LẤY CÁC THAM SỐ LỌC
        Integer categoryId = parseInteger(request.getParameter("categoryId"));
        Integer modelId = parseInteger(request.getParameter("modelId"));
        Integer variantId = parseInteger(request.getParameter("variantId"));
        Integer branchId = parseInteger(request.getParameter("branchId"));
        Integer employeeId = parseInteger(request.getParameter("employeeId"));
    
        // Set lại cho JSP để hiển thị đúng lựa chọn 
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedModelId", modelId);
        request.setAttribute("selectedVariantId", variantId);
        request.setAttribute("selectedBranchId", branchId);
        request.setAttribute("selectedEmployeeId", employeeId);

        if (reportType == null) {
            reportType = "product";
        }
    
        // 2. LẤY DATA BÁO CÁO (BẢNG)
        List<Map<String, Object>> data = null;
        if ("branch".equals(reportType)) {
            data = reportDAO.getSalesByBranch(startDate, endDate, branchId, employeeId);
        } else {
            data = reportDAO.getSalesByProduct(startDate, endDate, categoryId, modelId, variantId);
        }

        // 3. EXPORT, CHART 
        if ("export".equals(action)) {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            Integer userId = (user != null) ? user.getUserId() : null;
            
            // Tạo nội dung chi tiết cho log
            String reportName = "branch".equals(reportType) ? "Chi nhánh" : "Sản phẩm";
            String details = "Xuất báo cáo doanh thu theo " + reportName + " từ " + startDate + " đến " + endDate;

            // Ghi log
            systemLogDAO.logAction(
                userId, 
                LogAction.EXPORT_FINANCIAL_REPORT, // Hoặc bạn có thể thêm EXPORT_SALES_REPORT vào Enum
                EntityType.REPORT, 
                null, 
                request.getRemoteAddr(), 
                details
            );
            exportToCSV(response, data, reportType);
            return;
        }
        String format = request.getParameter("format");
        if ("json".equals(format)) {
            List<Map<String, Object>> chartData = reportDAO.getTopProductChart(startDate, endDate);
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(chartData));
            return;
        }

        // 4. LẤY DATA CHO CÁC DROPDOWN (LỌC ĐỘNG)
        request.setAttribute("categoryList", productCategoryDAO.getAllCategories());
        request.setAttribute("branchList", branchDAO.getAll());

        // Nếu chọn Category -> Lọc Model theo Category. Ngược lại -> Lấy tất cả
        if (categoryId != null) {
            request.setAttribute("modelList", productModelDAO.getModelsByCategoryId(categoryId));
        } else {
            request.setAttribute("modelList", productModelDAO.getAll());
        }

        // Nếu chọn Model -> Lọc Variant theo Model. Ngược lại -> Lấy tất cả
        if (modelId != null) {
            request.setAttribute("variantList", variantDAO.getVariantsByModelId(modelId));
        } else {
            request.setAttribute("variantList", variantDAO.getAllVariants());
        }

        // Nếu chọn Chi nhánh -> Lọc Nhân viên theo Chi nhánh. Ngược lại -> Lấy tất cả
        if (branchId != null) {
            request.setAttribute("employeeList", userDAO.getCashiersByBranchId(branchId));
        } else {
            request.setAttribute("employeeList", userDAO.getAllCashier());
        }

        // Gửi data sang JSP
        request.setAttribute("reportData", data);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("reportType", reportType);
    
        request.getRequestDispatcher("/views/Admin/sales-report.jsp").forward(request, response);
    }

    private void exportToCSV(HttpServletResponse response, List<Map<String, Object>> data, String type) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=SalesReport_" + type + ".csv");
        response.setCharacterEncoding("UTF-8");
        
        // Viết BOM cho Excel nhận diện UTF-8
        response.getWriter().write('\ufeff');
        
        if (!data.isEmpty()) {
            // Viết Header
            StringJoiner sjHeader = new StringJoiner(",");
            data.get(0).keySet().forEach(sjHeader::add);
            response.getWriter().println(sjHeader.toString());

            // Viết Data
            for (Map<String, Object> row : data) {
                StringJoiner sjRow = new StringJoiner(",");
                row.values().forEach(v -> sjRow.add(v.toString()));
                response.getWriter().println(sjRow.toString());
            }
        }
    }
    
    //Helper
    private Integer parseInteger(String value) {
        try {
            return (value == null || value.isEmpty()) ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
