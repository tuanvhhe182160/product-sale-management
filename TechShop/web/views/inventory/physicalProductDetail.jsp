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

<div class="card border-primary">
    <div class="card-header bg-primary text-white">
        <i class="fas fa-info-circle"></i> Product Information
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Variant</label>
                <p class="fs-6 mb-0">${product.variantName}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">SKU</label>
                <p class="fs-6 mb-0 font-monospace">${product.sku}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">IMEI</label>
                <p class="fs-6 mb-0 font-monospace">${product.imei}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Serial Number</label>
                <p class="fs-6 mb-0 font-monospace">${product.serialNumber}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Branch</label>
                <p class="fs-6 mb-0">${product.branchName}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Status</label>
                <p class="mb-0">
                    <span class="badge ${product.status == 'IN_STOCK' ? 'bg-primary' : 'bg-secondary'} fs-6">
                        ${product.status.replace('_', ' ')}
                    </span>
                </p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Import Date</label>
                <p class="fs-6 mb-0">${DateTimeUtil.format(product.importDate)}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Updated At</label>
                <p class="fs-6 mb-0">${DateTimeUtil.format(product.updatedAt)}</p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
