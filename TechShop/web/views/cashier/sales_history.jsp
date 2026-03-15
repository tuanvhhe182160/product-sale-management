<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/views/common/header.jsp" />

<fmt:setLocale value="vi_VN" />

<style>
    .stat-card {
        border-radius: 12px;
        padding: 20px;
        color: #fff;
        transition: transform .2s;
    }
    .stat-card:hover { transform: translateY(-3px); }
    .stat-card .stat-icon { font-size: 2rem; opacity: .7; }
    .stat-card .stat-value { font-size: 1.6rem; font-weight: 700; }
    .stat-card .stat-label { font-size: .85rem; opacity: .85; }
    .bg-gradient-primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    .bg-gradient-success { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
    .bg-gradient-info    { background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%); }

    .filter-bar {
        background: #fff;
        border-radius: 10px;
        padding: 16px 20px;
        box-shadow: 0 1px 3px rgba(0,0,0,.08);
        margin-bottom: 20px;
    }
    .table-container {
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 1px 3px rgba(0,0,0,.08);
        overflow: hidden;
    }
    .table th { background: #f8f9fa; font-size: .82rem; text-transform: uppercase; color: #6c757d; }
    .table td { vertical-align: middle; font-size: .88rem; }
    .badge-status { font-size: .75rem; padding: 4px 10px; border-radius: 20px; }
    .badge-payment { font-size: .72rem; padding: 3px 8px; border-radius: 12px; }
    .btn-view { padding: 3px 10px; font-size: .78rem; }
    .empty-state { padding: 60px 20px; text-align: center; color: #adb5bd; }
    .empty-state i { font-size: 3rem; margin-bottom: 12px; }
</style>

<!-- Stat Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="stat-card bg-gradient-primary d-flex align-items-center justify-content-between">
            <div>
                <div class="stat-value">${todayCount}</div>
                <div class="stat-label">Hóa đơn hôm nay</div>
            </div>
            <div class="stat-icon"><i class="fas fa-receipt"></i></div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card bg-gradient-success d-flex align-items-center justify-content-between">
            <div>
                <div class="stat-value"><fmt:formatNumber value="${todayRevenue}" type="number" groupingUsed="true" maxFractionDigits="0" /> đ</div>
                <div class="stat-label">Doanh thu hôm nay</div>
            </div>
            <div class="stat-icon"><i class="fas fa-coins"></i></div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card bg-gradient-info d-flex align-items-center justify-content-between">
            <div>
                <div class="stat-value"><fmt:formatNumber value="${monthRevenue}" type="number" groupingUsed="true" maxFractionDigits="0" /> đ</div>
                <div class="stat-label">Doanh thu tháng này</div>
            </div>
            <div class="stat-icon"><i class="fas fa-chart-line"></i></div>
        </div>
    </div>
</div>

<!-- Filter Bar -->
<div class="filter-bar">
    <form method="get" action="${pageContext.request.contextPath}/invoice" class="row g-2 align-items-end">
        <div class="col-md-3">
            <label class="form-label" style="font-size:.8rem;">Tìm kiếm</label>
            <input type="text" name="search" class="form-control form-control-sm"
                   placeholder="Mã HĐ, tên KH, SĐT..." value="${search}" />
        </div>
        <div class="col-md-2">
            <label class="form-label" style="font-size:.8rem;">Từ ngày</label>
            <input type="date" name="dateFrom" class="form-control form-control-sm" value="${dateFrom}" />
        </div>
        <div class="col-md-2">
            <label class="form-label" style="font-size:.8rem;">Đến ngày</label>
            <input type="date" name="dateTo" class="form-control form-control-sm" value="${dateTo}" />
        </div>
        <div class="col-md-2">
            <label class="form-label" style="font-size:.8rem;">Trạng thái</label>
            <select name="status" class="form-select form-select-sm">
                <option value="">Tất cả</option>
                <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
            </select>
        </div>
        <div class="col-md-3 d-flex gap-2 flex-wrap">
            <button type="submit" class="btn btn-primary btn-sm">
                <i class="fas fa-search"></i> Lọc
            </button>
            <a href="${pageContext.request.contextPath}/invoice" class="btn btn-outline-secondary btn-sm">
                <i class="fas fa-redo"></i> Xóa lọc
            </a>
            <a href="${pageContext.request.contextPath}/invoice?action=export&search=${search}&dateFrom=${dateFrom}&dateTo=${dateTo}&status=${status}"
               class="btn btn-outline-success btn-sm" title="Xuất Excel">
                <i class="fas fa-file-excel"></i> Xuất Excel
            </a>
            <a href="${pageContext.request.contextPath}/cashier" class="btn btn-success btn-sm">
                <i class="fas fa-cash-register"></i> Bán hàng
            </a>
        </div>
    </form>
</div>

<!-- Invoice Table -->
<div class="table-container">
    <c:choose>
        <c:when test="${empty invoices}">
            <div class="empty-state">
                <i class="fas fa-file-invoice"></i>
                <p>Chưa có hóa đơn nào</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Mã hóa đơn</th>
                            <th>Thời gian</th>
                            <th>Khách hàng</th>
                            <th class="text-center">SP</th>
                            <th class="text-end">Tổng tiền</th>
                            <th class="text-center">Thanh toán</th>
                            <th class="text-center">Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="inv" items="${invoices}" varStatus="loop">
                            <tr>
                                <td class="text-muted">${(currentPage - 1) * 15 + loop.index + 1}</td>
                                <td>
                                    <span style="font-weight:600; color:#4a5568;">${inv.invoiceCode}</span>
                                </td>
                                <td>${inv.invoiceDateFormatted}</td>
                                <td>
                                    <div style="font-weight:500;">${inv.customerName}</div>
                                    <small class="text-muted">${inv.customerPhone}</small>
                                </td>
                                <td class="text-center">
                                    <span class="badge bg-light text-dark">${inv.itemCount}</span>
                                </td>
                                <td class="text-end" style="font-weight:600; color:#2d3748;">
                                    <fmt:formatNumber value="${inv.finalAmount}" type="number" groupingUsed="true" maxFractionDigits="0" /> đ
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${inv.paymentMethod == 'CASH'}">
                                            <span class="badge-payment badge bg-success-subtle text-success">Tiền mặt</span>
                                        </c:when>
                                        <c:when test="${inv.paymentMethod == 'CARD'}">
                                            <span class="badge-payment badge bg-primary-subtle text-primary">Thẻ</span>
                                        </c:when>
                                        <c:when test="${inv.paymentMethod == 'TRANSFER'}">
                                            <span class="badge-payment badge bg-info-subtle text-info">Chuyển khoản</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-payment badge bg-secondary-subtle text-secondary">${inv.paymentMethod}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${inv.status == 'COMPLETED'}">
                                            <span class="badge-status badge bg-success">Hoàn thành</span>
                                        </c:when>
                                        <c:when test="${inv.status == 'CANCELLED'}">
                                            <span class="badge-status badge bg-danger">Đã hủy</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge bg-secondary">${inv.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/invoice/print?id=${inv.invoiceId}"
                                       class="btn btn-outline-primary btn-view" target="_blank"
                                       title="Xem hóa đơn">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Pagination + Info -->
<c:if test="${totalPages > 1}">
<div class="d-flex justify-content-between align-items-center mt-3 px-2">
    <small class="text-muted">
        Hiển thị ${(currentPage - 1) * 15 + 1} - ${(currentPage - 1) * 15 + invoices.size()} / ${totalItems} hóa đơn
    </small>
    <nav>
        <ul class="pagination pagination-sm mb-0">
            <c:if test="${currentPage > 1}">
                <li class="page-item">
                    <a class="page-link" href="${pageContext.request.contextPath}/invoice?page=${currentPage - 1}&search=${search}&dateFrom=${dateFrom}&dateTo=${dateTo}&status=${status}">
                        <i class="fas fa-chevron-left"></i>
                    </a>
                </li>
            </c:if>

            <c:set var="pgStart" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />
            <c:set var="pgEnd" value="${currentPage + 2 > totalPages ? totalPages : currentPage + 2}" />

            <c:forEach var="p" begin="${pgStart}" end="${pgEnd}">
                <li class="page-item ${p == currentPage ? 'active' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/invoice?page=${p}&search=${search}&dateFrom=${dateFrom}&dateTo=${dateTo}&status=${status}">${p}</a>
                </li>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <li class="page-item">
                    <a class="page-link" href="${pageContext.request.contextPath}/invoice?page=${currentPage + 1}&search=${search}&dateFrom=${dateFrom}&dateTo=${dateTo}&status=${status}">
                        <i class="fas fa-chevron-right"></i>
                    </a>
                </li>
            </c:if>
        </ul>
    </nav>
</div>
</c:if>

<jsp:include page="/views/common/footer.jsp" />
