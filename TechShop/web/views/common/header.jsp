<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .navbar {
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
        }
        .navbar-brand {
            font-weight: 700;
            font-size: 1.3rem;
        }
        .nav-link {
            font-weight: 500;
            transition: all 0.3s;
        }
        .nav-link:hover {
            transform: translateY(-2px);
        }
        .dropdown-menu {
            border: none;
            box-shadow: 0 4px 12px rgba(0,0,0,.15);
        }
        .active-page {
            background: rgba(255,255,255,.1);
            border-radius: 5px;
        }
    </style>
</head>
<body class="bg-light">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-store"></i> TechShop
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <c:if test="${sessionScope.user != null}">
                        <!-- Dashboard -->
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                                <i class="fas fa-home"></i> Dashboard
                            </a>
                        </li>
                        
                        <!-- Admin Menu -->
                        <c:if test="${sessionScope.userRole == 'Admin'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/user">
                                    <i class="fas fa-users"></i> Users
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/branch">
                                    <i class="fas fa-building"></i> Branches
                                <a class="nav-link" href="${pageContext.request.contextPath}/cashier-mgmt">
                                    <i class="fas fa-users-cog"></i> Cashiers
                                </a>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="productDropdown" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-box"></i> Products
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/category">
                                        <i class="fas fa-tags"></i> Categories
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/model">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/ProductCategory">
                                        <i class="fas fa-tags"></i> Categories
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/ProductModel">
                                        <i class="fas fa-cubes"></i> Models
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/variant">
                                        <i class="fas fa-cube"></i> Variants
                                    </a></li>
                                </ul>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="reportDropdown" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-box"></i> Reports
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/sales-report?reportType=branch">
                                        <i class="fas fa-tags"></i> By Branch
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/sales-report?reportType=product">
                                        <i class="fas fa-cubes"></i> By Product
                                    </a></li>
                                </ul>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/admin/audit-logs">
                                    <i class="fas fa-building"></i> Audit Logs
                                </a>
                            </li>
                                    <i class="fas fa-chart-line"></i> Reports
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/report">
                                        <i class="fas fa-chart-bar"></i> Báo cáo tổng quan
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/sales-report">
                                        <i class="fas fa-file-invoice-dollar"></i> Doanh số sản phẩm
                                    </a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/branch-report">
                                        <i class="fas fa-building"></i> Doanh số chi nhánh
                                    </a></li>
                                </ul>
                            </li>
                        </c:if>
                        
                        <!-- Shop Manager Menu -->
                        <c:if test="${sessionScope.userRole == 'Shop Manager'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/inventory">
                                    <i class="fas fa-warehouse"></i> Inventory
                                <a class="nav-link" href="${pageContext.request.contextPath}/cashier-mgmt">
                                    <i class="fas fa-users-cog"></i> Cashiers
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/report">
                                    <i class="fas fa-chart-bar"></i> Reports
                                </a>
                            </li>
                        </c:if>
                        
                        <!-- Cashier Menu -->
                        <c:if test="${sessionScope.userRole == 'Cashier'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/invoice">
                                    <i class="fas fa-cash-register"></i> Sales
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/customer">
                                    <i class="fas fa-user-friends"></i> Customers
                                <a class="nav-link" href="${pageContext.request.contextPath}/cashier">
                                    <i class="fas fa-shopping-cart"></i> POS
                                </a>
                            </li>
                        </c:if>
                            
                        <!-- Accounting Staff Menu -->
                        <c:if test="${sessionScope.userRole == 'Accounting Staff'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/report/financial">
                                <a class="nav-link" href="${pageContext.request.contextPath}/report">
                                    <i class="fas fa-cash-register"></i> Financial Report
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/accounting/invoices">
                                <a class="nav-link" href="${pageContext.request.contextPath}/invoice">
                                    <i class="fas fa-user-friends"></i> Check Invoice
                                </a>
                            </li>
                        </c:if>
                        
                        <!-- Customer Service Menu -->
                        <c:if test="${sessionScope.userRole == 'Customer Service'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/cs/warranty">
                                <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                                    <i class="fas fa-tools"></i> Warranty
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/customer">
                                <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                                    <i class="fas fa-user-friends"></i> Customers
                                </a>
                            </li>
                        </c:if>
                        
                        <!-- Technician Menu -->
                        <c:if test="${sessionScope.userRole == 'Technician'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/tech/warranty">
                                <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                                    <i class="fas fa-wrench"></i> Repairs
                                </a>
                            </li>
                        </c:if>
                    </c:if>
                </ul>
                
                <!-- User Profile -->
                <ul class="navbar-nav">
                    <c:if test="${sessionScope.user != null}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                <img src="${pageContext.request.contextPath}/uploads/${user.avatarUrl}"
                                    alt="Avatar"
                                    class="rounded-circle me-2"
                                    width="36"
                                    height="36"> ${sessionScope.userName}
                                <i class="fas fa-user-circle"></i> ${sessionScope.userName}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><h6 class="dropdown-header">
                                    <i class="fas fa-id-badge"></i> ${sessionScope.userEmail}
                                </h6></li>
                
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                    <i class="fas fa-user-edit"></i> Hồ sơ cá nhân
                                </a></li>
                
                                <li><hr class="dropdown-divider"></li>
                
                                <li><span class="dropdown-item-text">
                                    <i class="fas fa-user-tag"></i> Role: <strong>${sessionScope.userRole}</strong>
                                </span></li>
                                <c:if test="${sessionScope.branchName != null}">
                                    <li><span class="dropdown-item-text">
                                        <i class="fas fa-building"></i> Branch: <strong>${sessionScope.branchName}</strong>
                                     </span></li>
                                </c:if>
                
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                </a></li>
                            </ul>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container-fluid mt-4">
    <div class="container-fluid mt-4">
