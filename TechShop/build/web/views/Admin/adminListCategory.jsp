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

        <div class="d-flex gap-2 flex-wrap align-items-center">

            <a href="${pageContext.request.contextPath}/category/form" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i> New Category
            </a>


            <form class="d-flex gap-2 flex-wrap align-items-center"
                  method="get"
                  action="${pageContext.request.contextPath}/ProductCategory">

                <div class="search-box">
                    <i class="fas fa-search"></i>
                    <input name="q"
                           type="text"
                           class="form-control"
                           placeholder="Search by code / name..."
                           value="${param.q}" />
                </div>

                <select name="status" class="form-select">
                    <option value="ALL" ${empty param.status || param.status == 'ALL' ? 'selected' : ''}>
                        All Status
                    </option>
                    <option value="ACTIVE" ${param.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                    <option value="INACTIVE" ${param.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                </select>

                <button class="btn btn-outline-primary" type="submit">Apply</button>

                <a class="btn btn-outline-secondary"
                   href="${pageContext.request.contextPath}/ProductCategory">
                    Reset
                </a>
            </form>

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

                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/category/form">
                    <i class="fas fa-plus me-2"></i> New Category
                </a>





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
                                data-status="${c.status}"
                                data-text="${c.categoryCode} ${c.categoryName} ${c.description}"
                                style="cursor:pointer;">

                                <td class="text-muted">${(page - 1) * pageSize + st.index + 1}</td>


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
                                       href="${pageContext.request.contextPath}/category/form?categoryId=${c.categoryId}"

                                       onclick="event.stopPropagation();">
                                        <i class="fas fa-pen me-1"></i> Edit
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>


                    </tbody>
                </table>
                <c:if test="${totalPages > 1}">
                    <nav class="mt-3">
                        <ul class="pagination justify-content-center mb-0">

                            <!-- Prev -->
                            <li class="page-item ${page == 1 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/ProductCategory?page=${page-1}&q=${param.q}&status=${param.status}">
                                    Previous
                                </a>
                            </li>

                            <!-- Pages -->
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <li class="page-item ${p == page ? 'active' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/ProductCategory?page=${p}&q=${param.q}&status=${param.status}">
                                        ${p}
                                    </a>
                                </li>
                            </c:forEach>

                            <!-- Next -->
                            <li class="page-item ${page == totalPages ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/ProductCategory?page=${page+1}&q=${param.q}&status=${param.status}">
                                    Next
                                </a>
                            </li>

                        </ul>
                    </nav>
                </c:if>

            </div>
        </c:if>

    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".category-row").forEach(row => {
            row.addEventListener("click", function () {
                const url = this.dataset.url;
                if (url)
                    window.location.href = url;
            });
        });
    });
</script>



<%@ include file="../common/footer.jsp" %>
