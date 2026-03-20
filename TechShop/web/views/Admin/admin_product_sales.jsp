<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Báo cáo Doanh số Sản phẩm - Admin" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <%-- ===== PAGE HEADER ===== --%>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-cubes text-primary me-2"></i> Báo Cáo Doanh Số Theo Sản Phẩm
        </h2>
        <a href="${pageContext.request.contextPath}/admin/product-report?startDate=${startDate}&endDate=${endDate}&categoryId=${selectedCategory}&modelId=${selectedModel}&variantId=${selectedVariant}&action=export"
           class="btn btn-success shadow-sm">
            <i class="fas fa-file-csv me-1"></i> Xuất CSV
        </a>
    </div>

    <%-- ===== FILTER FORM ===== --%>
    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <form action="${pageContext.request.contextPath}/admin/product-report"
                  method="GET" class="row g-3 align-items-end" id="filterForm">

                <div class="col-md-2">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="startDate" class="form-control"
                           value="${startDate}" max="${today}" required>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="endDate" class="form-control"
                           value="${endDate}" max="${today}" required>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-bold text-muted small">Danh mục</label>
                    <select name="categoryId" id="categorySelect" class="form-select">
                        <option value="">-- Tất cả --</option>
                        <c:forEach items="${categories}" var="cat">
                            <option value="${cat.categoryId}"
                                <c:if test="${selectedCategory == cat.categoryId}">selected</c:if>>
                                ${cat.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-bold text-muted small">Model</label>
                    <select name="modelId" id="modelSelect" class="form-select">
                        <option value="">-- Tất cả --</option>
                        <c:forEach items="${models}" var="m">
                            <option value="${m.modelId}"
                                <c:if test="${selectedModel == m.modelId}">selected</c:if>>
                                ${m.modelName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-bold text-muted small">Biến thể</label>
                    <select name="variantId" id="variantSelect" class="form-select">
                        <option value="">-- Tất cả --</option>
                        <c:forEach items="${variants}" var="v">
                            <option value="${v.variantId}"
                                <c:if test="${selectedVariant == v.variantId}">selected</c:if>>
                                ${v.variantName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-filter me-1"></i> Áp dụng
                    </button>
                </div>

            </form>
        </div>
    </div>

    <%-- ===== KPI SUMMARY ===== --%>
    <c:set var="totalQty"   value="0"/>
    <c:set var="totalSales" value="0"/>
    <c:forEach items="${reportData}" var="row">
        <c:set var="totalQty"   value="${totalQty   + row.total_qty}"/>
        <c:set var="totalSales" value="${totalSales + row.total_sales}"/>
    </c:forEach>

    <div class="row mb-4">
        <div class="col-md-3 mb-3">
            <div class="card border-start border-primary border-4 shadow-sm h-100 py-2">
                <div class="card-body">
                    <div class="text-xs fw-bold text-primary text-uppercase mb-1">Số dòng kết quả</div>
                    <div class="h4 mb-0 fw-bold">${reportData.size()}</div>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card border-start border-success border-4 shadow-sm h-100 py-2">
                <div class="card-body">
                    <div class="text-xs fw-bold text-success text-uppercase mb-1">Tổng số lượng bán</div>
                    <div class="h4 mb-0 fw-bold">
                        <fmt:formatNumber value="${totalQty}" type="number" pattern="#,##0"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card border-start border-warning border-4 shadow-sm h-100 py-2">
                <div class="card-body">
                    <div class="text-xs fw-bold text-warning text-uppercase mb-1">Tổng doanh thu</div>
                    <div class="h4 mb-0 fw-bold">
                        <fmt:formatNumber value="${totalSales}" type="number" pattern="#,##0"/> &#x20AB;
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- ===== DATA TABLE ===== --%>
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h6 class="mb-0 fw-bold text-primary">
                <i class="fas fa-table me-2"></i>Chi tiết doanh số theo sản phẩm
            </h6>
            <small class="text-muted">
                Kỳ: ${startDate} &rarr; ${endDate}
            </small>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="reportTable">
                    <thead class="table-primary">
                        <tr>
                            <th class="px-3" style="width:50px">#</th>
                            <th>Danh mục</th>
                            <th>Model</th>
                            <th>Biến thể</th>
                            <th class="text-center" style="width:130px">Số lượng bán</th>
                            <th class="text-end" style="width:160px">Doanh thu</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-5">
                                        <i class="fas fa-box-open fa-3x mb-3 d-block text-muted"></i>
                                        <strong>Không có dữ liệu</strong> cho bộ lọc đã chọn.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${reportData}" var="row" varStatus="st">
                                    <tr>
                                        <td class="px-3 text-muted">${st.count}</td>
                                        <td>
                                            <span class="badge bg-light text-dark border">
                                                ${row.category_name}
                                            </span>
                                        </td>
                                        <td class="text-muted">${row.model_name}</td>
                                        <td class="fw-semibold">${row.variant_name}</td>
                                        <td class="text-center">
                                            <span class="badge bg-primary rounded-pill px-3 py-2 fs-6">
                                                <fmt:formatNumber value="${row.total_qty}"
                                                                  type="number" pattern="#,##0"/>
                                            </span>
                                        </td>
                                        <td class="text-end fw-bold text-success">
                                            <fmt:formatNumber value="${row.total_sales}"
                                                              type="number" pattern="#,##0"/> &#x20AB;
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>

                    <%-- Footer totals row --%>
                    <c:if test="${not empty reportData}">
                        <tfoot class="table-secondary fw-bold">
                            <tr>
                                <td colspan="4" class="px-3 text-end text-muted">Tổng cộng:</td>
                                <td class="text-center">
                                    <fmt:formatNumber value="${totalQty}"
                                                      type="number" pattern="#,##0"/>
                                </td>
                                <td class="text-end text-success">
                                    <fmt:formatNumber value="${totalSales}"
                                                      type="number" pattern="#,##0"/> &#x20AB;
                                </td>
                            </tr>
                        </tfoot>
                    </c:if>
                </table>
            </div>
        </div>
    </div>

</div><%-- /container-fluid --%>

<%-- ===== JAVASCRIPT: CASCADE DROPDOWNS via AJAX ===== --%>
<script>
(function () {
    const ctx         = '${pageContext.request.contextPath}';
    const catSelect   = document.getElementById('categorySelect');
    const modelSelect = document.getElementById('modelSelect');
    const varSelect   = document.getElementById('variantSelect');

    /* Utility: rebuild a <select> from an array of {fieldId, fieldLabel} objects */
    function rebuildSelect(sel, items, idField, labelField) {
        sel.innerHTML = '<option value="">-- T\u1EA5t c\u1EA3 --</option>';
        items.forEach(function(item) {
            const opt = document.createElement('option');
            opt.value       = item[idField];
            opt.textContent = item[labelField];
            sel.appendChild(opt);
        });
    }

    /* Category changed → reload Models, clear Variants */
    catSelect.addEventListener('change', function () {
        const catId = this.value;

        modelSelect.innerHTML = '<option value="">-- \u0110ang t\u1EA3i... --</option>';
        varSelect.innerHTML   = '<option value="">-- T\u1EA5t c\u1EA3 --</option>';

        if (!catId) {
            modelSelect.innerHTML = '<option value="">-- T\u1EA5t c\u1EA3 --</option>';
            return;
        }

        fetch(ctx + '/admin/product-report?action=models&categoryId=' + catId)
            .then(function(r) { return r.json(); })
            .then(function(data) {
                rebuildSelect(modelSelect, data, 'modelId', 'modelName');
            })
            .catch(function() {
                modelSelect.innerHTML = '<option value="">-- L\u1ED7i t\u1EA3i --</option>';
            });
    });

    /* Model changed → reload Variants */
    modelSelect.addEventListener('change', function () {
        const modelId = this.value;

        varSelect.innerHTML = '<option value="">-- \u0110ang t\u1EA3i... --</option>';

        if (!modelId) {
            varSelect.innerHTML = '<option value="">-- T\u1EA5t c\u1EA3 --</option>';
            return;
        }

        fetch(ctx + '/admin/product-report?action=variants&modelId=' + modelId)
            .then(function(r) { return r.json(); })
            .then(function(data) {
                rebuildSelect(varSelect, data, 'variantId', 'variantName');
            })
            .catch(function() {
                varSelect.innerHTML = '<option value="">-- L\u1ED7i t\u1EA3i --</option>';
            });
    });

}());
</script>

<%@ include file="../common/footer.jsp" %>
