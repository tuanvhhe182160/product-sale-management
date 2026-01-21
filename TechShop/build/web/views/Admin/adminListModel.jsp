<%--
    Document   : adminListModel
    Created on : Jan 20, 2026, 8:56:58 PM
    Author     : Admin
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Models - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    .page-title { font-weight: 800; }
    .sub-title { color: #6c757d; }

    .stat-card .label { color: #6c757d; font-size: .85rem; margin-bottom: 4px; }
    .stat-card .value { font-size: 1.6rem; font-weight: 800; margin: 0; }

    .search-box { position: relative; }
    .search-box i { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: #6c757d; }
    .search-box input { padding-left: 38px; }

    table thead th {
        font-size: .8rem;
        text-transform: uppercase;
        color: #6c757d;
        border-bottom: 1px solid rgba(0,0,0,.06) !important;
    }
    tbody tr:hover { background: rgba(13,110,253,.04); }
</style>

<!-- ===== Header ===== -->
<div class="card border-0 shadow-sm mb-4">
    <div class="card-body py-4 d-flex justify-content-between flex-wrap gap-3">
        <div>
            <h2 class="mb-1 page-title">
                <i class="fas fa-cubes text-primary me-2"></i> Product Models
            </h2>
            <div class="sub-title">
                <i class="fas fa-layer-group text-primary me-1"></i>
                Category ID: <span class="fw-semibold">${categoryId}</span>
            </div>
        </div>

        <!-- Back button -->
        <div class="d-flex align-items-center">
            <a class="btn btn-outline-secondary"
               href="${pageContext.request.contextPath}/ProductCategory">
                <i class="fas fa-arrow-left me-2"></i> Back to Categories
            </a>
        </div>
    </div>
</div>

<!-- ===== Stats ===== -->
<div class="row g-3 mb-4">
    <div class="col-md-6 col-lg-3">
        <div class="card border-0 shadow-sm h-100 stat-card">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="label">Total Models</div>
                    <p class="value"><c:out value="${models.size()}" /></p>
                </div>
                <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                    <i class="fas fa-cubes fa-2x text-primary"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-lg-3">
        <div class="card border-0 shadow-sm h-100 stat-card">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="label">Active</div>
                    <p class="value" id="activeCount">0</p>
                </div>
                <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                    <i class="fas fa-check-circle fa-2x text-info"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-lg-3">
        <div class="card border-0 shadow-sm h-100 stat-card">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="label">Inactive</div>
                    <p class="value" id="inactiveCount">0</p>
                </div>
                <div class="bg-secondary bg-opacity-10 p-3 rounded-circle">
                    <i class="fas fa-ban fa-2x text-secondary"></i>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-lg-3">
        <div class="card border-0 shadow-sm h-100 stat-card">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="label">Brands</div>
                    <p class="value" id="brandCount">-</p>
                </div>
                <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                    <i class="fas fa-tag fa-2x text-info"></i>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ===== Table ===== -->
<div class="card border-0 shadow-sm">
    <div class="card-header bg-white py-3 d-flex justify-content-between flex-wrap gap-3">
        <h5 class="mb-0">
            <i class="fas fa-list text-primary me-2"></i> Model List
        </h5>

        <div class="d-flex gap-2 flex-wrap align-items-center">
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input id="searchInput" type="text" class="form-control"
                       placeholder="Search by code / name / brand..." />
            </div>

            <select id="statusFilter" class="form-select">
                <option value="ALL">All Status</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
            </select>
        </div>
    </div>

    <div class="card-body">
        <c:if test="${empty models}">
            <div class="text-center py-5">
                <div class="bg-primary bg-opacity-10 d-inline-flex p-4 rounded-circle mb-3">
                    <i class="fas fa-folder-open fa-3x text-primary"></i>
                </div>
                <h5 class="mb-1">No models found</h5>
                <p class="text-muted mb-0">This category has no models yet.</p>
            </div>
        </c:if>

        <c:if test="${not empty models}">
            <div class="table-responsive">
                <table class="table align-middle mb-0" id="modelTable">
                    <thead class="bg-light">
                        <tr>
                            <th style="width: 70px;">#</th>
                            <th style="width: 160px;">Code</th>
                            <th>Name</th>
                            <th style="width: 160px;">Brand</th>
                            <th>Description</th>
                            <th style="width: 130px;">Status</th>
                            <th style="width: 180px;">Updated</th>
                            <th style="width: 180px;" class="text-end">Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${models}" var="m" varStatus="st">
                            <tr data-status="${m.status}"
                                data-text="${m.modelCode} ${m.modelName} ${m.brand}">
                                <td class="text-muted">${st.index + 1}</td>

                                <td>
                                    <span class="badge bg-primary bg-opacity-10 text-primary">
                                        ${m.modelCode}
                                    </span>
                                </td>

                                <td class="fw-semibold">${m.modelName}</td>

                                <td class="text-muted">
                                    <c:choose>
                                        <c:when test="${empty m.brand}">
                                            <i>-</i>
                                        </c:when>
                                        <c:otherwise>${m.brand}</c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="text-muted">
                                    <c:choose>
                                        <c:when test="${empty m.description}">
                                            <i>No description</i>
                                        </c:when>
                                        <c:otherwise>${m.description}</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${m.status == 'ACTIVE'}">
                                            <span class="badge bg-info bg-opacity-10 text-info">ACTIVE</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary bg-opacity-10 text-secondary">INACTIVE</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="text-muted">
                                    <c:choose>
                                        <c:when test="${m.updatedAt != null}">${m.updatedAt}</c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="text-end">
                                    <button type="button"
                                            class="btn btn-sm btn-outline-primary btn-edit-model"
                                            data-id="${m.modelId}">
                                        <i class="fas fa-pen me-1"></i> Edit
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>

<!-- ===== Edit Model Modal ===== -->
<div class="modal fade" id="editModelModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">

            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-pen text-primary me-2"></i> Edit Product Model
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body" id="editModelContent">
                <div class="text-center py-5">
                    <div class="spinner-border text-primary" role="status"></div>
                </div>
            </div>

        </div>
    </div>
</div>

<script>
(function () {
    const table = document.getElementById("modelTable");
    if (!table) return;

    const rows = Array.from(table.querySelectorAll("tbody tr"));
    const searchInput = document.getElementById("searchInput");
    const statusFilter = document.getElementById("statusFilter");
    const activeEl = document.getElementById("activeCount");
    const inactiveEl = document.getElementById("inactiveCount");
    const brandEl = document.getElementById("brandCount");

    function render() {
        const q = (searchInput.value || "").trim().toLowerCase();
        const st = (statusFilter.value || "ALL").toUpperCase();

        let active = 0, inactive = 0;
        const brands = new Set();

        rows.forEach(row => {
            const text = (row.dataset.text || "").toLowerCase();
            const status = (row.dataset.status || "").toUpperCase();

            const okText = !q || text.includes(q);
            const okStatus = (st === "ALL") || (status === st);
            const show = okText && okStatus;

            row.style.display = show ? "" : "none";

            if (show) {
                if (status === "ACTIVE") active++;
                if (status === "INACTIVE") inactive++;

                const brandCell = row.children[3];
                const brand = brandCell ? brandCell.textContent.trim() : "";
                if (brand && brand !== "-") brands.add(brand);
            }
        });

        if (activeEl) activeEl.textContent = active;
        if (inactiveEl) inactiveEl.textContent = inactive;
        if (brandEl) brandEl.textContent = brands.size;
    }

    searchInput.addEventListener("input", render);
    statusFilter.addEventListener("change", render);
    render();
})();
</script>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const modalEl = document.getElementById('editModelModal');
    const modal = new bootstrap.Modal(modalEl);
    const content = document.getElementById('editModelContent');

    document.querySelectorAll('.btn-edit-model').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            const id = this.dataset.id;
            if (!id) return;

            content.innerHTML = `
                <div class="text-center py-5">
                    <div class="spinner-border text-primary" role="status"></div>
                </div>
            `;

            fetch('${pageContext.request.contextPath}/ProductModel/edit?id=' + encodeURIComponent(id))
                .then(res => res.text())
                .then(html => {
                    content.innerHTML = html;
                    modal.show();
                })
                .catch(() => {
                    content.innerHTML = `<div class="alert alert-danger mb-0">Failed to load model.</div>`;
                });
        });
    });
});
</script>

<%@ include file="../common/footer.jsp" %>
