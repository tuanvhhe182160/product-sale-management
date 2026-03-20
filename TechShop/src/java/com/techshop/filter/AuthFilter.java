package com.techshop.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns = {
    "/dashboard",
    "/user", "/user/*",
    "/branch", "/branch/*",
    "/category", "/category/*",
    "/ProductCategory",
    "/model", "/model/*",
    "/ProductModel", "/ProductModel/*",
    "/variant", "/variant/*",
    "/product/*", "/product-detail",
    "/cashier", "/cart",
    "/invoice", "/invoice/*",
    "/inventory/*",
    "/transfer", "/transfer/*",
    "/cs/*",                   // CS warranty screens
    "/tech/*",                 // Technician warranty screens
    "/warranty/*",
    "/customer", "/customer/*",
    "/report", "/report/*",
    "/admin/*",
    "/accounting/*"            // Corrected: single star is valid servlet wildcard
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // 1. AUTHENTICATION
        HttpSession session = req.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            session = req.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        // 2. AUTHORIZATION
        String role = (String) session.getAttribute("userRole");

        if (checkPermission(path, role)) {
            chain.doFilter(request, response);
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }

    private boolean checkPermission(String path, String role) {
        if (role == null) return false;

        // ── ADMIN: full access ────────────────────────────────────────────
        if ("Admin".equalsIgnoreCase(role)) return true;

        // ── SHOP MANAGER ─────────────────────────────────────────────────
        if ("Shop Manager".equalsIgnoreCase(role)) {
            return path.equals("/dashboard")
                || path.startsWith("/admin/cashier-mgmt")   // fixed: match actual servlet URL
                || path.startsWith("/inventory")
                || path.startsWith("/transfer")
                || path.startsWith("/product")
                || path.startsWith("/report")
                || path.startsWith("/invoice");
        }

        // ── CASHIER ───────────────────────────────────────────────────────
        // NOTE: /variant/* removed — Cashier searches products via /cashier, not variant admin pages
        if ("Cashier".equalsIgnoreCase(role)) {
            return path.equals("/dashboard")
                || path.startsWith("/cashier")
                || path.startsWith("/cart")
                || path.startsWith("/invoice")
                || path.startsWith("/customer")
                || path.startsWith("/product");
        }

        // ── ACCOUNTING STAFF ──────────────────────────────────────────────
        if ("Accounting Staff".equalsIgnoreCase(role)) {
            return path.equals("/dashboard")
                || path.startsWith("/report/financial")
                || path.startsWith("/accounting")
                || path.startsWith("/invoice");
        }

        // ── CUSTOMER SERVICE ──────────────────────────────────────────────
        if ("Customer Service".equalsIgnoreCase(role)) {
            return path.equals("/dashboard")
                || path.startsWith("/cs")
                || path.startsWith("/warranty")
                || path.startsWith("/customer")
                || path.startsWith("/invoice");
        }

        // ── TECHNICIAN ────────────────────────────────────────────────────
        if ("Technician".equalsIgnoreCase(role)) {
            return path.equals("/dashboard")
                || path.startsWith("/tech")
                || path.startsWith("/warranty")
                || path.equals("/product-detail");
        }

        return false;
    }

    @Override
    public void destroy() {}
}