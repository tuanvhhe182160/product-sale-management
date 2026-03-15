<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-exchange-alt"></i> Request Stock Transfer
    </h2>
    <span class="badge bg-primary fs-6">
        <i class="fas fa-building"></i> ${sessionScope.branchName}
    </span>
</div>

<div class="card border-primary mx-auto" style="max-width: 560px;">
    <div class="card-header bg-primary text-white fw-semibold">
        <i class="fas fa-plus-circle"></i> New Transfer Request
    </div>
    <div class="card-body">

        <c:if test="${error != null}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/transfer/request">

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Source Branch <span class="text-danger">*</span>
                </label>
                <select class="form-select" name="fromBranchId" required>
                    <option value="">— Select source branch —</option>
                    <c:forEach var="b" items="${branches}">
                        <option value="${b.branchId}" ${b.branchId == selectedFromBranchId ? 'selected' : ''}>
                            ${b.branchName}
                        </option>
                    </c:forEach>
                </select>
                <div class="form-text">The branch you want to request stock from.</div>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Product Variant <span class="text-danger">*</span>
                </label>
                <select class="form-select" name="variantId" required>
                    <option value="">— Select variant —</option>
                    <c:forEach var="v" items="${variants}">
                        <option value="${v.variantId}" ${v.variantId == selectedVariantId ? 'selected' : ''}>
                            ${v.variantName} (${v.sku})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Quantity <span class="text-danger">*</span>
                </label>
                <input type="number" class="form-control" name="quantity"
                       min="1" max="100" value="${not empty quantity ? quantity : '1'}"
                       required style="max-width: 120px;">
                <div class="form-text">Maximum 100 units per request.</div>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Note <span class="text-muted fw-normal">(optional)</span></label>
                <textarea class="form-control" name="note" rows="2"
                          placeholder="Reason for request or special instructions...">${note}</textarea>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-paper-plane"></i> Submit Request
                </button>
                <a href="${pageContext.request.contextPath}/transfer" class="btn btn-outline-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
