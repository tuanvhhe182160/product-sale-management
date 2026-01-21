<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Dashboard - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    .page-title {
        font-weight: 800;
    }
    .sub-title {
        color: #6c757d;
    }

    .stat-card .label {
        color: #6c757d;
        font-size: .85rem;
        margin-bottom: 4px;
    }
    .stat-card .value {
        font-size: 1.6rem;
        font-weight: 800;
        margin: 0;
    }

    .search-box {
        position: relative;
    }
    .search-box i {
        position: absolute;
        left: 12px;
        top: 50%;
        transform: translateY(-50%);
        color: #6c757d;
    }
    .search-box input {
        padding-left: 38px;
    }

    table thead th {
        font-size: .8rem;
        text-transform: uppercase;
        color: #6c757d;
        border-bottom: 1px solid rgba(0,0,0,.06) !important;
    }
    tbody tr:hover {
        background: rgba(13,110,253,.04);
    }
</style>

<!-- ===== Header ===== -->
<div class="card border-0 shadow-sm mb-4">
    <div class="card-body py-4 d-flex justify-content-between flex-wrap gap-3">
        <div>
            <h2 class="mb-1 page-title">
                <i class="fas fa-layer-group text-primary me-2"></i> Product Categories
            </h2>
            <div class="sub-title">
                <i class="fas fa-shield-alt text-primary me-1"></i> Admin Panel • Manage category types
            </div>
        </div>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-6 col-lg-3">
        <div class="card border-0 shadow-sm h-100 stat-card">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="label">Total Categories</div>
                    <p class="value"><c:out value="${categories.size()}" /></p>
                </div>
                <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                    <i class="fas fa-tags fa-2x text-primary"></i>
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
                    <div class="label">Models</div>
                    <p class="value">-</p>
                </div>
                <div class="bg-info bg-opacity-10 p-3 rounded-circle">
                    <i class="fas fa-cubes fa-2x text-info"></i>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="card border-0 shadow-sm">
    <div class="card-header bg-white py-3 d-flex justify-content-between flex-wrap gap-3">
        <h5 class="mb-0">
            <i class="fas fa-list text-primary me-2"></i> Category List
        </h5>

        <!-- ✅ ONE place only: button + search + filter -->
        <div class="d-flex gap-2 flex-wrap align-items-center">

            <button type="button" class="btn btn-primary"
                    data-bs-toggle="modal" data-bs-target="#createCategoryModal">
                <i class="fas fa-plus me-2"></i> New Category
            </button>

            <div class="search-box">
                <i class="fas fa-search"></i>
                <input id="searchInput" type="text" class="form-control"
                       placeholder="Search by code / name..." />
            </div>

            <select id="statusFilter" class="form-select">
                <option value="ALL">All Status</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
            </select>
        </div>
    </div>

    <div class="card-body">

        <c:if test="${empty categories}">
            <div class="text-center py-5">
                <div class="bg-primary bg-opacity-10 d-inline-flex p-4 rounded-circle mb-3">
                    <i class="fas fa-folder-open fa-3x text-primary"></i>
                </div>
                <h5 class="mb-1">No categories found</h5>
                <p class="text-muted mb-3">Create your first category to start organizing products.</p>

                <!-- ✅ open modal instead of link -->
                <button type="button" class="btn btn-primary"
                        data-bs-toggle="modal" data-bs-target="#createCategoryModal">
                    <i class="fas fa-plus me-2"></i> New Category
                </button>
            </div>
        </c:if>

        <c:if test="${not empty categories}">
            <div class="table-responsive">
                <table class="table align-middle mb-0" id="categoryTable">
                    <thead class="bg-light">
                        <tr>
                            <th style="width: 70px;">#</th>
                            <th style="width: 160px;">Code</th>
                            <th>Name</th>
                            <th>Description</th>
                            <th style="width: 130px;">Status</th>
                            <th style="width: 180px;">Updated</th>
                            <th style="width: 180px;" class="text-end">Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${categories}" var="c" varStatus="st">
                            <tr class="category-row"
                                data-url="${pageContext.request.contextPath}/ProductModel?categoryId=${c.categoryId}"
                                style="cursor:pointer;">

                                <td class="text-muted">${st.index + 1}</td>

                                <td>
                                    <span class="badge bg-primary bg-opacity-10 text-primary">
                                        ${c.categoryCode}
                                    </span>
                                </td>

                                <td class="fw-semibold">${c.categoryName}</td>

                                <td class="text-muted">
                                    <c:choose>
                                        <c:when test="${empty c.description}">
                                            <i>No description</i>
                                        </c:when>
                                        <c:otherwise>${c.description}</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${c.status == 'ACTIVE'}">
                                            <span class="badge bg-info bg-opacity-10 text-info">ACTIVE</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary bg-opacity-10 text-secondary">INACTIVE</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="text-muted">
                                    <c:choose>
                                        <c:when test="${c.updatedAt != null}">${c.updatedAt}</c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="text-end">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/category/edit?id=${c.categoryId}"
                                       onclick="event.stopPropagation();">
                                        <i class="fas fa-pen me-1"></i> Edit
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>


                    </tbody>
                </table>
            </div>
        </c:if>

    </div>
