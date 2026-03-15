package com.techshop.servlet;

import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminVariantDeleteServlet", urlPatterns = {"/variant/delete"})
public class AdminVariantDeleteServlet extends HttpServlet {

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
            boolean success = variantDAO.deleteVariant(variantId);

            if (success) {
                // --- GHI LOG ---
                HttpSession session = request.getSession(false);
                User user = (session != null) ? (User) session.getAttribute("user") : null;
                Integer userId = (user != null) ? user.getUserId() : null;

                logDAO.logAction(
                    userId, 
                    LogAction.DELETE_PRODUCT_VARIANT,
                    EntityType.PRODUCT_VARIANT, 
                    variantId, 
                    request.getRemoteAddr(), 
                    "Xóa phiên bản sản phẩm (Variant ID: " + variantId + ")"
                );
                // -----------------------
                response.sendRedirect(request.getContextPath() + "/variant?success=delete");
            } else {
                request.setAttribute("error", "Không thể xóa variant!");
                response.sendRedirect(request.getContextPath() + "/variant");
            }
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
    public String getServletInfo() {
        return "Delete Variant Servlet - Delete variant";
    }
}


