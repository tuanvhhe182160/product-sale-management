<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - TechShop Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .login-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
            max-width: 450px;
            width: 100%;
        }
        .login-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px 30px;
            text-align: center;
        }
        .login-header h1 {
            font-size: 2rem;
            font-weight: 700;
            margin: 0;
            margin-bottom: 10px;
        }
        .login-header p {
            margin: 0;
            opacity: 0.9;
            font-size: 0.95rem;
        }
        .login-body {
            padding: 40px 30px;
        }
        .alert {
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 20px;
        }
        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 30px 0;
            color: #999;
        }
        .divider::before,
        .divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid #ddd;
        }
        .divider span {
            padding: 0 15px;
            font-size: 0.85rem;
        }
        .info-text {
            text-align: center;
            color: #666;
            font-size: 0.9rem;
            margin-top: 20px;
        }
        .info-text strong {
            color: #667eea;
        }
        #google-signin-button {
            display: flex;
            justify-content: center;
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <i class="fas fa-store fa-3x mb-3"></i>
            <h1>TechShop</h1>
            <p>Internal Management System</p>
        </div>
        
        <div class="login-body">
            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <i class="fas fa-exclamation-circle"></i>
                    <strong>Login Failed!</strong><br>
                    ${error}
                </div>
            </c:if>
            
            <!-- Success Message (after logout) -->
            <c:if test="${not empty message}">
                <div class="alert alert-success" role="alert">
                    <i class="fas fa-check-circle"></i>
                    ${message}
                </div>
            </c:if>
            
            <!-- Google One Tap Login -->
            <div id="g_id_onload"
                 data-client_id="${googleClientId}"
                 data-context="signin"
                 data-ux_mode="popup"
                 data-callback="handleCredentialResponse"
                 data-auto_prompt="false">
            </div>
            
            <!-- Google Sign-In Button -->
            <div id="google-signin-button">
                <div class="g_id_signin"
                     data-type="standard"
                     data-shape="rectangular"
                     data-theme="outline"
                     data-text="signin_with"
                     data-size="large"
                     data-logo_alignment="left"
                     data-width="350">
                </div>
            </div>
            
            <div class="divider">
                <span>Authorized Access Only</span>
            </div>
            
            <div class="info-text">
                <i class="fas fa-shield-alt"></i>
                Only registered employees can access this system.<br>
                <strong>Use your company Google account</strong>
            </div>
            
            <div class="alert alert-info mt-3 small">
                <i class="fas fa-info-circle"></i>
                Your email must be registered in the system by administrator.
            </div>
        </div>
    </div>
    
    <!-- Google Sign-In JavaScript Library -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    
    <!-- Handle Google OAuth Response -->
    <script>
        function handleCredentialResponse(response) {
            // Google returns JWT token in response.credential
            console.log("Google login successful, sending token to server...");
            
            // Create hidden form to submit token to server
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/login';
            
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'credential';
            input.value = response.credential;
            
            form.appendChild(input);
            document.body.appendChild(form);
            form.submit();
        }
        
        // Optional: Handle errors
        window.addEventListener('load', function() {
            // Check if Google Sign-In library loaded successfully
            if (typeof google === 'undefined') {
                console.error('Google Sign-In library failed to load');
                document.getElementById('google-signin-button').innerHTML = 
                    '<div class="alert alert-danger">Failed to load Google Sign-In. Please refresh the page.</div>';
            }
        });
    </script>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

