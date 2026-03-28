package com.techshop.servlet;

import com.techshop.dao.InventoryTransactionDAO;
import com.techshop.dao.PhysicalProductDAO;
import com.techshop.dao.StockTransferDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.EntityType;
import com.techshop.model.InventoryTransaction;
import com.techshop.model.LogAction;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.StockTransfer;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StockTransferApprovalServlet", urlPatterns = {"/transfer/approve"})
public class StockTransferApprovalServlet extends HttpServlet {

    private StockTransferDAO transferDAO;
    private PhysicalProductDAO physicalProductDAO;
    private InventoryTransactionDAO transactionDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() throws ServletException {
        transferDAO = new StockTransferDAO();
        physicalProductDAO = new PhysicalProductDAO();
        transactionDAO = new InventoryTransactionDAO();
        logDAO = new SystemLogDAO();
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

        // Only the source branch manager can approve/reject
        if (transfer.getFromBranchId() != user.getBranchId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to approve this transfer.");
            return;
        }

        // Load available IN_STOCK items only if PENDING
        List<PhysicalProduct> availableItems = null;
        if ("PENDING".equals(transfer.getStatus())) {
            availableItems = physicalProductDAO.getInStockByVariantAndBranch(
                    transfer.getVariantId(), transfer.getFromBranchId());
        } else {
            // Already decided — show the saved items
            availableItems = List.of();
        }

        request.setAttribute("transfer", transfer);
        request.setAttribute("availableItems", availableItems);
        request.setAttribute("pageTitle", "Approve Transfer Request");
        request.getRequestDispatcher("/views/inventory/transfer-approve.jsp").forward(request, response);
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
        if (transfer == null || transfer.getFromBranchId() != user.getBranchId()
                || !"PENDING".equals(transfer.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/transfer");
            return;
        }

        String action = request.getParameter("action");

        if ("approve".equals(action)) {
            handleApprove(request, response, transfer, user);
        } else if ("reject".equals(action)) {
            handleReject(request, response, transfer, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/transfer");
        }
    }

    private void handleApprove(HttpServletRequest request, HttpServletResponse response,
                                StockTransfer transfer, User user) throws ServletException, IOException {
        String[] selectedIds = request.getParameterValues("physicalId");

        if (selectedIds == null || selectedIds.length == 0) {
            forwardApproveWithError(request, response, transfer, "Please select at least one unit to transfer.");
            return;
        }

        if (selectedIds.length != transfer.getRequestedQuantity()) {
            forwardApproveWithError(request, response, transfer,
                    "You must select exactly " + transfer.getRequestedQuantity()
                    + " unit(s). You selected " + selectedIds.length + ".");
            return;
        }

        // Validate each selected item
        for (String idStr : selectedIds) {
            int physicalId;
            try {
                physicalId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                forwardApproveWithError(request, response, transfer, "Invalid unit selection.");
                return;
            }

            PhysicalProduct product = physicalProductDAO.getById(physicalId);
            if (product == null
                    || product.getBranchId() != transfer.getFromBranchId()
                    || !"IN_STOCK".equals(product.getStatus())
                    || product.getVariantId() != transfer.getVariantId()) {
                forwardApproveWithError(request, response, transfer,
                        "One or more selected units are no longer available. Please refresh and try again.");
                return;
            }
        }

        // Process approval: mark each item IN_TRANSFER, add to transfer, log transaction
        for (String idStr : selectedIds) {
            int physicalId = Integer.parseInt(idStr);

            physicalProductDAO.updateStatus(physicalId, "IN_TRANSFER");
            transferDAO.addItem(transfer.getTransferId(), physicalId);

            InventoryTransaction tx = new InventoryTransaction();
            tx.setTransactionType("TRANSFER_OUT");
            tx.setPhysicalId(physicalId);
            tx.setFromBranchId(transfer.getFromBranchId());
            tx.setToBranchId(transfer.getToBranchId());
            tx.setQuantity(1);
            tx.setReferenceId(transfer.getTransferId());
            tx.setPerformedBy(user.getUserId());
            tx.setNote("Transfer " + transfer.getTransferCode());
            transactionDAO.insert(tx);            
        }

        transferDAO.updateStatus(transfer.getTransferId(), "APPROVED", user.getUserId(), null);
        //Ghi Log
        try {
            String logDetails = "Duyệt xuất kho (Phiếu: " + transfer.getTransferCode() + ") với " + selectedIds.length + " sản phẩm.";
            
            logDAO.logAction(
                    user.getUserId(), 
                    com.techshop.model.LogAction.APPROVE_STOCK_TRANSFER,
                    com.techshop.model.EntityType.INVENTORY_TRANSACTION, 
                    transfer.getTransferId(), 
                    request.getRemoteAddr(), 
                    logDetails
            );
        } catch (Exception e) {
            System.err.println("Lỗi ghi log duyệt xuất kho: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/transfer?approved=1");
    }

    private void handleReject(HttpServletRequest request, HttpServletResponse response,
                               StockTransfer transfer, User user) throws IOException {
        String rejectNote = request.getParameter("rejectNote");
        if (rejectNote != null) rejectNote = rejectNote.trim();

        transferDAO.updateStatus(transfer.getTransferId(), "REJECTED", user.getUserId(), null);
        //Ghi log
        try {
            String reason = (rejectNote != null && !rejectNote.isEmpty()) ? rejectNote : "Không có lý do";
            String logDetails = "Từ chối yêu cầu chuyển kho (Phiếu: " + transfer.getTransferCode() + "). Lý do: " + reason;
            
            logDAO.logAction(
                    user.getUserId(), 
                    LogAction.REJECT_STOCK_TRANSFER, 
                    EntityType.INVENTORY_TRANSACTION, 
                    transfer.getTransferId(), 
                    request.getRemoteAddr(), 
                    logDetails
            );
        } catch (Exception e) {
            System.err.println("Lỗi ghi log từ chối xuất kho: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/transfer?rejected=1");
    }

    private void forwardApproveWithError(HttpServletRequest request, HttpServletResponse response,
                                         StockTransfer transfer, String error)
            throws ServletException, IOException {
        List<PhysicalProduct> availableItems = physicalProductDAO.getInStockByVariantAndBranch(
                transfer.getVariantId(), transfer.getFromBranchId());
        request.setAttribute("transfer", transfer);
        request.setAttribute("availableItems", availableItems);
        request.setAttribute("error", error);
        request.setAttribute("pageTitle", "Approve Transfer Request");
        request.getRequestDispatcher("/views/inventory/transfer-approve.jsp").forward(request, response);
    }
}
