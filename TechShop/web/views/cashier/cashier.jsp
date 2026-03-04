<%--
    cashier.jsp – Layout 2 cột: trái tìm sản phẩm, phải giỏ hàng + khách hàng
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Bán hàng - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
/* ── Bố cục tổng thể màn hình bán hàng ── */
.pos-layout {
    display: flex;
    gap: 0;
    height: calc(100vh - 70px);
    overflow: hidden;
    margin: -1.5rem -1.5rem 0;
}

/* ── Cột trái: danh sách sản phẩm ── */
.pos-left {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    background: #f4f6fb;
    overflow: hidden;
    border-right: 1px solid #e2e6ea;
}

.pos-left-header {
    padding: 14px 18px 10px;
    background: #fff;
    border-bottom: 1px solid #e9ecef;
    flex-shrink: 0;
}

.pos-left-header .title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
}

.pos-left-header h5 {
    margin: 0;
    font-size: 1rem;
    font-weight: 700;
    color: #1a202c;
}

.pos-products {
    flex: 1;
    overflow-y: auto;
    padding: 14px 16px;
}

/* ── Thẻ sản phẩm ── */
.product-card {
    border: 1.5px solid #e9ecef;
    border-radius: 10px;
    overflow: hidden;
    background: #fff;
    transition: box-shadow .18s, border-color .18s, transform .18s;
    height: 100%;
    display: flex;
    flex-direction: column;
    cursor: pointer;
}
.product-card:hover {
    box-shadow: 0 4px 18px rgba(13,110,253,.13);
    border-color: #0d6efd;
    transform: translateY(-2px);
}
.product-card.in-cart {
    border-color: #10b981;
    background: #f0fdf4;
}
.product-img-wrap {
    width: 100%;
    height: 140px;
    background: #f8f9fa;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    border-bottom: 1px solid #f0f0f0;
    position: relative;
}
.product-img-wrap img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    padding: 10px;
}
.product-img-wrap .no-img {
    color: #ced4da;
    font-size: 2.5rem;
}
.in-cart-badge {
    position: absolute;
    top: 8px;
    right: 8px;
    background: #10b981;
    color: #fff;
    font-size: .68rem;
    font-weight: 700;
    padding: 2px 7px;
    border-radius: 10px;
    letter-spacing: .3px;
}
.product-card .card-body {
    padding: 10px 12px;
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 4px;
}
.product-name {
    font-size: .82rem;
    font-weight: 600;
    color: #1a202c;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
.product-model {
    font-size: .72rem;
    color: #6c757d;
}
.badge-sku {
    font-size: .68rem;
    font-family: monospace;
    background: #e8f0fe;
    color: #1a56db;
    padding: 1px 6px;
    border-radius: 3px;
    font-weight: 700;
    display: inline-block;
}
.badge-cat {
    font-size: .67rem;
    background: #f3e8ff;
    color: #7c3aed;
    padding: 1px 6px;
    border-radius: 3px;
    font-weight: 600;
    display: inline-block;
}
.badge-imei-sm {
    font-family: monospace;
    font-size: .68rem;
    background: #e8f0fe;
    color: #1a56db;
    padding: 1px 6px;
    border-radius: 3px;
}
.product-price {
    font-size: .95rem;
    font-weight: 700;
    color: #0d6efd;
    margin-top: auto;
}
.product-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 7px 12px;
    border-top: 1px solid #f0f0f0;
    background: #fafafa;
    gap: 6px;
}
.btn-add-cart {
    flex: 1;
    font-size: .78rem;
    padding: 5px 10px;
    border-radius: 7px;
    font-weight: 600;
    background: #0d6efd;
    color: #fff;
    border: none;
    cursor: pointer;
    transition: background .15s;
    text-decoration: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
}
.btn-add-cart:hover { background: #0b5ed7; color: #fff; }
.btn-add-cart.added {
    background: #10b981;
    cursor: default;
}

/* ── Cột phải: giỏ hàng và thông tin khách ── */
.pos-right {
    width: 480px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    background: #fff;
    overflow: hidden;
}

/* Thanh tab chuyển giữa "Giỏ hàng" và "Khách hàng" */
.pos-right-header {
    padding: 12px 16px 0;
    border-bottom: 1px solid #e9ecef;
    background: #fff;
    flex-shrink: 0;
}

.pos-tabs {
    display: flex;
    gap: 0;
    border-bottom: none;
}
.pos-tab {
    flex: 1;
    text-align: center;
    padding: 8px 0;
    font-size: .82rem;
    font-weight: 600;
    color: #6c757d;
    cursor: pointer;
    border-bottom: 2.5px solid transparent;
    transition: color .15s, border-color .15s;
    user-select: none;
}
.pos-tab.active {
    color: #0d6efd;
    border-bottom-color: #0d6efd;
}

/* Thanh thông tin nhân viên và ngày giờ */
.customer-info-bar {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 16px;
    border-bottom: 1px solid #f0f0f0;
    background: #f8faff;
    flex-shrink: 0;
}
.customer-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #0d6efd;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1rem;
    flex-shrink: 0;
}
.customer-name-label {
    font-size: .82rem;
    font-weight: 600;
    color: #1a202c;
    line-height: 1.2;
}
.customer-phone-label {
    font-size: .72rem;
    color: #6c757d;
}
.date-label {
    font-size: .72rem;
    color: #6c757d;
    margin-left: auto;
    white-space: nowrap;
}

