package com.techshop.servlet;

import com.techshop.dao.InvoiceDAO;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.InvoiceCustomerForm;

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
 *   1. Nhận form từ cashier.jsp (POST /invoice/create)
 *   2. Lấy giỏ hàng của hóa đơn active từ session
 *   3. Gọi InvoiceDAO.createInvoice() — toàn bộ trong 1 DB transaction
 *   4. Nếu thành công:
 *      - Xóa hóa đơn đó khỏi invoiceMap trong session
 *      - Xóa customerForm của hóa đơn đó
 *      - Redirect sang /invoice/print?id=xxx
 *   5. Nếu thất bại: set flash error, redirect về /cashier
 */
@WebServlet("/invoice/create")
public class InvoiceCreateServlet extends HttpServlet {

    private static final String MAP_KEY          = "invoiceMap";
    private static final String ACTIVE_KEY       = "activeInvoiceId";
    private static final String CUSTOMER_MAP_KEY = "customerFormMap";
    private static final String COUNTER_KEY      = "invoiceCounter";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // ── Lấy invoiceId active và giỏ hàng tương ứng ──────────────────
        String activeInvoiceId = (String) session.getAttribute(ACTIVE_KEY);
        LinkedHashMap<String, List<CashierSaleItem>> invoiceMap = getInvoiceMap(session);
        List<CashierSaleItem> items = invoiceMap.get(activeInvoiceId);

        if (items == null || items.isEmpty()) {
            session.setAttribute("cartError", "Giỏ hàng trống, không thể tạo hóa đơn.");
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // ── Đọc thông tin từ form ────────────────────────────────────────
        InvoiceCustomerForm form = buildFormFromRequest(request);

        // Kiểm tra bắt buộc: phải có SĐT
        if (form.getPhone() == null || form.getPhone().trim().isEmpty()) {
            session.setAttribute("cartError", "Vui lòng nhập số điện thoại khách hàng.");
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        // ── Lấy thông tin session ────────────────────────────────────────
        int branchId  = getIntAttr(session, "branchId",  1);
        int cashierId = getIntAttr(session, "userId",    0);

        if (cashierId <= 0) {
            session.setAttribute("cartError", "Phiên đăng nhập không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        boolean saveCustomer = "true".equals(request.getParameter("saveCustomer"));

        System.out.println("[InvoiceCreate] phone=" + form.getPhone()
            + " customerId=" + form.getCustomerId()
            + " fullName=" + form.getFullName()
            + " saveCustomer=" + saveCustomer
            + " branchId=" + branchId
            + " cashierId=" + cashierId
            + " cartSize=" + items.size());

        // ── Gọi DAO tạo hóa đơn (1 DB transaction) ──────────────────────
        InvoiceDAO dao = new InvoiceDAO();
        int invoiceId = dao.createInvoice(form, items, branchId, cashierId, saveCustomer);

        if (invoiceId <= 0) {
            System.err.println("[InvoiceCreate] FAILED — invoiceId=" + invoiceId);
            session.setAttribute("cartError",
                "Tạo hóa đơn thất bại. Có thể sản phẩm vừa được bán bởi ca khác. Vui lòng kiểm tra lại giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/cashier");
            return;
        }

        System.out.println("[InvoiceCreate] SUCCESS — invoiceId=" + invoiceId);

        // ── Thành công: dọn dẹp session ─────────────────────────────────
        // Xóa hóa đơn vừa thanh toán khỏi invoiceMap
        invoiceMap.remove(activeInvoiceId);

        // Xóa customerForm của hóa đơn vừa thanh toán
        getCustomerFormMap(session).remove(activeInvoiceId);

        // Nếu còn hóa đơn khác → chuyển active sang hóa đơn đầu tiên còn lại
        // Nếu không còn hóa đơn nào → tạo hóa đơn mới rỗng
        if (invoiceMap.isEmpty()) {
            int counter = getIntAttr(session, COUNTER_KEY, 0) + 1;
            session.setAttribute(COUNTER_KEY, counter);
            String newId = "inv_" + counter;
            invoiceMap.put(newId, new ArrayList<>());
            session.setAttribute(ACTIVE_KEY, newId);
        } else {
            String nextId = invoiceMap.keySet().iterator().next();
            session.setAttribute(ACTIVE_KEY, nextId);
        }

        session.setAttribute(MAP_KEY, invoiceMap);

        // ── Redirect sang trang in hóa đơn ──────────────────────────────
        response.sendRedirect(request.getContextPath() + "/invoice/print?id=" + invoiceId);
    }

    // cashier.jsp dùng method="get" → hỗ trợ cả GET (chuyển sang POST)
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