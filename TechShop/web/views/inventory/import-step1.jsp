<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-file-import"></i> Import Products
    </h2>
    <span class="badge bg-primary fs-6">
        <i class="fas fa-building"></i> ${sessionScope.branchName}
    </span>
</div>

<c:if test="${error != null}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="fas fa-exclamation-circle"></i> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="card border-primary mx-auto" style="max-width: 560px;">
    <div class="card-header bg-primary text-white fw-semibold">
        <i class="fas fa-box"></i> Step 1 of 2 — Select Variant &amp; Quantity
    </div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/inventory/import">
            <div class="mb-3">
                <label class="form-label fw-semibold">Product Variant <span class="text-danger">*</span></label>
                <select class="form-select" name="variantId" required>
                    <option value="">— Select a variant —</option>
                    <c:forEach var="v" items="${variants}">
                        <option value="${v.variantId}" ${v.variantId == selectedVariantId ? 'selected' : ''}>
                            ${v.variantName} (${v.sku})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Quantity <span class="text-danger">*</span></label>
                <input type="number" class="form-control" name="quantity"
                       min="1" max="100" value="1" required style="max-width: 120px;">
                <div class="form-text">Maximum 100 units per import.</div>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-arrow-right"></i> Next
                </button>
                <a href="${pageContext.request.contextPath}/inventory/report" class="btn btn-outline-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
