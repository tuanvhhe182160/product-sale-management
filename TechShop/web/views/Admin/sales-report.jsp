<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />

<c:set var="pageTitle" value="Báo cáo doanh số - Admin" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid py-4">

    <!-- FILTER CARD -->
    <div class="card shadow-sm mb-4">
        <div class="card-body">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="mb-0">
                    <i class="fas fa-file-invoice-dollar text-primary me-2"></i>
                    Báo cáo doanh số
                </h3>

                <a href="${pageContext.request.contextPath}/admin/sales-report?${pageContext.request.queryString}&action=export" 
                   class="btn btn-success">
                    <i class="fas fa-file-export me-1"></i> Xuất CSV (Excel)
                </a>
            </div>
                  
                   <!-- Form -->
<form action="${pageContext.request.contextPath}/admin/sales-report"
      method="GET"
      class="mb-4">

    <div class="row g-3">
        <div class="col-md-3">
            <label class="form-label fw-bold">Từ ngày</label>
            <input type="date" id="startDate" name="startDate" class="form-control" value="${startDate}" max="${today}">
        </div>

        <div class="col-md-3">
            <label class="form-label fw-bold">Đến ngày</label>
            <input type="date" id="endDate" name="endDate" class="form-control" value="${endDate}" max="${today}">
        </div>

        <div class="col-md-3">
            <label class="form-label fw-bold">Loại báo cáo</label>
            <select name="reportType" class="form-select" onchange="this.form.submit()">
                <option value="product" ${reportType == 'product' ? 'selected' : ''}>Doanh số theo sản phẩm</option>
                <option value="branch" ${reportType == 'branch' ? 'selected' : ''}>Doanh số theo chi nhánh</option>
            </select>
        </div>

        <div class="col-md-3 d-flex align-items-end">
            <button type="submit" class="btn btn-primary w-100">
                <i class="fas fa-filter me-1"></i> Áp dụng bộ lọc
            </button>
        </div>
    </div>

    <div class="row g-3 mt-1">
        <c:if test="${reportType eq 'product'}">
            <div class="col-md-3">
                <label class="form-label fw-bold">Danh mục</label>
                <select name="categoryId" class="form-select" onchange="this.form.submit()">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="c" items="${categoryList}">
                        <option value="${c.categoryId}" ${selectedCategoryId == c.categoryId ? 'selected' : ''}>
                            ${c.categoryName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-3">
                <label class="form-label fw-bold">Model</label>
                <select name="modelId" class="form-select" onchange="this.form.submit()">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="m" items="${modelList}">
                        <option value="${m.modelId}" ${selectedModelId == m.modelId ? 'selected' : ''}>
                            ${m.modelName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-3">
                <label class="form-label fw-bold">Phiên bản</label>
                <select name="variantId" class="form-select">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="v" items="${variantList}">
                        <option value="${v.variantId}" ${selectedVariantId == v.variantId ? 'selected' : ''}>
                            ${v.variantName}
                        </option>
                    </c:forEach>
                </select>
            </div>
        </c:if>

        <c:if test="${reportType eq 'branch'}">
            <div class="col-md-3">
                <label class="form-label fw-bold">Chi nhánh</label>
                <select name="branchId" class="form-select" onchange="this.form.submit()">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="b" items="${branchList}">
                        <option value="${b.branchId}" ${selectedBranchId == b.branchId ? 'selected' : ''}>
                            ${b.branchName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-3">
                <label class="form-label fw-bold">Nhân viên</label>
                <select name="employeeId" class="form-select">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="e" items="${employeeList}">
                        <option value="${e.userId}" ${selectedEmployeeId == e.userId ? 'selected' : ''}>
                            ${e.fullName}
                        </option>
                    </c:forEach>
                </select>
            </div>
        </c:if>
    </div>
</form>
        </div>
    </div>

    <!-- TABLE CARD -->
    <div class="card shadow-sm">
        <div class="card-body p-0">

            <div class="table-responsive">
                <table class="table table-hover mb-0 align-middle">

                    <thead class="table-light">
    <tr>
        <c:choose>
            <c:when test="${reportType == 'branch'}">
                <th class="ps-4">
                    <c:choose>
                        <c:when test="${not empty selectedBranchId}">Nhân viên</c:when>
                        <c:otherwise>Chi nhánh</c:otherwise>
                    </c:choose>
                </th>
                <th>Số đơn hàng</th>
                <th class="text-end pe-4">Tổng doanh thu</th>
            </c:when>
            <c:otherwise>
                <th class="ps-4">Danh mục</th>
                <th>Model</th>
                <th class="ps-4">Tên variant</th>
                <th>Số lượng bán</th>
                <th class="text-end pe-4">Tổng doanh thu</th>
            </c:otherwise>
        </c:choose>
    </tr>
</thead>

<tbody>
    <c:if test="${empty reportData}">
        <tr>
            <td colspan="3" class="text-center py-5 text-muted">
                <i class="fas fa-folder-open fa-2x mb-3 d-block"></i>
                Không có dữ liệu trong khoảng thời gian này
            </td>
        </tr>
    </c:if>

    <c:forEach var="item" items="${reportData}">
        <tr>
            <c:if test="${reportType ne 'branch'}">
                <td class="ps-4 text-muted">${item.category_name}</td>
                <td class="text-muted">${item.model_name}</td>
            </c:if>
                
            <td class="ps-4 fw-bold">
                <c:choose>
                    <c:when test="${reportType == 'branch'}">
                        <c:choose>
                            <c:when test="${not empty selectedBranchId}">
                                ${item.employee_name} </c:when>
                            <c:otherwise>
                                ${item.branch_name}
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        ${item.variant_name}
                    </c:otherwise>
                </c:choose>
            </td>

            <td>
                <span class="badge bg-info bg-opacity-25 text-info px-3 py-2">
                    <c:choose>
                        <c:when test="${reportType == 'branch'}">
                            ${item.total_orders}
                        </c:when>
                        <c:otherwise>
                            ${item.total_qty}
                        </c:otherwise>
                    </c:choose>
                </span>
            </td>

            <td class="text-end pe-4 fw-bold text-primary">
                <fmt:formatNumber value="${item.total_sales}"
                                  type="number"
                                  groupingUsed="true" />
                đ
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
    const startInput = document.getElementById('startDate');
    const endInput = document.getElementById('endDate');

    startInput.addEventListener('change', function() {
        if (this.value) {
            endInput.min = this.value;
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