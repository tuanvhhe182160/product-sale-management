package com.techshop.servlet;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.techshop.dao.PasswordDAO;
import com.techshop.dao.UserDAO;
import com.techshop.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private String clientId;
    
    @Override
    public void init() throws ServletException {
        // Load Client ID from properties file
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("com/techshop/conf/oauth.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            clientId = prop.getProperty("google.client.id");
        } catch (Exception e) {
            System.err.println("Error loading OAuth config: " + e.getMessage());
            // Fallback to demo mode if config not found
            clientId = null;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("googleClientId", this.clientId);
        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Check for message parameter (after logout)
        String message = request.getParameter("message");
        if (message != null) {
            request.setAttribute("message", message);
        }
        
        // Đọc lỗi từ URL (do sendRedirect gửi qua)
        String error = request.getParameter("error");
        if (error != null) {
            request.setAttribute("error", error);
        }
        
        //request.getRequestDispatcher("/views/auth/login-real.jsp").forward(request, response);
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String loginType = request.getParameter("loginType");
        //PASSWORD
        if ("password".equals(loginType)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
        
            PasswordDAO dao = new PasswordDAO();
            User user = null;
            try {
                user = dao.authenticateWithPassword(email, password);
            } catch (Exception ex) {
                Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            if (user != null) {
                // Success
                System.out.println("Found user");
                authorizeToSystem(request, response, user.getEmail());
            } else {
                // Failed
                System.out.println("Found 0 user");
                response.sendRedirect(request.getContextPath() + "/login?error=Invalid email or password");
            }
        } 
        
        //GOOGLE
        else if ("google".equals(loginType)) {
            String credential = request.getParameter("credential"); //nhận credential từ client. Goolr login bắt đầu từ phía server
        
            // Verify Google token
            String email = verifyGoogleToken(credential);
        
            //Thất bại
            if (email == null) {
                request.setAttribute("error", "Invalid Google credentials");
                //request.getRequestDispatcher("/views/auth/login-real.jsp").forward(request, response);
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
        
            //thành công thì đi đến kiểm tra email có quyền gì trong hệ thống. Google stops here.
            authorizeToSystem(request, response, email);
        }
    }
    
    /**
     * Verify Google ID Token and extract email
     */
    private String verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                GsonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(clientId))
            .build();
            
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail(); //lấy email trong payload
                boolean emailVerified = payload.getEmailVerified(); //boolean kiểm tra email
                
                if (emailVerified) {
                    return email; //trả về email
                }
            }
        } catch (Exception e) {
            System.err.println("Error verifying Google token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Process login after email verification
     * System login
     */
    private void authorizeToSystem(HttpServletRequest request, HttpServletResponse response, String email)
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getByEmail(email.trim()); //kiểm tra quyền của user (data trong db)
        
        //không có quyền = cook
        if (user == null) {
            request.setAttribute("error", 
                "Email not authorized. Only registered employees can access this system. " +
                "Please contact your administrator.");
            //request.getRequestDispatcher("/views/auth/login-real.jsp").forward(request, response);
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
        session.setAttribute("avatar", user.getAvatarUrl());
        System.out.println("Avatar URL = " + user.getAvatarUrl());
        
        if (user.getBranchId() != null) {
            session.setAttribute("branchId", user.getBranchId());
            session.setAttribute("branchName", user.getBranchName());
        }
        
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
        
        System.out.println("✅ User logged in: " + user.getEmail() + " (" + user.getRoleName() + ")");
        
        // Redirect to dashboard or saved URL
        String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
        if (redirectUrl != null) {
            session.removeAttribute("redirectAfterLogin");
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}