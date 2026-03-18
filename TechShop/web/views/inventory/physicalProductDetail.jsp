<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>

<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-barcode"></i> Physical Product Detail
    </h2>
    <div class="d-flex gap-2">
        <c:if test="${product.status != 'IN_TRANSFER'}">
            <a href="${pageContext.request.contextPath}/inventory/edit?id=${product.physicalId}" class="btn btn-primary btn-sm">
                <i class="fas fa-edit"></i> Edit
            </a>
        </c:if>
        <a href="${pageContext.request.contextPath}/inventory/list" class="btn btn-outline-secondary btn-sm">
            <i class="fas fa-arrow-left"></i> Back to List
        </a>
    </div>
</div>

<c:if test="${not empty param.updated}">
    <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
        <i class="fas fa-check-circle"></i> Product updated successfully.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="row g-3">
    <%-- Image card --%>
    <div class="col-md-3">
        <div class="card border-primary h-100">
            <div class="card-body d-flex align-items-center justify-content-center">
                <c:choose>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="${product.imageUrl}" alt="${product.variantName}"
                             class="img-fluid" style="max-height: 220px; object-fit: contain;">
                    </c:when>
                    <c:otherwise>
                        <i class="fas fa-image text-muted" style="font-size: 100px;"></i>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%-- Information card --%>
    <div class="col-md-9">
        <div class="card border-primary h-100">
            <div class="card-header bg-primary text-white">
                <i class="fas fa-info-circle"></i> Product Information
            </div>
            <div class="card-body">
                <dl class="row mb-0">
                    <dt class="col-sm-4 text-muted fw-semibold">Category</dt>
                    <dd class="col-sm-8">${product.categoryName}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">Model</dt>
                    <dd class="col-sm-8">${product.modelName}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">Variant</dt>
                    <dd class="col-sm-8">${product.variantName}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">SKU</dt>
                    <dd class="col-sm-8 font-monospace">${product.sku}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">IMEI</dt>
                    <dd class="col-sm-8 font-monospace">${product.imei}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">Serial Number</dt>
                    <dd class="col-sm-8 font-monospace">${product.serialNumber}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">Status</dt>
                    <dd class="col-sm-8">
                        <span class="badge ${product.status == 'IN_STOCK' ? 'bg-primary' : 'bg-secondary'}">
                            ${product.status.replace('_', ' ')}
                        </span>
                    </dd>

                    <dt class="col-sm-4 text-muted fw-semibold">Import Date</dt>
                    <dd class="col-sm-8">${DateTimeUtil.format(product.importDate)}</dd>

                    <dt class="col-sm-4 text-muted fw-semibold">Updated At</dt>
                    <dd class="col-sm-8 mb-0">${DateTimeUtil.format(product.updatedAt)}</dd>
                </dl>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
