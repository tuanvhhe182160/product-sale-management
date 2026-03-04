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
        if (action == null) action = "";

        switch (action) {
            case "add":    handleAdd(request, response);    break;
            case "remove": handleRemove(request, response); break;
            case "cancel": handleCancel(request, response); break;
            default:
                response.sendRedirect(request.getContextPath() + "/cashier");
                break;
        }
    }

    // ── Thêm IMEI vào giỏ ──────────────────────────────────────────
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        int branchId = getBranchId(session);

        String physicalIdParam = request.getParameter("physicalId");
        if (physicalIdParam == null || physicalIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int physicalId;
        try {
            physicalId = Integer.parseInt(physicalIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier");
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

        String redirect = request.getParameter("redirect");
        if (redirect != null && !redirect.trim().isEmpty()) {
            response.sendRedirect(redirect);
        } else {
            response.sendRedirect(request.getContextPath() + "/cashier");
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String p = req.getParameter("physicalId");
            if (p != null) {
                try {
                    int pid = Integer.parseInt(p.trim());
                    List<CashierSaleItem> cart = getCart(session);
                    cart.removeIf(item -> item.getPhysicalId() == pid);
                    session.setAttribute(CART_KEY, cart);
                } catch (NumberFormatException ignored) {}
            }
        }
        String rd = req.getParameter("redirect");
        if (rd != null && !rd.trim().isEmpty()) {
            res.sendRedirect(rd);
        } else {
            res.sendRedirect(req.getContextPath() + "/cashier");
        }
    }

    private void handleCancel(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute(CART_KEY);
            String reason = req.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) reason = "Khong co ly do";
            session.setAttribute("cancelSuccess", "Da huy don hang. Ly do: " + reason);
        }
        res.sendRedirect(req.getContextPath() + "/cashier");
    }

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