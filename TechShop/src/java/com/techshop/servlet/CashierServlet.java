package com.techshop.servlet;

import com.techshop.dao.ProductCategoryDAO;
import com.techshop.dao.ProductModelDAO;
import com.techshop.dao.ProductSearchDAO;
import com.techshop.dao.VariantDAO;
import com.techshop.model.CashierSaleItem;
import com.techshop.model.InvoiceCustomerForm;
import com.techshop.model.ProductCategory;
import com.techshop.model.ProductModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/cashier")
public class CashierServlet extends HttpServlet {

    private static final int    PAGE_SIZE   = 20;
    private static final String MAP_KEY          = "invoiceMap";
    private static final String ACTIVE_KEY       = "activeInvoiceId";
    private static final String COUNTER_KEY      = "invoiceCounter";
    private static final String CUSTOMER_MAP_KEY = "customerFormMap";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);

        // Đảm bảo session luôn có ít nhất 1 hóa đơn
        ensureAtLeastOneInvoice(session);

        int branchId = 1;
        if (session.getAttribute("branchId") != null) {
            branchId = (int) session.getAttribute("branchId");
        }

        // Đọc bộ lọc tìm kiếm từ request
        String keyword    = emptyIfNull(request.getParameter("keyword"));
        String categoryId = emptyIfNull(request.getParameter("categoryId"));
        String modelId    = emptyIfNull(request.getParameter("modelId"));
        String sku        = emptyIfNull(request.getParameter("sku"));

        int page = 1;
        try {
            String p = request.getParameter("page");
            if (p != null && !p.isEmpty()) page = Integer.parseInt(p);
            if (page < 1) page = 1;
        } catch (NumberFormatException ignored) {}

        // Dropdown danh mục
        ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
        List<ProductCategory> categories = categoryDAO.getActiveCategories();

        // Dropdown model (lọc theo danh mục nếu có)
        ProductModelDAO modelDAO = new ProductModelDAO();
        List<ProductModel> models;

        if (!categoryId.isEmpty()) {
            models = modelDAO.getActiveModelsByCategoryId(Integer.parseInt(categoryId));
            // Kiểm tra modelId hiện tại có thuộc danh mục đang chọn không
            if (!modelId.isEmpty()) {
                boolean belongs = false;
                for (ProductModel m : models) {
                    if (String.valueOf(m.getModelId()).equals(modelId)) { belongs = true; break; }
                }
                if (!belongs) modelId = "";
            }
        } else {
            models = new VariantDAO().getAllActiveModels();
        }

        // Tìm kiếm sản phẩm (1 row = 1 IMEI)
        ProductSearchDAO searchDAO = new ProductSearchDAO();
        List<CashierSaleItem> list = searchDAO.searchForCashier(
                keyword, categoryId, modelId, sku, branchId, page, PAGE_SIZE);
        int totalItems = searchDAO.countForCashier(keyword, categoryId, modelId, sku, branchId);

        int totalPages = (int) Math.ceil(totalItems * 1.0 / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // Lấy hóa đơn đang active và giỏ hàng của nó
        String activeInvoiceId = getActiveInvoiceId(session);
        LinkedHashMap<String, List<CashierSaleItem>> invoiceMap = getInvoiceMap(session);
        List<CashierSaleItem> activeCart = invoiceMap.getOrDefault(activeInvoiceId, new ArrayList<>());

        // Tính tổng tiền hóa đơn active
        java.math.BigDecimal cartTotal = java.math.BigDecimal.ZERO;
        for (CashierSaleItem ci : activeCart) {
            if (ci.getUnitPrice() != null) cartTotal = cartTotal.add(ci.getUnitPrice());
        }

        // Lấy discountAmount từ customerForm đang lưu trong session (nếu có)
        InvoiceCustomerForm tempForm = getCustomerFormMap(session).get(activeInvoiceId);
        java.math.BigDecimal discountAmount = (tempForm != null && tempForm.getDiscountAmount() != null)
                ? tempForm.getDiscountAmount() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal finalAmount = cartTotal.subtract(discountAmount).max(java.math.BigDecimal.ZERO);

        // Gom physicalId của giỏ hàng active thành chuỗi để check "đã thêm" trong JSP
        StringBuilder cartIds = new StringBuilder(",");
        for (CashierSaleItem ci : activeCart) {
            cartIds.append(ci.getPhysicalId()).append(",");
        }

        // Tính range trang hiển thị trong pagination (tối đa 5 trang xung quanh trang hiện tại)
        int pgStart = Math.max(1, page - 2);
        int pgEnd   = Math.min(totalPages, page + 2);

        // Chuẩn bị danh sách tab hóa đơn để JSP chỉ cần render, không tính logic
        // Mỗi phần tử: id, label, itemCount, isActive, hasItems
        List<Map<String, Object>> invoiceTabs = new ArrayList<>();
        int tabIdx = 0;
        for (Map.Entry<String, List<CashierSaleItem>> entry : invoiceMap.entrySet()) {
            tabIdx++;
            Map<String, Object> tab = new java.util.LinkedHashMap<>();
            tab.put("id",        entry.getKey());
            tab.put("label",     "Hoa don " + tabIdx);
            tab.put("labelVi",   "H\u00f3a \u0111\u01a1n " + tabIdx);
            tab.put("itemCount", entry.getValue().size());
            tab.put("isActive",  entry.getKey().equals(activeInvoiceId));
            tab.put("hasItems",  !entry.getValue().isEmpty());
            invoiceTabs.add(tab);
        }

        // Truyền dữ liệu xuống JSP
        request.setAttribute("list",            list);
        request.setAttribute("totalPages",      totalPages);
        request.setAttribute("currentPage",     page);
        request.setAttribute("totalItems",      totalItems);
        request.setAttribute("pgStart",         pgStart);
        request.setAttribute("pgEnd",           pgEnd);
        request.setAttribute("keyword",         keyword);
        request.setAttribute("categoryId",      categoryId);
        request.setAttribute("modelId",         modelId);
        request.setAttribute("sku",             sku);
        request.setAttribute("categories",      categories);
        request.setAttribute("models",          models);
        request.setAttribute("invoiceMap",      invoiceMap);
        request.setAttribute("invoiceTabs",     invoiceTabs);
        request.setAttribute("activeInvoiceId", activeInvoiceId);
        request.setAttribute("activeCart",      activeCart);
        request.setAttribute("cartTotal",       cartTotal);
        request.setAttribute("discountAmount",  discountAmount);
        request.setAttribute("finalAmount",     finalAmount);
        request.setAttribute("cartIds",         cartIds.toString());

        // Lấy thông tin khách hàng đang nhập của hóa đơn active (nếu có)
        InvoiceCustomerForm activeCustomerForm = getCustomerFormMap(session).get(activeInvoiceId);
        if (activeCustomerForm == null) activeCustomerForm = new InvoiceCustomerForm();
        request.setAttribute("activeCustomerForm", activeCustomerForm);

        request.getRequestDispatcher("/views/cashier/cashier.jsp")
                .forward(request, response);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

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

    private void ensureAtLeastOneInvoice(HttpSession session) {
        LinkedHashMap<String, List<CashierSaleItem>> map = getInvoiceMap(session);
        if (map.isEmpty()) {
            Object c = session.getAttribute(COUNTER_KEY);
            int counter = (c instanceof Integer) ? (Integer) c + 1 : 1;
            session.setAttribute(COUNTER_KEY, counter);
            String id = "inv_" + counter;
            map.put(id, new ArrayList<>());
            session.setAttribute(MAP_KEY, map);
            session.setAttribute(ACTIVE_KEY, id);
        }
    }

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

    private String emptyIfNull(String s) {
        return s == null ? "" : s.trim();
    }
}