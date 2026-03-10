<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-chart-bar"></i> Branch Inventory Report
    </h2>
    <span class="badge bg-primary fs-6">
        <i class="fas fa-building"></i> ${sessionScope.branchName}
    </span>
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
            <span class="text-muted">Total physical products in branch</span>
            <span class="fs-4 fw-bold text-primary">${totalInventoryLevel}</span>
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
                            <th>No</th>
                            <th>SKU</th>
                            <th>Variant</th>
                            <th class="text-end">Inventory Level</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${inventoryItems}" varStatus="loop">
                            <tr>
                                <td>${loop.count}</td>
                                <td><strong>${item.sku}</strong></td>
                                <td>${item.variantName}</td>
                                <td class="text-end fw-semibold text-primary">${item.inventoryLevel}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

