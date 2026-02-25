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
                    <i class="fas fa-user-tag"></i> Role: <span class="badge bg-primary">${sessionScope.userRole}</span>
                    <c:if test="${sessionScope.branchName != null}">
                        | <i class="fas fa-building"></i> Branch: <span class="badge bg-info">${sessionScope.branchName}</span>
                    </c:if>
                </p>
            </div>
        </div>
    </div>
</div>

<c:if test="${dashboardType == 'admin'}">
    <div class="row g-3">
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
                    <small class="text-muted">Stock Transfer Ready</small>
                </div>
            </div>
        </div>
    </div>

    <div class="row mt-4">
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fas fa-chart-bar text-primary me-2"></i>Top 5 Sản phẩm bán chạy nhất</h5>
                </div>
                <div class="card-body">
                    <canvas id="adminProductSalesChart" style="min-height: 300px; width: 100%;"></canvas>
                </div>
            </div>
        </div>
        
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0"><i class="fas fa-bolt text-warning me-2"></i>Thao tác nhanh</h5>
                </div>
                <div class="card-body">
                    <div class="list-group list-group-flush mb-3">
                        <a href="${pageContext.request.contextPath}/user" class="list-group-item list-group-item-action border-0 px-0">
                            <i class="fas fa-users text-primary me-2"></i> Quản lý nhân viên
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/sales-report?reportType=product" class="list-group-item list-group-item-action border-0 px-0">
                            <i class="fas fa-chart-pie text-info me-2"></i> Báo cáo doanh số Sản phẩm
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/sales-report?reportType=branch" class="list-group-item list-group-item-action border-0 px-0">
                            <i class="fas fa-map-marked-alt text-success me-2"></i> Báo cáo doanh số Chi nhánh
                        </a>
                    </div>
                    <hr>
                    <div class="product-stats small">
                        <div class="d-flex justify-content-between mb-2">
                            <span><i class="fas fa-tags me-1"></i> Categories:</span>
                            <strong>${totalCategories}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span><i class="fas fa-cubes me-1"></i> Models:</span>
                            <strong>${totalModels}</strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span><i class="fas fa-cube me-1"></i> Active Variants:</span>
                            <span class="badge bg-success">${activeVariants}</span>
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

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            // Gọi đến SalesReportServlet của Admin với format json
            fetch('${pageContext.request.contextPath}/admin/sales-report?format=json')
                .then(response => response.json())
                .then(data => {
                    const labels = data.map(item => item.label);
                    const values = data.map(item => item.value);

                    const ctx = document.getElementById('adminProductSalesChart').getContext('2d');
                    new Chart(ctx, {
                        type: 'bar', // Sử dụng biểu đồ cột để so sánh sản phẩm
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Số lượng bán ra',
                                data: values,
                                backgroundColor: 'rgba(78, 115, 223, 0.6)',
                                borderColor: '#4e73df',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            indexAxis: 'y', // Biểu đồ ngang để dễ đọc tên sản phẩm
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: { display: false }
                            },
                            scales: {
                                x: { beginAtZero: true }
                            }
                        }
                    });
                });
        });
    </script>
<%@ include file="common/footer.jsp" %>