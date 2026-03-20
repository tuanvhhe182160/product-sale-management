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
                                <i class="fas fa-users text-info me-2"></i>Quản lý User
                                
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/cashier-mgmt" class="list-group-item list-group-item-action border-0 px-0">
                                <i class="fas fa-file-invoice-dollar text-info me-2"></i>Quản lý Cashier
                                
                        </a>
                        <a href="${pageContext.request.contextPath}/category" class="list-group-item list-group-item-action border-0 px-0">
                                <i class="fas fa-toolbox text-info me-2"></i>Quản lý sản phẩm
                                
                        </a>               
                        <a href="${pageContext.request.contextPath}/admin/product-report" class="list-group-item list-group-item-action border-0 px-0">
                            <i class="fas fa-chart-pie text-info me-2"></i> Báo cáo doanh số Sản phẩm
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/branch-report" class="list-group-item list-group-item-action border-0 px-0">
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
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/product-detail" class="btn btn-outline-warning w-100 py-3">
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
    <%-- ── ROW 1: KPI CARDS ── --%>
    <div class="row g-3 mb-4">

        <%-- Doanh thu hôm nay --%>
        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-success border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Doanh thu hôm nay</p>
                        <h3 class="mb-0 fw-bold text-success">
                            <fmt:formatNumber value="${todayRevenue}" type="number" pattern="#,##0"/> &#x20AB;
                        </h3>
                        <small class="text-muted">${todayInvoices} hóa đơn hoàn thành</small>
                    </div>
                    <div class="bg-success bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-calendar-day fa-2x text-success"></i>
                    </div>
                </div>
            </div>
        </div>

        <%-- Doanh thu tháng này --%>
        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-primary border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Doanh thu tháng này</p>
                        <h3 class="mb-0 fw-bold text-primary">
                            <fmt:formatNumber value="${monthRevenue}" type="number" pattern="#,##0"/> &#x20AB;
                        </h3>
                        <small class="text-muted">${monthInvoices} hóa đơn hoàn thành</small>
                    </div>
                    <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-money-bill-wave fa-2x text-primary"></i>
                    </div>
                </div>
            </div>
        </div>

        <%-- Lợi nhuận tháng này --%>
        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-info border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Lợi nhuận tháng này</p>
                        <h3 class="mb-0 fw-bold text-info">
                            <fmt:formatNumber value="${monthProfit}" type="number" pattern="#,##0"/> &#x20AB;
                        </h3>
                        <small class="text-muted">
                            <c:choose>
                                <c:when test="${monthRevenue > 0}">
                                    Biên lợi nhuận:
                                    <fmt:formatNumber value="${monthProfit / monthRevenue * 100}"
                                                      maxFractionDigits="1"/>%
                                </c:when>
                                <c:otherwise>Chưa có doanh thu</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                    <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-chart-line fa-2x text-info"></i>
                    </div>
                </div>
            </div>
        </div>

        <%-- Chờ đối soát (cảnh báo đỏ nếu > 0) --%>
        <div class="col-sm-6 col-xl-3">
            <a href="${pageContext.request.contextPath}/accounting/reconciliation"
               class="text-decoration-none">
                <div class="card border-0 shadow-sm h-100 border-start border-4
                    ${pendingRecon > 0 ? 'border-danger' : 'border-secondary'}">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-muted mb-1 small fw-bold text-uppercase">Chờ đối soát CK</p>
                            <h3 class="mb-0 fw-bold ${pendingRecon > 0 ? 'text-danger' : 'text-secondary'}">
                                ${pendingRecon} hóa đơn
                            </h3>
                            <small class="${pendingRecon > 0 ? 'text-danger' : 'text-muted'}">
                                <c:choose>
                                    <c:when test="${pendingRecon > 0}">
                                        <i class="fas fa-exclamation-triangle me-1"></i>Cần xác nhận
                                    </c:when>
                                    <c:otherwise>Đã đối soát hết</c:otherwise>
                                </c:choose>
                            </small>
                        </div>
                        <div class="bg-${pendingRecon > 0 ? 'danger' : 'secondary'} bg-opacity-10 p-3 rounded-circle">
                            <i class="fas fa-university fa-2x text-${pendingRecon > 0 ? 'danger' : 'secondary'}"></i>
                        </div>
                    </div>
                </div>
            </a>
        </div>

    </div><%-- /row 1 --%>

    <%-- ── ROW 2: CHART + PAYMENT BREAKDOWN + QUICK ACTIONS ── --%>
    <div class="row g-3 mb-4">

        <%-- Biểu đồ 7 ngày --%>
        <div class="col-lg-5">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h6 class="mb-0 fw-bold">
                        <i class="fas fa-chart-area text-primary me-2"></i>Doanh thu 7 ngày gần nhất
                    </h6>
                    <a href="${pageContext.request.contextPath}/report/financial"
                       class="btn btn-sm btn-outline-primary">Chi tiết</a>
                </div>
                <div class="card-body pb-2">
                    <canvas id="revenueChart" height="200"></canvas>
                </div>
            </div>
        </div>

        <%-- Phân bổ phương thức thanh toán --%>
        <div class="col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white py-3">
                    <h6 class="mb-0 fw-bold">
                        <i class="fas fa-credit-card text-info me-2"></i>Phương thức TT tháng này
                    </h6>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty payBreakdown}">
                            <p class="text-muted text-center py-4 small">Chưa có giao dịch tháng này.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="entry" items="${payBreakdown}">
                                <div class="mb-3">
                                    <div class="d-flex justify-content-between small mb-1">
                                        <span class="fw-semibold">
                                            <c:choose>
                                                <c:when test="${entry.key == 'CASH'}">
                                                    <i class="fas fa-money-bill text-success me-1"></i>Tiền mặt
                                                </c:when>
                                                <c:when test="${entry.key == 'CARD'}">
                                                    <i class="fas fa-credit-card text-primary me-1"></i>Thẻ
                                                </c:when>
                                                <c:when test="${entry.key == 'TRANSFER'}">
                                                    <i class="fas fa-university text-info me-1"></i>Chuyển khoản
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-layer-group text-secondary me-1"></i>Hỗn hợp
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <span class="text-muted">
                                            <fmt:formatNumber value="${entry.value}" type="number" pattern="#,##0"/> &#x20AB;
                                        </span>
                                    </div>
                                    <div class="progress" style="height:6px;">
                                        <div class="progress-bar
                                            ${entry.key == 'CASH' ? 'bg-success' :
                                              entry.key == 'CARD' ? 'bg-primary' :
                                              entry.key == 'TRANSFER' ? 'bg-info' : 'bg-secondary'}"
                                             style="width:
                                                ${monthRevenue > 0 ? entry.value / monthRevenue * 100 : 0}%">
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <%-- Quick Actions --%>
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white py-3">
                    <h6 class="mb-0 fw-bold">
                        <i class="fas fa-bolt text-warning me-2"></i>Thao tác nhanh
                    </h6>
                </div>
                <div class="card-body d-flex flex-column gap-2">

                    <a href="${pageContext.request.contextPath}/report/financial"
                       class="btn btn-outline-primary text-start">
                        <i class="fas fa-chart-pie me-2"></i>Báo cáo tài chính
                    </a>

                    <a href="${pageContext.request.contextPath}/accounting/invoices"
                       class="btn btn-outline-info text-start">
                        <i class="fas fa-receipt me-2"></i>Danh sách hóa đơn
                    </a>

                    <a href="${pageContext.request.contextPath}/accounting/reconciliation"
                       class="btn text-start ${pendingRecon > 0 ? 'btn-danger' : 'btn-outline-secondary'}">
                        <i class="fas fa-university me-2"></i>Đối soát chuyển khoản
                        <c:if test="${pendingRecon > 0}">
                            <span class="badge bg-white text-danger ms-1">${pendingRecon}</span>
                        </c:if>
                    </a>

                    <a href="${pageContext.request.contextPath}/accounting/close-period"
                       class="btn btn-outline-secondary text-start">
                        <i class="fas fa-lock me-2"></i>Chốt kỳ kế toán
                        <c:choose>
                            <c:when test="${not empty lastClosedMonth}">
                                <small class="text-muted ms-1">
                                    (Kỳ cuối: ${lastClosedMonth}/${lastClosedYear})
                                </small>
                            </c:when>
                            <c:otherwise>
                                <small class="text-muted ms-1">(Chưa có kỳ nào)</small>
                            </c:otherwise>
                        </c:choose>
                    </a>

                </div>
            </div>
        </div>

    </div><%-- /row 2 --%>

    <%-- ── ROW 3: TOP INVOICES TODAY ── --%>
    <div class="row">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h6 class="mb-0 fw-bold">
                        <i class="fas fa-file-invoice-dollar text-warning me-2"></i>
                        Hóa đơn lớn nhất hôm nay (Top 5)
                    </h6>
                    <a href="${pageContext.request.contextPath}/accounting/invoices"
                       class="btn btn-sm btn-outline-warning">Xem tất cả</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th class="px-3">Mã hóa đơn</th>
                                    <th>Khách hàng</th>
                                    <th class="text-center">Thanh toán</th>
                                    <th class="text-end">Giá trị</th>
                                    <th class="text-center">Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty topInvoices}">
                                        <tr>
                                            <td colspan="5" class="text-center text-muted py-4">
                                                <i class="fas fa-sun me-2"></i>Chưa có hóa đơn nào hôm nay.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="inv" items="${topInvoices}">
                                            <tr>
                                                <td class="px-3 fw-bold">${inv.invoiceCode}</td>
                                                <td class="text-muted">
                                                    ${empty inv.customerName ? 'Khách lẻ' : inv.customerName}
                                                </td>
                                                <td class="text-center">
                                                    <c:choose>
                                                        <c:when test="${inv.paymentMethod == 'CASH'}">
                                                            <span class="badge bg-success">Tiền mặt</span>
                                                        </c:when>
                                                        <c:when test="${inv.paymentMethod == 'CARD'}">
                                                            <span class="badge bg-primary">Thẻ</span>
                                                        </c:when>
                                                        <c:when test="${inv.paymentMethod == 'TRANSFER'}">
                                                            <span class="badge bg-info text-dark">CK</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">Hỗn hợp</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-end fw-bold text-success">
                                                    <fmt:formatNumber value="${inv.finalAmount}"
                                                                      type="number" pattern="#,##0"/> &#x20AB;
                                                </td>
                                                <td class="text-center">
                                                    <c:choose>
                                                        <c:when test="${inv.status == 'COMPLETED'}">
                                                            <span class="badge bg-success">Hoàn thành</span>
                                                        </c:when>
                                                        <c:when test="${inv.status == 'PENDING'}">
                                                            <span class="badge bg-warning text-dark">
                                                                <i class="fas fa-hourglass-half me-1"></i>Chờ đối soát
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">${inv.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Chart.js script for 7-day revenue sparkline --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <script>
    (function () {
        // Build labels and data from server-side revenueTrend
        const trendRaw = [
            <c:forEach var="d" items="${revenueTrend}" varStatus="s">
                { date: '${d[0]}', rev: ${d[1]} }<c:if test="${!s.last}">,</c:if>
            </c:forEach>
        ];

        // Fill missing days (last 7)
        const labels = [];
        const values = [];
        const trendMap = {};
        trendRaw.forEach(function(r) { trendMap[r.date] = r.rev; });

        for (let i = 6; i >= 0; i--) {
            const d = new Date();
            d.setDate(d.getDate() - i);
            const key = d.toISOString().slice(0, 10);
            labels.push(key.slice(5));   // MM-DD
            values.push(trendMap[key] || 0);
        }

        new Chart(document.getElementById('revenueChart'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (₫)',
                    data: values,
                    backgroundColor: 'rgba(13,110,253,0.15)',
                    borderColor:     'rgba(13,110,253,0.8)',
                    borderWidth: 2,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return new Intl.NumberFormat('vi-VN').format(ctx.parsed.y) + ' ₫';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(v) {
                                if (v >= 1e9) return (v/1e9).toFixed(1) + ' tỷ';
                                if (v >= 1e6) return (v/1e6).toFixed(0) + ' tr';
                                return v;
                            }
                        }
                    }
                }
            }
        });
    }());
    </script>

