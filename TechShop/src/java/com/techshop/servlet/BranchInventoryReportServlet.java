package com.techshop.servlet;

import com.techshop.dao.PhysicalProductDAO;
import com.techshop.model.BranchInventoryItem;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "BranchInventoryReportServlet", urlPatterns = {"/inventory/report"})
public class BranchInventoryReportServlet extends HttpServlet {

    private PhysicalProductDAO physicalProductDAO;

    @Override
    public void init() throws ServletException {
        physicalProductDAO = new PhysicalProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        Integer branchId = user.getBranchId();
        if (branchId == null) {
            request.setAttribute("error", "Your account is not assigned to any branch.");
            request.setAttribute("inventoryItems", Collections.emptyList());
            request.setAttribute("totalInventoryLevel", 0);
        } else {
            List<BranchInventoryItem> inventoryItems = physicalProductDAO.getInventoryLevelsByBranch(branchId);
            int totalInventoryLevel = 0;
            for (BranchInventoryItem item : inventoryItems) {
                totalInventoryLevel += item.getInventoryLevel();
            }

            request.setAttribute("inventoryItems", inventoryItems);
            request.setAttribute("totalInventoryLevel", totalInventoryLevel);
        }

        request.setAttribute("pageTitle", "Branch Inventory Report");
        request.getRequestDispatcher("/views/inventory/branchInventoryReport.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

