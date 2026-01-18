<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>
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
                        <c:if test="${sessionScope.user.roleName == 'Admin'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/user">
                                    <i class="fas fa-users"></i> Users
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/branch">
                                    <i class="fas fa-building"></i> Branches
                                </a>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="productDropdown" role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-box"></i> Products
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/category">Categories</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/model">Models</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/variant">Variants</a></li>
                                </ul>
                            </li>
                        </c:if>
                    </c:if>
                </ul>
                <ul class="navbar-nav">
                    <c:if test="${sessionScope.user != null}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user"></i> ${sessionScope.user.fullName}
                            </a>
                            <ul class="dropdown-menu">
                                <li><span class="dropdown-item-text">Role: ${sessionScope.user.roleName}</span></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt"></i> Logout
                                </a></li>
                            </ul>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container mt-4">