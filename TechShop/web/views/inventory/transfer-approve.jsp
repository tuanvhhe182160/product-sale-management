<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-clipboard-check"></i> Review Transfer Request
    </h2>
    <span class="badge bg-primary fs-6">
        <i class="fas fa-building"></i> ${sessionScope.branchName}
    </span>
</div>

<%-- Transfer context card --%>
<div class="card border-primary mb-3 mx-auto" style="max-width: 800px;">
    <div class="card-header bg-primary text-white fw-semibold">
        <i class="fas fa-exchange-alt"></i> ${transfer.transferCode}
        &nbsp;
        <jsp:include page="transfer-status-badge.jsp"><jsp:param name="status" value="${transfer.status}"/></jsp:include>
    </div>
    <div class="card-body py-3">
        <div class="row g-3">
            <div class="col-sm-4">
                <span class="text-muted small">Requesting Branch</span><br>
                <strong>${transfer.toBranchName}</strong>
            </div>
            <div class="col-sm-4">
                <span class="text-muted small">Variant</span><br>
                <strong>${transfer.variantName}</strong>
                <small class="text-muted ms-1">${transfer.sku}</small>
            </div>
            <div class="col-sm-2">
                <span class="text-muted small">Units Requested</span><br>
                <strong>${transfer.requestedQuantity}</strong>
            </div>
            <div class="col-sm-2">
                <span class="text-muted small">Requested By</span><br>
                <strong>${transfer.requestedByName}</strong>
            </div>
            <c:if test="${not empty transfer.note}">
                <div class="col-12">
                    <span class="text-muted small">Note</span><br>
                    <span>${transfer.note}</span>
                </div>
            </c:if>
        </div>
    </div>
</div>

<c:if test="${error != null}">
    <div class="alert alert-danger mx-auto" style="max-width: 800px;">
        <i class="fas fa-exclamation-circle"></i> ${error}
    </div>
</c:if>

<c:choose>
    <c:when test="${transfer.status == 'PENDING'}">

        <c:set var="availableCount" value="${availableItems.size()}" />
        <c:set var="hasEnough" value="${availableCount >= transfer.requestedQuantity}" />

        <c:if test="${!hasEnough}">
            <div class="alert alert-warning mx-auto" style="max-width: 800px;">
                <i class="fas fa-exclamation-triangle"></i>
                <strong>Insufficient stock:</strong> You have <strong>${availableCount}</strong> unit(s) of this variant,
                but <strong>${transfer.requestedQuantity}</strong> are requested.
                You can only reject this request.
            </div>
        </c:if>

        <div class="card border-primary mx-auto" style="max-width: 800px;">
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/transfer/approve" id="approveForm">
                    <input type="hidden" name="transferId" value="${transfer.transferId}">
                    <input type="hidden" name="action" id="actionInput" value="">

                    <c:if test="${hasEnough}">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h6 class="mb-0 fw-semibold">Select Units to Send</h6>
                            <span class="text-muted small">
                                <span id="selectedCount">0</span> of ${transfer.requestedQuantity} selected
                            </span>
                        </div>

                        <div class="table-responsive mb-3">
                            <table class="table table-bordered align-middle mb-0">
                                <thead class="table-primary">
                                    <tr>
                                        <th style="width:40px;">
                                            <input type="checkbox" id="selectAll" class="form-check-input">
                                        </th>
                                        <th>IMEI</th>
                                        <th>Serial Number</th>
                                        <th>Import Date</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${availableItems}">
                                        <tr>
                                            <td class="text-center">
                                                <input type="checkbox" class="form-check-input item-checkbox"
                                                       name="physicalId" value="${p.physicalId}">
                                            </td>
                                            <td class="fw-semibold text-primary">${p.imei}</td>
                                            <td class="text-muted">${p.serialNumber}</td>
                                            <td>${DateTimeUtil.format(p.importDate)}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>

                    <hr>

                    <div class="d-flex gap-2 flex-wrap">
                        <c:if test="${hasEnough}">
                            <button type="button" class="btn btn-success" id="approveBtn" disabled
                                    onclick="submitApprove()">
                                <i class="fas fa-check"></i> Approve &amp; Send
                            </button>
                        </c:if>

                        <button type="button" class="btn btn-danger" data-bs-toggle="collapse"
                                data-bs-target="#rejectSection">
                            <i class="fas fa-times"></i> Reject
                        </button>

                        <a href="${pageContext.request.contextPath}/transfer" class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left"></i> Back
                        </a>
                    </div>

                    <%-- Reject section (collapsed by default) --%>
                    <div class="collapse mt-3" id="rejectSection">
                        <div class="card card-body border-danger">
                            <label class="form-label fw-semibold text-danger">
                                Reason for rejection <span class="text-muted fw-normal">(optional)</span>
                            </label>
                            <textarea class="form-control mb-2" name="rejectNote" rows="2"
                                      placeholder="Explain why you are rejecting this request..."></textarea>
                            <button type="button" class="btn btn-danger btn-sm" onclick="submitReject()">
                                <i class="fas fa-times-circle"></i> Confirm Rejection
                            </button>
                        </div>
                    </div>

                </form>
            </div>
        </div>

        <script>
            var required = ${transfer.requestedQuantity};
            var checkboxes = document.querySelectorAll('.item-checkbox');
            var selectedCountEl = document.getElementById('selectedCount');
            var approveBtn = document.getElementById('approveBtn');
            var selectAll = document.getElementById('selectAll');

            function updateCount() {
                var checked = document.querySelectorAll('.item-checkbox:checked').length;
                if (selectedCountEl) selectedCountEl.textContent = checked;
                if (approveBtn) approveBtn.disabled = (checked !== required);
            }

            checkboxes.forEach(function(cb) {
                cb.addEventListener('change', function() {
                    // Enforce max selection
                    var checked = document.querySelectorAll('.item-checkbox:checked').length;
                    if (checked > required) {
                        this.checked = false;
                    }
                    updateCount();
                    if (selectAll) selectAll.checked = false;
                });
            });

            if (selectAll) {
                selectAll.addEventListener('change', function() {
                    // Select first N checkboxes
                    var count = 0;
                    checkboxes.forEach(function(cb) {
                        cb.checked = (count < required);
                        count++;
                    });
                    updateCount();
                });
            }

            function submitApprove() {
                document.getElementById('actionInput').value = 'approve';
                document.getElementById('approveForm').submit();
            }

            function submitReject() {
                document.getElementById('actionInput').value = 'reject';
                document.getElementById('approveForm').submit();
            }
        </script>

    </c:when>
    <c:otherwise>
        <%-- Already decided — show read-only result --%>
        <div class="card border-secondary mx-auto" style="max-width: 800px;">
            <div class="card-body text-center text-muted py-4">
                <i class="fas fa-info-circle fa-2x mb-2"></i>
                <p class="mb-0">This transfer has already been
                    <strong>${transfer.status == 'APPROVED' ? 'approved' : transfer.status == 'REJECTED' ? 'rejected' : 'processed'}</strong>.
                </p>
                <a href="${pageContext.request.contextPath}/transfer" class="btn btn-outline-secondary mt-3">
                    <i class="fas fa-arrow-left"></i> Back to Transfers
                </a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp" />
