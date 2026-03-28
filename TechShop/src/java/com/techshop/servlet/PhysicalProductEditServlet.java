package com.techshop.servlet;

import com.techshop.dao.PhysicalProductDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
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
import java.util.List;
import java.util.Set;

@WebServlet(name = "PhysicalProductEditServlet", urlPatterns = {"/inventory/edit"})
public class PhysicalProductEditServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of("IN_STOCK", "DEFECTIVE", "RESERVED");

    private PhysicalProductDAO physicalProductDAO;
    private VariantDAO variantDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() throws ServletException {
        physicalProductDAO = new PhysicalProductDAO();
        variantDAO = new VariantDAO();
        logDAO = new SystemLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PhysicalProduct product = resolveProduct(request, response);
        if (product == null) return;

        List<ProductVariant> variants = variantDAO.getAllActive();
        request.setAttribute("product", product);
        request.setAttribute("variants", variants);
        request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
        request.setAttribute("pageTitle", "Edit Physical Product");
        request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PhysicalProduct product = resolveProduct(request, response);
        if (product == null) return;

        String imei = request.getParameter("imei");
        String serialNumber = request.getParameter("serialNumber");
        String status = request.getParameter("status");
        String variantIdParam = request.getParameter("variantId");

        List<ProductVariant> variants = variantDAO.getAllActive();

        // Validate
        if (imei == null || imei.trim().isEmpty()) {
            request.setAttribute("error", "IMEI is required.");
            request.setAttribute("product", product);
            request.setAttribute("variants", variants);
            request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
            request.setAttribute("pageTitle", "Edit Physical Product");
            request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
            return;
        }

        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            request.setAttribute("error", "Serial Number is required.");
            request.setAttribute("product", product);
            request.setAttribute("variants", variants);
            request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
            request.setAttribute("pageTitle", "Edit Physical Product");
            request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
            return;
        }

        if (!ALLOWED_STATUSES.contains(status)) {
            request.setAttribute("error", "Invalid status selected.");
            request.setAttribute("product", product);
            request.setAttribute("variants", variants);
            request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
            request.setAttribute("pageTitle", "Edit Physical Product");
            request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
            return;
        }

        int variantId;
        try {
            variantId = Integer.parseInt(variantIdParam);
        } catch (NumberFormatException | NullPointerException e) {
            request.setAttribute("error", "Please select a valid variant.");
            request.setAttribute("product", product);
            request.setAttribute("variants", variants);
            request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
            request.setAttribute("pageTitle", "Edit Physical Product");
            request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
            return;
        }

        // Check IMEI uniqueness (excluding current product)
        if (!imei.trim().equals(product.getImei()) && physicalProductDAO.isImeiExists(imei.trim(), product.getPhysicalId())) {
            request.setAttribute("error", "IMEI already exists.");
            request.setAttribute("product", product);
            request.setAttribute("variants", variants);
            request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
            request.setAttribute("pageTitle", "Edit Physical Product");
            request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
            return;
        }

        // Check serial number uniqueness (excluding current product)
        if (!serialNumber.trim().equals(product.getSerialNumber()) && physicalProductDAO.isSerialNumberExists(serialNumber.trim(), product.getPhysicalId())) {
            request.setAttribute("error", "Serial Number already exists.");
            request.setAttribute("product", product);
            request.setAttribute("variants", variants);
            request.setAttribute("allowedStatuses", ALLOWED_STATUSES);
            request.setAttribute("pageTitle", "Edit Physical Product");
            request.getRequestDispatcher("/views/inventory/physical-product-edit.jsp").forward(request, response);
            return;
        }

        product.setImei(imei.trim());
        product.setSerialNumber(serialNumber.trim());
        product.setStatus(status);
        product.setVariantId(variantId);

        physicalProductDAO.updatePhysicalProduct(product);
        try {
            String logDetails = "Cập nhật sản phẩm kho (ID: " + product.getPhysicalId() + "). IMEI: " + imei.trim() + " | Trạng thái: " + status;
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            Integer userId = (user != null) ? user.getUserId() : null;
            logDAO.logAction(
                    userId, 
                    LogAction.UPDATE_PHYSICAL_PRODUCT, 
                    EntityType.PHYSICAL_PRODUCT,      
                    product.getPhysicalId(), 
                    request.getRemoteAddr(), 
                    logDetails
            );
        } catch (Exception e) {
            System.err.println("Lỗi ghi log cập nhật sản phẩm kho: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/inventory/detail?id=" + product.getPhysicalId() + "&updated=true");
    }

    private PhysicalProduct resolveProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        int physicalId;
        try {
            physicalId = Integer.parseInt(idParam);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return null;
        }

        PhysicalProduct product = physicalProductDAO.getById(physicalId);
        if (product == null || user.getBranchId() == null || product.getBranchId() != user.getBranchId()) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return null;
        }

        if ("IN_TRANSFER".equals(product.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/inventory/detail?id=" + physicalId);
            return null;
        }

        return product;
    }
}
