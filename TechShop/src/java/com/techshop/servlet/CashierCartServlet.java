package com.techshop.servlet;

import com.techshop.dao.CashierCartDAO;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.InvoiceCustomerForm;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * Servlet quản lý giỏ hàng Cashier.
 * Giỏ hàng lưu theo Variant + quantity (không lưu từng IMEI).
 * Khi checkout, InvoiceDAO sẽ pick random PhysicalProduct IN_STOCK.
 */
@WebServlet("/cart")
public class CashierCartServlet extends HttpServlet {

    private static final String MAP_KEY          = "invoiceMap";
    private static final String ACTIVE_KEY       = "activeInvoiceId";
    private static final String COUNTER_KEY      = "invoiceCounter";
    private static final String CUSTOMER_MAP_KEY = "customerFormMap";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);
        ensureAtLeastOneInvoice(session);

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":               handleAdd(request, response);               break;
            case "remove":            handleRemove(request, response);            break;
            case "updateQty":         handleUpdateQty(request, response);         break;
            case "cancel":            handleCancel(request, response);            break;
            case "newInvoice":        handleNewInvoice(request, response);        break;
            case "switchInvoice":     handleSwitchInvoice(request, response);     break;
            case "saveCustomerForm":  handleSaveCustomerForm(request, response);  break;
            default:
                response.sendRedirect(request.getContextPath() + "/cashier");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);
        ensureAtLeastOneInvoice(session);
        String action = request.getParameter("action");
        if ("saveCustomerForm".equals(action)) {
            handleSaveCustomerForm(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/cashier");
        }
    }

    // ── Thêm variant vào giỏ hàng (quantity=1) ─────────────────────────
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        int branchId = getBranchId(session);
        String invoiceId = getActiveInvoiceId(session);

        String variantIdParam = request.getParameter("variantId");
        if (variantIdParam == null || variantIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int variantId;
        try {
            variantId = Integer.parseInt(variantIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        List<CashierSaleItem> cart = getCart(session, invoiceId);

        // Kiểm tra variant đã có trong giỏ hàng chưa (bất kỳ hóa đơn nào)
        LinkedHashMap<String, List<CashierSaleItem>> allInvoices = getInvoiceMap(session);

        // Tính tổng quantity đã đặt cho variant này trên TẤT CẢ hóa đơn
        int totalOrdered = 0;
        for (List<CashierSaleItem> cartItems : allInvoices.values()) {
            for (CashierSaleItem ci : cartItems) {
                if (ci.getVariantId() == variantId) {
                    totalOrdered += ci.getQuantity();
                }
            }
        }

        // Kiểm tra tồn kho
        CashierCartDAO dao = new CashierCartDAO();
        int stock = dao.countInStock(variantId, branchId);

        if (totalOrdered >= stock) {
            session.setAttribute("cartError",
                "Không đủ tồn kho. Variant này chỉ còn " + stock + " sản phẩm, đã đặt " + totalOrdered + ".");
            redirectBack(request, response);
            return;
        }

        // Kiểm tra variant đã có trong hóa đơn active chưa → tăng quantity
        for (CashierSaleItem ci : cart) {
            if (ci.getVariantId() == variantId) {
                ci.setQuantity(ci.getQuantity() + 1);
                saveCart(session, invoiceId, cart);
                session.setAttribute("cartSuccess",
                    "Đã tăng số lượng " + ci.getVariantName() + " lên " + ci.getQuantity() + ".");
                redirectBack(request, response);
                return;
            }
        }

        // Chưa có → thêm mới
        CashierSaleItem item = dao.getVariantForCart(variantId, branchId);
        if (item != null && item.getStockCount() > 0) {
            item.setQuantity(1);
            item.setBranchId(branchId);
            cart.add(item);
            saveCart(session, invoiceId, cart);
            session.setAttribute("cartSuccess",
                "Đã thêm " + item.getVariantName() + " vào " + getInvoiceLabel(session, invoiceId) + ".");
        } else {
            session.setAttribute("cartError", "Sản phẩm không hợp lệ hoặc đã hết hàng tại chi nhánh.");
        }

        redirectBack(request, response);
    }

    // ── Cập nhật số lượng variant trong giỏ ─────────────────────────────
    private void handleUpdateQty(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        int branchId = getBranchId(session);
        String invoiceId = getActiveInvoiceId(session);

        String variantIdParam = request.getParameter("variantId");
        String qtyParam = request.getParameter("qty");
        if (variantIdParam == null || qtyParam == null) {
            redirectBack(request, response);
            return;
        }

        int variantId, newQty;
        try {
            variantId = Integer.parseInt(variantIdParam.trim());
            newQty = Integer.parseInt(qtyParam.trim());
        } catch (NumberFormatException e) {
            redirectBack(request, response);
            return;
        }

        if (newQty <= 0) {
            // Xóa khỏi giỏ
            handleRemoveByVariant(session, invoiceId, variantId);
            redirectBack(request, response);
            return;
        }

        // Tính tổng quantity đã đặt cho variant này trên CÁC hóa đơn KHÁC
        LinkedHashMap<String, List<CashierSaleItem>> allInvoices = getInvoiceMap(session);
        int otherOrdered = 0;
        for (Map.Entry<String, List<CashierSaleItem>> entry : allInvoices.entrySet()) {
            if (entry.getKey().equals(invoiceId)) continue;
            for (CashierSaleItem ci : entry.getValue()) {
                if (ci.getVariantId() == variantId) {
                    otherOrdered += ci.getQuantity();
                }
            }
        }

        CashierCartDAO dao = new CashierCartDAO();
        int stock = dao.countInStock(variantId, branchId);
        int maxAllowed = stock - otherOrdered;

        if (newQty > maxAllowed) {
            session.setAttribute("cartError",
                "Không đủ tồn kho. Còn " + stock + " sản phẩm" +
                (otherOrdered > 0 ? " (đã đặt " + otherOrdered + " ở hóa đơn khác)" : "") +
                ", tối đa có thể đặt " + maxAllowed + ".");
            redirectBack(request, response);
            return;
        }

        List<CashierSaleItem> cart = getCart(session, invoiceId);
        for (CashierSaleItem ci : cart) {
            if (ci.getVariantId() == variantId) {
                ci.setQuantity(newQty);
                break;
            }
        }
        saveCart(session, invoiceId, cart);
        redirectBack(request, response);
    }

    // ── Xóa variant khỏi giỏ hàng ──────────────────────────────────────
    private void handleRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String invoiceId = getActiveInvoiceId(session);
            String vid = request.getParameter("variantId");
            if (vid != null) {
                try {
                    int variantId = Integer.parseInt(vid.trim());
                    handleRemoveByVariant(session, invoiceId, variantId);
                } catch (NumberFormatException ignored) {}
            }
        }
        redirectBack(request, response);
    }

    private void handleRemoveByVariant(HttpSession session, String invoiceId, int variantId) {
        List<CashierSaleItem> cart = getCart(session, invoiceId);
        cart.removeIf(item -> item.getVariantId() == variantId);
        saveCart(session, invoiceId, cart);
    }

    // ── Đóng hóa đơn ───────────────────────────────────────────────────
    private void handleCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String invoiceId = request.getParameter("invoiceId");
            if (invoiceId == null || invoiceId.trim().isEmpty()) {
                invoiceId = getActiveInvoiceId(session);
            }

            LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
            String label = getInvoiceLabel(session, invoiceId);

            if (map.size() <= 1) {
                map.put(invoiceId, new ArrayList<>());
            } else {
                String nextId = findNeighborInvoice(map, invoiceId);
                map.remove(invoiceId);
                session.setAttribute(ACTIVE_KEY, nextId);
            }

            session.setAttribute(MAP_KEY, map);
            getCustomerFormMap(session).remove(invoiceId);
            session.setAttribute("cancelSuccess", "Đã đóng " + label + ".");
        }

        response.sendRedirect(request.getContextPath() + "/cashier");
    }

    // ── Tạo hóa đơn mới ────────────────────────────────────────────────
    private void handleNewInvoice(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);

        int counter = getCounter(session) + 1;
        session.setAttribute(COUNTER_KEY, counter);

        String newId = "inv_" + counter;
        map.put(newId, new ArrayList<>());
        session.setAttribute(MAP_KEY, map);
        session.setAttribute(ACTIVE_KEY, newId);

        response.sendRedirect(request.getContextPath() + "/cashier");
    }

    // ── Lưu thông tin khách hàng ────────────────────────────────────────
    private void handleSaveCustomerForm(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String invoiceId = request.getParameter("invoiceId");
            if (invoiceId == null || invoiceId.trim().isEmpty()) {
                invoiceId = getActiveInvoiceId(session);
            }

            if (invoiceId != null) {
                InvoiceCustomerForm form = new InvoiceCustomerForm();
                form.setCustomerId(emptyToNull(request.getParameter("customerId")));
                form.setPhone(emptyToNull(request.getParameter("phone")));
                form.setFullName(emptyToNull(request.getParameter("fullName")));
                form.setEmail(emptyToNull(request.getParameter("email")));
                form.setAddress(emptyToNull(request.getParameter("address")));
                String pm = request.getParameter("paymentMethod");
                form.setPaymentMethod(pm != null && !pm.isEmpty() ? pm : "CASH");
                form.setNote(emptyToNull(request.getParameter("note")));
                try {
                    String disc = request.getParameter("discountAmount");
                    if (disc != null && !disc.trim().isEmpty()) {
                        form.setDiscountAmount(new java.math.BigDecimal(disc.trim()));
                    }
                } catch (NumberFormatException ignored) {}
                try {
                    String rp = request.getParameter("redeemPoints");
                    if (rp != null && !rp.trim().isEmpty()) {
                        form.setRedeemPoints(Integer.parseInt(rp.trim()));
                    }
                } catch (NumberFormatException ignored) {}

                getCustomerFormMap(session).put(invoiceId, form);
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"ok\":true}");
    }

    // ── Chuyển hóa đơn ─────────────────────────────────────────────────
    private void handleSwitchInvoice(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String invoiceId = request.getParameter("invoiceId");
            LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
            if (invoiceId != null && map.containsKey(invoiceId)) {
                session.setAttribute(ACTIVE_KEY, invoiceId);
            }
        }

        response.sendRedirect(request.getContextPath() + "/cashier");
    }

    private void redirectBack(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String rd = request.getParameter("redirect");
        if (rd != null && !rd.trim().isEmpty()) {
            response.sendRedirect(rd);
        } else {
            response.sendRedirect(request.getContextPath() + "/cashier");
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, List<CashierSaleItem>> getInvoiceMap(HttpSession session) {
        Object obj = session.getAttribute(MAP_KEY);
        if (obj instanceof LinkedHashMap) return (LinkedHashMap<String, List<CashierSaleItem>>) obj;
        LinkedHashMap<String, List<CashierSaleItem>> map = new LinkedHashMap<>();
        session.setAttribute(MAP_KEY, map);
        return map;
    }

    private List<CashierSaleItem> getCart(HttpSession session, String invoiceId) {
        return getInvoiceMap(session).getOrDefault(invoiceId, new ArrayList<>());
    }

    private void saveCart(HttpSession session, String invoiceId, List<CashierSaleItem> cart) {
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        map.put(invoiceId, cart);
        session.setAttribute(MAP_KEY, map);
    }

    private String getActiveInvoiceId(HttpSession session) {
        String activeId = (String) session.getAttribute(ACTIVE_KEY);
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        if (activeId != null && map.containsKey(activeId)) return activeId;
        if (!map.isEmpty()) {
            String firstId = map.keySet().iterator().next();
            session.setAttribute(ACTIVE_KEY, firstId);
            return firstId;
        }
        return null;
    }

    private String getInvoiceLabel(HttpSession session, String invoiceId) {
        if (invoiceId == null) return "hóa đơn";
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        int idx = 1;
        for (String key : map.keySet()) {
            if (key.equals(invoiceId)) return "Hóa đơn " + idx;
            idx++;
        }
        return "hóa đơn";
    }

    private String findNeighborInvoice(LinkedHashMap<String, List<CashierSaleItem>> map, String removedId) {
        String prev = null;
        for (String key : map.keySet()) {
            if (key.equals(removedId)) {
                return prev != null ? prev : getNextKey(map, removedId);
            }
            prev = key;
        }
        return map.keySet().iterator().next();
    }

    private String getNextKey(LinkedHashMap<String, List<CashierSaleItem>> map, String currentKey) {
        boolean found = false;
        for (String key : map.keySet()) {
            if (found) return key;
            if (key.equals(currentKey)) found = true;
        }
        return map.keySet().iterator().next();
    }

    private void ensureAtLeastOneInvoice(HttpSession session) {
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        if (map.isEmpty()) {
            int counter = getCounter(session) + 1;
            session.setAttribute(COUNTER_KEY, counter);
            String id = "inv_" + counter;
            map.put(id, new ArrayList<>());
            session.setAttribute(MAP_KEY, map);
            session.setAttribute(ACTIVE_KEY, id);
        }
    }

    private int getCounter(HttpSession session) {
        Object c = session.getAttribute(COUNTER_KEY);
        return (c instanceof Integer) ? (Integer) c : 0;
    }

    private int getBranchId(HttpSession session) {
        Object b = session.getAttribute("branchId");
        return (b instanceof Integer) ? (Integer) b : 1;
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, InvoiceCustomerForm> getCustomerFormMap(HttpSession session) {
        Object obj = session.getAttribute(CUSTOMER_MAP_KEY);
        if (obj instanceof java.util.Map) return (java.util.Map<String, InvoiceCustomerForm>) obj;
        java.util.Map<String, InvoiceCustomerForm> map = new java.util.LinkedHashMap<>();
        session.setAttribute(CUSTOMER_MAP_KEY, map);
        return map;
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
