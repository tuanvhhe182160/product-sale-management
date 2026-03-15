<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />

<c:set var="pageTitle" value="Thống kê doanh số chi nhánh - Admin" />
<%@ include file="../common/header.jsp" %>

<style>
.stat-card { border-radius:12px; padding:20px; color:#fff; position:relative; overflow:hidden; }
.stat-card .stat-icon { position:absolute; right:15px; top:15px; font-size:2.5rem; opacity:.2; }
.stat-card .stat-value { font-size:1.8rem; font-weight:800; }
.stat-card .stat-label { font-size:.82rem; opacity:.85; margin-top:2px; }
.bg-gradient-blue { background:linear-gradient(135deg,#667eea,#764ba2); }
.bg-gradient-green { background:linear-gradient(135deg,#11998e,#38ef7d); }
.bg-gradient-orange { background:linear-gradient(135deg,#f2994a,#f2c94c); }
.bg-gradient-red { background:linear-gradient(135deg,#e44d26,#f16529); }
.chart-container { position:relative; height:350px; }
</style>

<div class="container-fluid py-4">

<!-- Tiêu đề + Nút quay lại -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <h3 class="mb-0">
        <i class="fas fa-building text-primary me-2"></i>
        Thống kê doanh số theo chi nhánh
    </h3>
    <a href="${pageContext.request.contextPath}/admin/sales-report" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left me-1"></i> Quay lại báo cáo chung
    </a>
</div>

<!-- Bộ lọc -->
<div class="card shadow-sm mb-4">
    <div class="card-body">
        <form method="GET" action="${pageContext.request.contextPath}/admin/branch-report">
            <div class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label fw-bold">Từ ngày</label>
                    <input type="date" name="startDate" class="form-control" value="${startDate}" max="${today}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold">Đến ngày</label>
                    <input type="date" name="endDate" class="form-control" value="${endDate}" max="${today}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold">Chi nhánh</label>
                    <select name="branchId" class="form-select">
                        <option value="">-- Tất cả chi nhánh --</option>
                        <c:forEach var="b" items="${branchList}">
                            <option value="${b.branchId}" ${selectedBranchId == b.branchId ? 'selected' : ''}>
                                ${b.branchName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-filter me-1"></i> Xem thống kê
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Thống kê tổng quan -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="stat-card bg-gradient-blue">
            <div class="stat-icon"><i class="fas fa-building"></i></div>
            <div class="stat-value">${overallStats.total_branches}</div>
            <div class="stat-label">Chi nhánh có doanh thu</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card bg-gradient-green">
            <div class="stat-icon"><i class="fas fa-receipt"></i></div>
            <div class="stat-value">${overallStats.total_orders}</div>
            <div class="stat-label">Tổng số đơn hàng</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card bg-gradient-orange">
            <div class="stat-icon"><i class="fas fa-coins"></i></div>
            <div class="stat-value">
                <fmt:formatNumber value="${overallStats.total_revenue}" type="number" groupingUsed="true"/>đ
            </div>
            <div class="stat-label">Tổng doanh thu</div>
        </div>
    </div>
</div>

<!-- Biểu đồ so sánh chi nhánh -->
<c:if test="${empty selectedBranchId}">
<div class="card shadow-sm mb-4">
    <div class="card-header bg-white py-3">
        <span class="fw-bold"><i class="fas fa-chart-bar me-2 text-primary"></i>Biểu đồ doanh thu theo chi nhánh</span>
    </div>
    <div class="card-body">
        <div class="chart-container">
            <canvas id="branchChart"></canvas>
        </div>
    </div>
</div>
</c:if>

<!-- Bảng doanh thu chi nhánh -->
<c:if test="${empty selectedBranchId}">
<div class="card shadow-sm mb-4">
    <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
        <span class="fw-bold"><i class="fas fa-table me-2 text-primary"></i>Doanh thu từng chi nhánh</span>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">#</th>
                        <th>Chi nhánh</th>
                        <th class="text-center">Số đơn hàng</th>
                        <th class="text-end">Doanh thu</th>
                        <th class="text-end pe-4">Tỷ lệ</th>
                        <th class="text-center">Chi tiết</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty branchSales}">
                        <tr>
                            <td colspan="6" class="text-center py-5 text-muted">
                                <i class="fas fa-folder-open fa-2x mb-3 d-block"></i>
                                Không có dữ liệu trong khoảng thời gian này
                            </td>
                        </tr>
                    </c:if>
                    <c:set var="idx" value="0" />
                    <c:forEach var="item" items="${branchSales}">
                        <c:set var="idx" value="${idx + 1}" />
                        <tr>
                            <td class="ps-4">${idx}</td>
                            <td class="fw-bold">${item.branch_name}</td>
                            <td class="text-center">
                                <span class="badge bg-info bg-opacity-25 text-info px-3 py-2">
                                    ${item.total_orders}
                                </span>
                            </td>
                            <td class="text-end fw-bold text-primary">
                                <fmt:formatNumber value="${item.total_sales}" type="number" groupingUsed="true"/>đ
                            </td>
                            <td class="text-end pe-4">
                                <c:if test="${overallStats.total_revenue > 0}">
                                    <fmt:formatNumber value="${item.total_sales / overallStats.total_revenue * 100}" 
                                                      type="number" maxFractionDigits="1"/>%
                                </c:if>
                                <c:if test="${overallStats.total_revenue == 0}">0%</c:if>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/admin/branch-report?startDate=${startDate}&endDate=${endDate}&branchId=${item.branch_id}" 
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="fas fa-eye"></i> Xem
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</c:if>

<!-- Chi tiết chi nhánh đã chọn -->
<c:if test="${not empty selectedBranchId}">

<!-- Biểu đồ doanh thu theo ngày -->
<div class="card shadow-sm mb-4">
    <div class="card-header bg-white py-3">
        <span class="fw-bold">
            <i class="fas fa-chart-line me-2 text-success"></i>
            Doanh thu theo ngày - ${selectedBranchName}
        </span>
    </div>
    <div class="card-body">
        <div class="chart-container">
            <canvas id="dailyChart"></canvas>
        </div>
    </div>
</div>

<!-- Bảng nhân viên -->
<div class="card shadow-sm mb-4">
    <div class="card-header bg-white py-3">
        <span class="fw-bold">
            <i class="fas fa-users me-2 text-primary"></i>
            Doanh số nhân viên - ${selectedBranchName}
        </span>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">#</th>
                        <th>Nhân viên</th>
                        <th class="text-center">Số đơn hàng</th>
                        <th class="text-end pe-4">Doanh thu</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty employeeDetail}">
                        <tr>
                            <td colspan="4" class="text-center py-5 text-muted">
                                Không có dữ liệu nhân viên
                            </td>
                        </tr>
                    </c:if>
                    <c:set var="empIdx" value="0" />
                    <c:forEach var="emp" items="${employeeDetail}">
                        <c:set var="empIdx" value="${empIdx + 1}" />
                        <tr>
                            <td class="ps-4">${empIdx}</td>
                            <td class="fw-bold">${emp.employee_name}</td>
                            <td class="text-center">
                                <span class="badge bg-info bg-opacity-25 text-info px-3 py-2">
                                    ${emp.total_orders}
                                </span>
                            </td>
                            <td class="text-end pe-4 fw-bold text-primary">
                                <fmt:formatNumber value="${emp.total_sales}" type="number" groupingUsed="true"/>đ
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

</c:if>

</div> <!-- end container-fluid -->

<!-- Chart.js CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<script>
const COLORS = ['#667eea','#11998e','#f2994a','#e44d26','#764ba2','#38ef7d','#f2c94c','#3498db','#e74c3c','#2ecc71'];

// Biểu đồ so sánh chi nhánh (bar + pie)
<c:if test="${empty selectedBranchId}">
(function() {
    const labels = [];
    const salesData = [];
    const ordersData = [];
    <c:forEach var="item" items="${branchSales}">
        labels.push('${item.branch_name}');
        salesData.push(${item.total_sales});
        ordersData.push(${item.total_orders});
    </c:forEach>

    if (labels.length > 0) {
        new Chart(document.getElementById('branchChart'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu (đ)',
                    data: salesData,
                    backgroundColor: COLORS.slice(0, labels.length),
                    borderRadius: 8,
                    barPercentage: 0.6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ctx.parsed.y.toLocaleString('vi-VN') + 'đ';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(v) { return v.toLocaleString('vi-VN') + 'đ'; }
                        }
                    }
                }
            }
        });
    }
})();
</c:if>

// Biểu đồ doanh thu theo ngày (line chart) khi đã chọn chi nhánh
<c:if test="${not empty selectedBranchId}">
(function() {
    const dailyLabels = [];
    const dailySales = [];
    <c:forEach var="d" items="${dailyRevenue}">
        dailyLabels.push('${d.sale_date}');
        dailySales.push(${d.total_sales});
    </c:forEach>

    if (dailyLabels.length > 0) {
        new Chart(document.getElementById('dailyChart'), {
            type: 'line',
            data: {
                labels: dailyLabels,
                datasets: [{
                    label: 'Doanh thu (đ)',
                    data: dailySales,
                    borderColor: '#11998e',
                    backgroundColor: 'rgba(17,153,142,0.1)',
                    fill: true,
                    tension: 0.3,
                    pointRadius: 4,
                    pointBackgroundColor: '#11998e'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ctx.parsed.y.toLocaleString('vi-VN') + 'đ';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(v) { return v.toLocaleString('vi-VN') + 'đ'; }
                        }
                    }
                }
            }
        });
    }
})();
</c:if>
</script>

<%@ include file="../common/footer.jsp" %>
