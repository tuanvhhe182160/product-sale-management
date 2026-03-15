<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                    <h5 class="mb-0"><i class="fas fa-chart-bar text-primary me-2"></i>Top 5 Sản phẩm bán chạy nhất trong 30 ngày qua</h5>
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
                        <a href="${pageContext.request.contextPath}/report" class="list-group-item list-group-item-action border-0 px-0">
                            <i class="fas fa-chart-pie text-info me-2"></i> Báo cáo doanh số
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
                            <a href="${pageContext.request.contextPath}/cashier-mgmt" class="btn btn-outline-success w-100 py-3">
                                <i class="fas fa-users-cog fa-2x mb-2 d-block"></i>
                                Manage Cashiers
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/ProductCategory" class="btn btn-outline-info w-100 py-3">
                                <i class="fas fa-box fa-2x mb-2 d-block"></i>
                                Manage Products
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/report" class="btn btn-outline-warning w-100 py-3">
                                <i class="fas fa-chart-line fa-2x mb-2 d-block"></i>
                                Sales Reports
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
                                <fmt:formatNumber value="${todayRevenue}" type="number" groupingUsed="true" maxFractionDigits="0" />
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
                                <fmt:formatNumber value="${monthRevenue}" type="currency" groupingUsed="true" currencySymbol="đ" maxFractionDigits="0" />
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
                            <a href="${pageContext.request.contextPath}/product-detail" class="btn btn-outline-success w-100 py-3">
                                <i class="fas fa-search fa-2x mb-2 d-block"></i>
                                Tra cứu sản phẩm
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Accounting Dashboard -->
<c:if test="${dashboardType == 'accounting'}">
    <div class="row g-3 mb-4">
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100 border-start border-success border-4">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small fw-bold text-uppercase">Doanh Thu (30 Ngày)</p>
                            <h3 class="mb-0 fw-bold text-success">
                                <fmt:formatNumber value="${totalRevenue30Days}" type="number" pattern="#,##0"/> ?
                                <fmt:formatNumber value="${totalRevenue30Days}" type="number" pattern="#,##0"/> ₫
                            </h3>
                        </div>
                        <div class="bg-success bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-money-bill-wave fa-2x text-success"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100 border-start border-primary border-4">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small fw-bold text-uppercase">Lợi Nhuận (30 Ngày)</p>
                            <h3 class="mb-0 fw-bold text-primary">
                                <fmt:formatNumber value="${totalProfit30Days}" type="number" pattern="#,##0"/> ?
                                <fmt:formatNumber value="${totalProfit30Days}" type="number" pattern="#,##0"/> ₫
                            </h3>
                        </div>
                        <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-chart-line fa-2x text-primary"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100 border-start border-warning border-4">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small fw-bold text-uppercase">Hóa Đơn (30 Ngày)</p>
                            <h3 class="mb-0 fw-bold text-warning">${totalInvoices30Days}</h3>
                        </div>
                        <div class="bg-warning bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-file-invoice-dollar fa-2x text-warning"></i>
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
                    <h5 class="mb-0"><i class="fas fa-toolbox text-secondary me-2"></i>Công cụ Tài chính</h5>
                </div>
                <div class="card-body">
                    <div class="row g-3 text-center">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/report/financial" class="text-decoration-none">
                            <a href="${pageContext.request.contextPath}/report" class="text-decoration-none">
                                <div class="p-4 border rounded bg-light hover-shadow transition-all">
                                    <i class="fas fa-chart-pie fa-3x text-primary mb-3"></i>
                                    <h6 class="text-dark fw-bold">Báo Cáo Tài Chính</h6>
                                    <p class="small text-muted mb-0">Xem và xuất file (CSV/Excel) doanh thu, lợi nhuận.</p>
                                </div>
                            </a>
                        </div>
                        
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/accounting/invoices" class="text-decoration-none">
                            <a href="${pageContext.request.contextPath}/invoice" class="text-decoration-none">
                                <div class="p-4 border rounded bg-light hover-shadow transition-all">
                                    <i class="fas fa-receipt fa-3x text-info mb-3"></i>
                                    <h6 class="text-dark fw-bold">Tra Cứu Hóa Đơn</h6>
                                    <p class="small text-muted mb-0">Xem chi tiết và đối soát các giao dịch bán hàng.</p>
                                </div>
                            </a>
                        </div>
                        
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/accounting/close-period?branchId=${sessionScope.branchId}" class="text-decoration-none">
                            <a href="${pageContext.request.contextPath}/dashboard" class="text-decoration-none">
                                <div class="p-4 border rounded bg-light hover-shadow transition-all">
                                    <i class="fas fa-lock fa-3x text-secondary mb-3"></i>
                                    <h6 class="text-dark fw-bold">Chốt Kỳ Kế Toán</h6>
                                    <p class="small text-muted mb-0">Khóa sổ dữ liệu giao dịch theo tháng/quý.</p>
                                </div>
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

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            // G?i ??n SalesReportServlet c?a Admin v?i format json
            // Gọi đến SalesReportServlet của Admin với format json
            fetch('${pageContext.request.contextPath}/admin/sales-report?format=json')
                .then(response => response.json())
                .then(data => {
                    const labels = data.map(item => item.label);
                    const values = data.map(item => item.value);

                    const ctx = document.getElementById('adminProductSalesChart').getContext('2d');
                    new Chart(ctx, {
                        type: 'bar', // S? d?ng bi?u ?? c?t ?? so sánh s?n ph?m
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'S? l??ng bán ra',
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
                            indexAxis: 'y', // Bi?u ?? ngang ?? d? ??c tên s?n ph?m
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
