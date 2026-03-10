<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Dashboard - TechShop" />
<%@ include file="common/header.jsp" %>

<div class="row">
    <div class="col-12">
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body py-4">
                <h2 class="mb-1">
                    <i class="fas fa-home text-primary"></i>
                    Welcome back, <strong>${sessionScope.userName}</strong>!
                </h2>
                <p class="text-muted mb-0">
                    <i class="fas fa-user-tag"></i> Role: <span class="badge bg-primary">${sessionScope.userRole}</span>
                    <c:if test="${sessionScope.branchName != null}">
                        | <i class="fas fa-building"></i> Branch: <span class="badge bg-info">${sessionScope.branchName}</span>
                    </c:if>
                </p>
            </div>
        </div>
    </div>
</div>

<!-- Admin Dashboard -->
<c:if test="${dashboardType == 'admin'}">
    <div class="row g-3">
        <!-- Statistics Cards -->
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Total Users</p>
                            <h3 class="mb-0 fw-bold">${totalUsers}</h3>
                        </div>
                        <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-users fa-2x text-primary"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Branches</p>
                            <h3 class="mb-0 fw-bold">${totalBranches}</h3>
                            <small class="text-success">
                                <i class="fas fa-check-circle"></i> ${activeBranches} Active
                            </small>
                        </div>
                        <div class="bg-success bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-building fa-2x text-success"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Product Catalog</p>
                            <h3 class="mb-0 fw-bold">${totalVariants}</h3>
                            <small class="text-muted">
                                ${totalCategories} categories, ${totalModels} models
                            </small>
                        </div>
                        <div class="bg-warning bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-box fa-2x text-warning"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Inventory</p>
                            <h3 class="mb-0 fw-bold">-</h3>
                        </div>
                        <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-warehouse fa-2x text-info"></i>
                        </div>
                    </div>
                    <small class="text-muted">Coming in Iteration 2</small>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Product Breakdown -->
    <div class="row mt-4">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0"><i class="fas fa-chart-pie text-info"></i> Product Overview</h5>
                </div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-md-4">
                            <div class="p-3">
                                <i class="fas fa-tags fa-3x text-primary mb-2"></i>
                                <h4 class="fw-bold">${totalCategories}</h4>
                                <p class="text-muted mb-0">Categories</p>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="p-3">
                                <i class="fas fa-cubes fa-3x text-success mb-2"></i>
                                <h4 class="fw-bold">${totalModels}</h4>
                                <p class="text-muted mb-0">Models</p>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="p-3">
                                <i class="fas fa-cube fa-3x text-warning mb-2"></i>
                                <h4 class="fw-bold">${totalVariants}</h4>
                                <p class="text-muted mb-0">Variants</p>
                                <small class="text-success">${activeVariants} active</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Quick Actions -->
    <div class="row mt-4">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0"><i class="fas fa-bolt text-warning"></i> Quick Actions</h5>
                </div>
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/user" class="btn btn-outline-primary w-100 py-3">
                                <i class="fas fa-users fa-2x mb-2 d-block"></i>
                                Manage Users
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/branch" class="btn btn-outline-success w-100 py-3">
                                <i class="fas fa-building fa-2x mb-2 d-block"></i>
                                Manage Branches
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/category" class="btn btn-outline-info w-100 py-3">
                                <i class="fas fa-box fa-2x mb-2 d-block"></i>
                                Manage Products
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/system-log" class="btn btn-outline-secondary w-100 py-3">
                                <i class="fas fa-history fa-2x mb-2 d-block"></i>
                                View System Logs
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Manager Dashboard -->
<c:if test="${dashboardType == 'manager'}">
    <div class="row g-3">
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Branch Staff</p>
                            <h3 class="mb-0 fw-bold">${branchStaff}</h3>
                        </div>
                        <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-users fa-2x text-primary"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Inventory</p>
                            <h3 class="mb-0 fw-bold">-</h3>
                        </div>
                        <div class="bg-success bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-warehouse fa-2x text-success"></i>
                        </div>
                    </div>
                    <small class="text-muted">Iteration 2</small>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Today's Sales</p>
                            <h3 class="mb-0 fw-bold">-</h3>
                        </div>
                        <div class="bg-warning bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-cash-register fa-2x text-warning"></i>
                        </div>
                    </div>
                    <small class="text-muted">Iteration 2</small>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row mt-4">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center py-5">
                    <i class="fas fa-chart-line fa-4x text-muted mb-3"></i>
                    <h5 class="text-muted">More features coming in Iteration 2</h5>
                    <p class="text-muted">Inventory management, sales reports, and more...</p>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Cashier Dashboard -->
<c:if test="${dashboardType == 'cashier'}">
    <div class="row g-3 mb-4">
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Hóa đơn hôm nay</p>
                            <h3 class="mb-0 fw-bold">${todayCount}</h3>
                        </div>
                        <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-receipt fa-2x text-primary"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Doanh thu hôm nay</p>
                            <h3 class="mb-0 fw-bold" style="font-size:1.3rem;">
                                <fmt:formatNumber value="${todayRevenue}" type="number" groupingUsed="true" maxFractionDigits="0" />đ
                            </h3>
                        </div>
                        <div class="bg-success bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-coins fa-2x text-success"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Doanh thu tháng này</p>
                            <h3 class="mb-0 fw-bold" style="font-size:1.3rem;">
                                <fmt:formatNumber value="${monthRevenue}" type="number" groupingUsed="true" maxFractionDigits="0" />đ
                            </h3>
                        </div>
                        <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-chart-line fa-2x text-info"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Tổng HĐ hoàn thành</p>
                            <h3 class="mb-0 fw-bold">${totalCompleted}</h3>
                        </div>
                        <div class="bg-warning bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-check-circle fa-2x text-warning"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3 mb-4">
        <div class="col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Sản phẩm tồn kho (chi nhánh)</p>
                            <h3 class="mb-0 fw-bold">${inStockCount}</h3>
                            <small class="text-muted">PhysicalProduct IN_STOCK</small>
                        </div>
                        <div class="bg-secondary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-boxes fa-2x text-secondary"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0"><i class="fas fa-bolt text-warning"></i> Thao tác nhanh</h5>
                </div>
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/cashier" class="btn btn-primary w-100 py-3">
                                <i class="fas fa-cash-register fa-2x mb-2 d-block"></i>
                                Bán hàng
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/invoice" class="btn btn-outline-info w-100 py-3">
                                <i class="fas fa-history fa-2x mb-2 d-block"></i>
                                Lịch sử bán hàng
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/customer" class="btn btn-outline-success w-100 py-3">
                                <i class="fas fa-user-friends fa-2x mb-2 d-block"></i>
                                Khách hàng
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Default Dashboard -->
<c:if test="${dashboardType == 'default'}">
    <div class="row">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center py-5">
                    <i class="fas fa-rocket fa-4x text-success mb-3"></i>
                    <h4>Welcome to TechShop Management System</h4>
                    <p class="text-muted">Your role-specific features will appear here.</p>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- System Info (For Demo) -->
<div class="row mt-4">
    <div class="col-12">
        <div class="alert alert-info border-0 shadow-sm">
            <div class="d-flex align-items-center">
                <i class="fas fa-info-circle fa-2x me-3"></i>
                <div>
                    <h6 class="mb-1">Iteration 1 - Core Foundation</h6>
                    <small>✅ Authentication | ✅ User Management | ✅ Branch Management | ✅ Product Management (Basic)</small>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>