/* Thanh tab hóa đơn (hóa đơn 1, 2, ...) */
.invoice-tabs-bar {
    display: flex;
    align-items: center;
    padding: 6px 10px;
    border-bottom: 1px solid #f0f0f0;
    background: #f8f9fa;
    gap: 4px;
    flex-shrink: 0;
    overflow-x: auto;
}
.invoice-tab-pill {
    display: flex;
    align-items: center;
    gap: 5px;
    background: #fff;
    border: 1.5px solid #e2e6ea;
    border-radius: 20px;
    padding: 3px 10px;
    font-size: .75rem;
    font-weight: 600;
    color: #495057;
    cursor: pointer;
    white-space: nowrap;
    transition: all .15s;
}
.invoice-tab-pill.active {
    background: #0d6efd;
    border-color: #0d6efd;
    color: #fff;
}
.invoice-tab-pill .close-tab {
    font-size: .65rem;
    opacity: .6;
    margin-left: 2px;
}
.btn-new-invoice {
    background: none;
    border: 1.5px dashed #ced4da;
    border-radius: 50%;
    width: 26px;
    height: 26px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6c757d;
    cursor: pointer;
    font-size: .8rem;
    flex-shrink: 0;
    transition: all .15s;
}
.btn-new-invoice:hover {
    border-color: #0d6efd;
    color: #0d6efd;
}

/* Danh sách sản phẩm trong giỏ hàng */
.cart-items-wrap {
    flex: 1;
    overflow-y: auto;
    padding: 0;
}

.cart-item-row {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 14px;
    border-bottom: 1px solid #f4f6fb;
    transition: background .1s;
}
.cart-item-row:hover {
    background: #f8faff;
}

.cart-item-thumb {
    width: 42px;
    height: 42px;
    object-fit: contain;
    border-radius: 8px;
    background: #f8f9fa;
    border: 1px solid #e9ecef;
    padding: 3px;
    flex-shrink: 0;
}
.cart-item-no-thumb {
    width: 42px;
    height: 42px;
    border-radius: 8px;
    background: #f1f5f9;
    border: 1px solid #e9ecef;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ced4da;
    font-size: 1rem;
    flex-shrink: 0;
}
.cart-item-info {
    flex: 1;
    min-width: 0;
}
.cart-item-name {
    font-size: .78rem;
    font-weight: 600;
    color: #1a202c;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.cart-item-imei {
    font-family: monospace;
    font-size: .7rem;
    color: #1a56db;
    background: #e8f0fe;
    padding: 1px 5px;
    border-radius: 3px;
    display: inline-block;
    margin-top: 2px;
}
.cart-item-price {
    font-size: .85rem;
    font-weight: 700;
    color: #0d6efd;
    white-space: nowrap;
    flex-shrink: 0;
}
.btn-remove-cart-item {
    background: none;
    border: none;
    padding: 3px 5px;
    color: #ced4da;
    cursor: pointer;
    font-size: .8rem;
    flex-shrink: 0;
    border-radius: 4px;
    transition: color .15s, background .15s;
}
.btn-remove-cart-item:hover {
    color: #dc3545;
    background: #fee2e2;
}

/* Trạng thái giỏ hàng trống */
.cart-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    color: #adb5bd;
    gap: 10px;
}
.cart-empty i { font-size: 2.5rem; }
.cart-empty p { margin: 0; font-size: .85rem; }

/* Panel nhập thông tin khách hàng */
.customer-panel {
    flex: 1;
    overflow-y: auto;
    padding: 14px 16px;
}
.form-label.req::after {
    content: " *";
    color: #dc3545;
}
.error-text {
    font-size: .72rem;
    color: #dc3545;
}

/* Khu vực tổng tiền và nút thanh toán */
.pos-footer {
    border-top: 1.5px solid #e9ecef;
    padding: 12px 16px;
    background: #fff;
    flex-shrink: 0;
}
.total-line {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4px;
    font-size: .82rem;
    color: #6c757d;
}
.total-line.grand {
    font-size: 1.05rem;
    font-weight: 700;
    color: #0d6efd;
    margin-bottom: 10px;
    padding-top: 6px;
    border-top: 1px dashed #dee2e6;
}
.btn-checkout {
    width: 100%;
    background: #0d6efd;
    color: #fff;
    border: none;
    border-radius: 10px;
    padding: 10px;
    font-size: .92rem;
    font-weight: 700;
    cursor: pointer;
    transition: background .15s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}
