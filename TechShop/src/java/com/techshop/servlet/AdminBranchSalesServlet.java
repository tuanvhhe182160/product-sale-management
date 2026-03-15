package com.techshop.servlet;

import com.techshop.dao.AdminReportDAO;
import com.techshop.dao.BranchDAO;
import com.techshop.model.Branch;
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

@WebServlet("/admin/branch-report")
public class AdminBranchSalesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String branchIdStr = req.getParameter("branchId");
        String format = req.getParameter("format");

        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        AdminReportDAO dao = new AdminReportDAO();
        Integer branchId = (branchIdStr != null && !branchIdStr.isEmpty()) ? Integer.parseInt(branchIdStr) : null;

        // JSON API cho biểu đồ
        if ("json".equals(format)) {
            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            List<Map<String, Object>> chartData = dao.getBranchSalesChart(startDate, endDate);
            out.print(new Gson().toJson(chartData));
            out.flush();
            return;
        }

        // Thống kê tổng quan
        Map<String, Object> overallStats = dao.getBranchOverallStats(startDate, endDate);
        req.setAttribute("overallStats", overallStats);

        // Doanh thu theo từng chi nhánh
        List<Map<String, Object>> branchSales = dao.getBranchSalesChart(startDate, endDate);
        req.setAttribute("branchSales", branchSales);

        // Dropdown chi nhánh
        List<Branch> branchList = new BranchDAO().getAllActive();
        req.setAttribute("branchList", branchList);
        req.setAttribute("selectedBranchId", branchId);

        // Nếu đã chọn chi nhánh -> lấy chi tiết nhân viên + doanh thu theo ngày
        if (branchId != null) {
            List<Map<String, Object>> employeeDetail = dao.getBranchEmployeeDetail(startDate, endDate, branchId);
            req.setAttribute("employeeDetail", employeeDetail);

            List<Map<String, Object>> dailyRevenue = dao.getBranchDailyRevenue(startDate, endDate, branchId);
            req.setAttribute("dailyRevenue", dailyRevenue);

            // Tìm tên chi nhánh đã chọn
            for (Branch b : branchList) {
                if (b.getBranchId() == branchId) {
                    req.setAttribute("selectedBranchName", b.getBranchName());
                    break;
                }
            }
        }

        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.setAttribute("pageTitle", "Thống kê doanh số chi nhánh - Admin");

        req.getRequestDispatcher("/views/Admin/admin_branch_sales.jsp").forward(req, resp);
    }
}
