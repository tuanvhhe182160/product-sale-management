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

<div class="card border-primary mb-3">
    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
        <span><i class="fas fa-info-circle"></i> Transfer Information</span>
        <jsp:include page="transferStatusBadge.jsp">
            <jsp:param name="status" value="${transfer.status}"/>
        </jsp:include>
    </div>
    <div class="card-body">
        <div class="row g-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Transfer Code</label>
                <p class="fs-6 mb-0 font-monospace">${transfer.transferCode}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Variant</label>
                <p class="fs-6 mb-0">${transfer.variantName} <span class="text-muted font-monospace small">(${transfer.sku})</span></p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">From Branch</label>
                <p class="fs-6 mb-0">${transfer.fromBranchName}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">To Branch</label>
                <p class="fs-6 mb-0">${transfer.toBranchName}</p>
            </div>
            <div class="col-md-3">
                <label class="form-label fw-semibold text-muted small">Requested Qty</label>
                <p class="fs-6 mb-0">${transfer.requestedQuantity}</p>
            </div>
            <div class="col-md-3">
                <label class="form-label fw-semibold text-muted small">Actual Items</label>
                <p class="fs-6 mb-0">${transfer.itemCount}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Requested By</label>
                <p class="fs-6 mb-0">${transfer.requestedByName}</p>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold text-muted small">Request Date</label>
                <p class="fs-6 mb-0">${DateTimeUtil.format(transfer.requestDate)}</p>
            </div>
            <c:if test="${not empty transfer.approvedByName}">
                <div class="col-md-6">
                    <label class="form-label fw-semibold text-muted small">Approved / Rejected By</label>
                    <p class="fs-6 mb-0">${transfer.approvedByName}</p>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold text-muted small">Approval Date</label>
                    <p class="fs-6 mb-0">${DateTimeUtil.format(transfer.approvalDate)}</p>
                </div>
            </c:if>
            <c:if test="${transfer.status == 'COMPLETED'}">
                <div class="col-md-6">
                    <label class="form-label fw-semibold text-muted small">Completion Date</label>
                    <p class="fs-6 mb-0">${DateTimeUtil.format(transfer.completionDate)}</p>
                </div>
            </c:if>
            <c:if test="${not empty transfer.note}">
                <div class="col-12">
                    <label class="form-label fw-semibold text-muted small">Note</label>
                    <p class="fs-6 mb-0">${transfer.note}</p>
                </div>
            </c:if>
        </div>
    </div>
</div>

<div class="card border-primary">
    <div class="card-header bg-primary text-white">
        <i class="fas fa-boxes"></i> Transfer Items
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty items}">
                <div class="text-center text-muted py-4">
                    <i class="fas fa-box-open fa-2x mb-2"></i>
                    <p class="mb-0">No items have been assigned to this transfer yet.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-primary">
                        <tr>
                            <th>No</th>
                            <th>Variant</th>
                            <th>SKU</th>
                            <th>IMEI</th>
                            <th>Serial Number</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${items}" varStatus="loop">
                            <tr>
                                <td>${loop.count}</td>
                                <td>${item.variantName}</td>
                                <td class="font-monospace">${item.sku}</td>
                                <td class="font-monospace">${item.imei}</td>
                                <td class="font-monospace">${item.serialNumber}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
