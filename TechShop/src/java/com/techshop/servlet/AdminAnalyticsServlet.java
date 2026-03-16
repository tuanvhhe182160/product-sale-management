package com.techshop.servlet;

import com.techshop.dao.AdminAnalyticsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminAnalyticsServlet", urlPatterns = {"/admin/analytics/warranty"})
public class AdminAnalyticsServlet extends HttpServlet {

    private final AdminAnalyticsDAO analyticsDAO = new AdminAnalyticsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        // 1. Kéo dữ liệu thống kê từ DAO
        request.setAttribute("generalStats", analyticsDAO.getGeneralWarrantyStats());
        request.setAttribute("techPerformance", analyticsDAO.getTechnicianPerformance());
        request.setAttribute("productDefects", analyticsDAO.getDefectRateByProduct());

        // 2. Chuyển hướng sang giao diện Dashboard
        request.getRequestDispatcher("/views/Admin/warranty-analytics.jsp").forward(request, response);
    }
}