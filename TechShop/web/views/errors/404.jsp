<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 Not Found - TechShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: #eef2f7;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 20px;
        }
        .error-card {
            background: white;
            text-align: center;
            padding: 50px 40px;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(13, 110, 253, 0.1);
            max-width: 550px;
            width: 100%;
            border-top: 5px solid #0d6efd;
        }
        .error-icon {
            font-size: 6rem;
            color: #0d6efd;
            filter: drop-shadow(0 10px 15px rgba(13, 110, 253, 0.3));
            margin-bottom: 30px;
            animation: bounce 2s infinite;
        }
        .error-code {
            font-size: 4rem;
            font-weight: 800;
            background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin: 0;
            line-height: 1.2;
        }
        .btn-blue-glow {
            background-color: #0d6efd;
            border: none;
            padding: 12px 35px;
            font-weight: 600;
            border-radius: 50px;
            color: white;
            transition: all 0.3s ease;
            box-shadow: 0 8px 20px rgba(13, 110, 253, 0.3);
            text-decoration: none;
        }
        .btn-blue-glow:hover {
            background-color: #0b5ed7;
            transform: translateY(-3px);
            box-shadow: 0 12px 25px rgba(13, 110, 253, 0.4);
            color: white;
        }
        @keyframes bounce {
            0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
            40% {transform: translateY(-20px);}
            60% {transform: translateY(-10px);}
        }
    </style>
</head>
<body>
    <div class="error-card">
        <div class="error-icon">
            <i class="fas fa-search-location"></i> </div>
        <h1 class="error-code">404</h1>
        <h2 class="h3 fw-bold mt-2 mb-3 text-dark">Không tìm thấy trang</h2>
        <p class="text-muted mb-4 fs-6">
            Đường dẫn bạn truy cập có thể bị hỏng hoặc trang đã bị xóa.<br>
            Vui lòng kiểm tra lại URL.
        </p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-blue-glow">
            <i class="fas fa-home me-2"></i> Trở về trang chủ
        </a>
    </div>
</body>
</html>
