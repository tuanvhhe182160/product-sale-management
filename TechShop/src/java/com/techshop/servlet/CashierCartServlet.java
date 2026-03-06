package com.techshop.servlet;

import com.techshop.dao.CashierCartDAO;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.InvoiceCustomerForm;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;


@WebServlet("/cart")
public class CashierCartServlet extends HttpServlet {

    // Tên các key lưu trong session
    private static final String MAP_KEY          = "invoiceMap";
    private static final String ACTIVE_KEY       = "activeInvoiceId";
    private static final String COUNTER_KEY      = "invoiceCounter";
    private static final String CUSTOMER_MAP_KEY = "customerFormMap"; // Thông tin khách theo invoiceId

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Đảm bảo session luôn có ít nhất 1 hóa đơn trước khi xử lý
        HttpSession session = request.getSession(true);
        ensureAtLeastOneInvoice(session);

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":               handleAdd(request, response);               break;
            case "remove":            handleRemove(request, response);            break;
            case "cancel":            handleCancel(request, response);            break;
            case "newInvoice":        handleNewInvoice(request, response);        break;
            case "switchInvoice":     handleSwitchInvoice(request, response);     break;
            case "saveCustomerForm":  handleSaveCustomerForm(request, response);  break;
            default:
                // Không có trang giỏ hàng riêng — về trang bán hàng
                response.sendRedirect(request.getContextPath() + "/cashier");
                break;
        }
    }

    // saveCustomerForm được gọi bằng POST từ fetch() trong JS
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

    // ── Thêm 1 sản phẩm (IMEI) vào hóa đơn đang active ────────────────────
    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        int branchId = getBranchId(session);
        String invoiceId = getActiveInvoiceId(session);

        // Kiểm tra tham số physicalId có hợp lệ không
        String physicalIdParam = request.getParameter("physicalId");
        if (physicalIdParam == null || physicalIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        int physicalId;
        try {
            physicalId = Integer.parseInt(physicalIdParam.trim());
        } catch (NumberFormatException e) {
            // physicalId không phải số — bỏ qua
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        List<CashierSaleItem> cart = getCart(session, invoiceId);

        // Kiểm tra IMEI này đã có trong đơn hàng chưa (tránh thêm trùng)
        for (CashierSaleItem item : cart) {
            if (item.getPhysicalId() == physicalId) {
                session.setAttribute("cartError", "IMEI này đã có trong hóa đơn đang chọn.");
                redirectBack(request, response);
                return;
            }
        }

        // Truy vấn DB: lấy thông tin sản phẩm, đồng thời kiểm tra còn IN_STOCK không
        CashierCartDAO dao = new CashierCartDAO();
        CashierSaleItem item = dao.getCashierSaleItemByPhysical(physicalId, branchId);
        if (item != null) {
            cart.add(item);
            saveCart(session, invoiceId, cart);
            session.setAttribute("cartSuccess",
                    "Đã thêm IMEI " + item.getImei() + " vào " + getInvoiceLabel(session, invoiceId) + ".");
        } else {
            // Không tìm thấy: IMEI không tồn tại, không thuộc chi nhánh, hoặc đã bán
            session.setAttribute("cartError", "IMEI không hợp lệ hoặc sản phẩm đã hết hàng tại chi nhánh.");
        }

        redirectBack(request, response);
    }

    // ── Xóa 1 sản phẩm khỏi hóa đơn đang active ───────────────────────────
    private void handleRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String invoiceId = getActiveInvoiceId(session);
            String p = request.getParameter("physicalId");
            if (p != null) {
                try {
                    int pid = Integer.parseInt(p.trim());
                    List<CashierSaleItem> cart = getCart(session, invoiceId);
                    cart.removeIf(item -> item.getPhysicalId() == pid);
                    saveCart(session, invoiceId, cart);
                } catch (NumberFormatException ignored) {
                    // physicalId không hợp lệ — bỏ qua
                }
            }
        }

        redirectBack(request, response);
    }

    // ── Đóng hóa đơn đang active (hỏi xác nhận đã xử lý ở JS phía JSP) ────
    // Nếu còn nhiều hóa đơn: xóa hóa đơn này và chuyển sang hóa đơn kề bên.
    // Nếu chỉ còn 1 hóa đơn: xóa giỏ hàng nhưng giữ lại tab (không xóa tab).
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

            String reason = request.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) reason = "Không có lý do";

            if (map.size() <= 1) {
                // Chỉ còn 1 hóa đơn — xóa giỏ hàng nhưng giữ tab
                map.put(invoiceId, new ArrayList<>());
            } else {
                // Tìm hóa đơn kề để chuyển sang sau khi đóng
                String nextId = findNeighborInvoice(map, invoiceId);
                map.remove(invoiceId);
                session.setAttribute(ACTIVE_KEY, nextId);
            }

            session.setAttribute(MAP_KEY, map);
            // Xóa thông tin khách hàng của hóa đơn vừa đóng
            getCustomerFormMap(session).remove(invoiceId);
            session.setAttribute("cancelSuccess",
                    "Đã đóng " + label + ". Lý do: " + reason);
        }

        response.sendRedirect(request.getContextPath() + "/cashier");
    }

    // ── Tạo hóa đơn mới và chuyển sang hóa đơn mới ─────────────────────────
    private void handleNewInvoice(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);

        // Tăng bộ đếm để tạo ID và tên duy nhất
        int counter = getCounter(session) + 1;
        session.setAttribute(COUNTER_KEY, counter);

        String newId = "inv_" + counter;
        map.put(newId, new ArrayList<>());
        session.setAttribute(MAP_KEY, map);
        session.setAttribute(ACTIVE_KEY, newId);

        response.sendRedirect(request.getContextPath() + "/cashier");
    }

    // ── Lưu thông tin khách hàng đang nhập vào session theo invoiceId ────────
    // Được gọi tự động bằng fetch() trước khi chuyển tab, không reload trang.
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
                // Map đúng theo DB: Customer + Invoice
                form.setCustomerId(emptyToNull(request.getParameter("customerId")));
                form.setPhone(emptyToNull(request.getParameter("phone")));
                form.setFullName(emptyToNull(request.getParameter("fullName")));
                form.setEmail(emptyToNull(request.getParameter("email")));
                form.setAddress(emptyToNull(request.getParameter("address")));
                // Invoice fields
                String pm = request.getParameter("paymentMethod");
                form.setPaymentMethod(pm != null && !pm.isEmpty() ? pm : "CASH");
                form.setNote(emptyToNull(request.getParameter("note")));
                // Discount amount
                try {
                    String disc = request.getParameter("discountAmount");
                    if (disc != null && !disc.trim().isEmpty()) {
                        form.setDiscountAmount(new java.math.BigDecimal(disc.trim()));
                    }
                } catch (NumberFormatException ignored) {}

                getCustomerFormMap(session).put(invoiceId, form);
            }
        }

        // Trả về JSON đơn giản — JS không cần xử lý response
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"ok\":true}");
    }

    // ── Chuyển sang hóa đơn khác (đổi tab active) ──────────────────────────
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

    // ── Quay về cashier, giữ nguyên bộ lọc đang chọn ──────────────────────
    private void redirectBack(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String rd = request.getParameter("redirect");
        if (rd != null && !rd.trim().isEmpty()) {
            response.sendRedirect(rd);
        } else {
            response.sendRedirect(request.getContextPath() + "/cashier");
        }
    }

    // ── Helpers: quản lý invoiceMap ─────────────────────────────────────────

    /** Lấy invoiceMap từ session, tạo mới nếu chưa có */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, List<CashierSaleItem>> getInvoiceMap(HttpSession session) {
        Object obj = session.getAttribute(MAP_KEY);
        if (obj instanceof LinkedHashMap) {
            return (LinkedHashMap<String, List<CashierSaleItem>>) obj;
        }
        LinkedHashMap<String, List<CashierSaleItem>> map = new LinkedHashMap<>();
        session.setAttribute(MAP_KEY, map);
        return map;
    }

    /** Lấy giỏ hàng của 1 hóa đơn cụ thể, tạo mới nếu chưa có */
    private List<CashierSaleItem> getCart(HttpSession session, String invoiceId) {
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        return map.getOrDefault(invoiceId, new ArrayList<>());
    }

    /** Lưu giỏ hàng vào hóa đơn cụ thể */
    private void saveCart(HttpSession session, String invoiceId, List<CashierSaleItem> cart) {
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        map.put(invoiceId, cart);
        session.setAttribute(MAP_KEY, map);
    }

    /** Lấy ID hóa đơn đang active, nếu không có thì lấy hóa đơn đầu tiên */
    private String getActiveInvoiceId(HttpSession session) {
        String activeId = (String) session.getAttribute(ACTIVE_KEY);
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        if (activeId != null && map.containsKey(activeId)) return activeId;
        // activeId không hợp lệ — lấy hóa đơn đầu tiên
        if (!map.isEmpty()) {
            String firstId = map.keySet().iterator().next();
            session.setAttribute(ACTIVE_KEY, firstId);
            return firstId;
        }
        return null;
    }

    /** Lấy nhãn hiển thị của hóa đơn (vd: "Hóa đơn 1") */
    private String getInvoiceLabel(HttpSession session, String invoiceId) {
        if (invoiceId == null) return "hóa đơn";
        // Tìm vị trí trong map để lấy số thứ tự hiển thị
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        int idx = 1;
        for (String key : map.keySet()) {
            if (key.equals(invoiceId)) return "Hóa đơn " + idx;
            idx++;
        }
        return "hóa đơn";
    }

    /** Tìm hóa đơn kề bên để chuyển sang khi đóng 1 hóa đơn */
    private String findNeighborInvoice(LinkedHashMap<String, List<CashierSaleItem>> map, String removedId) {
        String prev = null;
        for (String key : map.keySet()) {
            if (key.equals(removedId)) {
                // Nếu có hóa đơn trước thì dùng, không thì dùng hóa đơn tiếp theo
                return prev != null ? prev : getNextKey(map, removedId);
            }
            prev = key;
        }
        return map.keySet().iterator().next();
    }

    /** Lấy key tiếp theo sau key cho trước trong map */
    private String getNextKey(LinkedHashMap<String, List<CashierSaleItem>> map, String currentKey) {
        boolean found = false;
        for (String key : map.keySet()) {
            if (found) return key;
            if (key.equals(currentKey)) found = true;
        }
        return map.keySet().iterator().next();
    }

    /** Đảm bảo session luôn có ít nhất 1 hóa đơn */
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

    /** Lấy giá trị bộ đếm hóa đơn từ session */
    private int getCounter(HttpSession session) {
        Object c = session.getAttribute(COUNTER_KEY);
        return (c instanceof Integer) ? (Integer) c : 0;
    }

    /** Lấy branchId của nhân viên đang đăng nhập từ session */
    private int getBranchId(HttpSession session) {
        Object b = session.getAttribute("branchId");
        return (b instanceof Integer) ? (Integer) b : 1;
    }

    /** Lấy map lưu thông tin khách hàng theo invoiceId */
    @SuppressWarnings("unchecked")
    private java.util.Map<String, InvoiceCustomerForm> getCustomerFormMap(HttpSession session) {
        Object obj = session.getAttribute(CUSTOMER_MAP_KEY);
        if (obj instanceof java.util.Map) {
            return (java.util.Map<String, InvoiceCustomerForm>) obj;
        }
        java.util.Map<String, InvoiceCustomerForm> map = new java.util.LinkedHashMap<>();
        session.setAttribute(CUSTOMER_MAP_KEY, map);
        return map;
    }

    /** Chuyển chuỗi rỗng thành null */
    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}