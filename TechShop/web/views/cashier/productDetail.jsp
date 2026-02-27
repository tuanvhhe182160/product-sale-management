<%-- 
    Document   : productDetail
    Created on : Feb 26, 2026, 11:41:42 AM
    Author     : Admin
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Chi tiết sản phẩm - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    /* ── Layout ── */
    .detail-card {
        border: 1px solid #e9ecef;
        border-radius: 12px;
        background: #fff;
        overflow: hidden;
        box-shadow: 0 2px 12px rgba(0,0,0,.06);
    }
    .detail-img-wrap {
        background: #f8f9fa;
        border-bottom: 1px solid #f0f0f0;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 280px;
        padding: 20px;
    }
    .detail-img-wrap img {
        max-height: 100%;
        max-width: 100%;
        object-fit: contain;
    }
    .no-img-lg {
        font-size: 5rem;
        color: #ced4da;
    }

    /* ── Badges ── */
    .badge-cat  { font-size:.75rem; background:#f3e8ff; color:#7c3aed; padding:3px 10px; border-radius:20px; font-weight:600; }
    .badge-sku  { font-size:.75rem; font-family:monospace; background:#e8f0fe; color:#1a56db; padding:3px 10px; border-radius:20px; font-weight:600; }
    .badge-imei { font-size:.8rem;  font-family:monospace; background:#f0fdf4; color:#15803d; padding:4px 12px; border-radius:6px; font-weight:700; letter-spacing:.5px; }

    /* ── Status badge ── */
    .status-IN_STOCK   { background:#d1fae5; color:#065f46; }
    .status-SOLD       { background:#fee2e2; color:#991b1b; }
    .status-RESERVED   { background:#fef3c7; color:#92400e; }
    .status-DEFECTIVE  { background:#fce7f3; color:#9d174d; }
    .status-IN_TRANSFER{ background:#e0f2fe; color:#0c4a6e; }
    .status-WARRANTY   { background:#f3e8ff; color:#6b21a8; }
    .status-badge { font-size:.8rem; padding:4px 12px; border-radius:20px; font-weight:700; display:inline-block; }

    /* ── Section headers ── */
    .section-title {
        font-size:.8rem;
        font-weight:700;
        text-transform:uppercase;
        letter-spacing:.08em;
        color:#9ca3af;
        margin-bottom:12px;
        padding-bottom:6px;
        border-bottom:1px solid #f0f0f0;
    }

    /* ── Info grid ── */
    .info-row {
        display:flex;
        justify-content:space-between;
        align-items:flex-start;
        padding:7px 0;
        border-bottom:1px solid #f9fafb;
        font-size:.88rem;
        gap:12px;
    }
    .info-row:last-child { border-bottom:none; }
    .info-label { color:#6b7280; min-width:130px; flex-shrink:0; }
    .info-value { color:#111827; font-weight:500; text-align:right; word-break:break-all; }

    /* ── Attributes chips ── */
    .attr-chip {
        display:inline-flex;
        align-items:center;
        gap:6px;
        background:#f8fafc;
        border:1px solid #e2e8f0;
        border-radius:8px;
        padding:6px 12px;
        font-size:.83rem;
    }
    .attr-chip .attr-key { color:#64748b; font-weight:500; }
    .attr-chip .attr-val { color:#0f172a; font-weight:700; }

    /* ── Timeline ── */
    .timeline { position:relative; padding-left:20px; }
    .timeline::before {
        content:'';
        position:absolute;
        left:7px; top:8px; bottom:8px;
        width:2px;
        background:#e5e7eb;
        border-radius:2px;
    }
    .timeline-item { position:relative; padding:0 0 18px 20px; }
    .timeline-item:last-child { padding-bottom:0; }
    .timeline-dot {
        position:absolute;
        left:-13px; top:4px;
        width:14px; height:14px;
        border-radius:50%;
        border:2px solid #fff;
        box-shadow:0 0 0 2px #d1d5db;
    }
    .ttype-IMPORT      .timeline-dot { background:#10b981; box-shadow:0 0 0 2px #10b981; }
    .ttype-TRANSFER_IN .timeline-dot { background:#3b82f6; box-shadow:0 0 0 2px #3b82f6; }
    .ttype-TRANSFER_OUT .timeline-dot { background:#f59e0b; box-shadow:0 0 0 2px #f59e0b; }
    .ttype-SALE        .timeline-dot { background:#ef4444; box-shadow:0 0 0 2px #ef4444; }
    .ttype-RETURN      .timeline-dot { background:#8b5cf6; box-shadow:0 0 0 2px #8b5cf6; }
    .ttype-ADJUST      .timeline-dot { background:#6b7280; box-shadow:0 0 0 2px #6b7280; }
    .timeline-date { font-size:.75rem; color:#9ca3af; }
    .timeline-main { font-size:.87rem; font-weight:600; color:#111827; }
    .timeline-sub  { font-size:.78rem; color:#6b7280; margin-top:2px; }

    .type-label {
        font-size:.72rem; font-weight:700; padding:2px 8px; border-radius:4px; display:inline-block;
    }
    .tl-IMPORT       { background:#d1fae5; color:#065f46; }
    .tl-TRANSFER_IN  { background:#dbeafe; color:#1e40af; }
    .tl-TRANSFER_OUT { background:#fef3c7; color:#92400e; }
    .tl-SALE         { background:#fee2e2; color:#991b1b; }
    .tl-RETURN       { background:#f3e8ff; color:#6b21a8; }
    .tl-ADJUST       { background:#f3f4f6; color:#374151; }

    /* ── Warranty ── */
    .warranty-card {
        border:1px solid #e5e7eb;
        border-radius:8px;
        padding:14px 16px;
        margin-bottom:10px;
        background:#fafafa;
    }
    .warranty-card:last-child { margin-bottom:0; }
    .ws-PENDING     { border-left:3px solid #f59e0b; }
    .ws-IN_PROGRESS { border-left:3px solid #3b82f6; }
    .ws-COMPLETED   { border-left:3px solid #10b981; }
    .ws-REJECTED    { border-left:3px solid #ef4444; }
    .ws-CANCELLED   { border-left:3px solid #9ca3af; }

    .w-status-badge { font-size:.72rem; font-weight:700; padding:2px 8px; border-radius:10px; display:inline-block; }
    .ws-b-PENDING     { background:#fef3c7; color:#92400e; }
    .ws-b-IN_PROGRESS { background:#dbeafe; color:#1e40af; }
    .ws-b-COMPLETED   { background:#d1fae5; color:#065f46; }
    .ws-b-REJECTED    { background:#fee2e2; color:#991b1b; }
    .ws-b-CANCELLED   { background:#f3f4f6; color:#374151; }

    /* ── Price ── */
    .price-big { font-size:1.6rem; font-weight:800; color:#0d6efd; }
    .warranty-info { font-size:.85rem; color:#6b7280; }
</style>

<!-- ===== Header ===== -->
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
    <a href="${pageContext.request.contextPath}/cart"
       class="btn btn-outline-primary position-relative">
        <i class="fas fa-shopping-cart me-2"></i>Giỏ hàng
        <c:if test="${not empty sessionScope.saleCart && sessionScope.saleCart.size() > 0}">
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                ${sessionScope.saleCart.size()}
            </span>
        </c:if>
    </a>
</div>

<div class="row g-4">

    <!-- ===== CỘT TRÁI: Ảnh + Thông tin chính ===== -->
    <div class="col-lg-4">

        <!-- Ảnh -->
        <div class="detail-card mb-4">
            <div class="detail-img-wrap">
                <c:choose>
                    <c:when test="${not empty item.imageUrl}">
                        <img src="${item.imageUrl}"
                             alt="${item.variantName}"
                             onerror="this.style.display='none';this.nextElementSibling.style.display='flex';" />
                        <div class="no-img-lg" style="display:none;">
                            <i class="fas fa-image"></i>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="no-img-lg"><i class="fas fa-image"></i></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="p-3">
                <div class="d-flex gap-2 flex-wrap mb-2">
                    <span class="badge-cat">${item.categoryName}</span>
                    <span class="badge-sku">${item.sku}</span>
                </div>
                <div class="fw-bold fs-6 mb-1">${item.variantName}</div>
                <div class="text-muted small mb-3">${item.brand} · ${item.modelName}</div>
                <div class="price-big mb-1">
                    <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/>đ
                </div>
                <div class="warranty-info">
                    <i class="fas fa-shield-alt me-1 text-success"></i>Bảo hành ${item.warrantyMonths} tháng
                </div>
            </div>
        </div>

        <!-- Thông tin máy vật lý -->
        <div class="detail-card p-3">
            <div class="section-title"><i class="fas fa-barcode me-1"></i>Thông tin thiết bị</div>

            <div class="mb-3">
                <div class="text-muted small mb-1">IMEI</div>
                <span class="badge-imei">${item.imei}</span>
            </div>

            <div class="info-row">
                <span class="info-label">Serial Number</span>
                <span class="info-value" style="font-family:monospace;">
                    <c:choose>
                        <c:when test="${not empty item.serialNumber}">${item.serialNumber}</c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="info-row">
                <span class="info-label">Tình trạng</span>
                <span class="info-value">
                    <span class="status-badge status-${item.status}">
                        <c:choose>
                            <c:when test="${item.status == 'IN_STOCK'}">Còn hàng</c:when>
                            <c:when test="${item.status == 'SOLD'}">Đã bán</c:when>
                            <c:when test="${item.status == 'RESERVED'}">Đã đặt</c:when>
                            <c:when test="${item.status == 'DEFECTIVE'}">Lỗi</c:when>
                            <c:when test="${item.status == 'IN_TRANSFER'}">Đang chuyển</c:when>
                            <c:when test="${item.status == 'WARRANTY'}">Bảo hành</c:when>
                            <c:otherwise>${item.status}</c:otherwise>
                        </c:choose>
                    </span>
                </span>
            </div>
            <div class="info-row">
                <span class="info-label">Chi nhánh</span>
                <span class="info-value">${item.branchName}</span>
            </div>
            <div class="info-row">
                <span class="info-label">Ngày nhập kho</span>
                <span class="info-value">
                    <c:choose>
                        <c:when test="${not empty item.importDateStr}">${item.importDateStr}</c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="info-row">
                <span class="info-label">Ngày bán</span>
                <span class="info-value">
                    <c:choose>
                        <c:when test="${not empty item.saleDateStr}">${item.saleDateStr}</c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </span>
            </div>

            <!-- Nút thêm vào giỏ (chỉ hiện nếu còn hàng) -->
            <c:if test="${item.status == 'IN_STOCK'}">
                <a href="${pageContext.request.contextPath}/cart?action=add&physicalId=${item.physicalId}&redirect=${pageContext.request.contextPath}/cashier?keyword=${backKeyword}%26categoryId=${backCategoryId}%26modelId=${backModelId}%26sku=${backSku}%26page=${backPage}"
                   class="btn btn-success w-100 mt-3">
                    <i class="fas fa-cart-plus me-2"></i>Thêm vào giỏ hàng
                </a>
            </c:if>
        </div>

    </div>

    <!-- ===== CỘT PHẢI: Thông số + Lịch sử ===== -->
    <div class="col-lg-8 d-flex flex-column gap-4">

        <!-- Mô tả model -->
        <c:if test="${not empty item.modelDesc}">
            <div class="detail-card p-3">
                <div class="section-title"><i class="fas fa-align-left me-1"></i>Mô tả sản phẩm</div>
                <p class="mb-0 text-secondary" style="font-size:.88rem;line-height:1.7;">${item.modelDesc}</p>
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

        <!-- Lịch sử giao dịch kho -->
        <div class="detail-card p-3">
            <div class="section-title"><i class="fas fa-history me-1"></i>Lịch sử giao dịch kho</div>
            <c:choose>
                <c:when test="${empty inventoryHistory}">
                    <p class="text-muted small mb-0">Chưa có giao dịch nào.</p>
                </c:when>
                <c:otherwise>
                    <div class="timeline">
                        <c:forEach var="t" items="${inventoryHistory}">
                            <div class="timeline-item ttype-${t.type}">
                                <div class="timeline-dot"></div>
                                <div class="timeline-date">${t.date}</div>
                                <div class="timeline-main d-flex align-items-center gap-2 flex-wrap">
                                    <span class="type-label tl-${t.type}">${t.type}</span>
                                    <c:if test="${not empty t.fromBranch}">
                                        <span class="text-muted small">${t.fromBranch}</span>
                                        <i class="fas fa-arrow-right text-muted small"></i>
                                    </c:if>
                                    <c:if test="${not empty t.toBranch}">
                                        <span class="text-muted small">${t.toBranch}</span>
                                    </c:if>
                                </div>
                                <div class="timeline-sub">
                                    Bởi: ${t.performedBy}
                                    <c:if test="${not empty t.note}"> · ${t.note}</c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Lịch sử bảo hành -->
        <div class="detail-card p-3">
            <div class="section-title"><i class="fas fa-tools me-1"></i>Lịch sử bảo hành</div>
            <c:choose>
                <c:when test="${empty warrantyHistory}">
                    <p class="text-muted small mb-0 text-success">
                        <i class="fas fa-check-circle me-1"></i>Máy chưa có lịch sử bảo hành.
                    </p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="w" items="${warrantyHistory}">
                        <div class="warranty-card ws-${w.status}">
                            <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                                <span class="fw-600 small" style="font-family:monospace;">${w.code}</span>
                                <span class="w-status-badge ws-b-${w.status}">${w.status}</span>
                            </div>
                            <div class="info-row" style="border:none;padding:3px 0;">
                                <span class="info-label">Ngày tiếp nhận</span>
                                <span class="info-value">${w.requestDate}</span>
                            </div>
                            <c:if test="${w.completionDate != '—'}">
                            <div class="info-row" style="border:none;padding:3px 0;">
                                <span class="info-label">Ngày hoàn thành</span>
                                <span class="info-value">${w.completionDate}</span>
                            </div>
                            </c:if>
                            <div class="mt-2">
                                <div class="text-muted small mb-1">Mô tả lỗi</div>
                                <div class="small">${w.issue}</div>
                            </div>
                            <c:if test="${not empty w.resolution}">
                            <div class="mt-2">
                                <div class="text-muted small mb-1">Kết quả xử lý</div>
                                <div class="small text-success">${w.resolution}</div>
                            </div>
                            </c:if>
                            <div class="mt-2 text-muted small">
                                CS: ${w.csName}
                                <c:if test="${not empty w.techName}"> · KT: ${w.techName}</c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</div>

<%@ include file="../common/footer.jsp" %>
