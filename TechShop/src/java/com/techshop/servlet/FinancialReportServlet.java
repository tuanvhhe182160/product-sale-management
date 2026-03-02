package com.techshop.servlet;

import com.techshop.dao.ReportDAO;
import com.techshop.model.FinancialReportItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "FinancialReportServlet", urlPatterns = {"/report/financial"})
public class FinancialReportServlet extends HttpServlet {

    private ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy ngày lọc từ URL (nếu không có thì mặc định lấy 30 ngày gần nhất)
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().minusDays(30).toString();
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        // Lấy dữ liệu từ DAO
        List<FinancialReportItem> reportData = reportDAO.getFinancialReport(startDate, endDate);

        // 2. KIỂM TRA NẾU NGƯỜI DÙNG BẤM NÚT "EXPORT CSV"
        String action = request.getParameter("action");
        if ("export".equals(action)) {
            exportToCSV(response, reportData, startDate, endDate);
            return; // Trả file xong thì dừng, không chuyển sang JSP nữa
        }

        // 3. Nếu không phải export, đẩy dữ liệu ra JSP hiển thị
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("reportData", reportData);
        request.getRequestDispatcher("/views/accounting/financial-report.jsp").forward(request, response);
    }

    // Hàm xuất file CSV siêu mượt
    private void exportToCSV(HttpServletResponse response, List<FinancialReportItem> data, String start, String end) 
            throws IOException {
        
        // Set Header để trình duyệt hiểu đây là file download
        response.setContentType("text/csv; charset=UTF-8");
        String fileName = "Financial_Report_" + start + "_to_" + end + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Ghi Byte Order Mark (BOM) để Excel nhận diện đúng tiếng Việt UTF-8
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        try (PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(response.getOutputStream(), "UTF-8"))) {
            // In dòng tiêu đề cột
            out.println("Ngày,Số đơn hàng,Doanh thu (VNĐ),Chi phí (VNĐ),Lợi nhuận (VNĐ)");

            // Lặp qua danh sách và in từng dòng dữ liệu
            for (FinancialReportItem item : data) {
                out.printf("%s,%d,%.0f,%.0f,%.0f\n",
                        item.getPeriod(),
                        item.getTotalOrders(),
                        item.getTotalRevenue(),
                        item.getTotalCost(),
                        item.getTotalProfit());
            }
            out.flush();
        }
    }
}