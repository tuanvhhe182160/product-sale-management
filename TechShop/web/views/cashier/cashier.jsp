<%--
    Document   : cashier
    Author     : Cashier Module
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Product Search - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    /* ── Product Card ── */
    .product-card {
        border: 1px solid #e9ecef;
        border-radius: 10px;
        overflow: hidden;
        transition: box-shadow .2s, transform .2s;
        background: #fff;
        height: 100%;
        display: flex;
        flex-direction: column;
    }
    .product-card:hover {
        box-shadow: 0 6px 20px rgba(0,0,0,.10);
        transform: translateY(-2px);
    }

    /* Image area */
    .product-img-wrap {
        width: 100%;
        height: 170px;
        background: #f8f9fa;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
        border-bottom: 1px solid #f0f0f0;
    }
    .product-img-wrap img {
        width: 100%;
        height: 100%;
        object-fit: contain;
        padding: 12px;
    }
    .product-img-wrap .no-img {
        color: #ced4da;
        font-size: 3rem;
    }

    /* Card body */
    .product-card .card-body {
        padding: 12px 14px;
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 6px;
    }

    .product-name {
        font-size: .9rem;
        font-weight: 600;
        color: #212529;
        line-height: 1.3;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }
    .product-model {
        font-size: .78rem;
        color: #6c757d;
    }
    .badge-sku {
        font-size: .72rem;
        font-family: monospace;
        background: #e8f0fe;
        color: #1a56db;
        padding: 2px 7px;
        border-radius: 4px;
        font-weight: 600;
        display: inline-block;
    }
    .badge-cat {
        font-size: .72rem;
        background: #f3e8ff;
        color: #7c3aed;
        padding: 2px 7px;
        border-radius: 4px;
        font-weight: 600;
        display: inline-block;
    }
    .product-price {
        font-size: 1rem;
        font-weight: 700;
        color: #0d6efd;
        margin-top: auto;
    }
    .product-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 14px;
        border-top: 1px solid #f0f0f0;
        background: #fafafa;
        font-size: .78rem;
    }

    /* Stock badges */
    .stock-ok  {
        color: #0f766e;
        background: #d1fae5;
        padding: 2px 8px;
        border-radius: 10px;
        font-weight: 600;
    }
    .stock-low {
        color: #b45309;
        background: #fef3c7;
        padding: 2px 8px;
        border-radius: 10px;
        font-weight: 600;
    }
    .stock-out {
        color: #dc2626;
        background: #fee2e2;
        padding: 2px 8px;
        border-radius: 10px;
        font-weight: 600;
    }
</style>

<!-- ===== Page Header ===== -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h4 class="mb-1 fw-bold">
            <i class="fas fa-search text-primary me-2"></i>Product Search
        </h4>
        <span class="text-muted small">
            Total: <strong>${totalItems}</strong> product(s)
        </span>
    </div>
    <a href="${pageContext.request.contextPath}/cart"
       class="btn btn-primary position-relative">
        <i class="fas fa-shopping-cart me-2"></i>Giỏ hàng
        <c:if test="${not empty sessionScope.saleCart && sessionScope.saleCart.size() > 0}">
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                ${sessionScope.saleCart.size()}
            </span>
        </c:if>
    </a>
</div>

