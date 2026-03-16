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

// Filter áp dụng cho tất cả các trang chức năng
@WebFilter(filterName = "AuthFilter", urlPatterns = {
    "/dashboard",
    "/user",
    "/user/*",
    "/branch/*",
    "/category/*",
    "/ProductCategory",
    "/model/*",
    "/ProductModel",
    "/ProductModel/*",
    "/variant",
    "/variant/*",
    "/product/*",
    "/product-detail",
    "/cashier-mgmt",
    "/cashier",
    "/cart",
    "/invoice",
    "/invoice/*",
    "/inventory/*",
    "/warranty/*",
    "/customer/*",
    "/report",
    "/report/*",
    "/admin/*",
    "/accounting/*"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Lấy đường dẫn tương đối (Ví dụ: /user/list, /invoice/create)
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length()); // Bỏ phần /techshop đi

        // 1. CHECK LOGIN (AUTHENTICATION)
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            // Chưa login -> Lưu trang muốn vào và đá về login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // 2. CHECK ROLE (AUTHORIZATION)
        // Lấy role từ session (Chuỗi này phải khớp với Database: 'Admin', 'Shop Manager'...)
        String userRole = (String) session.getAttribute("userRole");

        if (checkPermission(path, userRole)) {
            // Được phép đi tiếp
            chain.doFilter(request, response);
        } else {
            // Không có quyền -> Trang 403
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        }
    }

    /**
     * Hàm phân quyền chi tiết dựa trên Use Case
     */
    private boolean checkPermission(String path, String role) {
        if (role == null) return false;

        // --- 1. ADMIN (UC03 -> UC07) ---
        // Admin được vào mọi ngóc ngách hệ thống
        if ("Admin".equalsIgnoreCase(role)) {
            return true; 
        }

        // --- 2. SHOP MANAGER (UC08 -> UC16) ---
        if ("Shop Manager".equalsIgnoreCase(role)) {
            // Được phép: Dashboard, Kho, Sản phẩm (tại cửa hàng), Báo cáo cửa hàng, Quản lý Cashier
            if (path.equals("/dashboard") ||
                path.equals("/cashier-mgmt") ||  // Quản lý cashier chi nhánh
                path.startsWith("/inventory") || // Nhập kho, chuyển kho, kiểm kê
                path.startsWith("/product") ||   // Quản lý sản phẩm tại cửa hàng
                path.startsWith("/report") ||    // Xem báo cáo cửa hàng
                path.startsWith("/invoice")) {   // Xem hóa đơn của cửa hàng (thường Manager cần xem)
                return true;
            }
            return false; // Chặn: /user, /branch, /accounting...
        }

        // --- 3. CASHIER (UC17 -> UC24) ---
        if ("Cashier".equalsIgnoreCase(role)) {
            // Được phép: Dashboard, POS, Bán hàng (Invoice), Khách hàng, Tìm kiếm sản phẩm
            if (path.equals("/dashboard") ||
                path.startsWith("/cashier") ||   // POS bán hàng
                path.startsWith("/cart") ||      // Giỏ hàng
                path.startsWith("/invoice") ||   // Thanh toán, xuất hóa đơn
                path.startsWith("/customer") ||  // Tạo khách hàng
                path.startsWith("/product") ||   // Tìm kiếm sản phẩm (UC17)
                path.startsWith("/variant")) {   // Tìm biến thể sản phẩm để bán
                return true;
            }
            // Chặn: /inventory (không được nhập kho), /report (không xem doanh thu tổng)
            return false;
        }

        // --- 4. ACCOUNTING STAFF (UC25 -> UC28) ---
        if ("Accounting Staff".equalsIgnoreCase(role)) {
            // Được phép: Dashboard, Xem doanh thu, Xem hóa đơn
            if (path.equals("/dashboard") ||
                path.startsWith("/report/financial") ||    // Báo cáo doanh thu/lợi nhuận
                path.startsWith("/accounting/invoices") ||
                path.startsWith("/invoice")) {   // Xem danh sách hóa đơn
                return true;
            }
            return false;
        }

        // --- 5. CUSTOMER SERVICE (UC29 -> UC33) ---
        if ("Customer Service".equalsIgnoreCase(role)) {
            // Được phép: Dashboard, Bảo hành, Khách hàng, Tra cứu đơn hàng
            if (path.equals("/dashboard") ||
                path.startsWith("/warranty") ||  // Tiếp nhận bảo hành
                path.startsWith("/customer") ||  // Tra cứu khách
                path.startsWith("/cs") ||
                path.startsWith("/invoice")) {   // Tra cứu đơn hàng
                return true;
            }
            return false;
        }

        // --- 6. TECHNICIAN (UC34) ---
        if ("Technician".equalsIgnoreCase(role)) {
            // Được phép: Dashboard, Cập nhật bảo hành
            if (path.equals("/dashboard") ||
                path.startsWith("/warranty")) {  // Cập nhật trạng thái sửa chữa
                return true;
            }
            return false;
        }

        return false; // chặn role lạ
    }

    @Override
    public void destroy() {
    }
}