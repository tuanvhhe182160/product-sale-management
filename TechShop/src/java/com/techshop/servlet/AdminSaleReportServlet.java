package com.techshop.servlet;

import com.google.gson.Gson;
import com.techshop.dao.AdminReportDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
   private final Gson gson = new Gson();
   
   protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String reportType = request.getParameter("reportType"); // "product" hoặc "branch"
        String action = request.getParameter("action"); // "export" hoặc "view"

        // Mặc định 7 ngày gần nhất
        if (startDate == null || endDate == null) {
            startDate = java.time.LocalDate.now().minusDays(7).toString();
            endDate = java.time.LocalDate.now().toString();
        }

        List<Map<String, Object>> data;
        if ("branch".equals(reportType)) {
            data = reportDAO.getSalesByBranch(startDate, endDate);
        } else {
            data = reportDAO.getSalesByProduct(startDate, endDate);
        }

        if ("export".equals(action)) {
            exportToCSV(response, data, reportType);
            return;
        }
        
        String format = request.getParameter("format");
        if ("json".equals(format)) {
            List<Map<String, Object>> chartData = reportDAO.getTopProductChart(startDate, endDate);
            response.setContentType("application/json");
            response.getWriter().write(new com.google.gson.Gson().toJson(chartData));
            return;
        }

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
}
