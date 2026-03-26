<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>

<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-edit"></i> Edit Physical Product
    </h2>
    <a href="${pageContext.request.contextPath}/inventory/detail?id=${product.physicalId}" class="btn btn-outline-secondary btn-sm">
        <i class="fas fa-arrow-left"></i> Back to Detail
    </a>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle"></i> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="card border-primary">
    <div class="card-header bg-primary text-white">
        <i class="fas fa-pen"></i> Edit Details
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/inventory/edit?id=${product.physicalId}">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Variant</label>
                    <select class="form-select" name="variantId" required>
                        <c:forEach var="v" items="${variants}">
                            <option value="${v.variantId}" ${v.variantId == product.variantId ? 'selected' : ''}>
                                ${v.variantName} (${v.sku})
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">Status</label>
                    <select class="form-select" name="status" required>
                        <option value="IN_STOCK" ${product.status == 'IN_STOCK' ? 'selected' : ''}>IN STOCK</option>
                        <option value="DEFECTIVE" ${product.status == 'DEFECTIVE' ? 'selected' : ''}>DEFECTIVE</option>
                        <option value="RESERVED" ${product.status == 'RESERVED' ? 'selected' : ''}>RESERVED</option>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">IMEI</label>
                    <input type="text" class="form-control font-monospace" name="imei"
                           value="${product.imei}" required>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">Serial Number</label>
                    <input type="text" class="form-control font-monospace" name="serialNumber"
                           value="${product.serialNumber}" required>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold text-muted small">Branch</label>
                    <p class="form-control-plaintext">${product.branchName}</p>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold text-muted small">Import Date</label>
                    <p class="form-control-plaintext">${DateTimeUtil.format(product.importDate)}</p>
                </div>
            </div>

            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Changes
                </button>
                <a href="${pageContext.request.contextPath}/inventory/detail?id=${product.physicalId}" class="btn btn-outline-secondary">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
