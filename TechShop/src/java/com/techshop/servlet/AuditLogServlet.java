/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;

import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.SystemLog;
import com.techshop.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author justi
 */
@WebServlet(name="AuditLogServlet", urlPatterns={"/admin/audit-logs"})
public class AuditLogServlet extends HttpServlet {
   private SystemLogDAO logDAO = new SystemLogDAO();
   
   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Nhận các tham số lọc từ UI
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String actionFilter = request.getParameter("actionFilter");
        String searchKeyword = request.getParameter("searchKeyword");
        
        // 2. Lấy dữ liệu từ DAO
        int page = 1;
        int pageSize = 20;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        int offset = (page - 1) * pageSize;
        List<SystemLog> logs = logDAO.getLogsWithFilters(
            startDate, endDate, actionFilter, searchKeyword, offset, pageSize);

        int totalLogs = logDAO.countLogsWithFilters(startDate, endDate, actionFilter, searchKeyword);
        int totalPages = (int) Math.ceil((double) totalLogs / pageSize);

        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalLogs", totalLogs);
        
        // 3. Xử lý xuất file CSV nếu Admin yêu cầu (Use Case: Export Audit Logs)
        String action = request.getParameter("action");
        if ("export".equals(action)) {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            Integer userId = (user != null) ? user.getUserId() : null;

            logDAO.logAction(
                userId, 
                LogAction.EXPORT_AUDIT_LOGS, 
                EntityType.SYSTEM, 
                null, 
                request.getRemoteAddr(), 
                "Xuất file CSV nhật ký hệ thống (Audit Logs)"
            );
            exportToCSV(response, logs);
            return;
        }

        // 4. Lấy danh sách Actions để tạo Filter Dropdown
        List<String> actionList = logDAO.getUniqueActions();

        // 5. Trả dữ liệu về JSP
        request.setAttribute("logs", logs);
        request.setAttribute("actionList", actionList);
        
        // Giữ lại trạng thái lọc trên giao diện
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("actionFilter", actionFilter);
        request.setAttribute("searchKeyword", searchKeyword);
        
        request.getRequestDispatcher("/views/Admin/audit-log.jsp").forward(request, response);
    }

    private void exportToCSV(HttpServletResponse response, List<SystemLog> logs) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Audit_Logs_" + LocalDate.now() + ".csv\"");
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}); // BOM for Excel

        try (PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(response.getOutputStream(), "UTF-8"))) {
            out.println("ID,Thời Gian,Người Thực Hiện,Hành Động,Đối Tượng,ID Đối Tượng,Địa Chỉ IP,Chi Tiết");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (SystemLog log : logs) {
                String dateStr = log.getCreatedAt() != null ? log.getCreatedAt().format(formatter) : "";
                String userName = log.getUserName() != null ? log.getUserName() : "Hệ thống";
                String entityId = log.getEntityId() != null ? String.valueOf(log.getEntityId()) : "";
                
                // Chuẩn hóa chuỗi details để tránh lỗi CSV nếu có dấu phẩy hoặc xuống dòng
                String details = log.getDetails() != null ? log.getDetails().replace("\"", "\"\"").replace("\n", " ") : "";
                
                out.printf("%d,%s,%s,%s,%s,%s,%s,\"%s\"\n",
                        log.getLogId(),
                        dateStr,
                        userName,
                        log.getAction(),
                        log.getEntityType(),
                        entityId,
                        log.getIpAddress(),
                        details);
            }
            out.flush();
        }
    }
}