</div>

<!--        form create        -->
<div class="modal fade" id="createCategoryModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">

            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-plus text-primary me-2"></i> Create Product Category
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/category/create">
                <div class="modal-body">

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mb-3">${error}</div>
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label">Category Code <span class="text-danger">*</span></label>
                        <input type="text" class="form-control"
                               name="categoryCode"
                               value="${categoryCode}"
                               maxlength="20" required />
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Category Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control"
                               name="categoryName"
                               value="${categoryName}"
                               maxlength="100" required />
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control"
                                  name="description"
                                  rows="3"
                                  maxlength="255">${description}</textarea>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        Cancel
                    </button>
                    <button type="submit" class="btn btn-primary">
                        Create
                    </button>
                </div>
            </form>

        </div>
    </div>
</div>

<script>
    (function () {
        const table = document.getElementById("categoryTable");
        if (!table)
            return;

        const rows = Array.from(table.querySelectorAll("tbody tr"));
        const searchInput = document.getElementById("searchInput");
        const statusFilter = document.getElementById("statusFilter");
        const activeEl = document.getElementById("activeCount");
        const inactiveEl = document.getElementById("inactiveCount");

        function render() {
            const q = (searchInput.value || "").trim().toLowerCase();
            const st = (statusFilter.value || "ALL").toUpperCase();

            let active = 0, inactive = 0;

            rows.forEach(row => {
                const text = (row.dataset.text || "").toLowerCase();
                const status = (row.dataset.status || "").toUpperCase();

                const okText = !q || text.includes(q);
                const okStatus = (st === "ALL") || (status === st);
                const show = okText && okStatus;

                row.style.display = show ? "" : "none";

                if (show) {
                    if (status === "ACTIVE")
                        active++;
                    if (status === "INACTIVE")
                        inactive++;
                }
            });

            if (activeEl)
                activeEl.textContent = active;
            if (inactiveEl)
                inactiveEl.textContent = inactive;
        }

        searchInput.addEventListener("input", render);
        statusFilter.addEventListener("change", render);
        render();
    })();
</script>

<!-- ✅ auto open modal when servlet returns error -->
<c:if test="${not empty error}">
    <script>
        window.addEventListener('load', function () {
            const el = document.getElementById('createCategoryModal');
            if (el && window.bootstrap)
                new bootstrap.Modal(el).show();
        });
    </script>
    <script>
        (function () {
            document.querySelectorAll('.category-row').forEach(row => {
                row.addEventListener('click', function () {
                    const url = this.dataset.url;
                    if (url)
                        window.location.href = url;
                });
            });
        })();
    </script>





</c:if>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".category-row").forEach(row => {
            row.addEventListener("click", function () {
                const url = this.dataset.url;
                console.log("Redirect to:", url); // 🔥 DEBUG
                if (url) {
                    window.location.href = url;
                }
            });
        });
    });
</script>

<%@ include file="../common/footer.jsp" %>