</c:if>


<!-- CS Dashboard -->
<c:if test="${dashboardType == 'customer_service'}">
    <div class="row mb-4">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0"><i class="fas fa-bolt text-warning me-2"></i>Thao Tác Nhanh (Customer Service)</h5>
                </div>
                <div class="card-body">
                    <div class="row g-3 text-center">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/cs/warranty/list" class="text-decoration-none">
                                <div class="p-4 border rounded bg-light hover-shadow transition-all">
                                    <i class="fas fa-search fa-3x text-primary mb-3"></i>
                                    <h6 class="text-dark fw-bold">Theo Dõi Bảo Hành</h6>
                                    <p class="small text-muted mb-0">Tra cứu tiến độ sửa chữa cho khách gọi lên.</p>
                                </div>
                            </a>
                        </div>
                        
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/cs/warranty" class="text-decoration-none">
                                <div class="p-4 border rounded bg-light hover-shadow transition-all">
                                    <i class="fas fa-plus-circle fa-3x text-success mb-3"></i>
                                    <h6 class="text-dark fw-bold">Kiểm Tra & Lập Phiếu</h6>
                                    <p class="small text-muted mb-0">Check IMEI và tạo yêu cầu bảo hành mới.</p>
                                </div>
                            </a>
                        </div>
                        
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/customer" class="text-decoration-none">
                                <div class="p-4 border rounded bg-light hover-shadow transition-all">
                                    <i class="fas fa-users fa-3x text-info mb-3"></i>
                                    <h6 class="text-dark fw-bold">Hồ Sơ Khách Hàng</h6>
                                    <p class="small text-muted mb-0">Quản lý thông tin và lịch sử mua hàng.</p>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Technician Dashboard -->
