package com.techshop.servlet;

import com.techshop.dao.CashierCartDAO;
import com.techshop.model.CashierSaleItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý giỏ hàng IMEI của Cashier trong session.
 *
 * Actions (param "action"):
 *   add    : thêm physicalId vào giỏ
 *   remove : xóa physicalId khỏi giỏ
 *   clear  : xóa toàn bộ giỏ
 *   view   : hiển thị trang giỏ hàng (mặc định)
 */
@WebServlet("/cart")
public class CashierCartServlet extends HttpServlet {

    private static final String CART_KEY = "saleCart"; // key trong session

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "view";

        switch (action) {
            case "add":    handleAdd(request, response);    break;
            case "remove": handleRemove(request, response); break;
            case "clear":  handleClear(request, response);  break;
            default:       showCart(request, response);     break;
        }
    }

    // ── Thêm IMEI vào giỏ ──────────────────────────────────────────
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(true);
        int branchId = getBranchId(session);

        String physicalIdParam = request.getParameter("physicalId");
        if (physicalIdParam == null || physicalIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int physicalId;
        try {
            physicalId = Integer.parseInt(physicalIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<CashierSaleItem> cart = getCart(session);

        // Kiểm tra đã có trong giỏ chưa
        boolean alreadyIn = false;
        for (CashierSaleItem item : cart) {
            if (item.getPhysicalId() == physicalId) {
                alreadyIn = true;
                break;
            }
        }

        if (!alreadyIn) {
            CashierCartDAO dao = new CashierCartDAO();
            CashierSaleItem item = dao.getCashierSaleItemByPhysical(physicalId, branchId);
            if (item != null) {
                cart.add(item);
                session.setAttribute(CART_KEY, cart);
                session.setAttribute("cartSuccess", "Đã thêm IMEI " + item.getImei() + " vào giỏ hàng.");
            } else {
                session.setAttribute("cartError", "IMEI không hợp lệ hoặc đã hết hàng.");
            }
        } else {
            session.setAttribute("cartError", "IMEI này đã có trong giỏ hàng.");
        }

        // Quay lại trang trước (stock-check) hoặc cart
        String redirect = request.getParameter("redirect");
        if (redirect != null && !redirect.isEmpty()) {
            response.sendRedirect(redirect);
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    // ── Xóa IMEI khỏi giỏ ──────────────────────────────────────────
    private void handleRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String physicalIdParam = request.getParameter("physicalId");
            if (physicalIdParam != null) {
                try {
                    int physicalId = Integer.parseInt(physicalIdParam.trim());
                    List<CashierSaleItem> cart = getCart(session);
                    cart.removeIf(item -> item.getPhysicalId() == physicalId);
                    session.setAttribute(CART_KEY, cart);
                } catch (NumberFormatException ignored) {}
            }
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // ── Xóa toàn bộ giỏ ────────────────────────────────────────────
    private void handleClear(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(CART_KEY);
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // ── Hiển thị trang giỏ hàng ────────────────────────────────────
    private void showCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        List<CashierSaleItem> cart = getCart(session);

        // Tính tổng tiền
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (CashierSaleItem item : cart) {
            if (item.getUnitPrice() != null) {
                total = total.add(item.getUnitPrice());
            }
        }

        // Flash messages
        String cartSuccess = (String) session.getAttribute("cartSuccess");
        String cartError   = (String) session.getAttribute("cartError");
        session.removeAttribute("cartSuccess");
        session.removeAttribute("cartError");

        request.setAttribute("cart",        cart);
        request.setAttribute("total",       total);
        request.setAttribute("cartSuccess", cartSuccess);
        request.setAttribute("cartError",   cartError);

        request.getRequestDispatcher("/views/cashier/cashierCart.jsp")
                .forward(request, response);
    }

    // ── Helpers ─────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private List<CashierSaleItem> getCart(HttpSession session) {
        List<CashierSaleItem> cart = (List<CashierSaleItem>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    private int getBranchId(HttpSession session) {
        Object b = session.getAttribute("branchId");
        return (b instanceof Integer) ? (Integer) b : 1;
    }
}