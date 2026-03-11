<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>

<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-mobile-alt"></i> Physical Product List
    </h2>
    <div class="d-flex align-items-center gap-2">
        <span class="badge bg-primary fs-6">
            <i class="fas fa-building"></i> ${sessionScope.branchName}
        </span>
        <a href="${pageContext.request.contextPath}/inventory/import" class="btn btn-success btn-sm">
            <i class="fas fa-file-import"></i> Import Products
        </a>
    </div>
</div>

<c:if test="${not empty param.imported}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle"></i>
        Successfully imported <strong>${param.imported}</strong> unit(s)
        <c:if test="${not empty param.variantName}"> of <strong>${param.variantName}</strong></c:if>.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${error != null}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle"></i> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<div class="card border-primary mb-3">
    <div class="card-body">
        <form class="row g-3" method="get" action="${pageContext.request.contextPath}/inventory/list">
            <div class="col-md-3">
                <label class="form-label fw-semibold text-primary">Search IMEI</label>
                <input type="text" class="form-control" name="imei" value="${imei}" placeholder="Enter IMEI">
            </div>

            <div class="col-md-2">
                <label class="form-label fw-semibold text-primary">Status</label>
                <select class="form-select" name="status">
                    <option value="">All status</option>
                    <c:forEach var="s" items="${statusOptions}">
                        <option value="${s}" ${s == status ? 'selected' : ''}>${s}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-3">
                <label class="form-label fw-semibold text-primary">Variant</label>
                <select class="form-select" name="variantId">
                    <option value="">All variants</option>
                    <c:forEach var="v" items="${variantOptions}">
                        <option value="${v.variantId}" ${v.variantId == selectedVariantId ? 'selected' : ''}>
                                ${v.variantName} (${v.sku})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-md-2">
                <label class="form-label fw-semibold text-primary">Import date</label>
                <input type="date" class="form-control" name="importDate" value="${importDate}">
            </div>

            <input type="hidden" name="sortBy" value="${sortBy}">
            <input type="hidden" name="sortDir" value="${sortDir}">

            <div class="col-12 d-flex gap-2">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-search"></i> Apply
                </button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/inventory/list">
                    <i class="fas fa-times"></i> Clear
                </a>
            </div>
        </form>
    </div>
</div>

<div class="card border-primary">
    <div class="card-body">
        <c:if test="${empty physicalProducts}">
            <div class="text-center text-muted py-4">
                <i class="fas fa-box-open fa-2x mb-2"></i>
                <p class="mb-0">No physical products found in your branch.</p>
            </div>
        </c:if>

        <c:if test="${not empty physicalProducts}">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <small class="text-muted">
                    Showing ${(currentPage - 1) * pageSize + 1}–${(currentPage - 1) * pageSize + physicalProducts.size()}
                    of ${totalCount} records
                </small>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-primary">
                    <tr>
                        <th>No</th>
                        <th>SKU</th>
                        <th>Variant</th>
                        <th>IMEI</th>
                        <th>Serial Number</th>
                        <th>Status</th>
                        <th>Import Date</th>
                        <th>Sale Date</th>
                        <th>Updated At</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${physicalProducts}" varStatus="loop">
                        <tr>
                            <td class="text-muted">${(currentPage - 1) * pageSize + loop.count}</td>
                            <td><strong>${p.sku}</strong></td>
                            <td>${p.variantName}</td>
                            <td><span class="fw-semibold text-primary">${p.imei}</span></td>
                            <td>${p.serialNumber}</td>
                            <td>
                                <span class="badge ${p.status == 'IN_STOCK' ? 'bg-primary' : 'bg-secondary'}">${p.status}</span>
                            </td>
                            <td>${DateTimeUtil.format(p.importDate)}</td>
                            <td>${DateTimeUtil.format(p.saleDate)}</td>
                            <td>${DateTimeUtil.format(p.updatedAt)}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <nav>
                    <ul class="pagination justify-content-center mb-0">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage - 1}">&laquo;</a>
                        </li>

                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <c:if test="${p >= currentPage - 2 && p <= currentPage + 2}">
                                <li class="page-item ${p == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="?page=${p}">${p}</a>
                                </li>
                            </c:if>
                        </c:forEach>

                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="?page=${currentPage + 1}">&raquo;</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </c:if>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

