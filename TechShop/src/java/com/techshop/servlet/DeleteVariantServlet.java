package com.techshop.servlet;

import com.techshop.dao.VariantDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DeleteVariantServlet", urlPatterns = {"/variant/delete"})
public class DeleteVariantServlet extends HttpServlet {

    private VariantDAO variantDAO;

    @Override
    public void init() throws ServletException {
        variantDAO = new VariantDAO();
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

