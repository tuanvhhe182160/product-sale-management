<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            padding: 20px;
        }
        .login-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
            max-width: 480px;
            width: 100%;
        }
        .login-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px 20px;
            text-align: center;
        }
        .login-header h1 {
            font-size: 1.8rem;
            font-weight: 700;
            margin: 0;
        }
        .login-body {
            padding: 30px;
        }
        /* Custom Tabs Styling */
        .nav-tabs {
            border-bottom: 2px solid #eee;
            margin-bottom: 25px;
            justify-content: center;
        }
        .nav-tabs .nav-link {
            border: none;
            color: #888;
            font-weight: 600;
            padding: 10px 20px;
        }
        .nav-tabs .nav-link.active {
            color: #667eea;
            border-bottom: 2px solid #667eea;
            background: transparent;
        }
        .form-label {
            font-weight: 500;
            color: #444;
        }
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px;
            font-weight: 600;
            transition: transform 0.2s;
        }
        .btn-login:hover {
            transform: translateY(-2px);
            color: white;
            opacity: 0.9;
        }
        #google-signin-button {
            display: flex;
            justify-content: center;
            margin: 20px 0;
        }
        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 20px 0;
            color: #999;
            font-size: 0.8rem;
        }
        .divider::before, .divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid #eee;
        }
        .divider span { padding: 0 10px; }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <i class="fas fa-store fa-2x mb-2"></i>
            <h1>TechShop</h1>
            <p class="mb-0 opacity-75">Internal Management System</p>
        </div>
        
        <div class="login-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger small" role="alert">
                    <i class="fas fa-exclamation-circle me-1"></i> ${error}
                </div>
            </c:if>
            <c:if test="${not empty message}">
                <div class="alert alert-success small" role="alert">
                    <i class="fas fa-check-circle me-1"></i> ${message}
                </div>
            </c:if>

            <ul class="nav nav-tabs" id="loginTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="google-tab" data-bs-toggle="tab" data-bs-target="#googleLogin" type="button" role="tab">
                        <i class="fab fa-google me-1"></i> Google
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="password-tab" data-bs-toggle="tab" data-bs-target="#passwordLogin" type="button" role="tab">
                        <i class="fas fa-key me-1"></i> Password
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="loginTabsContent">
                <div class="tab-pane fade show active" id="googleLogin" role="tabpanel">
                    <div id="g_id_onload"
                         data-client_id="${googleClientId}"
                         data-context="signin"
                         data-ux_mode="popup"
                         data-callback="handleCredentialResponse"
                         data-auto_prompt="false">
                    </div>
                    <div id="google-signin-button">
                        <div class="g_id_signin" data-type="standard" data-shape="rectangular" data-theme="outline" 
                             data-text="signin_with" data-size="large" data-logo_alignment="left" data-width="350">
                        </div>
                    </div>
                    <div class="text-center text-muted small mt-3">
                        <i class="fas fa-shield-alt"></i> Use your company Google account
                    </div>
                </div>

                <div class="tab-pane fade" id="passwordLogin" role="tabpanel">
                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <input type="hidden" name="loginType" value="password">
                        
                        <div class="mb-3">
                            <label class="form-label small">Email Address</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light border-end-0"><i class="fas fa-envelope text-muted"></i></span>
                                <input type="email" name="email" class="form-control border-start-0" placeholder="name@company.com" required>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label small">Password</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light border-end-0"><i class="fas fa-lock text-muted"></i></span>
                                <input type="password" name="password" class="form-control border-start-0" placeholder="••••••••" required>
                            </div>
                        </div>
                        
                        <button type="submit" class="btn btn-login w-100 rounded-pill mt-2">Sign In</button>
                        
                        <div class="text-center mt-3">
                            <a href="${pageContext.request.contextPath}/forgot-password" class="text-decoration-none small text-muted">Forgot Password?</a>
                        </div>
                    </form>
                </div>
            </div>

            <div class="divider">
                <span>Authorized Personnel Only</span>
            </div>
            
            <div class="alert alert-info py-2 px-3 small border-0 shadow-sm">
                <i class="fas fa-info-circle me-1"></i> Email must be pre-registered by Admin.
            </div>
        </div>
    </div>

    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        function handleCredentialResponse(response) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/login';
            
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'credential';
            input.value = response.credential;
            
            const typeInput = document.createElement('input');
            typeInput.type = 'hidden';
            typeInput.name = 'loginType';
            typeInput.value = 'google';
            
            form.appendChild(input);
            form.appendChild(typeInput);
            document.body.appendChild(form);
            form.submit();
        }

        window.addEventListener('load', function() {
            if (typeof google === 'undefined') {
                document.getElementById('google-signin-button').innerHTML = 
                    '<div class="alert alert-warning small">Google Sign-In unavailable. Use Password.</div>';
            }
        });
    </script>
</body>
</html>
