<%-- Chi tiết sản phẩm (Variant) + danh sách PhysicalProduct --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Chi tiết sản phẩm - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
.detail-card { border:1px solid #e9ecef; border-radius:12px; background:#fff; overflow:hidden; box-shadow:0 2px 12px rgba(0,0,0,.06); }
.detail-img-wrap { background:#f8f9fa; border-bottom:1px solid #f0f0f0; display:flex; align-items:center; justify-content:center; height:280px; padding:20px; }
.detail-img-wrap img { max-height:100%; max-width:100%; object-fit:contain; }
.no-img-lg { font-size:5rem; color:#ced4da; }
.badge-cat { font-size:.75rem; background:#f3e8ff; color:#7c3aed; padding:3px 10px; border-radius:20px; font-weight:600; }
.badge-sku { font-size:.75rem; font-family:monospace; background:#e8f0fe; color:#1a56db; padding:3px 10px; border-radius:20px; font-weight:600; }
.section-title { font-size:.8rem; font-weight:700; text-transform:uppercase; letter-spacing:.08em; color:#9ca3af; margin-bottom:12px; padding-bottom:6px; border-bottom:1px solid #f0f0f0; }
.info-row { display:flex; justify-content:space-between; align-items:flex-start; padding:7px 0; border-bottom:1px solid #f9fafb; font-size:.88rem; gap:12px; }
.info-row:last-child { border-bottom:none; }
.info-label { color:#6b7280; min-width:130px; flex-shrink:0; }
.info-value { color:#111827; font-weight:500; text-align:right; word-break:break-all; }
.attr-chip { display:inline-flex; align-items:center; gap:6px; background:#f8fafc; border:1px solid #e2e8f0; border-radius:8px; padding:6px 12px; font-size:.83rem; }
.attr-chip .attr-key { color:#64748b; font-weight:500; }
.attr-chip .attr-val { color:#0f172a; font-weight:700; }
.price-big { font-size:1.6rem; font-weight:800; color:#0d6efd; }
.warranty-info { font-size:.85rem; color:#6b7280; }
.status-IN_STOCK { background:#d1fae5; color:#065f46; }
.status-SOLD { background:#fee2e2; color:#991b1b; }
.status-RESERVED { background:#fef3c7; color:#92400e; }
.status-DEFECTIVE { background:#fce7f3; color:#9d174d; }
.status-IN_TRANSFER { background:#e0f2fe; color:#0c4a6e; }
.status-WARRANTY { background:#f3e8ff; color:#6b21a8; }
.status-badge { font-size:.75rem; padding:3px 10px; border-radius:20px; font-weight:700; display:inline-block; }
.pp-table th { font-size:.78rem; font-weight:600; color:#6b7280; text-transform:uppercase; letter-spacing:.05em; }
.pp-table td { font-size:.85rem; vertical-align:middle; }
.imei-mono { font-family:monospace; font-size:.82rem; font-weight:600; color:#1a56db; }
</style>

<!-- Header -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <a href="${pageContext.request.contextPath}/cashier?keyword=${backKeyword}&categoryId=${backCategoryId}&modelId=${backModelId}&sku=${backSku}&page=${backPage}"
           class="btn btn-sm btn-outline-secondary mb-2">
            <i class="fas fa-arrow-left me-1"></i>Quay lại
        </a>
        <h4 class="mb-0 fw-bold">
            <i class="fas fa-info-circle text-primary me-2"></i>Chi tiết sản phẩm
        </h4>
    </div>
</div>

<div class="row g-4">

    <!-- CỘT TRÁI: Ảnh + Thông tin variant -->
    <div class="col-lg-4">
        <div class="detail-card mb-4">
            <div class="detail-img-wrap">
                <c:choose>
                    <c:when test="${not empty variant.imageUrl}">
                        <img src="${variant.imageUrl}" alt="${variant.variantName}"
                             onerror="this.style.display='none';this.nextElementSibling.style.display='flex';" />
                        <div class="no-img-lg" style="display:none;"><i class="fas fa-image"></i></div>
                    </c:when>
                    <c:otherwise>
                        <div class="no-img-lg"><i class="fas fa-image"></i></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="p-3">
                <div class="d-flex gap-2 flex-wrap mb-2">
                    <span class="badge-cat">${variant.categoryName}</span>
                    <span class="badge-sku">${variant.sku}</span>
                </div>
                <div class="fw-bold fs-6 mb-1">${variant.variantName}</div>
                <div class="text-muted small mb-3">${variant.brand} · ${variant.modelName}</div>
                <div class="price-big mb-1">
                    <fmt:formatNumber value="${variant.unitPrice}" type="number" groupingUsed="true"/>đ
                </div>
                <div class="warranty-info">
                    <i class="fas fa-shield-alt me-1 text-success"></i>Bảo hành ${variant.warrantyMonths} tháng
                </div>
            </div>
        </div>

        <!-- Thông tin phân cấp: Category → Model → Variant -->
        <div class="detail-card p-3">
            <div class="section-title"><i class="fas fa-sitemap me-1"></i>Phân cấp sản phẩm</div>
            <div class="info-row">
                <span class="info-label">Danh mục</span>
                <span class="info-value">${variant.categoryName}</span>
            </div>
            <div class="info-row">
                <span class="info-label">Model</span>
                <span class="info-value">${variant.brand} ${variant.modelName}</span>
            </div>
            <div class="info-row">
                <span class="info-label">Phiên bản</span>
                <span class="info-value">${variant.variantName}</span>
            </div>
            <div class="info-row">
                <span class="info-label">SKU</span>
                <span class="info-value" style="font-family:monospace;">${variant.sku}</span>
            </div>
            <div class="info-row">
                <span class="info-label">Trạng thái</span>
                <span class="info-value">
                    <c:choose>
                        <c:when test="${variant.status == 'ACTIVE'}">
                            <span class="status-badge status-IN_STOCK">Đang bán</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-badge status-SOLD">Ngừng bán</span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </div>

    <!-- CỘT PHẢI: Thông số + Danh sách PhysicalProduct -->
    <div class="col-lg-8 d-flex flex-column gap-4">

        <!-- Mô tả model -->
        <c:if test="${not empty variant.modelDesc}">
            <div class="detail-card p-3">
                <div class="section-title"><i class="fas fa-align-left me-1"></i>Mô tả sản phẩm</div>
                <p class="mb-0 text-secondary" style="font-size:.88rem;line-height:1.7;">${variant.modelDesc}</p>
            </div>
        </c:if>

        <!-- Thông số kỹ thuật -->
        <c:if test="${not empty attributes}">
            <div class="detail-card p-3">
                <div class="section-title"><i class="fas fa-microchip me-1"></i>Thông số kỹ thuật</div>
                <div class="d-flex flex-wrap gap-2">
                    <c:forEach var="attr" items="${attributes}">
                        <div class="attr-chip">
                            <span class="attr-key">${attr.key}:</span>
                            <span class="attr-val">${attr.value}</span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <!-- Danh sách PhysicalProduct (IMEI) -->
        <div class="detail-card p-3">
            <div class="section-title">
                <i class="fas fa-barcode me-1"></i>Danh sách máy vật lý (${physicalProducts.size()} máy)
            </div>
            <c:choose>
                <c:when test="${empty physicalProducts}">
                    <p class="text-muted small mb-0">Không có máy nào tại chi nhánh này.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover pp-table mb-0">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>IMEI</th>
                                    <th>Serial Number</th>
                                    <th>Trạng thái</th>
                                    <th>Chi nhánh</th>
                                    <th>Ngày nhập</th>
                                    <th>Ngày bán</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="pp" items="${physicalProducts}" varStatus="st">
                                    <tr>
                                        <td>${st.index + 1}</td>
                                        <td><span class="imei-mono">${pp.imei}</span></td>
                                        <td style="font-family:monospace;font-size:.82rem;">
                                            <c:choose>
                                                <c:when test="${not empty pp.serialNumber}">${pp.serialNumber}</c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="status-badge status-${pp.status}">
                                                <c:choose>
                                                    <c:when test="${pp.status == 'IN_STOCK'}">Còn hàng</c:when>
                                                    <c:when test="${pp.status == 'SOLD'}">Đã bán</c:when>
                                                    <c:when test="${pp.status == 'RESERVED'}">Đã đặt</c:when>
                                                    <c:when test="${pp.status == 'DEFECTIVE'}">Lỗi</c:when>
                                                    <c:when test="${pp.status == 'IN_TRANSFER'}">Đang chuyển</c:when>
                                                    <c:when test="${pp.status == 'WARRANTY'}">Bảo hành</c:when>
                                                    <c:otherwise>${pp.status}</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td>${pp.branchName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty pp.importDateStr}">${pp.importDateStr}</c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty pp.saleDateStr}">${pp.saleDateStr}</c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
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

<%@ include file="../common/footer.jsp" %>
