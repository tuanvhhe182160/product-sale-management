<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Báo cáo Doanh thu - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
.stat-card { border-radius:12px; padding:20px; color:#fff; position:relative; overflow:hidden; }
.stat-card .stat-icon { position:absolute; right:15px; top:15px; font-size:2.5rem; opacity:.2; }
.stat-card .stat-value { font-size:1.8rem; font-weight:800; }
.stat-card .stat-label { font-size:.82rem; opacity:.85; margin-top:2px; }
.bg-gradient-blue { background:linear-gradient(135deg,#667eea,#764ba2); }
.bg-gradient-green { background:linear-gradient(135deg,#11998e,#38ef7d); }
.bg-gradient-orange { background:linear-gradient(135deg,#f2994a,#f2c94c); }
.report-table th { font-size:.78rem; text-transform:uppercase; letter-spacing:.05em; color:#6b7280; }
.report-table td { vertical-align:middle; font-size:.88rem; }
</style>

<h4 class="mb-4 fw-bold"><i class="fas fa-chart-line text-primary me-2"></i>Báo cáo Doanh thu</h4>

<!-- Bộ lọc -->
<div class="card mb-4">
    <div class="card-body py-3">
        <form method="get" action="${pageContext.request.contextPath}/report" class="row g-2 align-items-end">
            <div class="col-md-3">
                <label class="form-label small fw-bold">Chi nhánh</label>
                <select name="branchId" class="form-select form-select-sm">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="b" items="${branches}">
                        <option value="${b.branchId}" <c:if test="${branchId == b.branchId}">selected</c:if>>${b.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label small fw-bold">Từ ngày</label>
                <input type="date" name="dateFrom" class="form-control form-control-sm" value="${dateFrom}">
            </div>
            <div class="col-md-3">
                <label class="form-label small fw-bold">Đến ngày</label>
                <input type="date" name="dateTo" class="form-control form-control-sm" value="${dateTo}">
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary btn-sm w-100">
                    <i class="fas fa-filter me-1"></i>Xem báo cáo
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Thống kê tổng quan -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="stat-card bg-gradient-blue">
            <div class="stat-icon"><i class="fas fa-receipt"></i></div>
            <div class="stat-value">${totalInvoices}</div>
            <div class="stat-label">Tổng hóa đơn</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card bg-gradient-green">
            <div class="stat-icon"><i class="fas fa-coins"></i></div>
            <div class="stat-value"><fmt:formatNumber value="${totalRevenue}" type="number" groupingUsed="true"/>đ</div>
            <div class="stat-label">Tổng doanh thu</div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card bg-gradient-orange">
            <div class="stat-icon"><i class="fas fa-calculator"></i></div>
            <div class="stat-value"><fmt:formatNumber value="${avgPerInvoice}" type="number" groupingUsed="true"/>đ</div>
            <div class="stat-label">Trung bình / hóa đơn</div>
        </div>
    </div>
</div>

<div class="row g-4">
<!-- Doanh thu theo chi nhánh -->
<div class="col-lg-6">
<div class="card">
    <div class="card-header bg-white py-3">
        <span class="fw-bold"><i class="fas fa-building me-1"></i>Doanh thu theo Chi nhánh</span>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty byBranch}">
                <p class="text-muted text-center py-4">Không có dữ liệu.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover report-table mb-0">
                        <thead><tr><th>Chi nhánh</th><th class="text-end">Số HĐ</th><th class="text-end">Doanh thu</th></tr></thead>
                        <tbody>
                        <c:forEach var="row" items="${byBranch}">
                            <tr>
                                <td class="fw-semibold">${row[1]}</td>
                                <td class="text-end">${row[2]}</td>
                                <td class="text-end fw-bold text-success"><fmt:formatNumber value="${row[3]}" type="number" groupingUsed="true"/>đ</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</div>

<!-- Doanh thu theo ngày -->
<div class="col-lg-6">
<div class="card">
    <div class="card-header bg-white py-3">
        <span class="fw-bold"><i class="fas fa-calendar-day me-1"></i>Doanh thu theo Ngày</span>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty daily}">
                <p class="text-muted text-center py-4">Không có dữ liệu.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive" style="max-height:400px; overflow-y:auto;">
                    <table class="table table-hover report-table mb-0">
                        <thead><tr><th>Ngày</th><th class="text-end">Số HĐ</th><th class="text-end">Doanh thu</th></tr></thead>
                        <tbody>
                        <c:forEach var="row" items="${daily}">
                            <tr>
                                <td>${row[0]}</td>
                                <td class="text-end">${row[1]}</td>
                                <td class="text-end fw-bold text-success"><fmt:formatNumber value="${row[2]}" type="number" groupingUsed="true"/>đ</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</div>
</div>

<%@ include file="../common/footer.jsp" %>
