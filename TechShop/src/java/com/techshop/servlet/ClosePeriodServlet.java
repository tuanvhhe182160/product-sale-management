/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.techshop.servlet;

import com.techshop.dao.AccountingPeriodDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.AccountingPeriod;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AccountingPeriodServlet", urlPatterns = {"/accounting/close-period"})
public class ClosePeriodServlet extends HttpServlet {

    private AccountingPeriodDAO periodDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() {
        periodDAO = new AccountingPeriodDAO();
        logDAO = new SystemLogDAO();
    }

    // ==================
    // GET → list hoặc preview
    // ==================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {

            case "preview":
                previewPeriod(request, response);
                break;

            case "list":
            default:
                listClosedPeriods(request, response);
                break;
        }
    }

    // ==================
    // POST → close kỳ
    // ==================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("close".equals(action)) {
            closePeriod(request, response);
        } else {
            response.sendRedirect("accounting/close-period");
        }
    }

    // ===============================
    // 1️⃣ Xem trước dữ liệu chốt kỳ
    // ===============================
    private void previewPeriod(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int branchId = Integer.parseInt(request.getParameter("branchId"));
        int month = Integer.parseInt(request.getParameter("month"));
        int year = Integer.parseInt(request.getParameter("year"));

        AccountingPeriod period = periodDAO.calculatePeriod(branchId, month, year);

        request.setAttribute("previewPeriod", period);
        request.getRequestDispatcher("/views/accounting/preview.jsp")
               .forward(request, response);
    }

    // ===============================
    // 2️⃣ Chốt kỳ
    // ===============================
    private void closePeriod(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int branchId = Integer.parseInt(request.getParameter("branchId"));
        int month = Integer.parseInt(request.getParameter("month"));
        int year = Integer.parseInt(request.getParameter("year"));

        // 1. Kiểm tra đã chốt chưa
        if (periodDAO.isPeriodClosed(branchId, month, year)) {
            response.sendRedirect("accounting/close-period?error=already_closed");
            return;
        }

        // 2. Tính dữ liệu
        AccountingPeriod period = periodDAO.calculatePeriod(branchId, month, year);

        // 3. Lấy user từ session (filter đã đảm bảo có login)
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        period.setClosedBy(userId);

        // 4. Insert
        boolean success = periodDAO.closePeriod(period);

        if (success) {
            // --- GHI LOG ---
            logDAO.logAction(
                userId, 
                LogAction.CLOSE_ACCOUNTING_PERIOD, 
                EntityType.ACCOUNTING, 
                null, 
                request.getRemoteAddr(), 
                "Chốt sổ kế toán tháng " + month + "/" + year + " (Chi nhánh ID: " + branchId + ")"
            );
            // ---------------------------------------------------------------
            response.sendRedirect(
                request.getContextPath() +
                "/accounting/close-period?branchId=" + branchId + "&success=closed"
            );
        } else {
            response.sendRedirect(
                request.getContextPath() +
                "/accounting/close-period?branchId=" + branchId + "&error=failed"
                );
        }
    }

    // ===============================
    // 3️⃣ Danh sách kỳ đã chốt
    // ===============================
    private void listClosedPeriods(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int branchId = Integer.parseInt(request.getParameter("branchId"));

        List<AccountingPeriod> list = periodDAO.getClosedPeriods(branchId);

        request.setAttribute("periodList", list);
        request.getRequestDispatcher("/views/accounting/list.jsp")
               .forward(request, response);
    }
}