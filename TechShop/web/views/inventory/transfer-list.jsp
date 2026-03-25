<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>
<jsp:include page="../common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-exchange-alt"></i> Stock Transfers
    </h2>
    <div class="d-flex align-items-center gap-2">
        <span class="badge bg-primary fs-6">
            ${sessionScope.branchName}
        </span>
        <a href="${pageContext.request.contextPath}/transfer/request" class="btn btn-success btn-sm">
            <i class="fas fa-plus"></i> New Request
        </a>
    </div>
</div>

<c:if test="${not empty param.created}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle"></i> Transfer request created successfully. Waiting for source branch to approve.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${not empty param.approved}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle"></i> Transfer approved. Items are now marked as IN_TRANSFER.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${not empty param.rejected}">
    <div class="alert alert-warning alert-dismissible fade show">
        <i class="fas fa-times-circle"></i> Transfer request has been rejected.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${not empty param.completed}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle"></i> Transfer completed. <strong>${param.qty}</strong> unit(s) added to your
        inventory.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${error != null}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="fas fa-exclamation-circle"></i> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<%-- Count active items for tab badges --%>
<c:set var="myRequestsCount" value="0"/>
<c:forEach var="t" items="${incoming}">
    <c:if test="${t.status == 'PENDING' || t.status == 'APPROVED'}">
        <c:set var="myRequestsCount" value="${myRequestsCount + 1}"/>
    </c:if>
</c:forEach>

<c:set var="pendingForMyStockCount" value="0"/>
<c:forEach var="t" items="${outgoing}">
    <c:if test="${t.status == 'PENDING'}">
        <c:set var="pendingForMyStockCount" value="${pendingForMyStockCount + 1}"/>
    </c:if>
</c:forEach>

<ul class="nav nav-tabs mb-3" id="transferTabs">
    <li class="nav-item">
        <a class="nav-link active" data-bs-toggle="tab" href="#myRequests">
            <i class="fas fa-inbox"></i> My Requests
            <c:if test="${myRequestsCount > 0}">
                <span class="badge bg-secondary ms-1">${myRequestsCount}</span>
            </c:if>
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-bs-toggle="tab" href="#requestsForMyStock">
            <i class="fas fa-warehouse"></i> Requests For My Stock
            <c:if test="${pendingForMyStockCount > 0}">
                <span class="badge bg-secondary ms-1">${pendingForMyStockCount}</span>
            </c:if>
        </a>
    </li>
</ul>

<div class="tab-content">
    <div class="tab-pane fade show active" id="myRequests">
        <div class="card border-primary">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty incoming}">
                        <div class="text-center text-muted py-4">
                            <i class="fas fa-inbox fa-2x mb-2"></i>
                            <p class="mb-0">No transfer requests yet. Click "Request Transfer" to request stock from
                                another branch.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-primary">
                                <tr>
                                    <th>Code</th>
                                    <th>From Branch</th>
                                    <th>Variant</th>
                                    <th>SKU</th>
                                    <th class="text-center">Qty</th>
                                    <th class="text-center">Status</th>
                                    <th>Requested</th>
                                    <th style="width: 1%"></th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="t" items="${incoming}">
                                    <tr>
                                        <td>${t.transferCode}</td>
                                        <td>${t.fromBranchName}</td>
                                        <td>${t.variantName}</td>
                                        <td>${t.sku}</td>
                                        <td class="text-center">${t.requestedQuantity}</td>
                                        <td class="text-center">
                                            <jsp:include page="transfer-status-badge.jsp">
                                                <jsp:param name="status" value="${t.status}"/>
                                            </jsp:include>
                                        </td>
                                        <td>${DateTimeUtil.format(t.requestDate)}</td>
                                        <td>
                                            <div class="dropdown">
                                                <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="dropdown">
                                                    <i class="fas fa-ellipsis-h"></i>
                                                </button>
                                                <ul class="dropdown-menu dropdown-menu-end" style="min-width: fit-content; white-space: nowrap;">
                                                    <li>
                                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/transfer/detail?id=${t.transferId}">
                                                            Details
                                                        </a>
                                                    </li>
                                                    <c:if test="${t.status == 'APPROVED'}">
                                                        <li><hr class="dropdown-divider"></li>
                                                        <li>
                                                            <a class="dropdown-item text-success" href="${pageContext.request.contextPath}/transfer/receive?id=${t.transferId}">
                                                                Receive
                                                            </a>
                                                        </li>
                                                    </c:if>
                                                </ul>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="tab-pane fade" id="requestsForMyStock">
        <div class="card border-primary">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty outgoing}">
                        <div class="text-center text-muted py-4">
                            <i class="fas fa-warehouse fa-2x mb-2"></i>
                            <p class="mb-0">No other branches have requested stock from your branch.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-primary">
                                <tr>
                                    <th>Code</th>
                                    <th>Requested By</th>
                                    <th>For Branch</th>
                                    <th>Variant</th>
                                    <th>SKU</th>
                                    <th class="text-center">Qty</th>
                                    <th class="text-center">Status</th>
                                    <th>Requested</th>
                                    <th style="width: 1%"></th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="t" items="${outgoing}">
                                    <tr>
                                        <td>${t.transferCode}</td>
                                        <td>${t.requestedByName}</td>
                                        <td>${t.toBranchName}</td>
                                        <td>${t.variantName}</td>
                                        <td>${t.sku}</td>
                                        <td class="text-center">${t.requestedQuantity}</td>
                                        <td class="text-center">
                                            <jsp:include page="transfer-status-badge.jsp">
                                                <jsp:param name="status" value="${t.status}"/>
                                            </jsp:include>
                                        </td>
                                        <td>${DateTimeUtil.format(t.requestDate)}</td>
                                        <td>
                                            <div class="dropdown">
                                                <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="dropdown">
                                                    <i class="fas fa-ellipsis-h"></i>
                                                </button>
                                                <ul class="dropdown-menu dropdown-menu-end" style="min-width: fit-content; white-space: nowrap;">
                                                    <li>
                                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/transfer/detail?id=${t.transferId}">
                                                            Details
                                                        </a>
                                                    </li>
                                                    <c:if test="${t.status == 'PENDING'}">
                                                        <li><hr class="dropdown-divider"></li>
                                                        <li>
                                                            <a class="dropdown-item text-primary" href="${pageContext.request.contextPath}/transfer/approve?id=${t.transferId}">
                                                                Review
                                                            </a>
                                                        </li>
                                                    </c:if>
                                                </ul>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
