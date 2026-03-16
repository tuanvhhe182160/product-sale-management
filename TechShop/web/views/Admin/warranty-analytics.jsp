<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Thông Số Bảo Hành - TechShop" />
<%@ include file="../common/header.jsp" %>
<div class="container-fluid py-4 px-4 mb-5">
    
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary fw-bold">Bảng Điều Khiển: Thống Kê Bảo Hành</h2>
        <button class="btn btn-outline-primary" onclick="window.print()">🖨️ Xuất Báo Cáo</button>
    </div>

    <div class="row mb-4 g-3">
        <div class="col-md-3">
            <div class="card card-stats shadow-sm h-100 border-start border-primary border-4">
                <div class="card-body">
                    <h6 class="text-muted fw-bold text-uppercase">Tổng Số Yêu Cầu</h6>
                    <h2 class="text-primary mb-0">${generalStats.totalRequests}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card card-stats shadow-sm h-100 border-start border-success border-4">
                <div class="card-body">
                    <h6 class="text-muted fw-bold text-uppercase">Tỷ Lệ Hoàn Thành</h6>
                    <h2 class="text-success mb-0">${generalStats.completionRate}%</h2>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card card-stats shadow-sm h-100 border-start border-warning border-4">
                <div class="card-body">
                    <h6 class="text-muted fw-bold text-uppercase">Đang Xử Lý & Chờ</h6>
                    <h2 class="text-warning mb-0">${generalStats.totalInProgress + generalStats.totalPending}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card card-stats shadow-sm h-100 border-start border-danger border-4">
                <div class="card-body">
                    <h6 class="text-muted fw-bold text-uppercase">Tỷ Lệ Bị Từ Chối</h6>
                    <c:set var="rejectRate" value="${generalStats.totalRequests > 0 ? (generalStats.totalRejected * 100.0 / generalStats.totalRequests) : 0}" />
                    <h2 class="text-danger mb-0"><fmt:formatNumber value="${rejectRate}" maxFractionDigits="1"/>%</h2>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-4 g-3">
        <div class="col-md-7">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white fw-bold">Hiệu Suất Kỹ Thuật Viên</div>
                <div class="card-body">
                    <canvas id="techChart" height="100"></canvas>
                </div>
            </div>
        </div>
        
        <div class="col-md-5">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white fw-bold">Top Sản Phẩm Lỗi Nhiều Nhất</div>
                <div class="card-body d-flex justify-content-center">
                    <canvas id="productChart" height="200"></canvas>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header bg-dark text-white fw-bold">Chi Tiết KPI Kỹ Thuật Viên</div>
                <div class="card-body p-0">
                    <table class="table table-hover table-striped mb-0">
                        <thead>
                            <tr>
                                <th>Kỹ Thuật Viên</th>
                                <th class="text-center">Số Ca Tiếp Nhận</th>
                                <th class="text-center">Đã Hoàn Thành</th>
                                <th class="text-center">Tỷ Lệ Xong</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="tech" items="${techPerformance}">
                                <tr>
                                    <td class="fw-bold">${tech.technicianName}</td>
                                    <td class="text-center">${tech.handledRequests}</td>
                                    <td class="text-center text-success fw-bold">${tech.completedRequests}</td>
                                    <td class="text-center">
                                        <c:set var="rate" value="${tech.handledRequests > 0 ? (tech.completedRequests * 100.0 / tech.handledRequests) : 0}" />
                                        <fmt:formatNumber value="${rate}" maxFractionDigits="1"/>%
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header bg-danger text-white fw-bold">Cảnh Báo Chất Lượng Sản Phẩm (Top 10)</div>
                <div class="card-body p-0">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Tên Sản Phẩm (Phiên bản)</th>
                                <th class="text-center">Số Lần Lỗi</th>
                                <th class="text-center">Mức Độ</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="prod" items="${productDefects}">
                                <tr>
                                    <td>${prod.productName}</td>
                                    <td class="text-center fw-bold text-danger">${prod.defectCount}</td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${prod.defectCount >= 10}"><span class="badge bg-danger">NGHIÊM TRỌNG</span></c:when>
                                            <c:when test="${prod.defectCount >= 5}"><span class="badge bg-warning text-dark">CAO</span></c:when>
                                            <c:otherwise><span class="badge bg-info">TRUNG BÌNH</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // 1. Dữ liệu cho Biểu đồ Kỹ thuật viên (Bar Chart)
    const techLabels = [
        <c:forEach items="${techPerformance}" var="tech" varStatus="loop">
            "${tech.technicianName}"${!loop.last ? ',' : ''}
        </c:forEach>
    ];
    const techHandled = [
        <c:forEach items="${techPerformance}" var="tech" varStatus="loop">
            ${tech.handledRequests}${!loop.last ? ',' : ''}
        </c:forEach>
    ];
    const techCompleted = [
        <c:forEach items="${techPerformance}" var="tech" varStatus="loop">
            ${tech.completedRequests}${!loop.last ? ',' : ''}
        </c:forEach>
    ];

    new Chart(document.getElementById('techChart'), {
        type: 'bar',
        data: {
            labels: techLabels,
            datasets: [
                {
                    label: 'Đã Tiếp Nhận',
                    backgroundColor: 'rgba(54, 162, 235, 0.5)',
                    borderColor: 'rgb(54, 162, 235)',
                    borderWidth: 1,
                    data: techHandled
                },
                {
                    label: 'Đã Hoàn Thành',
                    backgroundColor: 'rgba(75, 192, 192, 0.5)',
                    borderColor: 'rgb(75, 192, 192)',
                    borderWidth: 1,
                    data: techCompleted
                }
            ]
        },
        options: { responsive: true, scales: { y: { beginAtZero: true } } }
    });

    // 2. Dữ liệu cho Biểu đồ Sản Phẩm Lỗi (Doughnut Chart)
    const productLabels = [
        <c:forEach items="${productDefects}" var="prod" varStatus="loop">
            "${prod.productName}"${!loop.last ? ',' : ''}
        </c:forEach>
    ];
    const productData = [
        <c:forEach items="${productDefects}" var="prod" varStatus="loop">
            ${prod.defectCount}${!loop.last ? ',' : ''}
        </c:forEach>
    ];

    new Chart(document.getElementById('productChart'), {
        type: 'doughnut',
        data: {
            labels: productLabels,
            datasets: [{
                data: productData,
                backgroundColor: [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
                    '#FF9F40', '#C9CBCF', '#E7E9ED', '#71B37C', '#EC932F'
                ]
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });
</script>
<%@ include file="../common/footer.jsp" %>  