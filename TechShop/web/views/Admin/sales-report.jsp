<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Báo cáo doanh số - Admin" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid py-4">
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 class="mb-0"><i class="fas fa-file-invoice-dollar text-primary me-2"></i>Báo cáo doanh số</h3>
                <a href="${pageContext.request.contextPath}/admin/sales-report?action=export&startDate=${startDate}&endDate=${endDate}&reportType=${reportType}" 
                   class="btn btn-success">
                    <i class="fas fa-file-export me-1"></i> Xuất CSV (Excel)
                </a>
            </div>
            
            <form action="${pageContext.request.contextPath}/admin/sales-report" method="GET" class="row g-3">
                <div class="col-md-3">
                    <label class="form-label small fw-bold">Từ ngày</label>
                    <input type="date" name="startDate" id="startDate" class="form-control" 
                        value="${startDate}" max="${today}">
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-bold">Đến ngày</label>
                    <input type="date" name="endDate" id="endDate" class="form-control" 
                        value="${endDate}" max="${today}">
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-bold">Loại báo cáo</label>
                    <select name="reportType" class="form-select">
                        <option value="product" ${reportType == 'product' ? 'selected' : ''}>Doanh số theo sản phẩm</option>
                        <option value="branch" ${reportType == 'branch' ? 'selected' : ''}>Doanh số theo chi nhánh</option>
                    </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-filter me-1"></i> Áp dụng bộ lọc
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="bg-light">
                        <tr>
                            <c:choose>
                                <c:when test="${reportType == 'branch'}">
                                    <th class="ps-4">Chi nhánh</th>
                                    <th>Số lượng đơn hàng</th>
                                    <th class="text-end pe-4">Tổng doanh thu</th>
                                </c:when>
                                <c:otherwise>
                                    <th class="ps-4">Tên sản phẩm</th>
                                    <th>Số lượng bán ra</th>
                                    <th class="text-end pe-4">Tổng doanh thu</th>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty reportData}">
                            <tr>
                                <td colspan="3" class="text-center py-5 text-muted">
                                    <i class="fas fa-folder-open fa-3x mb-3 d-block"></i>
                                    Không có dữ liệu trong khoảng thời gian này.
                                </td>
                            </tr>
                        </c:if>
                        <c:forEach var="item" items="${reportData}">
                            <tr>
                                <td class="ps-4 fw-bold">
                                    ${reportType == 'branch' ? item.branch_name : item.variant_name}
                                </td>
                                <td>
                                    <span class="badge bg-info bg-opacity-10 text-info px-3">
                                        ${reportType == 'branch' ? item.order_count : item.total_qty}
                                    </span>
                                </td>
                                <td class="text-end pe-4 fw-bold text-primary">
                                    <fmt:formatNumber value="${item.total_sales}" type="currency" currencySymbol="đ" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
    // Validation ngay tại trình duyệt
    const startInput = document.getElementById('startDate');
    const endInput = document.getElementById('endDate');

    startInput.addEventListener('change', function() {
        if (this.value) {
            endInput.min = this.value; // Ngày kết thúc không được nhỏ hơn ngày bắt đầu
        }
    });

    endInput.addEventListener('change', function() {
        if (this.value && startInput.value && this.value < startInput.value) {
            alert('Ngày kết thúc không được nhỏ hơn ngày bắt đầu!');
            this.value = startInput.value;
        }
    });
</script>
<%@ include file="../common/footer.jsp" %>