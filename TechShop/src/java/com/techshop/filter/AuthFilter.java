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
    "/user/*",
    "/branch/*",
    "/category/*",
    "/model/*",
    "/variant/*",
    "/product/*",
    "/invoice/*",
    "/inventory/*",
    "/warranty/*",
    "/customer/*",
    "/report/*"
})
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized - protecting resources");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Get current session (don't create new one)
        HttpSession session = httpRequest.getSession(false);
        
        // Get request URI
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (isLoggedIn) {
            // User is authenticated - allow access
            
            // Optional: Log access for audit
            String userEmail = (String) session.getAttribute("userEmail");
            String userRole = (String) session.getAttribute("userRole");
            System.out.println("Access granted: " + userEmail + " (" + userRole + ") → " + requestURI);
            
            // Continue to the requested page
            chain.doFilter(request, response);
            
        } else {
            // User is NOT authenticated - redirect to login
            
            System.out.println("Access denied: Not logged in → " + requestURI);
            
            // Save the original requested URL (to redirect back after login)
            session = httpRequest.getSession(true); // Create session to store redirect URL
            session.setAttribute("redirectAfterLogin", requestURI);
            
            // Redirect to login page
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }
}
