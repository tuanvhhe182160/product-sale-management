package com.techshop.servlet;

import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.ProductModel;
import com.techshop.model.ProductVariant;
import com.techshop.model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminVariantEditServlet", urlPatterns = {"/variant/edit"})
public class AdminVariantEditServlet extends HttpServlet {

    private VariantDAO variantDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() throws ServletException {
        variantDAO = new VariantDAO();
        logDAO = new SystemLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            int variantId = Integer.parseInt(request.getParameter("id"));
            ProductVariant variant = variantDAO.getVariantById(variantId);

            if (variant == null) {
                request.setAttribute("error", "Không tìm thấy variant!");
                response.sendRedirect(request.getContextPath() + "/variant");
                return;
            }

            List<ProductModel> models = variantDAO.getAllActiveModels();
            request.setAttribute("variant", variant);
            request.setAttribute("models", models);
            request.setAttribute("pageTitle", "Chỉnh sửa Variant");
            request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/variant");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/variant");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            int variantId = Integer.parseInt(request.getParameter("variantId"));
            int modelId = Integer.parseInt(request.getParameter("modelId"));
            String sku = request.getParameter("sku").trim();
            String variantName = request.getParameter("variantName").trim();
            BigDecimal basePrice = new BigDecimal(request.getParameter("basePrice"));
            String costPriceStr = request.getParameter("costPrice");
            BigDecimal costPrice = (costPriceStr != null && !costPriceStr.trim().isEmpty())
                    ? new BigDecimal(costPriceStr) : null;
            int warrantyMonths = Integer.parseInt(request.getParameter("warrantyMonths"));
            String imageUrl = request.getParameter("imageUrl");
            if (imageUrl != null) {
                imageUrl = imageUrl.trim();
            }
            String status = request.getParameter("status");

            if (variantDAO.isSkuExists(sku, variantId)) {
                ProductVariant variant = variantDAO.getVariantById(variantId);
                List<ProductModel> models = variantDAO.getAllActiveModels();
                request.setAttribute("error", "SKU đã tồn tại trong hệ thống!");
                request.setAttribute("variant", variant);
                request.setAttribute("models", models);
                request.setAttribute("pageTitle", "Chỉnh sửa Variant");
                request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
                return;
            }

            ProductVariant variant = new ProductVariant();
            variant.setVariantId(variantId);
            variant.setModelId(modelId);
            variant.setSku(sku);
            variant.setVariantName(variantName);
            variant.setBasePrice(basePrice);
            variant.setCostPrice(costPrice);
            variant.setWarrantyMonths(warrantyMonths);
            variant.setImageUrl(imageUrl);
            variant.setStatus(status);

            boolean success = variantDAO.updateVariant(variant);

            if (success) {
                //Ghi log
                try {
                    HttpSession session = request.getSession(false);
                    if (session != null && session.getAttribute("user") != null) {
                        User user = (User) session.getAttribute("user");
                        String logDetails = "Cập nhật phiên bản sản phẩm (ID: " + variantId + " | SKU: " + sku + "). Trạng thái: " + status + " | Giá bán: " + basePrice;
                        
                        logDAO.logAction(
                                user.getUserId(), 
                                LogAction.UPDATE_PRODUCT_VARIANT,
                                EntityType.PRODUCT_VARIANT, 
                                variantId, 
                                request.getRemoteAddr(), 
                                logDetails
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log cập nhật phiên bản sản phẩm: " + e.getMessage());
                }
                
                response.sendRedirect(request.getContextPath() + "/variant?success=update");
            } else {
                request.setAttribute("error", "Không thể cập nhật variant!");
                ProductVariant existingVariant = variantDAO.getVariantById(variantId);
                List<ProductModel> models = variantDAO.getAllActiveModels();
                request.setAttribute("variant", existingVariant);
                request.setAttribute("models", models);
                request.setAttribute("pageTitle", "Chỉnh sửa Variant");
                request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            try {
                int variantId = Integer.parseInt(request.getParameter("variantId"));
                ProductVariant variant = variantDAO.getVariantById(variantId);
                List<ProductModel> models = variantDAO.getAllActiveModels();
                request.setAttribute("variant", variant);
                request.setAttribute("models", models);
                request.setAttribute("pageTitle", "Chỉnh sửa Variant");
                request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/variant");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            try {
                int variantId = Integer.parseInt(request.getParameter("variantId"));
                ProductVariant variant = variantDAO.getVariantById(variantId);
                List<ProductModel> models = variantDAO.getAllActiveModels();
                request.setAttribute("variant", variant);
                request.setAttribute("models", models);
                request.setAttribute("pageTitle", "Chỉnh sửa Variant");
                request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/variant");
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Edit Variant Servlet - Show form and update variant";
    }
}
