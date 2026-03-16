<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - TechShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .forgot-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            width: 100%;
            max-width: 450px;
            overflow: hidden;
        }
        .forgot-header {
            background: #f8f9fa;
            padding: 30px;
            text-align: center;
            border-bottom: 1px solid #eee;
        }
        .forgot-body { padding: 30px; }
        .btn-send {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px;
            font-weight: 600;
        }
        .btn-send:hover { opacity: 0.9; color: white; }
    </style>
</head>
<body>
    <div class="forgot-container">
        <div class="forgot-header">
            <i class="fas fa-envelope-open-text fa-3x text-primary mb-3"></i>
            <h3>Quên mật khẩu?</h3>
            <p class="text-muted small">Nhập email để nhận liên kết khôi phục</p>
        </div>
        
        <div class="forgot-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger small">
                    <i class="fas fa-exclamation-circle me-2"></i> ${error}
                </div>
            </c:if>
            <c:if test="${not empty message}">
                <div class="alert alert-success small">
                    <i class="fas fa-check-circle me-2"></i> ${message}
                </div>
            </c:if>
            
            <form action="forgot-password" method="POST">
                <div class="mb-4">
                    <label class="form-label small">Địa chỉ Email</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-at text-muted"></i></span>
                        <input type="email" name="email" class="form-control" 
                               placeholder="name@company.com" required>
                    </div>
                </div>
                
                <button type="submit" class="btn btn-send w-100 rounded-pill mb-3">
                    Gửi yêu cầu khôi phục
                </button>
                
                <div class="text-center">
                    <a href="login" class="text-decoration-none small text-muted">
                        <i class="fas fa-arrow-left me-1"></i> Quay lại đăng nhập
                    </a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
