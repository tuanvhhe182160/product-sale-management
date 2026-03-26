package com.techshop.servlet;

import com.techshop.dao.InvoiceDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.EntityType;
import com.techshop.model.InvoiceCustomerForm;
import com.techshop.model.LogAction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Servlet xử lý xác nhận thanh toán từ màn hình Cashier.
 *
 * Luồng:
 *   1. Duyệt TẤT CẢ hóa đơn đang mở có sản phẩm
 *   2. Mỗi hóa đơn → tạo 1 Invoice riêng trong DB (dùng customerForm riêng)
 *   3. Nếu tất cả thành công → dọn session, redirect sang trang in
 *   4. Nếu có lỗi → flash error, redirect về /cashier
 */
@WebServlet("/invoice/create")
public class InvoiceCreateServlet extends HttpServlet {

    private static final String MAP_KEY          = "invoiceMap";
    private static final String ACTIVE_KEY       = "activeInvoiceId";
    private static final String CUSTOMER_MAP_KEY = "customerFormMap";
    private static final String COUNTER_KEY      = "invoiceCounter";
    private final SystemLogDAO logDAO = new SystemLogDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // ── Lấy thông tin chung ──────────────────────────────────────────
        int branchId  = getIntAttr(session, "branchId", 1);
        int cashierId = getIntAttr(session, "userId",   0);

        if (cashierId <= 0) {
            session.setAttribute("cartError", "Phiên đăng nhập không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // ── Lưu form hóa đơn active hiện tại vào customerFormMap ─────────
        String activeInvoiceId = (String) session.getAttribute(ACTIVE_KEY);
        InvoiceCustomerForm activeForm = buildFormFromRequest(request);
        boolean saveCustomer = "true".equals(request.getParameter("saveCustomer"));
        Map<String, InvoiceCustomerForm> customerFormMap = getCustomerFormMap(session);
        customerFormMap.put(activeInvoiceId, activeForm);

        // ── Duyệt tất cả hóa đơn có sản phẩm ───────────────────────────
        LinkedHashMap<String, List<CashierSaleItem>> invoiceMap = getInvoiceMap(session);
        List<Integer> createdInvoiceIds = new ArrayList<>();
        List<String> processedKeys = new ArrayList<>();

        for (Map.Entry<String, List<CashierSaleItem>> entry : invoiceMap.entrySet()) {
            String invKey = entry.getKey();
            List<CashierSaleItem> items = entry.getValue();

            if (items == null || items.isEmpty()) continue;

            // Lấy customerForm riêng của hóa đơn này
            InvoiceCustomerForm form = customerFormMap.get(invKey);
            if (form == null || form.getPhone() == null || form.getPhone().trim().isEmpty()) {
                // Hóa đơn có sản phẩm nhưng chưa nhập SĐT → báo lỗi
                session.setAttribute("cartError",
                    "Hóa đơn chưa có thông tin khách hàng. Vui lòng nhập SĐT cho tất cả hóa đơn trước khi thanh toán.");
                response.sendRedirect(request.getContextPath() + "/cashier");
                return;
            }

            // Tạo hóa đơn riêng cho tab này
            InvoiceDAO dao = new InvoiceDAO();
            int invoiceId = dao.createInvoice(form, items, branchId, cashierId, saveCustomer, form.getRedeemPoints());

            if (invoiceId <= 0) {
                System.err.println("[InvoiceCreate] FAILED for invKey=" + invKey);
                session.setAttribute("cartError",
                    "Tạo hóa đơn thất bại. Có thể sản phẩm vừa được bán bởi ca khác. Vui lòng kiểm tra lại giỏ hàng.");
                response.sendRedirect(request.getContextPath() + "/cashier");
                return;
            }

            System.out.println("[InvoiceCreate] SUCCESS invKey=" + invKey + " => invoiceId=" + invoiceId);
            // --- GHI LOG ---
            String customerName = (form.getFullName() != null && !form.getFullName().isEmpty()) ? form.getFullName() : "Khách lẻ";
            logDAO.logAction(
                cashierId, 
                LogAction.CREATE_INVOICE, 
                EntityType.INVOICE, 
                invoiceId, 
                request.getRemoteAddr(), 
                "Thu ngân thanh toán thành công hóa đơn mua hàng (Khách: " + customerName + " - SĐT: " + form.getPhone() + ")"
            );
            // -----------------------
            createdInvoiceIds.add(invoiceId);
            processedKeys.add(invKey);
        }

        if (createdInvoiceIds.isEmpty()) {
            session.setAttribute("cartError", "Không có hóa đơn nào có sản phẩm để thanh toán.");
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // ── Dọn dẹp session ─────────────────────────────────────────────
        for (String key : processedKeys) {
            invoiceMap.remove(key);
            customerFormMap.remove(key);
        }

        // Tạo hóa đơn mới rỗng nếu không còn tab nào
        if (invoiceMap.isEmpty()) {
            int counter = getIntAttr(session, COUNTER_KEY, 0) + 1;
            session.setAttribute(COUNTER_KEY, counter);
            String newId = "inv_" + counter;
            invoiceMap.put(newId, new ArrayList<>());
            session.setAttribute(ACTIVE_KEY, newId);
        } else {
            session.setAttribute(ACTIVE_KEY, invoiceMap.keySet().iterator().next());
        }
        session.setAttribute(MAP_KEY, invoiceMap);

        // ── Redirect sang trang in ──────────────────────────────────────
        // Truyền tất cả invoiceId qua query string
        StringBuilder qs = new StringBuilder();
        for (int id : createdInvoiceIds) {
            if (qs.length() > 0) qs.append("&");
            qs.append("id=").append(id);
        }
        response.sendRedirect(request.getContextPath() + "/invoice/print?" + qs.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    // ── Build InvoiceCustomerForm từ request params ──────────────────────
    private InvoiceCustomerForm buildFormFromRequest(HttpServletRequest request) {
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
                form.setDiscountAmount(new BigDecimal(disc.trim()));
            }
        } catch (NumberFormatException ignored) {}
        try {
            String rp = request.getParameter("redeemPoints");
            if (rp != null && !rp.trim().isEmpty()) {
                form.setRedeemPoints(Integer.parseInt(rp.trim()));
            }
        } catch (NumberFormatException ignored) {}
        return form;
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, List<CashierSaleItem>> getInvoiceMap(HttpSession session) {
        Object obj = session.getAttribute(MAP_KEY);
        if (obj instanceof LinkedHashMap) {
            return (LinkedHashMap<String, List<CashierSaleItem>>) obj;
        }
        return new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private Map<String, InvoiceCustomerForm> getCustomerFormMap(HttpSession session) {
        Object obj = session.getAttribute(CUSTOMER_MAP_KEY);
        if (obj instanceof Map) return (Map<String, InvoiceCustomerForm>) obj;
        return new LinkedHashMap<>();
    }

    private int getIntAttr(HttpSession session, String key, int defaultVal) {
        Object val = session.getAttribute(key);
        return (val instanceof Integer) ? (Integer) val : defaultVal;
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
