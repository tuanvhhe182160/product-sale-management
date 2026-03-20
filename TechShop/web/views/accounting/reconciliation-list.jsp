<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Đối Soát Chuyển Khoản - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <%-- Page Header --%>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-university text-warning me-2"></i> Đối Soát Thanh Toán Chuyển Khoản
        </h2>
        <span class="badge bg-warning text-dark fs-6 px-3 py-2">
            <i class="fas fa-clock me-1"></i> ${totalCount} hóa đơn chờ xác nhận
        </span>
    </div>

    <%-- Flash messages --%>
    <c:if test="${not empty successMsg}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>${successMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>${errorMsg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <%-- Info box --%>
    <div class="alert alert-info border-0 shadow-sm mb-4">
        <i class="fas fa-info-circle me-2"></i>
        <strong>Hướng dẫn:</strong> Kiểm tra sao kê ngân hàng, sau đó nhấn <strong>Xác Nhận</strong>
        cho từng hóa đơn đã nhận tiền thực tế. Hóa đơn CASH và CARD không hiển thị ở đây (đã xác nhận ngay tại quầy).
    </div>

    <%-- Date filter --%>
    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <form action="${pageContext.request.contextPath}/accounting/reconciliation"
                  method="GET" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="dateFrom" class="form-control"
                           value="${dateFrom}" max="${today}">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="dateTo" class="form-control"
                           value="${dateTo}" max="${today}">
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary px-4 w-100">
                        <i class="fas fa-filter me-2"></i>Lọc
                    </button>
                </div>
            </form>
        </div>
    </div>

    <%-- Invoice table --%>
    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-warning">
                        <tr>
                            <th class="px-3">#</th>
                            <th>Mã Hóa Đơn</th>
                            <th>Ngày Tạo</th>
                            <th>Khách Hàng</th>
                            <th>Thu Ngân</th>
                            <th>Hình Thức</th>
                            <th class="text-end">Số Tiền</th>
                            <th class="text-center">Trạng Thái</th>
                            <th class="text-center">Thao Tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty pendingInvoices}">
                                <tr>
                                    <td colspan="9" class="text-center text-muted py-5">
                                        <i class="fas fa-check-double fa-3x text-success mb-3 d-block"></i>
                                        <strong>Không có hóa đơn nào chờ đối soát</strong>
                                        <br><small>Tất cả hóa đơn chuyển khoản trong kỳ đã được xác nhận.</small>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${pendingInvoices}" var="inv" varStatus="st">
                                    <tr>
                                        <td class="px-3 text-muted">${(currentPage-1)*20 + st.count}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/accounting/invoice-detail?id=${inv.invoiceId}"
                                               class="fw-bold text-decoration-none"
                                               target="_blank">${inv.invoiceCode}</a>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${inv.invoiceDateAsDate}"
                                                            pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                        <td>${empty inv.customerName ? 'Khách lẻ' : inv.customerName}</td>
                                        <td>${empty inv.cashierName ? 'N/A' : inv.cashierName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${inv.paymentMethod == 'TRANSFER'}">
                                                    <span class="badge bg-info text-dark">
                                                        <i class="fas fa-university me-1"></i>Chuyển khoản
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">
                                                        <i class="fas fa-layer-group me-1"></i>Hỗn hợp
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-end fw-bold text-success">
                                            <fmt:formatNumber value="${inv.finalAmount}"
                                                              type="number" pattern="#,##0"/> ₫
                                        </td>
                                        <td class="text-center">
                                            <span class="badge bg-warning text-dark">
                                                <i class="fas fa-hourglass-half me-1"></i>Chờ xác nhận
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <button type="button"
                                                    class="btn btn-success btn-sm px-3"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#confirmModal"
                                                    data-invoice-id="${inv.invoiceId}"
                                                    data-invoice-code="${inv.invoiceCode}"
                                                    data-amount="${inv.finalAmount}">
                                                <i class="fas fa-check me-1"></i>Xác Nhận
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <%-- Pagination --%>
        <c:if test="${totalPages > 1}">
            <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                <small class="text-muted">Trang ${currentPage} / ${totalPages}</small>
                <nav>
                    <ul class="pagination pagination-sm mb-0">
                        <c:if test="${currentPage > 1}">
                            <li class="page-item">
                                <a class="page-link"
                                   href="?dateFrom=${dateFrom}&dateTo=${dateTo}&page=${currentPage-1}">
                                    &laquo;
                                </a>
                            </li>
                        </c:if>
                        <c:forEach begin="1" end="${totalPages}" var="pg">
                            <li class="page-item ${pg == currentPage ? 'active' : ''}">
                                <a class="page-link"
                                   href="?dateFrom=${dateFrom}&dateTo=${dateTo}&page=${pg}">${pg}</a>
                            </li>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <li class="page-item">
                                <a class="page-link"
                                   href="?dateFrom=${dateFrom}&dateTo=${dateTo}&page=${currentPage+1}">
                                    &raquo;
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </div>
        </c:if>
    </div>
</div>

<%-- Confirm Modal --%>
<div class="modal fade" id="confirmModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title">
                    <i class="fas fa-check-circle me-2"></i>Xác Nhận Đã Nhận Tiền
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="mb-2">Hóa đơn: <strong id="modalInvoiceCode"></strong></p>
                <p class="mb-3">Số tiền: <strong id="modalAmount" class="text-success fs-5"></strong></p>
                <div class="alert alert-warning mb-0">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Hành động này <strong>không thể hoàn tác</strong>. Chỉ xác nhận khi đã kiểm tra
                    sao kê và tiền đã vào tài khoản.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times me-1"></i>Hủy
                </button>
                <form method="POST"
                      action="${pageContext.request.contextPath}/accounting/reconciliation"
                      id="confirmForm">
                    <input type="hidden" name="action" value="confirm">
                    <input type="hidden" name="invoiceId" id="hiddenInvoiceId">
                    <input type="hidden" name="invoiceCode" id="hiddenInvoiceCode">
                    <button type="submit" class="btn btn-success px-4">
                        <i class="fas fa-check me-1"></i>Xác Nhận Đã Nhận Tiền
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById('confirmModal').addEventListener('show.bs.modal', function(event) {
    const btn = event.relatedTarget;
    const invoiceId   = btn.getAttribute('data-invoice-id');
    const invoiceCode = btn.getAttribute('data-invoice-code');
    const amount      = parseFloat(btn.getAttribute('data-amount'));

    document.getElementById('modalInvoiceCode').textContent = invoiceCode;
    document.getElementById('modalAmount').textContent =
        new Intl.NumberFormat('vi-VN').format(amount) + ' ₫';
    document.getElementById('hiddenInvoiceId').value   = invoiceId;
    document.getElementById('hiddenInvoiceCode').value = invoiceCode;
});
</script>

<%@ include file="../common/footer.jsp" %>