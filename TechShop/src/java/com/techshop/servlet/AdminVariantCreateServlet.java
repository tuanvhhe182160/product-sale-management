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

@WebServlet(name = "AdminVariantCreateServlet", urlPatterns = {"/variant/create"})
public class AdminVariantCreateServlet extends HttpServlet {

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
            List<ProductModel> models = variantDAO.getAllActiveModels();
            request.setAttribute("models", models);
            request.setAttribute("pageTitle", "Tạo Variant mới");
            request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
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

            if (variantDAO.isSkuExists(sku)) {
                request.setAttribute("error", "SKU đã tồn tại trong hệ thống!");
                List<ProductModel> models = variantDAO.getAllActiveModels();
                request.setAttribute("models", models);
                request.setAttribute("pageTitle", "Tạo Variant mới");
                request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
                return;
            }

            ProductVariant variant = new ProductVariant();
            variant.setModelId(modelId);
            variant.setSku(sku);
            variant.setVariantName(variantName);
            variant.setBasePrice(basePrice);
            variant.setCostPrice(costPrice);
            variant.setWarrantyMonths(warrantyMonths);
            variant.setImageUrl(imageUrl);
            variant.setStatus(status != null ? status : "ACTIVE");

            int variantId = variantDAO.createVariant(variant);

            if (variantId > 0) {
                //Ghi log
                try {
                    HttpSession session = request.getSession(false);
                    if (session != null && session.getAttribute("user") != null) {
                        User user = (User) session.getAttribute("user");
                        String logDetails = "Thêm mới phiên bản sản phẩm (SKU: " + sku + "). Tên: " + variantName + " | Giá bán: " + basePrice;
                        
                        logDAO.logAction(
                                user.getUserId(), 
                                LogAction.CREATE_PRODUCT_VARIANT,
                                EntityType.PRODUCT_VARIANT,  
                                variantId,
                                request.getRemoteAddr(), 
                                logDetails
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi ghi log tạo phiên bản sản phẩm: " + e.getMessage());
                }
                
                response.sendRedirect(request.getContextPath() + "/variant?success=create");
            } else {
                request.setAttribute("error", "Không thể tạo variant mới!");
                List<ProductModel> models = variantDAO.getAllActiveModels();
                request.setAttribute("models", models);
                request.setAttribute("pageTitle", "Tạo Variant mới");
                request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            List<ProductModel> models = variantDAO.getAllActiveModels();
            request.setAttribute("models", models);
            request.setAttribute("pageTitle", "Tạo Variant mới");
            request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            List<ProductModel> models = variantDAO.getAllActiveModels();
            request.setAttribute("models", models);
            request.setAttribute("pageTitle", "Tạo Variant mới");
            request.getRequestDispatcher("/views/variant/variant-form.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Create Variant Servlet - Show form and store new variant";
    }
}
