<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-box-open"></i> Receive Transfer
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
                <span class="text-muted small">From Branch</span><br>
                <strong>${transfer.fromBranchName}</strong>
            </div>
            <div class="col-sm-4">
                <span class="text-muted small">Variant</span><br>
                <strong>${transfer.variantName}</strong>
                <small class="text-muted ms-1">${transfer.sku}</small>
            </div>
            <div class="col-sm-2">
                <span class="text-muted small">Units</span><br>
                <strong>${items.size()}</strong>
            </div>
            <div class="col-sm-2">
                <span class="text-muted small">Approved On</span><br>
                <small>${DateTimeUtil.format(transfer.approvalDate)}</small>
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

<div class="card border-primary mx-auto" style="max-width: 800px;">
    <div class="card-body">
        <h6 class="fw-semibold mb-3">Incoming Units</h6>

        <div class="table-responsive mb-4">
            <table class="table table-bordered align-middle">
                <thead class="table-primary">
                    <tr>
                        <th>#</th>
                        <th>Variant</th>
                        <th>SKU</th>
                        <th>IMEI</th>
                        <th>Serial Number</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${items}" varStatus="loop">
                        <tr>
                            <td class="text-muted">${loop.count}</td>
                            <td>${item.variantName}</td>
                            <td><strong>${item.sku}</strong></td>
                            <td><span class="fw-semibold text-primary">${item.imei}</span></td>
                            <td class="text-muted">${item.serialNumber}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="alert alert-info mb-4">
            Confirming receipt will add these <strong>${items.size()}</strong> unit(s) to your branch inventory.
        </div>

        <form method="post" action="${pageContext.request.contextPath}/transfer/receive"
              onsubmit="return confirm('Confirm receipt of ${items.size()} unit(s)? This cannot be undone.');">
            <input type="hidden" name="transferId" value="${transfer.transferId}">
            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-success">
                    <i class="fas fa-check-circle"></i> Confirm Receipt
                </button>
                <a href="${pageContext.request.contextPath}/transfer" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left"></i> Back
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
