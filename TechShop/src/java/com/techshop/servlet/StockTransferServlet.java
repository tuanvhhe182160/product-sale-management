package com.techshop.servlet;

import com.techshop.dao.StockTransferDAO;
import com.techshop.model.StockTransfer;
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

@WebServlet(name = "StockTransferServlet", urlPatterns = {"/transfer"})
public class StockTransferServlet extends HttpServlet {

    private StockTransferDAO transferDAO;

    @Override
    public void init() throws ServletException {
        transferDAO = new StockTransferDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            request.setAttribute("error", "Your account is not assigned to any branch.");
            request.setAttribute("outgoing", Collections.emptyList());
            request.setAttribute("incoming", Collections.emptyList());
        } else {
            int branchId = user.getBranchId();
            List<StockTransfer> outgoing = transferDAO.getOutgoing(branchId);
            List<StockTransfer> incoming = transferDAO.getIncoming(branchId);
            request.setAttribute("outgoing", outgoing);
            request.setAttribute("incoming", incoming);
        }

        request.setAttribute("pageTitle", "Stock Transfers");
        request.getRequestDispatcher("/views/inventory/transfer-list.jsp").forward(request, response);
    }
}
