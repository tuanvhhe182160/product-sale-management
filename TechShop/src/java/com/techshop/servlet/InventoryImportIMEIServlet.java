package com.techshop.servlet;

import com.techshop.dao.InventoryTransactionDAO;
import com.techshop.dao.PhysicalProductDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.InventoryTransaction;
import com.techshop.model.PhysicalProduct;
import com.techshop.model.ProductVariant;
import com.techshop.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "InventoryImportIMEIServlet", urlPatterns = {"/inventory/import/imei"})
public class InventoryImportIMEIServlet extends HttpServlet {

    private VariantDAO variantDAO;
    private PhysicalProductDAO physicalProductDAO;
    private InventoryTransactionDAO inventoryTransactionDAO;

    @Override
    public void init() throws ServletException {
        variantDAO = new VariantDAO();
        physicalProductDAO = new PhysicalProductDAO();
        inventoryTransactionDAO = new InventoryTransactionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        Integer variantId = parseIntOrNull(request.getParameter("variantId"));
        Integer quantity  = parseIntOrNull(request.getParameter("quantity"));

        if (variantId == null || quantity == null || quantity < 1 || quantity > 100) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        ProductVariant variant = variantDAO.getVariantById(variantId);
        if (variant == null || !"ACTIVE".equals(variant.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        request.setAttribute("variant", variant);
        request.setAttribute("quantity", quantity);
        request.setAttribute("importDate", LocalDate.now().toString());
        request.setAttribute("imeis", new String[quantity]);
        request.setAttribute("serials", new String[quantity]);
        request.setAttribute("errors", new String[quantity]);
        request.setAttribute("pageTitle", "Import Products - Enter IMEIs");
        request.getRequestDispatcher("/views/inventory/importStep2.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        Integer variantId = parseIntOrNull(request.getParameter("variantId"));
        Integer quantity  = parseIntOrNull(request.getParameter("quantity"));

        if (variantId == null || quantity == null || quantity < 1 || quantity > 100) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        ProductVariant variant = variantDAO.getVariantById(variantId);
        if (variant == null || !"ACTIVE".equals(variant.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/inventory/import");
            return;
        }

        // Collect all form values
        String importDateParam = request.getParameter("importDate");
        String[] imeis   = new String[quantity];
        String[] serials = new String[quantity];
        for (int i = 0; i < quantity; i++) {
            String imei   = request.getParameter("imei_" + i);
            String serial = request.getParameter("serial_" + i);
            imeis[i]   = imei   != null ? imei.trim()   : "";
            serials[i] = serial != null ? serial.trim() : "";
        }

        // Validate
        boolean hasError   = false;
        String  dateError  = null;
        String[] rowErrors = new String[quantity];
        LocalDateTime importDate = null;

        try {
            LocalDate date = LocalDate.parse(importDateParam.trim());
            if (date.isAfter(LocalDate.now())) {
                dateError = "Import date cannot be in the future.";
                hasError  = true;
            } else {
                importDate = date.atStartOfDay();
            }
        } catch (Exception e) {
            dateError = "Import date is invalid.";
            hasError  = true;
        }

        Set<String> batchImeis = new HashSet<>();
        for (int i = 0; i < quantity; i++) {
            if (imeis[i].isEmpty()) {
                rowErrors[i] = "IMEI is required.";
                hasError = true;
            } else if (!batchImeis.add(imeis[i])) {
                rowErrors[i] = "Duplicate IMEI in this batch.";
                hasError = true;
            } else if (physicalProductDAO.isImeiExists(imeis[i])) {
                rowErrors[i] = "IMEI already exists in the system.";
                hasError = true;
            } else if (!serials[i].isEmpty() && physicalProductDAO.isSerialNumberExists(serials[i])) {
                rowErrors[i] = "Serial number already exists in the system.";
                hasError = true;
            }
        }

        if (hasError) {
            request.setAttribute("variant", variant);
            request.setAttribute("quantity", quantity);
            request.setAttribute("importDate", importDateParam);
            request.setAttribute("imeis", imeis);
            request.setAttribute("serials", serials);
            request.setAttribute("errors", rowErrors);
            request.setAttribute("dateError", dateError);
            request.setAttribute("pageTitle", "Import Products - Enter IMEIs");
            request.getRequestDispatcher("/views/inventory/importStep2.jsp").forward(request, response);
            return;
        }

        // Save all units
        int savedCount = 0;
        for (int i = 0; i < quantity; i++) {
            PhysicalProduct product = new PhysicalProduct();
            product.setVariantId(variantId);
            product.setBranchId(user.getBranchId());
            product.setImei(imeis[i]);
            product.setSerialNumber(serials[i].isEmpty() ? null : serials[i]);
            product.setStatus("IN_STOCK");
            product.setImportDate(importDate);

            int physicalId = physicalProductDAO.createPhysicalProduct(product);
            if (physicalId > 0) {
                InventoryTransaction tx = new InventoryTransaction();
                tx.setTransactionType("IMPORT");
                tx.setPhysicalId(physicalId);
                tx.setToBranchId(user.getBranchId());
                tx.setQuantity(1);
                tx.setPerformedBy(user.getUserId());
                tx.setNote("Goods receipt: " + variant.getSku());
                inventoryTransactionDAO.insert(tx);
                savedCount++;
            }
        }

        String variantName = URLEncoder.encode(variant.getVariantName(), "UTF-8");
        response.sendRedirect(request.getContextPath() + "/inventory/list?imported=" + savedCount + "&variantName=" + variantName);
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
