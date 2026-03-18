package com.techshop.servlet;

import com.techshop.dao.StockTransferDAO;
import com.techshop.model.StockTransfer;
import com.techshop.model.StockTransferItem;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StockTransferDetailServlet", urlPatterns = {"/transfer/detail"})
public class StockTransferDetailServlet extends HttpServlet {

    private StockTransferDAO stockTransferDAO;

    @Override
    public void init() throws ServletException {
        stockTransferDAO = new StockTransferDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        int transferId;
        try {
            transferId = Integer.parseInt(idParam);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        StockTransfer transfer = stockTransferDAO.getById(transferId);

        if (transfer == null || user.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        // Only allow branches involved in the transfer to view it
        int branchId = user.getBranchId();
        if (transfer.getFromBranchId() != branchId && transfer.getToBranchId() != branchId) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        List<StockTransferItem> items = stockTransferDAO.getItemsByTransferId(transferId);

        request.setAttribute("transfer", transfer);
        request.setAttribute("items", items);
        request.setAttribute("pageTitle", "Transfer Detail");
        request.getRequestDispatcher("/views/inventory/transferDetail.jsp").forward(request, response);
    }
}
