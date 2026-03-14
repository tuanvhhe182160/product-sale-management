<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Báo cáo Tài chính - TechShop" />
<%@ include file="../common/header.jsp" %>

<c:set var="sumRevenue" value="0" />
<c:set var="sumProfit" value="0" />
<c:set var="sumOrders" value="0" />
<c:forEach items="${reportData}" var="item">
    <c:set var="sumRevenue" value="${sumRevenue + item.totalRevenue}" />
    <c:set var="sumProfit" value="${sumProfit + item.totalProfit}" />
    <c:set var="sumOrders" value="${sumOrders + item.totalOrders}" />
</c:forEach>

<div class="container-fluid px-4 py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-chart-line text-primary me-2"></i> Báo Cáo Tài Chính
        </h2>
        <a href="${pageContext.request.contextPath}/report/financial?startDate=${startDate}&endDate=${endDate}&action=export" 
           class="btn btn-success shadow-sm">
            <i class="fas fa-file-csv fa-sm text-white-50 me-1"></i> Xuất Excel / CSV
        </a>
    </div>

    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <form action="${pageContext.request.contextPath}/report/financial" method="GET" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="startDate" class="form-control" value="${startDate}" max="${today}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="endDate" class="form-control" value="${endDate}" max="${today}" required>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary px-4 w-100">
                        <i class="fas fa-filter me-2"></i>Lọc Báo Cáo
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-xl-4 col-md-6 mb-4">
            <div class="card border-start border-success border-4 shadow-sm h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs fw-bold text-success text-uppercase mb-1">Tổng Doanh Thu (Kỳ này)</div>
                            <div class="h3 mb-0 fw-bold text-gray-800">
                                <fmt:formatNumber value="${sumRevenue}" type="number" pattern="#,##0"/> ₫
                            </div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-money-bill-wave fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-xl-4 col-md-6 mb-4">
            <div class="card border-start border-primary border-4 shadow-sm h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs fw-bold text-primary text-uppercase mb-1">Tổng Lợi Nhuận (Kỳ này)</div>
                            <div class="h3 mb-0 fw-bold text-gray-800">
                                <fmt:formatNumber value="${sumProfit}" type="number" pattern="#,##0"/> ₫
                            </div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-chart-pie fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-xl-4 col-md-6 mb-4">
            <div class="card border-start border-warning border-4 shadow-sm h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs fw-bold text-warning text-uppercase mb-1">Số Giao Dịch Bán Hàng</div>
                            <div class="h3 mb-0 fw-bold text-gray-800">${sumOrders} Đơn</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-shopping-cart fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xl-12 col-lg-12 mb-4">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-header bg-white py-3 d-flex flex-row align-items-center justify-content-between">
                    <h6 class="m-0 fw-bold text-primary">Biểu Đồ Tăng Trưởng</h6>
                </div>
                <div class="card-body">
                    <div class="chart-area" style="height: 350px;">
                        <canvas id="financialChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm border-0 mb-4">
        <div class="card-header bg-white py-3">
            <h6 class="m-0 fw-bold text-primary">Chi Tiết Từng Ngày</h6>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover table-bordered text-center align-middle" width="100%" cellspacing="0">
                    <thead class="table-light">
                        <tr>
                            <th>Thời gian (Ngày)</th>
                            <th>Số đơn hàng</th>
                            <th>Doanh thu</th>
                            <th>Chi phí vốn</th>
                            <th>Lợi nhuận</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="5" class="text-muted py-4">Không có dữ liệu giao dịch trong khoảng thời gian này.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${reportData}" var="item">
                                    <tr>
                                        <td class="fw-bold">${item.period}</td>
                                        <td>${item.totalOrders}</td>
                                        <td class="text-success fw-bold">
                                            <fmt:formatNumber value="${item.totalRevenue}" type="number" pattern="#,##0"/> ₫
                                        </td>
                                        <td class="text-danger">
                                            <fmt:formatNumber value="${item.totalCost}" type="number" pattern="#,##0"/> ₫
                                        </td>
                                        <td class="text-primary fw-bold">
                                            <fmt:formatNumber value="${item.totalProfit}" type="number" pattern="#,##0"/> ₫
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

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // 1. Trích xuất dữ liệu từ JSTL (Đảo ngược mảng để vẽ từ quá khứ đến hiện tại)
    const labels = [
        <c:forEach items="${reportData}" var="item" varStatus="status">
            '${item.period}'${!status.last ? ',' : ''}
        </c:forEach>
    ].reverse();

    const revenueData = [
        <c:forEach items="${reportData}" var="item" varStatus="status">
            ${item.totalRevenue}${!status.last ? ',' : ''}
        </c:forEach>
    ].reverse();

    const profitData = [
        <c:forEach items="${reportData}" var="item" varStatus="status">
            ${item.totalProfit}${!status.last ? ',' : ''}
        </c:forEach>
    ].reverse();

    // 2. Vẽ biểu đồ
    const ctx = document.getElementById('financialChart').getContext('2d');
    new Chart(ctx, {
        type: 'line', // Biểu đồ đường
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Doanh Thu',
                    data: revenueData,
                    borderColor: '#1cc88a', // Màu xanh lá success
                    backgroundColor: 'rgba(28, 200, 138, 0.1)',
                    pointBackgroundColor: '#1cc88a',
                    borderWidth: 2,
                    tension: 0.3, // Làm cong đường nối
                    fill: true
                },
                {
                    label: 'Lợi Nhuận',
                    data: profitData,
                    borderColor: '#4e73df', // Màu xanh dương primary
                    backgroundColor: 'rgba(78, 115, 223, 0.1)',
                    pointBackgroundColor: '#4e73df',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true
                }
            ]
        },
        options: {
            maintainAspectRatio: false,
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) { label += ': '; }
                            if (context.parsed.y !== null) {
                                // Format số tiền trong Tooltip
                                label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y);
                            }
                            return label;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            // Rút gọn các số lớn trên trục Y (ví dụ 10.000.000 -> 10 Tr)
                            if (value >= 1000000) return (value / 1000000) + ' Tr';
                            if (value >= 1000) return (value / 1000) + ' K';
                            return value;
                        }
                    }
                }
            }
        }
    });
});
</script>

<%@ include file="../common/footer.jsp" %>