package com.techshop.servlet;

import com.techshop.dao.UserDAOTest;
import com.techshop.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    //private static final String CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID_HERE";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idTokenString = request.getParameter("credential");        
        
        String email = request.getParameter("email");
        
        // If email not provided, show error
        if (email == null || email.trim().isEmpty()) {
            // For demo: show a simple form to enter email
            showSimpleEmailForm(request, response);
            return;
        }
        
        // Verify with database
        UserDAOTest userDAO = new UserDAOTest();
        User user = userDAO.getByEmail(email.trim());
        
        if (user == null) {
            // User not found or not active
            request.setAttribute("error", "Email not authorized or account is inactive. Please contact administrator.");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Login successful - create session
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userRole", user.getRoleName());
        session.setAttribute("roleId", user.getRoleId());
        
        if (user.getBranchId() != null) {
            session.setAttribute("branchId", user.getBranchId());
            session.setAttribute("branchName", user.getBranchName());
        }
        
        // Set session timeout (30 minutes)
        session.setMaxInactiveInterval(30 * 60);
        
        // Log login activity (optional for iteration 1)
        System.out.println("User logged in: " + user.getEmail() + " (" + user.getRoleName() + ")");
        
        // Redirect based on role
        String redirectUrl = getRedirectUrlByRole(request, user.getRoleName());
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * For DEMO: Show simple form to enter email
     * REMOVE this in production when real Google OAuth is implemented
     */
    private void showSimpleEmailForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>Demo Login</title>");
        response.getWriter().println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body class='bg-light'>");
        response.getWriter().println("<div class='container mt-5'>");
        response.getWriter().println("<div class='row justify-content-center'>");
        response.getWriter().println("<div class='col-md-6'>");
        response.getWriter().println("<div class='card'>");
        response.getWriter().println("<div class='card-header bg-primary text-white'>");
        response.getWriter().println("<h4>Demo Login - Enter Email</h4>");
        response.getWriter().println("</div>");
        response.getWriter().println("<div class='card-body'>");
        response.getWriter().println("<p class='text-danger'><strong>DEMO MODE:</strong> Enter your email from database</p>");
        response.getWriter().println("<form method='post' action='" + request.getContextPath() + "/login'>");
        response.getWriter().println("<div class='mb-3'>");
        response.getWriter().println("<label class='form-label'>Email:</label>");
        response.getWriter().println("<input type='email' name='email' class='form-control' placeholder='tuanvhhe182160@fpt.edu.vn' required>");
        response.getWriter().println("</div>");
        response.getWriter().println("<button type='submit' class='btn btn-primary w-100'>Login</button>");
        response.getWriter().println("</form>");
        response.getWriter().println("<hr>");
        response.getWriter().println("<p class='text-muted small'>Available test accounts:</p>");
        response.getWriter().println("<ul class='small text-muted'>");
        response.getWriter().println("<li>tuanvhhe182160@fpt.edu.vn (Admin)</li>");
        response.getWriter().println("<li>manager.hn1@store.com (Shop Manager)</li>");
        response.getWriter().println("<li>cashier.hn1@store.com (Cashier)</li>");
        response.getWriter().println("</ul>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</div>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }
    
    /**
     * Determine redirect URL based on user role
     */
    private String getRedirectUrlByRole(HttpServletRequest request, String roleName) {
        String contextPath = request.getContextPath();
        
        switch (roleName) {
            case "Admin":
                return contextPath + "/dashboard";
            case "Shop Manager":
                return contextPath + "/dashboard";
            case "Cashier":
                return contextPath + "/dashboard";
            case "Accounting Staff":
                return contextPath + "/dashboard";
            case "Customer Service":
                return contextPath + "/dashboard";
            case "Technician":
                return contextPath + "/dashboard";
            default:
                return contextPath + "/dashboard";
        }
    }
    
    /**
     * PRODUCTION METHOD (commented out for Iteration 1)
     * Uncomment and use this when implementing real Google OAuth
     */
    /*
    private String verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                GsonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();
            
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                return email;
            }
        } catch (Exception e) {
            System.err.println("Error verifying Google token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    */
}

/*
 * ═══════════════════════════════════════════════════════════════
 * SETUP INSTRUCTIONS - READ BEFORE USING
 * ═══════════════════════════════════════════════════════════════
 * 
 * ITERATION 1 - DEMO MODE:
 * -----------------------
 * 1. Current code uses SIMPLIFIED login (enter email directly)
 * 2. This is FOR DEMO ONLY - suitable for iteration 1
 * 3. No actual Google OAuth verification (unsafe for production!)
 * 
 * TO USE DEMO MODE:
 * 1. Access: http://localhost:8080/store/login
 * 2. Click "Sign in with Google"
 * 3. Enter email from database (e.g., admin@store.com)
 * 4. System checks if email exists and is ACTIVE
 * 5. If yes → Create session and redirect
 * 
 * PRODUCTION SETUP (for later iterations):
 * ----------------------------------------
 * 1. Go to: https://console.cloud.google.com/
 * 2. Create new project: "TechShop Management"
 * 3. Enable Google+ API
 * 4. Create OAuth 2.0 credentials
 * 5. Add authorized redirect URIs:
 *    - http://localhost:8080/store/login
 *    - http://yourdomain.com/store/login
 * 6. Copy Client ID and paste in CLIENT_ID constant
 * 7. Uncomment verifyGoogleToken() method
 * 8. Remove showSimpleEmailForm() method
 * 9. Update doPost() to use real token verification
 * 
 * LIBRARIES NEEDED FOR PRODUCTION:
 * - google-api-client-1.35.2.jar
 * - google-oauth-client-1.34.1.jar
 * - gson-2.10.1.jar
 * 
 * Add these to WEB-INF/lib/ when implementing real OAuth
 */