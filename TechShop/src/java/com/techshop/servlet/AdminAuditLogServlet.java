package com.techshop.servlet;

import com.techshop.dao.SystemLogDAO;
import com.techshop.model.SystemLog;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/audit-logs")
public class AdminAuditLogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String actionFilter = req.getParameter("actionFilter");
        String searchKeyword = req.getParameter("searchKeyword");
        String action = req.getParameter("action");

        SystemLogDAO dao = new SystemLogDAO();

        // CSV export
        if ("export".equals(action)) {
            List<SystemLog> logs = dao.getLogsWithFilters(startDate, endDate, actionFilter, searchKeyword);
            exportCsv(resp, logs);
            return;
        }

        List<SystemLog> logs = dao.getLogsWithFilters(startDate, endDate, actionFilter, searchKeyword);
        List<String> actionList = dao.getUniqueActions();

        req.setAttribute("logs", logs);
        req.setAttribute("actionList", actionList);
        req.setAttribute("startDate", startDate != null ? startDate : "");
        req.setAttribute("endDate", endDate != null ? endDate : "");
        req.setAttribute("actionFilter", actionFilter != null ? actionFilter : "ALL");
        req.setAttribute("searchKeyword", searchKeyword != null ? searchKeyword : "");
        req.setAttribute("pageTitle", "Audit Logs - TechShop Admin");

        req.getRequestDispatcher("/views/Admin/audit-log.jsp").forward(req, resp);
    }

    private void exportCsv(HttpServletResponse resp, List<SystemLog> logs) throws IOException {
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=audit_logs.csv");
        PrintWriter out = resp.getWriter();
        out.write('\ufeff');
        out.println("ID,Time,User,Action,Entity,IP,Details");
        for (SystemLog log : logs) {
            out.println(log.getLogId() + ","
                    + (log.getCreatedAt() != null ? log.getCreatedAt().toString() : "") + ","
                    + (log.getUserName() != null ? "\"" + log.getUserName() + "\"" : "") + ","
                    + (log.getAction() != null ? log.getAction() : "") + ","
                    + (log.getEntityType() != null ? log.getEntityType() : "") + ","
                    + (log.getIpAddress() != null ? log.getIpAddress() : "") + ","
                    + (log.getDetails() != null ? "\"" + log.getDetails().replace("\"", "\"\"") + "\"" : ""));
        }
        out.flush();
    }
}
