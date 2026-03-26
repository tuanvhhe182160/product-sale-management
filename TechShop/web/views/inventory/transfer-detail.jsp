<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>

<jsp:include page="../common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-exchange-alt"></i> Transfer Detail
    </h2>
    <a href="${pageContext.request.contextPath}/transfer" class="btn btn-outline-secondary btn-sm">
        <i class="fas fa-arrow-left"></i> Back to List
    </a>
</div>

<div class="card border-primary">
    <div class="card-header bg-primary text-white">
        <i class="fas fa-info-circle"></i> Transfer Information
    </div>
    <div class="card-body">

        <%-- Transfer code + status --%>
        <div class="d-flex align-items-center gap-3 mb-4">
            <span class="font-monospace fs-5 fw-semibold">${transfer.transferCode}</span>
            <jsp:include page="transfer-status-badge.jsp">
                <jsp:param name="status" value="${transfer.status}"/>
            </jsp:include>
        </div>

        <hr class="my-3">

        <%-- Route --%>
        <div class="d-flex align-items-center gap-2 mb-4">
            <span class="fw-semibold">${transfer.fromBranchName}</span>
            <i class="fas fa-long-arrow-alt-right text-primary"></i>
            <span class="fw-semibold">${transfer.toBranchName}</span>
        </div>

        <hr class="my-3">

        <%-- Details --%>
        <dl class="row mb-0">
            <dt class="col-sm-3 text-muted fw-semibold">Variant</dt>
            <dd class="col-sm-9">${transfer.variantName} <span class="text-muted font-monospace small">(${transfer.sku})</span></dd>

            <dt class="col-sm-3 text-muted fw-semibold">Requested Qty</dt>
            <dd class="col-sm-9">${transfer.requestedQuantity}</dd>

            <dt class="col-sm-3 text-muted fw-semibold">Requested By</dt>
            <dd class="col-sm-9">${transfer.requestedByName}</dd>

            <dt class="col-sm-3 text-muted fw-semibold">Request Date</dt>
            <dd class="col-sm-9">${DateTimeUtil.format(transfer.requestDate)}</dd>

            <c:if test="${not empty transfer.approvedByName}">
                <dt class="col-sm-3 text-muted fw-semibold">
                    <c:choose>
                        <c:when test="${transfer.status == 'REJECTED'}">Rejected By</c:when>
                        <c:otherwise>Approved By</c:otherwise>
                    </c:choose>
                </dt>
                <dd class="col-sm-9">${transfer.approvedByName}</dd>

                <dt class="col-sm-3 text-muted fw-semibold">Approval Date</dt>
                <dd class="col-sm-9">${DateTimeUtil.format(transfer.approvalDate)}</dd>
            </c:if>

            <c:if test="${transfer.status == 'COMPLETED'}">
                <dt class="col-sm-3 text-muted fw-semibold">Completion Date</dt>
                <dd class="col-sm-9">${DateTimeUtil.format(transfer.completionDate)}</dd>
            </c:if>

            <c:if test="${not empty transfer.note}">
                <dt class="col-sm-3 text-muted fw-semibold">Note</dt>
                <dd class="col-sm-9 mb-0">${transfer.note}</dd>
            </c:if>
        </dl>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
