<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                    <i class="fas fa-user-tag"></i>
                    Role: <span class="badge bg-primary">${sessionScope.userRole}</span>
                    <c:if test="${sessionScope.branchName != null}">
                        | <i class="fas fa-building"></i>
                        Branch: <span class="badge bg-info">${sessionScope.branchName}</span>
                    </c:if>
                </p>
            </div>
        </div>
    </div>
</div>

<!-- ================= ADMIN DASHBOARD ================= -->
<c:if test="${dashboardType == 'admin'}">
    <div class="row g-3">

        <!-- Total Users -->
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

        <!-- Total Branches -->
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Total Branches</p>
                            <h3 class="mb-0 fw-bold">${totalBranches}</h3>
                        </div>
                        <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-building fa-2x text-primary"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Products -->
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Products</p>
                            <h3 class="mb-0 fw-bold">-</h3>
                        </div>
                        <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-box fa-2x text-info"></i>
                        </div>
                    </div>
                    <small class="text-muted">Coming in Iteration 2</small>
                </div>
            </div>
        </div>

        <!-- Revenue -->
        <div class="col-md-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small">Revenue</p>
                            <h3 class="mb-0 fw-bold">-</h3>
                        </div>
                        <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-dollar-sign fa-2x text-info"></i>
                        </div>
                    </div>
                    <small class="text-muted">Coming in Iteration 2</small>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row mt-4">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0">
                        <i class="fas fa-bolt text-primary"></i> Quick Actions
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/user"
                               class="btn btn-outline-primary w-100 py-3">
                                <i class="fas fa-users fa-2x mb-2 d-block"></i>
                                Manage Users
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/branch"
                               class="btn btn-outline-primary w-100 py-3">
                                <i class="fas fa-building fa-2x mb-2 d-block"></i>
                                Manage Branches
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/category"
                               class="btn btn-outline-info w-100 py-3">
                                <i class="fas fa-box fa-2x mb-2 d-block"></i>
                                Manage Products
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- ================= MANAGER DASHBOARD ================= -->
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
                        <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-warehouse fa-2x text-primary"></i>
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
                        <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-cash-register fa-2x text-info"></i>
                        </div>
                    </div>
                    <small class="text-muted">Iteration 2</small>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- ================= CASHIER DASHBOARD ================= -->
<c:if test="${dashboardType == 'cashier'}">
    <div class="row">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center py-5">
                    <i class="fas fa-shopping-cart fa-4x text-primary mb-3"></i>
                    <h4>Ready to Make Sales!</h4>
                    <p class="text-muted mb-4">Sales module will be available in Iteration 2</p>
                    <button class="btn btn-primary btn-lg" disabled>
                        <i class="fas fa-plus-circle"></i> New Sale (Coming Soon)
                    </button>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- ================= DEFAULT DASHBOARD ================= -->
<c:if test="${dashboardType == 'default'}">
    <div class="row">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center py-5">
                    <i class="fas fa-rocket fa-4x text-primary mb-3"></i>
                    <h4>Welcome to TechShop Management System</h4>
                    <p class="text-muted">Your role-specific features will appear here.</p>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- ================= SYSTEM INFO ================= -->
<div class="row mt-4">
    <div class="col-12">
        <div class="alert alert-info border-0 shadow-sm">
            <div class="d-flex align-items-center">
                <i class="fas fa-info-circle fa-2x me-3"></i>
                <div>
                    <h6 class="mb-1">Iteration 1 - Core Foundation</h6>
                    <small>
                        ✅ Authentication |
                        ✅ User Management |
                        ✅ Branch Management |
                        ✅ Product Management (Basic)
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
