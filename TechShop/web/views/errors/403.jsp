<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 Access Denied - TechShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        /* Cấu trúc căn giữa màn hình */
        body {
            background: #eef2f7; /* Nền xanh nhạt dịu mắt */
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 20px;
        }

        /* Hộp chứa nội dung chính */
        .error-card {
            background: white;
            text-align: center;
            padding: 50px 40px;
            border-radius: 20px;
            /* Đổ bóng màu xanh nhẹ */
            box-shadow: 0 15px 35px rgba(13, 110, 253, 0.1);
            max-width: 550px;
            width: 100%;
            border-top: 5px solid #0d6efd; /* Đường viền xanh trên cùng */
        }

        /* Icon chính */
        .error-icon-container {
            margin-bottom: 30px;
        }
        
        .error-icon {
            font-size: 6rem;
            color: #0d6efd; /* Màu xanh chủ đạo của Bootstrap Primary */
            /* Hiệu ứng bóng mờ cho icon */
            filter: drop-shadow(0 10px 15px rgba(13, 110, 253, 0.3));
            animation: float 3s ease-in-out infinite;
        }

        /* Typography */
        .error-code {
            font-size: 4rem;
            font-weight: 800;
            /* Gradient xanh cho chữ số 403 */
            background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin: 0;
            line-height: 1.2;
        }

        .error-title {
            font-size: 1.75rem;
            font-weight: 700;
            color: #212529;
            margin-bottom: 15px;
        }

        .error-description {
            color: #6c757d;
            font-size: 1.1rem;
            margin-bottom: 35px;
            line-height: 1.6;
        }

        /* Nút bấm màu xanh */
        .btn-blue-glow {
            background-color: #0d6efd;
            border: none;
            padding: 12px 35px;
            font-weight: 600;
            font-size: 1rem;
            border-radius: 50px;
            color: white;
            transition: all 0.3s ease;
            box-shadow: 0 8px 20px rgba(13, 110, 253, 0.3);
        }

        .btn-blue-glow:hover {
            background-color: #0b5ed7; /* Màu đậm hơn khi di chuột */
            transform: translateY(-3px);
            box-shadow: 0 12px 25px rgba(13, 110, 253, 0.4);
            color: white;
        }
        
        /* Hiệu ứng icon trôi nhẹ */
        @keyframes float {
            0% { transform: translateY(0px); }
            50% { transform: translateY(-15px); }
            100% { transform: translateY(0px); }
        }
    </style>
</head>
<body>
    <div class="error-card">
        <div class="error-icon-container">
            <i class="fas fa-user-lock error-icon"></i>
        </div>
        
        <h1 class="error-code">403</h1>
        <h2 class="error-title">Truy cập bị từ chối</h2>
        
        <p class="error-description">
            Xin lỗi, tài khoản của bạn <strong>không có đủ quyền hạn</strong> để truy cập vào khu vực này. 
            <br>Vui lòng kiểm tra lại hoặc liên hệ quản trị viên.
        </p>
        
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-blue-glow">
            <i class="fas fa-arrow-left me-2"></i> Quay về Dashboard
        </a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
