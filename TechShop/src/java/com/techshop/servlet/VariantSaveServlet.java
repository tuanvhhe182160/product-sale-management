package com.techshop.servlet;

import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.dao.VariantAttributeDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.ProductVariant;
import com.techshop.model.User;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/variant/save")
public class VariantSaveServlet extends HttpServlet {

    private VariantDAO variantDAO;
    private VariantAttributeDAO attributeDAO;
    private SystemLogDAO logDAO;

    @Override
    public void init() {

        variantDAO = new VariantDAO();
        attributeDAO = new VariantAttributeDAO();
        logDAO = new SystemLogDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String idRaw = request.getParameter("variantId");

            int modelId = Integer.parseInt(request.getParameter("modelId"));
            String sku = request.getParameter("sku").trim();
            String name = request.getParameter("variantName").trim();

            BigDecimal basePrice =
                    new BigDecimal(request.getParameter("basePrice"));

            String costRaw = request.getParameter("costPrice");
            BigDecimal costPrice = (costRaw == null || costRaw.isBlank())
                    ? null
                    : new BigDecimal(costRaw);

            int warranty =
                    Integer.parseInt(request.getParameter("warrantyMonths"));

            String status = request.getParameter("status");

            String imageUrl = request.getParameter("imageUrl");

            ProductVariant variant = new ProductVariant();

            variant.setModelId(modelId);
            variant.setSku(sku);
            variant.setVariantName(name);
            variant.setBasePrice(basePrice);
            variant.setCostPrice(costPrice);
            variant.setWarrantyMonths(warranty);
            variant.setStatus(status);
            variant.setImageUrl(imageUrl);

            /*
            ===================            ATTRIBUTE PROCESSING
            ===================             */
            String[] names = request.getParameterValues("attributeName[]");
            String[] values = request.getParameterValues("attributeValue[]");

            Map<String, String> attributes = new HashMap<>();

            if (names != null) {

                for (int i = 0; i < names.length; i++) {

                    String attrName = names[i];
                    String attrValue = values[i];

                    if (attrName != null && !attrName.isBlank()) {

                        attributes.put(attrName.trim(), attrValue.trim());
                    }
                }
            }

            /*
            ===================            USER
            ===================             */
            HttpSession session = request.getSession(false);
            User user = (session != null)
                    ? (User) session.getAttribute("user") : null;

            Integer userId = user != null ? user.getUserId() : null;

            /*
            ===================            CREATE
            ===================             */
            if (idRaw == null || idRaw.isEmpty()) {

                int id = variantDAO.createVariant(variant);

                attributeDAO.insertAttributes(id, attributes);

                logDAO.logAction(
                        userId,
                        LogAction.CREATE_PRODUCT_VARIANT,
                        EntityType.PRODUCT_VARIANT,
                        id,
                        request.getRemoteAddr(),
                        "Tạo variant " + name
                );

            } else {

                /*
                ===================                UPDATE
                ===================                 */
                int id = Integer.parseInt(idRaw);

                variant.setVariantId(id);

                variantDAO.updateVariant(variant);

                // delete old attributes
                attributeDAO.deleteAttributesByVariantId(id);

                // insert new
                attributeDAO.insertAttributes(id, attributes);

                logDAO.logAction(
                        userId,
                        LogAction.UPDATE_PRODUCT_VARIANT,
                        EntityType.PRODUCT_VARIANT,
                        id,
                        request.getRemoteAddr(),
                        "Cập nhật variant " + name
                );
            }

            response.sendRedirect(
                    request.getContextPath() + "/variant"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/variant"
            );
        }
    }
}