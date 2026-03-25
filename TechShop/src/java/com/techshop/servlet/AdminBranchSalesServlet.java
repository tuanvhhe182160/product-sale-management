package com.techshop.servlet;

import com.techshop.dao.AdminReportDAO;
import com.techshop.dao.BranchDAO;
import com.techshop.model.Branch;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Báo cáo Tổng quan & Doanh số theo Chi nhánh dành cho Admin.
 * URL: /admin/branch-report
 * * Đã gộp từ RevenueOverviewServlet và AdminBranchSalesServlet cũ.
 */
@WebServlet(name = "AdminBranchSalesServlet", urlPatterns = {"/admin/branch-report"})
public class AdminBranchSalesServlet extends HttpServlet {

    private final AdminReportDAO reportDAO = new AdminReportDAO();
    private final BranchDAO branchDAO      = new BranchDAO();
    private final Gson gson                = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        // 1. Xử lý ngày tháng (Mặc định: 30 ngày qua)
        String startDate = request.getParameter("startDate");
        String endDate   = request.getParameter("endDate");
        
        if (startDate == null || startDate.trim().isEmpty()) {
            startDate = LocalDate.now().minusDays(30).toString();
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        // 2. Lấy các tham số filter
        Integer branchId = parseIntOrNull(request.getParameter("branchId"));
        String action    = request.getParameter("action");

        // ==========================================
        // 3. XỬ LÝ API JSON CHO BIỂU ĐỒ (AJAX)
        // ==========================================
        if ("chart".equals(action)) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            // Trả về dữ liệu để vẽ biểu đồ doanh thu các chi nhánh
            List<Map<String, Object>> chartData = reportDAO.getBranchSalesChart(startDate, endDate);
            out.print(gson.toJson(chartData));
            out.flush();
            return; // Dừng tại đây, không load JSP
        }

        // ==========================================
        // 4. CHUẨN BỊ DỮ LIỆU BÁO CÁO CHO TRANG JSP
        // ==========================================
        
        // 4.1. Thống kê tổng quan
        Map<String, Object> overallStats = reportDAO.getBranchOverallStats(startDate, endDate);
        request.setAttribute("overallStats", overallStats);

        // 4.2. Danh sách doanh thu so sánh giữa các chi nhánh
        List<Map<String, Object>> branchSales = reportDAO.getBranchSalesChart(startDate, endDate);
        request.setAttribute("branchSales", branchSales);

        // (ĐÃ XÓA đoạn dailyRevenue ở đây)

        // 4.3 & 4.4. Xử lý khi chọn cụ thể 1 chi nhánh
        List<Branch> branches = branchDAO.getAllActive();
        if (branchId != null) {
            
            // Lấy doanh thu theo ngày (ĐÃ CHUYỂN VÀO ĐÂY ĐỂ TRÁNH LỖI NULL)
            List<Map<String, Object>> dailyRevenue = reportDAO.getBranchDailyRevenue(startDate, endDate, branchId);
            request.setAttribute("dailyRevenue", dailyRevenue);

            // Lấy danh sách nhân viên của chi nhánh đó
            List<Map<String, Object>> employeeDetail = reportDAO.getBranchEmployeeDetail(startDate, endDate, branchId);
            request.setAttribute("employeeDetail", employeeDetail);

            // Tìm và gắn tên chi nhánh để hiển thị ra UI
            branches.stream()
                    .filter(b -> b.getBranchId() == branchId)
                    .findFirst()
                    .ifPresent(b -> request.setAttribute("selectedBranchName", b.getBranchName()));
        }

        // ==========================================
        // 5. ĐẨY DỮ LIỆU RA VIEW (JSP)
        // ==========================================
        request.setAttribute("branches", branches);
        
        // Giữ trạng thái form
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("selectedBranchId", branchId);
        request.setAttribute("pageTitle", "Báo cáo Doanh thu & Chi nhánh - Admin");

        request.getRequestDispatcher("/views/Admin/admin-branch-sales.jsp").forward(request, response);
    }

    /**
     * Hàm tiện ích giúp parse String sang Integer an toàn
     */
    private static Integer parseIntOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { 
            return Integer.parseInt(s.trim()); 
        } catch (NumberFormatException e) { 
            return null; 
        }
    }
}