<c:if test="${dashboardType == 'technician'}">
    <div class="row g-3 mb-4">
        <div class="col-md-6">
            <a href="${pageContext.request.contextPath}/tech/warranty/list?status=PENDING" class="text-decoration-none">
                <div class="card border-0 shadow-sm h-100 border-start border-warning border-4 hover-shadow" style="transition: transform 0.2s;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <p class="text-muted mb-1 small fw-bold text-uppercase">Bảo Hành Mới (Chờ Nhận)</p>
                                <h3 class="mb-0 fw-bold text-warning">${pendingCount != null ? pendingCount : 0} ca</h3>
                            </div>
                            <div class="bg-warning bg-opacity-10 p-3 rounded-circle">
                                <i class="fas fa-exclamation-circle fa-2x text-warning"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </a>
        </div>
        
        <div class="col-md-6">
            <a href="${pageContext.request.contextPath}/tech/warranty/list?status=IN_PROGRESS" class="text-decoration-none">
                <div class="card border-0 shadow-sm h-100 border-start border-info border-4 hover-shadow" style="transition: transform 0.2s;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <p class="text-muted mb-1 small fw-bold text-uppercase">Đang Xử Lý (Của bạn)</p>
                                <h3 class="mb-0 fw-bold text-info">${inProgressCount != null ? inProgressCount : 0} ca</h3>
                            </div>
                            <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                                <i class="fas fa-tools fa-2x text-info"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </a>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0"><i class="fas fa-search text-primary me-2"></i>Tra Cứu Nhanh Hồ Sơ</h5>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/tech/warranty/list" method="GET" class="row g-3 mb-3">
                        <div class="col-md-10">
                            <input type="text" name="search" class="form-control form-control-lg bg-light" placeholder="Nhập IMEI hoặc Mã phiếu để vào việc ngay..." required>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary btn-lg w-100"><i class="fas fa-search"></i> Tra Cứu</button>
                        </div>
                    </form>
                    
                    <hr class="text-muted my-4">
                    
                    <a href="${pageContext.request.contextPath}/tech/warranty/list" class="btn btn-outline-secondary px-4">
                        <i class="fas fa-list me-2"></i> Mở toàn bộ danh sách
                    </a>
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
            fetch('${pageContext.request.contextPath}/admin/sales-report?format=json')
                .then(response => response.json())
                .then(data => {
                    const labels = data.map(item => item.label);
                    const values = data.map(item => item.value);

                    const ctx = document.getElementById('adminProductSalesChart').getContext('2d');
                    new Chart(ctx, {
                        type: 'bar',
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
                            indexAxis: 'y',
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