<!-- ===== Search / Filter Form ===== -->
<div class="card border-0 shadow-sm mb-4">
    <div class="card-body py-3">
        <form method="get" action="${pageContext.request.contextPath}/cashier" class="row g-2 align-items-end">
            <input type="hidden" name="page" id="pageHidden" value="1" />

            <div class="col-sm-5 col-md-3">
                <label class="form-label small mb-1 text-muted">Tìm kiếm</label>
                <div class="input-group input-group-sm">
                    <span class="input-group-text bg-white">
                        <i class="fas fa-search text-muted"></i>
                    </span>
                    <input type="text" class="form-control" name="keyword"
                           value="${keyword}" placeholder="Tên, thương hiệu, model…" />
                </div>
            </div>

            <div class="col-sm-4 col-md-2">
                <label class="form-label small mb-1 text-muted">Danh mục</label>
                <select class="form-select form-select-sm" name="categoryId" id="categorySelect">
                    <option value="">All Categories</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}"
                                ${categoryId == cat.categoryId ? 'selected' : ''}>
                            ${cat.categoryName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-sm-4 col-md-2">
                <label class="form-label small mb-1 text-muted">Model</label>
                <select class="form-select form-select-sm" name="modelId" id="modelSelect">
                    <option value="">All Models</option>
                    <c:forEach var="m" items="${models}">
                        <option value="${m.modelId}"
                                ${modelId == m.modelId ? 'selected' : ''}>
                            ${m.modelName}<c:if test="${not empty m.brand}"> (${m.brand})</c:if>
                            </option>
                    </c:forEach>
                </select>
            </div>

            <div class="col-sm-4 col-md-2">
                <label class="form-label small mb-1 text-muted">SKU</label>
                <input type="text" class="form-control form-control-sm" name="sku"
                       value="${sku}" placeholder="VD: IP15PM-256…" />
            </div>

            <div class="col-auto">
                <button type="submit" class="btn btn-primary btn-sm">
                    <i class="fas fa-search me-1"></i>Search
                </button>
                <a href="${pageContext.request.contextPath}/cashier"
                   class="btn btn-outline-secondary btn-sm ms-1">
                    Reset
                </a>
            </div>
        </form>
    </div>
</div>

<!-- ===== Product Cards ===== -->
<c:choose>
    <c:when test="${empty list}">
        <div class="text-center py-5 text-muted">
            <i class="fas fa-box-open fa-2x mb-3 d-block"></i>
            <p class="mb-0">No products found.</p>
            <c:if test="${not empty keyword || not empty categoryId || not empty modelId || not empty sku}">
                <a href="${pageContext.request.contextPath}/cashier"
                   class="btn btn-sm btn-outline-secondary mt-3">Clear filters</a>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>

        <div class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-xl-5 g-3 mb-4">
            <c:forEach items="${list}" var="v">
                <div class="col">
                    <div class="product-card shadow-sm">

                        <!-- Image -->
                        <div class="product-img-wrap">
                            <c:choose>
                                <c:when test="${not empty v.imageUrl}">
                                    <img src="${v.imageUrl}"
                                         alt="${v.variantName}"
                                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';" />
                                    <div class="no-img" style="display:none;">
                                        <i class="fas fa-image"></i>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-img">
                                        <i class="fas fa-image"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Body -->
                        <div class="card-body">
                            <div>
                                <span class="badge-cat">${v.categoryName}</span>
                            </div>
                            <div class="product-name" title="${v.variantName}">
                                ${v.variantName}
                            </div>
                            <div class="product-model">
                                ${v.brand}
                                <c:if test="${not empty v.brand && not empty v.modelName}"> · </c:if>
                                ${v.modelName}
                            </div>
                            <div>
                                <span class="badge-sku">${v.sku}</span>
                            </div>
                            <!-- IMEI -->
                            <div class="mt-1" style="font-family:monospace;font-size:.75rem;color:#1a56db;background:#e8f0fe;padding:2px 7px;border-radius:4px;display:inline-block;">
                                ${v.imei}
                            </div>
                            <div class="product-price">
                                <fmt:formatNumber value="${v.unitPrice}" type="number" groupingUsed="true"/>đ
                            </div>
                        </div>

                        <!-- Footer: chi tiết + add to cart -->
                        <div class="product-footer">
                            <a href="${pageContext.request.contextPath}/product-detail?physicalId=${v.physicalId}&keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${currentPage}"
                               class="btn btn-outline-secondary btn-sm" title="Xem chi tiết">
                                <i class="fas fa-info-circle"></i>
                            </a>
                            <a href="${pageContext.request.contextPath}/cart?action=add&physicalId=${v.physicalId}&redirect=${pageContext.request.contextPath}/cashier?keyword=${keyword}%26categoryId=${categoryId}%26modelId=${modelId}%26sku=${sku}%26page=${currentPage}"
                               class="btn btn-success btn-sm" title="Thêm vào giỏ hàng">
                                <i class="fas fa-cart-plus me-1"></i>Thêm giỏ
                            </a>
                        </div>

                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- ===== Pagination ===== -->
        <c:if test="${totalPages > 1}">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-2">
                <span class="text-muted small">
                    Page <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                    &nbsp;·&nbsp; <strong>${totalItems}</strong> item(s)
                </span>
                <nav>
                    <ul class="pagination pagination-sm mb-0">

                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${currentPage - 1}">
                                &laquo;
                            </a>
                        </li>

                        <c:set var="pgStart" value="${currentPage - 2 < 2 ? 2 : currentPage - 2}" />
                        <c:set var="pgEnd"   value="${currentPage + 2 > totalPages - 1 ? totalPages - 1 : currentPage + 2}" />

                        <%-- Trang đầu luôn hiện --%>
                        <li class="page-item ${currentPage == 1 ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=1">
                                1
                            </a>
                        </li>

                        <%-- ... trái nếu window không liền trang 1 --%>
                        <c:if test="${pgStart > 2}">
                            <li class="page-item disabled"><span class="page-link">…</span></li>
                            </c:if>

                        <%-- Các trang giữa --%>
                        <c:forEach begin="${pgStart}" end="${pgEnd}" var="p">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${p}">
                                    ${p}
                                </a>
                            </li>
                        </c:forEach>

                        <%-- ... phải nếu window không liền trang cuối --%>
                        <c:if test="${pgEnd < totalPages - 1}">
                            <li class="page-item disabled"><span class="page-link">…</span></li>
                            </c:if>

                        <%-- Trang cuối luôn hiện (chỉ khi totalPages > 1) --%>
                        <c:if test="${totalPages > 1}">
                            <li class="page-item ${currentPage == totalPages ? 'active' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${totalPages}">
                                    ${totalPages}
                                </a>
                            </li>
                        </c:if>

                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${currentPage + 1}">
                                &raquo;
                            </a>
                        </li>

                    </ul>
                </nav>
            </div>
        </c:if>

    </c:otherwise>
</c:choose>

<script>
    document.getElementById('categorySelect').addEventListener('change', function () {
        document.getElementById('modelSelect').value = '';
        document.getElementById('pageHidden').value = '1';
        this.closest('form').submit();
    });
</script>

<%@ include file="../common/footer.jsp" %>