.btn-checkout:hover { background: #0b5ed7; }
.btn-checkout:disabled {
    background: #dee2e6;
    color: #adb5bd;
    cursor: not-allowed;
}
.btn-cancel-order {
    width: 100%;
    background: none;
    border: 1.5px solid #e9ecef;
    border-radius: 10px;
    padding: 7px;
    font-size: .78rem;
    font-weight: 600;
    color: #6c757d;
    cursor: pointer;
    margin-top: 7px;
    transition: all .15s;
}
.btn-cancel-order:hover {
    border-color: #dc3545;
    color: #dc3545;
}

/* Thanh tìm kiếm sản phẩm */
.search-bar-wrap .input-group-text {
    background: #fff;
    border-right: 0;
}
.search-bar-wrap .form-control {
    border-left: 0;
}

/* Phân trang nhỏ gọn */
.pagination-compact {
    display: flex;
    gap: 3px;
    align-items: center;
    flex-wrap: wrap;
}
.pagination-compact a, .pagination-compact span {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 30px;
    height: 30px;
    border-radius: 7px;
    font-size: .75rem;
    font-weight: 600;
    border: 1px solid #dee2e6;
    background: #fff;
    color: #495057;
    text-decoration: none;
    padding: 0 6px;
    transition: all .12s;
}
.pagination-compact a:hover { background: #e8f0fe; border-color: #0d6efd; color: #0d6efd; }
.pagination-compact a.active, .pagination-compact span.active {
    background: #0d6efd;
    border-color: #0d6efd;
    color: #fff;
}
.pagination-compact span.dots {
    border: none;
    background: none;
    color: #adb5bd;
}

/* Tùy chỉnh thanh cuộn mỏng cho các vùng scroll */
.pos-products::-webkit-scrollbar,
.cart-items-wrap::-webkit-scrollbar,
.customer-panel::-webkit-scrollbar { width: 5px; }
.pos-products::-webkit-scrollbar-track,
.cart-items-wrap::-webkit-scrollbar-track,
.customer-panel::-webkit-scrollbar-track { background: transparent; }
.pos-products::-webkit-scrollbar-thumb,
.cart-items-wrap::-webkit-scrollbar-thumb,
.customer-panel::-webkit-scrollbar-thumb { background: #dee2e6; border-radius: 3px; }

/* Thông báo nổi góc trên phải (thêm thành công / lỗi) */
.flash-toast {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 9999;
    min-width: 280px;
    max-width: 360px;
    border-radius: 10px;
    box-shadow: 0 8px 24px rgba(0,0,0,.12);
    padding: 12px 16px;
    font-size: .85rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 10px;
    animation: slideInRight .25s ease;
}
@keyframes slideInRight {
    from { transform: translateX(60px); opacity: 0; }
    to   { transform: translateX(0);   opacity: 1; }
}
.flash-success { background: #f0fdf4; border-left: 4px solid #10b981; color: #065f46; }
.flash-error   { background: #fff5f5; border-left: 4px solid #ef4444; color: #7f1d1d; }
</style>

<!-- ===== Flash messages ===== -->
<c:if test="${not empty sessionScope.cartSuccess}">
    <div class="flash-toast flash-success" id="flashMsg">
        <i class="fas fa-check-circle"></i>
        <span>${sessionScope.cartSuccess}</span>
    </div>
    <c:remove var="cartSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.cartError}">
    <div class="flash-toast flash-error" id="flashMsg">
        <i class="fas fa-exclamation-circle"></i>
        <span>${sessionScope.cartError}</span>
    </div>
    <c:remove var="cartError" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.cancelSuccess}">
    <div class="flash-toast flash-success" id="flashMsg">
        <i class="fas fa-ban"></i>
        <span>${sessionScope.cancelSuccess}</span>
    </div>
    <c:remove var="cancelSuccess" scope="session"/>
</c:if>

<%-- Gom physicalId của các sản phẩm trong giỏ thành chuỗi để kiểm tra "đã thêm" phía dưới --%>
<c:set var="cartIds" value="," />
<c:forEach var="ci" items="${sessionScope.saleCart}">
    <c:set var="cartIds" value="${cartIds}${ci.physicalId}," />
</c:forEach>

<!-- ===== POS Layout ===== -->
<div class="pos-layout">

    <!-- ══════════ CỘT TRÁI: Sản phẩm ══════════ -->
    <div class="pos-left">

        <!-- Header tìm kiếm -->
        <div class="pos-left-header">
            <div class="title-row">
                <h5><i class="fas fa-barcode text-primary me-2"></i>Tìm sản phẩm</h5>
                <span class="text-muted" style="font-size:.78rem;">
                    <strong>${totalItems}</strong> IMEI khả dụng
                </span>
            </div>

            <form method="get" action="${pageContext.request.contextPath}/cashier"
                  class="row g-2 align-items-end">
                <input type="hidden" name="page" value="1" />

                <div class="col-md-4 col-sm-6">
                    <div class="input-group input-group-sm search-bar-wrap">
                        <span class="input-group-text"><i class="fas fa-search text-muted"></i></span>
                        <input type="text" class="form-control" name="keyword"
                               value="${keyword}" placeholder="Tên, IMEI, thương hiệu…" />
                    </div>
                </div>

                <div class="col-md-2 col-sm-4">
                    <select class="form-select form-select-sm" name="categoryId" id="categorySelect">
                        <option value="">Tất cả danh mục</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryId}"
                                    ${categoryId == cat.categoryId ? 'selected' : ''}>
                                ${cat.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2 col-sm-4">
                    <select class="form-select form-select-sm" name="modelId" id="modelSelect">
                        <option value="">Tất cả model</option>
                        <c:forEach var="m" items="${models}">
                            <option value="${m.modelId}"
                                    ${modelId == m.modelId ? 'selected' : ''}>
                                ${m.modelName}<c:if test="${not empty m.brand}"> (${m.brand})</c:if>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2 col-sm-4">
                    <input type="text" class="form-control form-control-sm" name="sku"
                           value="${sku}" placeholder="SKU…" />
                </div>

                <div class="col-auto d-flex gap-1">
                    <button type="submit" class="btn btn-primary btn-sm">
                        <i class="fas fa-search"></i>
                    </button>
                    <a href="${pageContext.request.contextPath}/cashier"
                       class="btn btn-outline-secondary btn-sm">
                        <i class="fas fa-undo"></i>
                    </a>
                </div>
            </form>
        </div>

        <!-- Danh sách sản phẩm -->
        <div class="pos-products">
            <c:choose>
                <c:when test="${empty list}">
                    <div style="text-align:center;padding:60px 20px;color:#adb5bd;">
                        <i class="fas fa-box-open fa-2x"></i>
                        <p style="margin-top:12px;font-size:.88rem;">Không tìm thấy sản phẩm nào.</p>
                        <c:if test="${not empty keyword || not empty categoryId || not empty modelId || not empty sku}">
                            <a href="${pageContext.request.contextPath}/cashier"
                               class="btn btn-sm btn-outline-secondary mt-2">Xóa bộ lọc</a>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-xl-5 g-2 mb-3">
                        <c:forEach items="${list}" var="v">
                            <c:set var="isInCart" value="${fn:contains(cartIds, ','.concat(v.physicalId).concat(','))}" />
                            <div class="col">
                                <div class="product-card ${isInCart ? 'in-cart' : ''}">

                                    <div class="product-img-wrap">
                                        <c:choose>
                                            <c:when test="${not empty v.imageUrl}">
                                                <img src="${v.imageUrl}" alt="${v.variantName}"
                                                     onerror="this.style.display='none';this.nextElementSibling.style.display='flex';" />
                                                <div class="no-img" style="display:none;"><i class="fas fa-image"></i></div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="no-img"><i class="fas fa-image"></i></div>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${isInCart}">
                                            <span class="in-cart-badge">✓ Đã thêm</span>
                                        </c:if>
                                    </div>

                                    <div class="card-body">
                                        <div><span class="badge-cat">${v.categoryName}</span></div>
                                        <div class="product-name" title="${v.variantName}">${v.variantName}</div>
                                        <div class="product-model">${v.brand}<c:if test="${not empty v.brand && not empty v.modelName}"> · </c:if>${v.modelName}</div>
                                        <div><span class="badge-sku">${v.sku}</span></div>
                                        <div class="mt-1"><span class="badge-imei-sm">${v.imei}</span></div>
                                        <div class="product-price">
                                            <fmt:formatNumber value="${v.unitPrice}" type="number" groupingUsed="true"/>đ
                                        </div>
                                    </div>

                                    <div class="product-footer">
                                        <a href="${pageContext.request.contextPath}/product-detail?physicalId=${v.physicalId}&keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${currentPage}"
                                           class="btn btn-outline-secondary btn-sm" title="Xem chi tiết"
                                           style="font-size:.75rem;padding:4px 8px;">
                                            <i class="fas fa-info-circle"></i>
                                        </a>

                                        <c:choose>
                                            <c:when test="${isInCart}">
                                                <span class="btn-add-cart added">
                                                    <i class="fas fa-check"></i> Đã thêm
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/cart?action=add&physicalId=${v.physicalId}&redirect=${pageContext.request.contextPath}/cashier?keyword=${keyword}%26categoryId=${categoryId}%26modelId=${modelId}%26sku=${sku}%26page=${currentPage}"
                                                   class="btn-add-cart">
                                                    <i class="fas fa-plus"></i> Thêm
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-2">
                            <span class="text-muted" style="font-size:.75rem;">
                                Trang <strong>${currentPage}</strong>/${totalPages}
                                &nbsp;·&nbsp; <strong>${totalItems}</strong> IMEI
                            </span>
                            <div class="pagination-compact">
                                <c:if test="${currentPage > 1}">
                                    <a href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${currentPage-1}">&laquo;</a>
                                </c:if>

                                <c:set var="pgStart" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />
                                <c:set var="pgEnd"   value="${currentPage + 2 > totalPages ? totalPages : currentPage + 2}" />

                                <c:if test="${pgStart > 1}">
                                    <a href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=1">1</a>
                                    <c:if test="${pgStart > 2}"><span class="dots">…</span></c:if>
                                </c:if>

                                <c:forEach begin="${pgStart}" end="${pgEnd}" var="p">
                                    <c:choose>
                                        <c:when test="${p == currentPage}">
                                            <span class="active">${p}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${p}">${p}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${pgEnd < totalPages}">
                                    <c:if test="${pgEnd < totalPages - 1}"><span class="dots">…</span></c:if>
                                    <a href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${totalPages}">${totalPages}</a>
                                </c:if>

                                <c:if test="${currentPage < totalPages}">
                                    <a href="${pageContext.request.contextPath}/cashier?keyword=${keyword}&categoryId=${categoryId}&modelId=${modelId}&sku=${sku}&page=${currentPage+1}">&raquo;</a>
                                </c:if>
                            </div>
                        </div>
                    </c:if>

                </c:otherwise>
            </c:choose>
        </div>

    </div><!-- /pos-left -->


    <!-- ══════════ CỘT PHẢI: Đơn hàng + Khách ══════════ -->
    <div class="pos-right">

        <!-- Thông tin nhân viên + ngày -->
        <div class="customer-info-bar">
            <div class="customer-avatar">
                <i class="fas fa-user-tie" style="font-size:.9rem;"></i>
            </div>
            <div>
                <div class="customer-name-label">
                    ${not empty sessionScope.fullName ? sessionScope.fullName : 'Cashier'}
                </div>
                <div class="customer-phone-label">
                    ${not empty sessionScope.branchName ? sessionScope.branchName : 'Chi nhánh'}
                </div>
            </div>
            <div class="date-label" id="clockLabel">
                <script>
                    (function tick(){
                        var now = new Date();
                        var d = ('0'+now.getDate()).slice(-2)+'/'+('0'+(now.getMonth()+1)).slice(-2)+'/'+now.getFullYear();
                        var t = ('0'+now.getHours()).slice(-2)+':'+('0'+now.getMinutes()).slice(-2);
                        document.getElementById('clockLabel').textContent = d + '  ' + t;
                        setTimeout(tick, 30000);
                    })();
                </script>
            </div>
        </div>

        <!-- Invoice tab bar -->
        <div class="invoice-tabs-bar">
            <div class="invoice-tab-pill active">
                <i class="fas fa-sync-alt" style="font-size:.65rem;opacity:.7;"></i>
                Hóa đơn 1
                <c:choose>
                    <c:when test="${not empty sessionScope.saleCart}">
                        <a href="${pageContext.request.contextPath}/cart?action=cancel&reason=Dong+hoa+don"
                           class="close-tab"
                           onclick="return confirm('Xóa toàn bộ giỏ hàng và đóng hóa đơn?')"
                           title="Đóng hóa đơn / Xóa giỏ hàng"
                           style="color:inherit;text-decoration:none;">✕</a>
                    </c:when>
                    <c:otherwise>
                        <span class="close-tab">✕</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <button class="btn-new-invoice" title="Tạo hóa đơn mới">
                <i class="fas fa-plus"></i>
            </button>
        </div>

        <!-- Tabs: Giỏ hàng / Khách hàng -->
        <div class="pos-right-header">
            <div class="pos-tabs">
                <div class="pos-tab active" id="tabCart" onclick="switchTab('cart')">
                    <i class="fas fa-shopping-cart me-1"></i>
                    Giỏ hàng
                    <c:if test="${not empty sessionScope.saleCart && sessionScope.saleCart.size() > 0}">
                        <span style="background:#0d6efd;color:#fff;border-radius:9px;padding:1px 6px;font-size:.65rem;margin-left:4px;">
                            ${sessionScope.saleCart.size()}
                        </span>
                    </c:if>
                </div>
                <div class="pos-tab" id="tabCustomer" onclick="switchTab('customer')">
                    <i class="fas fa-user me-1"></i>Khách hàng
                </div>
            </div>
        </div>

        <!-- ── Panel: Giỏ hàng ── -->
        <div id="panelCart" style="display:flex;flex-direction:column;flex:1;overflow:hidden;">
            <div class="cart-items-wrap">
                <c:choose>
                    <c:when test="${empty sessionScope.saleCart}">
                        <div class="cart-empty">
                            <i class="fas fa-shopping-cart"></i>
                            <p>Giỏ hàng trống</p>
                            <p style="font-size:.75rem;color:#ced4da;">Nhấn <strong>Thêm</strong> vào sản phẩm bên trái</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${sessionScope.saleCart}" var="item" varStatus="st">
                            <div class="cart-item-row">
                                <c:choose>
                                    <c:when test="${not empty item.imageUrl}">
                                        <img src="${item.imageUrl}" class="cart-item-thumb" alt="${item.variantName}"
                                             onerror="this.style.display='none';this.nextElementSibling.style.display='flex';" />
                                        <div class="cart-item-no-thumb" style="display:none;"><i class="fas fa-image"></i></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="cart-item-no-thumb"><i class="fas fa-image"></i></div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="cart-item-info">
                                    <div class="cart-item-name" title="${item.variantName}">${item.variantName}</div>
                                    <div style="display:flex;align-items:center;gap:5px;margin-top:3px;flex-wrap:wrap;">
                                        <span class="cart-item-imei">${item.imei}</span>
                                        <span style="font-size:.67rem;color:#6c757d;">${item.sku}</span>
                                    </div>
                                </div>

                                <div class="cart-item-price">
                                    <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/>đ
                                </div>

                                <a href="${pageContext.request.contextPath}/cart?action=remove&physicalId=${item.physicalId}&redirect=${pageContext.request.contextPath}/cashier?keyword=${keyword}%26categoryId=${categoryId}%26modelId=${modelId}%26sku=${sku}%26page=${currentPage}"
                                   class="btn-remove-cart-item"
                                   onclick="return confirm('Xóa IMEI ${item.imei} khỏi giỏ?')"
                                   title="Xóa">
                                    <i class="fas fa-times"></i>
                                </a>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Tổng tiền + Checkout -->
            <div class="pos-footer">
                <c:set var="cartTotal" value="0" />
                <c:forEach var="ci" items="${sessionScope.saleCart}">
                    <c:set var="cartTotal" value="${cartTotal + ci.unitPrice}" />
                </c:forEach>

                <div class="total-line">
                    <span>Số lượng:</span>
                    <span>
                        <c:choose>
                            <c:when test="${not empty sessionScope.saleCart}">${sessionScope.saleCart.size()} sản phẩm</c:when>
                            <c:otherwise>0 sản phẩm</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="total-line">
                    <span>Giảm giá:</span>
                    <span>0đ</span>
                </div>
                <div class="total-line grand">
                    <span>Khách cần trả:</span>
                    <span><fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/>đ</span>
                </div>

                <c:choose>
                    <c:when test="${not empty sessionScope.saleCart && sessionScope.saleCart.size() > 0}">
                        <button class="btn-checkout" onclick="switchTab('customer')">
                            <i class="fas fa-check-circle"></i>
                            Tiến hành thanh toán
                        </button>
                        <button class="btn-cancel-order" data-bs-toggle="modal" data-bs-target="#cancelModal">
                            <i class="fas fa-ban me-1"></i>Hủy đơn hàng
                        </button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn-checkout" disabled>
                            <i class="fas fa-check-circle"></i>
                            Tiến hành thanh toán
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- ── Panel: Khách hàng + Thanh toán ── -->
        <div id="panelCustomer" style="display:none;flex-direction:column;flex:1;overflow:hidden;">
            <div class="customer-panel">
                <form method="get" action="${pageContext.request.contextPath}/invoice/create">

                    <!-- Số điện thoại -->
                    <div class="mb-3">
                        <label class="form-label small fw-semibold req" style="font-size:.78rem;">
                            Số điện thoại khách hàng
                        </label>
                        <div class="input-group input-group-sm">
                            <span class="input-group-text bg-white"><i class="fas fa-phone text-muted"></i></span>
                            <input type="text" class="form-control" name="phone"
                                   placeholder="+84 _ _ _ _ _ _ _ _ _"
                                   pattern="[0-9]{9,11}" required
                                   title="Nhập số điện thoại 9-11 chữ số" />
                        </div>
                        <div class="form-text" style="font-size:.7rem;">Nhập SĐT để tra cứu hoặc tạo khách hàng mới.</div>
                    </div>

                    <!-- Địa chỉ giao hàng -->
                    <div style="background:#f8faff;border:1px solid #e2e8f0;border-radius:10px;padding:12px 14px;margin-bottom:14px;">
                        <div style="font-size:.75rem;font-weight:600;color:#4a5568;margin-bottom:8px;">
                            <i class="fas fa-map-marker-alt text-primary me-1"></i>Địa chỉ giao hàng
                        </div>
                        <div class="mb-2">
                            <input type="text" class="form-control form-control-sm" name="recipientName"
                                   placeholder="Tên người nhận" />
                        </div>
                        <div class="mb-2">
                            <input type="text" class="form-control form-control-sm" name="recipientPhone"
                                   placeholder="Số điện thoại người nhận" />
                        </div>
                        <div class="mb-2">
                            <input type="text" class="form-control form-control-sm" name="addressDetail"
                                   placeholder="Địa chỉ chi tiết (Số nhà, ngõ, đường)" />
                        </div>
                        <div class="row g-2">
                            <div class="col-6">
                                <input type="text" class="form-control form-control-sm" name="district"
                                       placeholder="Khu vực" />
                            </div>
                            <div class="col-6">
                                <input type="text" class="form-control form-control-sm" name="ward"
                                       placeholder="Phường/Xã" />
                            </div>
                        </div>
                    </div>

                    <!-- Phương thức thanh toán -->
                    <div class="mb-3">
                        <label class="form-label small fw-semibold" style="font-size:.78rem;">
                            Phương thức thanh toán
                        </label>
                        <div class="row g-2">
                            <div class="col-6">
                                <label style="display:flex;align-items:center;gap:7px;border:1.5px solid #e2e6ea;border-radius:8px;padding:7px 10px;cursor:pointer;font-size:.78rem;transition:border-color .12s;">
                                    <input type="radio" name="paymentMethod" value="CASH" checked style="accent-color:#0d6efd;" />
                                    💵 Tiền mặt
                                </label>
                            </div>
                            <div class="col-6">
                                <label style="display:flex;align-items:center;gap:7px;border:1.5px solid #e2e6ea;border-radius:8px;padding:7px 10px;cursor:pointer;font-size:.78rem;transition:border-color .12s;">
                                    <input type="radio" name="paymentMethod" value="TRANSFER" style="accent-color:#0d6efd;" />
                                    📱 Chuyển khoản
                                </label>
                            </div>
                            <div class="col-6">
                                <label style="display:flex;align-items:center;gap:7px;border:1.5px solid #e2e6ea;border-radius:8px;padding:7px 10px;cursor:pointer;font-size:.78rem;transition:border-color .12s;">
                                    <input type="radio" name="paymentMethod" value="CARD" style="accent-color:#0d6efd;" />
                                    💳 Thẻ ngân hàng
                                </label>
                            </div>
                            <div class="col-6">
                                <label style="display:flex;align-items:center;gap:7px;border:1.5px solid #e2e6ea;border-radius:8px;padding:7px 10px;cursor:pointer;font-size:.78rem;transition:border-color .12s;">
                                    <input type="radio" name="paymentMethod" value="MIXED" style="accent-color:#0d6efd;" />
                                    🔀 Kết hợp
                                </label>
                            </div>
                        </div>
                    </div>

                    <!-- Thu hộ (COD) -->
                    <div class="mb-3" style="display:flex;align-items:center;justify-content:space-between;background:#f8faff;border-radius:8px;padding:10px 12px;border:1px solid #e2e8f0;">
                        <div>
                            <div style="font-size:.78rem;font-weight:600;color:#1a202c;">Thu hộ tiền (COD)</div>
                            <div style="font-size:.7rem;color:#6c757d;">Shipper thu tiền khi giao hàng</div>
                        </div>
                        <div class="form-check form-switch mb-0">
                            <input class="form-check-input" type="checkbox" name="isCod" id="codSwitch" role="switch"
                                   style="width:2.2rem;height:1.1rem;cursor:pointer;" />
                        </div>
                    </div>

                    <!-- Ghi chú -->
                    <div class="mb-3">
                        <label class="form-label small fw-semibold" style="font-size:.78rem;">Ghi chú đơn hàng</label>
                        <textarea class="form-control form-control-sm" name="note"
                                  rows="2" placeholder="Ghi chú cho đơn hàng hoặc bưu tá…"></textarea>
                    </div>

                    <!-- Tóm tắt giỏ hàng bên dưới -->
                    <div style="background:#f8faff;border:1px solid #e2e8f0;border-radius:10px;padding:10px 14px;margin-bottom:14px;">
                        <div style="font-size:.75rem;font-weight:600;color:#4a5568;margin-bottom:8px;">
                            <i class="fas fa-shopping-cart text-primary me-1"></i>
                            Sản phẩm trong đơn
                            <c:if test="${not empty sessionScope.saleCart}">
                                (${sessionScope.saleCart.size()})
                            </c:if>
                        </div>
                        <c:choose>
                            <c:when test="${empty sessionScope.saleCart}">
                                <div style="font-size:.75rem;color:#adb5bd;text-align:center;padding:10px 0;">Chưa có sản phẩm</div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${sessionScope.saleCart}" var="ci">
                                    <div style="display:flex;justify-content:space-between;align-items:center;padding:5px 0;border-bottom:1px solid #f0f0f0;font-size:.75rem;">
                                        <div style="flex:1;min-width:0;">
                                            <div style="font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${ci.variantName}</div>
                                            <span style="font-family:monospace;font-size:.67rem;color:#1a56db;background:#e8f0fe;padding:1px 4px;border-radius:3px;">${ci.imei}</span>
                                        </div>
                                        <div style="font-weight:700;color:#0d6efd;margin-left:10px;white-space:nowrap;">
                                            <fmt:formatNumber value="${ci.unitPrice}" type="number" groupingUsed="true"/>đ
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Footer checkout từ panel khách -->
                    <div class="total-line grand" style="display:flex;justify-content:space-between;font-size:1rem;font-weight:700;color:#0d6efd;margin-bottom:12px;">
                        <span>Khách cần trả:</span>
                        <span><fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/>đ</span>
                    </div>

                    <c:choose>
                        <c:when test="${not empty sessionScope.saleCart && sessionScope.saleCart.size() > 0}">
                            <button type="submit" class="btn-checkout">
                                <i class="fas fa-check-circle"></i>
                                Xác nhận thanh toán
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="btn-checkout" disabled>
                                <i class="fas fa-check-circle"></i>
                                Xác nhận thanh toán
                            </button>
                        </c:otherwise>
                    </c:choose>

                    <button type="button" class="btn-cancel-order mt-2" onclick="switchTab('cart')">
                        <i class="fas fa-arrow-left me-1"></i>Quay lại giỏ hàng
                    </button>

                </form>
            </div>
        </div>

    </div><!-- /pos-right -->

</div><!-- /pos-layout -->


<!-- ===== Modal Hủy Đơn ===== -->
<div class="modal fade" id="cancelModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" style="max-width:420px;">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-danger text-white py-3">
                <h6 class="modal-title fw-bold mb-0">
                    <i class="fas fa-ban me-2"></i>Xác nhận hủy đơn hàng
                </h6>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="bg-light rounded p-3 mb-3">
                    <div class="small text-muted mb-1">Đơn hàng sẽ bị hủy</div>
                    <div class="d-flex justify-content-between">
                        <span class="small">
                            <c:choose>
                                <c:when test="${not empty sessionScope.saleCart}">
                                    <strong>${sessionScope.saleCart.size()}</strong> sản phẩm
                                </c:when>
                                <c:otherwise>0 sản phẩm</c:otherwise>
                            </c:choose>
                        </span>
                        <span class="small fw-bold text-danger">
                            <fmt:formatNumber value="${cartTotal}" type="number" groupingUsed="true"/>đ
                        </span>
                    </div>
                </div>
                <div class="alert alert-warning py-2 small mb-3">
                    <i class="fas fa-info-circle me-1"></i>
                    Sau khi hủy, tất cả sản phẩm sẽ về danh sách tồn kho. Hành động này <strong>không thể hoàn tác</strong>.
                </div>
                <label class="form-label small fw-semibold">Lý do hủy <span class="text-danger">*</span></label>
                <div class="d-flex flex-column gap-2 mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason" id="r1" value="Khách đổi ý, không mua nữa" checked>
                        <label class="form-check-label small" for="r1">Khách đổi ý, không mua nữa</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason" id="r2" value="Chọn nhầm sản phẩm">
                        <label class="form-check-label small" for="r2">Chọn nhầm sản phẩm</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason" id="r3" value="Khách không đủ tiền thanh toán">
                        <label class="form-check-label small" for="r3">Khách không đủ tiền thanh toán</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason" id="r4" value="other">
                        <label class="form-check-label small" for="r4">Lý do khác...</label>
                    </div>
                </div>
                <div id="otherReasonBox" style="display:none;">
                    <textarea class="form-control form-control-sm" id="otherReasonText"
                              rows="2" placeholder="Nhập lý do hủy..."></textarea>
                </div>
            </div>
            <div class="modal-footer py-2 gap-2">
                <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">
                    <i class="fas fa-arrow-left me-1"></i>Quay lại
                </button>
                <button type="button" class="btn btn-danger btn-sm" id="confirmCancelBtn">
                    <i class="fas fa-ban me-1"></i>Xác nhận hủy
                </button>
            </div>
        </div>
    </div>
</div>

<script>
<%-- Ghi chú: fn:contains đã được khai báo qua taglib functions ở đầu file --%>

// Chuyển đổi giữa tab "Giỏ hàng" và "Khách hàng"
function switchTab(tab) {
    var isCart = tab === 'cart';
    document.getElementById('tabCart').classList.toggle('active', isCart);
    document.getElementById('tabCustomer').classList.toggle('active', !isCart);
    document.getElementById('panelCart').style.display = isCart ? 'flex' : 'none';
    document.getElementById('panelCustomer').style.display = isCart ? 'none' : 'flex';
}

// Khi đổi danh mục thì reset model và tự động submit form tìm kiếm
document.getElementById('categorySelect').addEventListener('change', function() {
    document.getElementById('modelSelect').value = '';
    this.closest('form').submit();
});

// Xử lý modal hủy đơn hàng: hiện ô nhập khi chọn "Lý do khác"
document.querySelectorAll('input[name="cancelReason"]').forEach(function(r) {
    r.addEventListener('change', function() {
        document.getElementById('otherReasonBox').style.display =
            this.value === 'other' ? 'block' : 'none';
    });
});

document.getElementById('confirmCancelBtn').addEventListener('click', function() {
    var sel = document.querySelector('input[name="cancelReason"]:checked');
    var reason = sel ? sel.value : '';
    if (reason === 'other') {
        reason = document.getElementById('otherReasonText').value.trim();
        if (!reason) {
            document.getElementById('otherReasonText').focus();
            document.getElementById('otherReasonText').classList.add('is-invalid');
            return;
        }
    }
    window.location.href = '${pageContext.request.contextPath}/cart?action=cancel&reason=' + encodeURIComponent(reason);
});

// Tự động ẩn thông báo flash sau 3.5 giây
var flashMsg = document.getElementById('flashMsg');
if (flashMsg) {
    setTimeout(function() {
        flashMsg.style.transition = 'opacity .4s';
        flashMsg.style.opacity = '0';
        setTimeout(function() { flashMsg.remove(); }, 400);
    }, 3500);
}
</script>

<%@ include file="../common/footer.jsp" %>
