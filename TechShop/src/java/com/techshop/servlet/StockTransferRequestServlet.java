package com.techshop.servlet;

import com.techshop.dao.BranchDAO;
import com.techshop.dao.StockTransferDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.Branch;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.ProductVariant;
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
import java.util.stream.Collectors;

@WebServlet(name = "StockTransferRequestServlet", urlPatterns = {"/transfer/request"})
public class StockTransferRequestServlet extends HttpServlet {

    private BranchDAO branchDAO;
    private VariantDAO variantDAO;
    private StockTransferDAO transferDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() throws ServletException {
        branchDAO = new BranchDAO();
        variantDAO = new VariantDAO();
        transferDAO = new StockTransferDAO();
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

        int myBranchId = user.getBranchId();
        List<Branch> allBranches = branchDAO.getAllActive();
        List<Branch> otherBranches = allBranches.stream()
                .filter(b -> b.getBranchId() != myBranchId)
                .collect(Collectors.toList());

        List<ProductVariant> variants = variantDAO.getAllActive();

        request.setAttribute("branches", otherBranches);
        request.setAttribute("variants", variants);
        request.setAttribute("pageTitle", "Request Stock Transfer");
        request.getRequestDispatcher("/views/inventory/transfer-request.jsp").forward(request, response);
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

        int myBranchId = user.getBranchId();

        // Validate fromBranchId
        int fromBranchId;
        try {
            fromBranchId = Integer.parseInt(request.getParameter("fromBranchId"));
            if (fromBranchId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Please select a source branch.", myBranchId);
            return;
        }

        if (fromBranchId == myBranchId) {
            forwardWithError(request, response, "Cannot request stock from your own branch.", myBranchId);
            return;
        }

        // Validate variantId
        int variantId;
        try {
            variantId = Integer.parseInt(request.getParameter("variantId"));
            if (variantId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Please select a product variant.", myBranchId);
            return;
        }

        ProductVariant variant = variantDAO.getVariantById(variantId);
        if (variant == null || !"ACTIVE".equals(variant.getStatus())) {
            forwardWithError(request, response, "Selected variant is not valid.", myBranchId);
            return;
        }

        // Validate quantity
        int quantity;
        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
            if (quantity < 1 || quantity > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            forwardWithError(request, response, "Quantity must be between 1 and 100.", myBranchId);
            return;
        }

        // Validate source branch exists
        Branch fromBranch = branchDAO.getById(fromBranchId);
        if (fromBranch == null || !"ACTIVE".equals(fromBranch.getStatus())) {
            forwardWithError(request, response, "Selected source branch is not valid.", myBranchId);
            return;
        }

        String note = request.getParameter("note");
        if (note != null) note = note.trim();
        if (note != null && note.isEmpty()) note = null;

        StockTransfer transfer = new StockTransfer();
        transfer.setFromBranchId(fromBranchId);
        transfer.setToBranchId(myBranchId);
        transfer.setVariantId(variantId);
        transfer.setRequestedQuantity(quantity);
        transfer.setRequestedBy(user.getUserId());
        transfer.setNote(note);

        int transferId = transferDAO.create(transfer);
        if (transferId > 0) {
            //ghi log
            String logDetails = "Tạo yêu cầu chuyển kho: Xuất " + quantity + " sản phẩm (Variant ID: " + variantId + ") từ Chi nhánh " + fromBranchId + " về Chi nhánh " + myBranchId;
            
            logDAO.logAction(
                    user.getUserId(), 
                    LogAction.CREATE_STOCK_TRANSFER_REQUEST,
                    EntityType.INVENTORY_TRANSACTION,       
                    transferId, 
                    request.getRemoteAddr(), 
                    logDetails
            );
            response.sendRedirect(request.getContextPath() + "/transfer?created=1");
        } else {
            forwardWithError(request, response, "Failed to create transfer request. Please try again.", myBranchId);
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  String error, int myBranchId) throws ServletException, IOException {
        int fromBranchId = 0;
        int variantId = 0;
        try { fromBranchId = Integer.parseInt(request.getParameter("fromBranchId")); } catch (Exception ignored) {}
        try { variantId = Integer.parseInt(request.getParameter("variantId")); } catch (Exception ignored) {}

        List<Branch> allBranches = branchDAO.getAllActive();
        final int myBranchIdFinal = myBranchId;
        List<Branch> otherBranches = allBranches.stream()
                .filter(b -> b.getBranchId() != myBranchIdFinal)
                .collect(Collectors.toList());

        request.setAttribute("error", error);
        request.setAttribute("branches", otherBranches);
        request.setAttribute("variants", variantDAO.getAllActive());
        request.setAttribute("selectedFromBranchId", fromBranchId);
        request.setAttribute("selectedVariantId", variantId);
        request.setAttribute("quantity", request.getParameter("quantity"));
        request.setAttribute("note", request.getParameter("note"));
        request.setAttribute("pageTitle", "Request Stock Transfer");
        request.getRequestDispatcher("/views/inventory/transfer-request.jsp").forward(request, response);
    }
}
