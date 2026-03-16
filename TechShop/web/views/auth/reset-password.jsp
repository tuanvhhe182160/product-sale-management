<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - TechShop</title>
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
        .reset-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            width: 100%;
            max-width: 450px;
            overflow: hidden;
        }
        .reset-header {
            background: #f8f9fa;
            padding: 30px;
            text-align: center;
            border-bottom: 1px solid #eee;
        }
        .reset-body { padding: 30px; }
        .btn-reset {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="reset-container">
        <div class="reset-header">
            <i class="fas fa-lock-open fa-3x text-primary mb-3"></i>
            <h3>Đặt lại mật khẩu</h3>
            <p class="text-muted">Nhập mật khẩu mới cho tài khoản của bạn</p>
        </div>
        
        <div class="reset-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger small">
                    <i class="fas fa-exclamation-circle me-2"></i> ${error}
                </div>
            </c:if>

            <form action="reset-password" method="POST" id="resetForm">
                <input type="hidden" name="token" value="${token}">
                
                <div class="mb-3">
                    <label class="form-label small">Mật khẩu mới</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-key text-muted"></i></span>
                        <input type="password" name="newPassword" id="newPassword" 
                               class="form-control" placeholder="Tối thiểu 8 ký tự" required>
                    </div>
                    <div class="password-requirements small text-muted mt-1" style="font-size: 0.75rem;">
                        * 8+ ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.
                    </div>
                </div>

                <div class="mb-4">
                    <label class="form-label small">Xác nhận mật khẩu mới</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-check text-muted"></i></span>
                        <input type="password" name="confirmPassword" id="confirmPassword" 
                               class="form-control" placeholder="Nhập lại mật khẩu" required>
                    </div>
                    <div id="msg" class="small mt-2"></div>
                </div>

                <button type="submit" class="btn btn-reset w-100 rounded-pill mb-3">
                    Cập nhật mật khẩu
                </button>
                
                <div class="text-center">
                    <a href="login" class="text-decoration-none small text-muted">Hủy bỏ và quay lại đăng nhập</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Kiểm tra mật khẩu khớp nhau ngay tại client
        const form = document.getElementById('resetForm');
        const pass = document.getElementById('newPassword');
        const confirm = document.getElementById('confirmPassword');
        const msg = document.getElementById('msg');

        form.addEventListener('submit', function(e) {
            if (pass.value.length < 8) {
                e.preventDefault();
                msg.innerHTML = "Mật khẩu phải dài ít nhất 8 ký tự!";
                msg.className = "small mt-2 text-danger";
            } else if (pass.value !== confirm.value) {
                e.preventDefault();
                msg.innerHTML = "Mật khẩu xác nhận không khớp!";
                msg.className = "small mt-2 text-danger";
            }
        });
    </script>
</body>
</html>
