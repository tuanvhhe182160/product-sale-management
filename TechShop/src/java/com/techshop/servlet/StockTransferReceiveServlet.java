package com.techshop.servlet;

import com.techshop.dao.InventoryTransactionDAO;
import com.techshop.dao.PhysicalProductDAO;
import com.techshop.dao.StockTransferDAO;
import com.techshop.model.InventoryTransaction;
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

@WebServlet(name = "StockTransferReceiveServlet", urlPatterns = {"/transfer/receive"})
public class StockTransferReceiveServlet extends HttpServlet {

    private StockTransferDAO transferDAO;
    private PhysicalProductDAO physicalProductDAO;
    private InventoryTransactionDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        transferDAO = new StockTransferDAO();
        physicalProductDAO = new PhysicalProductDAO();
        transactionDAO = new InventoryTransactionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        int transferId;
        try {
            transferId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        StockTransfer transfer = transferDAO.getById(transferId);
        if (transfer == null) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        // Only the destination branch manager can receive
        if (transfer.getToBranchId() != user.getBranchId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to receive this transfer.");
            return;
        }

        if (!"APPROVED".equals(transfer.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        List<StockTransferItem> items = transferDAO.getItemsByTransferId(transferId);

        request.setAttribute("transfer", transfer);
        request.setAttribute("items", items);
        request.setAttribute("pageTitle", "Receive Transfer");
        request.getRequestDispatcher("/views/inventory/transferReceive.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        int transferId;
        try {
            transferId = Integer.parseInt(request.getParameter("transferId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        StockTransfer transfer = transferDAO.getById(transferId);
        if (transfer == null || transfer.getToBranchId() != user.getBranchId()
                || !"APPROVED".equals(transfer.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        List<StockTransferItem> items = transferDAO.getItemsByTransferId(transferId);

        // Update each item: move to this branch with IN_STOCK status, log TRANSFER_IN
        for (StockTransferItem item : items) {
            physicalProductDAO.setBranchAndStatus(item.getPhysicalId(), transfer.getToBranchId(), "IN_STOCK");

            InventoryTransaction tx = new InventoryTransaction();
            tx.setTransactionType("TRANSFER_IN");
            tx.setPhysicalId(item.getPhysicalId());
            tx.setFromBranchId(transfer.getFromBranchId());
            tx.setToBranchId(transfer.getToBranchId());
            tx.setQuantity(1);
            tx.setReferenceId(transfer.getTransferId());
            tx.setPerformedBy(user.getUserId());
            tx.setNote("Transfer " + transfer.getTransferCode());
            transactionDAO.insert(tx);
        }

        transferDAO.updateStatus(transfer.getTransferId(), "COMPLETED", null, user.getUserId());
        response.sendRedirect(request.getContextPath() + "/transfer?completed=1&qty=" + items.size());
    }
}
