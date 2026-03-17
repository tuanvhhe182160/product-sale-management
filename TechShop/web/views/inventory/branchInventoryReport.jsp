<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-chart-bar"></i> Inventory Report
    </h2>
    <div class="d-flex align-items-center gap-2">
        <span class="badge bg-primary fs-6">
            <i class="fas fa-building"></i> ${sessionScope.branchName}
        </span>
        <a href="${pageContext.request.contextPath}/inventory/import" class="btn btn-success btn-sm">
            <i class="fas fa-plus"></i> Import Products
        </a>
    </div>
</div>

<c:if test="${error != null}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle"></i> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="card border-primary mb-3">
    <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
            <span class="text-muted">Total units in stock</span>
            <span class="fs-4 fw-bold text-primary">${totalInventoryLevel} units</span>
        </div>
    </div>
</div>

<div class="card border-primary">
    <div class="card-body">
        <c:if test="${empty inventoryItems}">
            <div class="text-center text-muted py-4">
                <i class="fas fa-box-open fa-2x mb-2"></i>
                <p class="mb-0">No inventory data found for your branch.</p>
            </div>
        </c:if>

        <c:if test="${not empty inventoryItems}">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-primary">
                    <tr>
                        <th>Variant</th>
                        <th>SKU</th>
                        <th>Category</th>
                        <th class="text-end">Base Price</th>
                        <th class="text-end">Cost Price</th>
                        <th class="text-end">Warranty</th>
                        <th class="text-end">Current Stock</th>
                        <th class="text-center">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${inventoryItems}">
                        <tr class="${item.inventoryLevel == 0 ? 'table-danger' : ''}">
                            <td>${item.variantName}</td>
                            <td>${item.sku}</td>
                            <td>${item.categoryName}</td>
                            <td class="text-end">
                                <fmt:formatNumber value="${item.basePrice}" type="number" groupingUsed="true"
                                                  maxFractionDigits="0"/> ₫
                            </td>
                            <td class="text-end">
                                <fmt:formatNumber value="${item.costPrice}" type="number" groupingUsed="true"
                                                  maxFractionDigits="0"/> ₫
                            </td>
                            <td class="text-end">${item.warrantyMonths} months</td>
                            <td class="text-end fw-semibold ${item.inventoryLevel == 0 ? 'text-danger' : 'text-primary'}">${item.inventoryLevel}
                                units
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/inventory/list?variantId=${item.variantId}&status=IN_STOCK"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="fas fa-list"></i> View Units
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
