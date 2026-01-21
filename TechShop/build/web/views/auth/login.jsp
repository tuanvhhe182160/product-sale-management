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
        .google-btn {
            width: 100%;
            padding: 15px;
            border: 2px solid #ddd;
            border-radius: 10px;
            background: white;
            color: #333;
            font-size: 1rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .google-btn:hover {
            background: #f8f9fa;
            border-color: #667eea;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
        }
        .google-btn img {
            width: 24px;
            height: 24px;
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
        .demo-badge {
            background: #ffc107;
            color: #000;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
            display: inline-block;
            margin-bottom: 15px;
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
            <!-- Demo Mode Badge -->
            <div class="text-center">
                <span class="demo-badge">
                    <i class="fas fa-flask"></i> DEMO MODE
                </span>
            </div>
            
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
            
            <!-- Demo Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-3">
                    <label class="form-label fw-bold">
                        <i class="fas fa-envelope"></i> Email Address
                    </label>
                    <input type="email" 
                           name="email" 
                           class="form-control form-control-lg" 
                           placeholder="admin@store.com" 
                           required 
                           autofocus>
                    <small class="text-muted">Enter your registered email</small>
                </div>
                
                <button type="submit" class="google-btn">
                    <img src="https://www.google.com/favicon.ico" alt="Google">
                    Sign in with Email
                </button>
            </form>
            
            <div class="divider">
                <span>Demo Test Accounts</span>
            </div>
            
            <div class="info-text">
                <ul class="list-unstyled text-start small">
                    <li class="mb-2">
                        <i class="fas fa-user-shield text-danger"></i> 
                        <strong>tuanvhhe182160@fpt.edu.vn</strong> (Admin)
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-user-tie text-primary"></i> 
                        <strong>manager.hn1@store.com</strong> (Shop Manager)
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-cash-register text-success"></i> 
                        <strong>cashier.hn1@store.com</strong> (Cashier)
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-headset text-info"></i> 
                        <strong>accounting@store.com</strong> (Accounting)
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-headset text-info"></i> 
                        <strong>cs@store.com</strong> (Customer Service)
                    </li>
                    <li class="mb-2">
                        <i class="fas fa-headset text-info"></i> 
                        <strong>tech@store.com</strong> (Technician)
                    </li>
                </ul>
            </div>
            
            <div class="alert alert-warning mt-3 small">
                <i class="fas fa-exclamation-triangle"></i>
                <strong>Demo Mode:</strong> No password required. Just enter email from database.
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>