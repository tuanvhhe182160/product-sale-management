package com.techshop.servlet;

import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.SystemLogDAO; 
import com.techshop.model.ProductCategory;
import com.techshop.model.EntityType; 
import com.techshop.model.LogAction; 
import com.techshop.model.User; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name="CategorySaveServlet", urlPatterns={"/category/save"})
public class CategorySaveServlet extends HttpServlet {

    private final ProductCategoryDAO dao = new ProductCategoryDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO(); 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idRaw = request.getParameter("categoryId"); 
        String code = request.getParameter("categoryCode");
        String name = request.getParameter("categoryName");
        String desc = request.getParameter("description");
        String status = request.getParameter("status");

        code = (code == null) ? "" : code.trim();
        name = (name == null) ? "" : name.trim();
        desc = (desc == null) ? null : desc.trim();
        if (desc != null && desc.isEmpty()) desc = null;

        boolean isEdit = (idRaw != null && !idRaw.trim().isEmpty());

        String error = null;

        if (code.isEmpty()) error = "Category code is required.";
        else if (code.length() > 20) error = "Category code must be <= 20 characters.";
        else if (name.isEmpty()) error = "Category name is required.";
        else if (name.length() > 100) error = "Category name must be <= 100 characters.";
        else if (desc != null && desc.length() > 255) error = "Description must be <= 255 characters.";

        if (!isEdit && error == null && dao.existsByCode(code)) {
            error = "Category code already exists.";
        }

        if (error != null) {
            request.setAttribute("error", error);

            if (isEdit) {
                try {
                    int id = Integer.parseInt(idRaw);
                    ProductCategory category = dao.getById(id);
                    if (category != null) request.setAttribute("category", category);
                } catch (Exception ignored) {}
            } else {
                request.setAttribute("categoryCode", code);
                request.setAttribute("categoryName", name);
                request.setAttribute("description", desc);
            }

            request.getRequestDispatcher("/views/Admin/category-form.jsp")
                   .forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        Integer userId = (user != null) ? user.getUserId() : null;
        // ---------------------------------

        if (!isEdit) {
            ProductCategory c = new ProductCategory();
            c.setCategoryCode(code);
            c.setCategoryName(name);
            c.setDescription(desc);
            c.setStatus("ACTIVE"); 
            dao.createCategory(c);
            
            // --- GHI LOG ---
            logDAO.logAction(
                userId, 
                LogAction.CREATE_CATEGORY, 
                EntityType.CATEGORY, 
                null, 
                request.getRemoteAddr(), 
                "Thêm mới danh mục: " + name + " (Mã: " + code + ")"
            );
            // ----------------------------------
            
        } else {
            int id;
            try {
                id = Integer.parseInt(idRaw);
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/category");
                return;
            }

            ProductCategory c = new ProductCategory();
            c.setCategoryId(id);
            c.setCategoryCode(code);
            c.setCategoryName(name);
            c.setDescription(desc);
            c.setStatus(status); 
            dao.updateCategory(c);
            
            // --- GHI LOG ---
            logDAO.logAction(
                userId, 
                LogAction.UPDATE_CATEGORY, 
                EntityType.CATEGORY, 
                id, 
                request.getRemoteAddr(), 
                "Cập nhật thông tin danh mục: " + name + " (Mã: " + code + ")"
            );
            // ----------------------------------
        }

        response.sendRedirect(request.getContextPath() + "/category");
    }
}