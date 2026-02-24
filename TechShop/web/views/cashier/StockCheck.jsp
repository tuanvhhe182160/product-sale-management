<%--
    Document   : stockCheck
    Author     : Cashier Module
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Stock Check - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    .product-img-box {
        width: 110px;
        height: 110px;
        background: #f8f9fa;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
        border: 1px solid #e9ecef;
        flex-shrink: 0;
    }
    .product-img-box img {
        width: 100%;
        height: 100%;
        object-fit: contain;
        padding: 8px;
    }
    .product-img-box .no-img {
        font-size: 2.5rem;
        color: #ced4da;
    }

    .stat-box {
        border-radius: 10px;
        padding: 14px 18px;
        text-align: center;
        border: 1px solid transparent;
    }
    .stat-box .num  { font-size: 1.8rem; font-weight: 800; line-height: 1; }
    .stat-box .lbl  { font-size: .78rem; margin-top: 4px; }

    .stat-instock  { background: #d1fae5; border-color: #6ee7b7; color: #065f46; }
    .stat-sold     { background: #e0e7ff; border-color: #a5b4fc; color: #3730a3; }
    .stat-reserved { background: #fef3c7; border-color: #fcd34d; color: #92400e; }
    .stat-defective{ background: #fee2e2; border-color: #fca5a5; color: #991b1b; }

    .badge-imei {
        font-family: monospace;
        font-size: .8rem;
        background: #f1f5f9;
        color: #334155;
        padding: 2px 8px;
        border-radius: 4px;
        font-weight: 600;
    }
    .badge-serial {
        font-family: monospace;
        font-size: .78rem;
        color: #6c757d;
    }

    table thead th {
        font-size: .78rem;
        text-transform: uppercase;
        letter-spacing: .5px;
        color: #6c757d;
        border-bottom: 2px solid #dee2e6;
        white-space: nowrap;
    }
    tbody tr:hover { background: rgba(13,110,253,.04); }

    .status-pill {
        display: inline-block;
        font-size: .75rem;
        font-weight: 700;
        padding: 3px 10px;
        border-radius: 20px;
    }
    .pill-instock   { background: #d1fae5; color: #065f46; }
    .pill-sold      { background: #e0e7ff; color: #3730a3; }
    .pill-reserved  { background: #fef3c7; color: #92400e; }
    .pill-defective { background: #fee2e2; color: #991b1b; }
    .pill-other     { background: #f1f5f9; color: #475569; }

    .filter-btn { font-size: .82rem; }
    .filter-btn.active { font-weight: 700; }
</style>

<!-- ===== Back button + title ===== -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h4 class="mb-1 fw-bold">
            <i class="fas fa-boxes text-primary me-2"></i>Stock Check
        </h4>
        <span class="text-muted small">Chi tiết tồn kho tại chi nhánh</span>
    </div>
    <a href="${pageContext.request.contextPath}/cashier?keyword=${backKeyword}&categoryId=${backCategoryId}&modelId=${backModelId}&sku=${backSku}&page=${backPage}"
       class="btn btn-outline-secondary btn-sm">
        <i class="fas fa-arrow-left me-1"></i> Quay lại
    </a>
</div>

<!-- ===== Product Info Card ===== -->
<div class="card border-0 shadow-sm mb-4">
    <div class="card-body">
        <div class="d-flex gap-4 flex-wrap align-items-start">

            <!-- Image -->
            <div class="product-img-box">
                <c:choose>
                    <c:when test="${not empty variant.imageUrl}">
                        <img src="${pageContext.request.contextPath}/${variant.imageUrl}"
                             alt="${variant.variantName}"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';" />
                        <div class="no-img" style="display:none;">
                            <i class="fas fa-image"></i>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="no-img"><i class="fas fa-image"></i></div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Info -->
            <div class="flex-grow-1">
                <div class="text-muted small mb-1">${variant.categoryName}</div>
                <h5 class="fw-bold mb-1">${variant.variantName}</h5>
                <div class="text-muted small mb-2">
                    ${variant.brand}
                    <c:if test="${not empty variant.brand && not empty variant.modelName}"> · </c:if>
                    ${variant.modelName}
                </div>
                <div class="d-flex flex-wrap gap-2 align-items-center">
                    <span style="font-family:monospace;font-size:.82rem;background:#e8f0fe;color:#1a56db;padding:3px 9px;border-radius:4px;font-weight:600;">
                        ${variant.sku}
                    </span>
                    <span class="text-muted small">BH ${variant.warrantyMonths} tháng</span>
                    <span class="fw-bold text-primary">
                        <fmt:formatNumber value="${variant.basePrice}" type="number" groupingUsed="true"/>đ
                    </span>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- ===== Stock Summary ===== -->
<div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
        <div class="stat-box stat-instock">
            <div class="num">${cntInStock}</div>
            <div class="lbl"><i class="fas fa-check-circle me-1"></i>Còn hàng</div>
        </div>
    </div>
    <div class="col-6 col-md-3">
        <div class="stat-box stat-reserved">
            <div class="num">${cntReserved}</div>
            <div class="lbl"><i class="fas fa-clock me-1"></i>Đã giữ chỗ</div>
        </div>
    </div>
    <div class="col-6 col-md-3">
        <div class="stat-box stat-sold">
            <div class="num">${cntSold}</div>
            <div class="lbl"><i class="fas fa-shopping-cart me-1"></i>Đã bán</div>
        </div>
    </div>
    <div class="col-6 col-md-3">
        <div class="stat-box stat-defective">
            <div class="num">${cntDefective}</div>
            <div class="lbl"><i class="fas fa-tools me-1"></i>Lỗi / Bảo hành</div>
        </div>
    </div>
</div>

<!-- ===== Filter tabs + Table ===== -->
<div class="card border-0 shadow-sm">

    <!-- Filter bar -->
    <div class="card-header bg-white py-2 d-flex align-items-center gap-2 flex-wrap">
        <span class="text-muted small me-1">Lọc theo trạng thái:</span>

        <c:set var="baseUrl"
               value="${pageContext.request.contextPath}/stock-check?variantId=${variant.variantId}&keyword=${backKeyword}&categoryId=${backCategoryId}&modelId=${backModelId}&sku=${backSku}&page=${backPage}" />

        <a href="${baseUrl}&statusFilter=ALL"
           class="btn btn-sm filter-btn ${statusFilter == 'ALL' ? 'btn-secondary active' : 'btn-outline-secondary'}">
            Tất cả (${cntInStock + cntReserved + cntSold + cntDefective})
        </a>
        <a href="${baseUrl}&statusFilter=IN_STOCK"
           class="btn btn-sm filter-btn ${statusFilter == 'IN_STOCK' ? 'btn-success active' : 'btn-outline-success'}">
            <i class="fas fa-check-circle me-1"></i>Còn hàng (${cntInStock})
        </a>
        <a href="${baseUrl}&statusFilter=RESERVED"
           class="btn btn-sm filter-btn ${statusFilter == 'RESERVED' ? 'btn-warning active' : 'btn-outline-warning'}">
            <i class="fas fa-clock me-1"></i>Giữ chỗ (${cntReserved})
        </a>
        <a href="${baseUrl}&statusFilter=SOLD"
           class="btn btn-sm filter-btn ${statusFilter == 'SOLD' ? 'btn-primary active' : 'btn-outline-primary'}">
            <i class="fas fa-shopping-cart me-1"></i>Đã bán (${cntSold})
        </a>
        <a href="${baseUrl}&statusFilter=DEFECTIVE"
           class="btn btn-sm filter-btn ${statusFilter == 'DEFECTIVE' ? 'btn-danger active' : 'btn-outline-danger'}">
            <i class="fas fa-tools me-1"></i>Lỗi (${cntDefective})
        </a>
    </div>

    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty physicalList}">
                <div class="text-center py-5 text-muted">
                    <i class="fas fa-box-open fa-2x mb-3 d-block"></i>
                    <p class="mb-0">Không có sản phẩm nào với trạng thái này.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead class="bg-light">
                            <tr>
                                <th class="ps-3" style="width:50px">#</th>
                                <th>IMEI</th>
                                <th style="width:160px">Serial Number</th>
                                <th style="width:120px">Trạng thái</th>
                                <th style="width:160px">Ngày nhập</th>
                                <th style="width:160px">Ngày bán</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${physicalList}" var="p" varStatus="st">
                                <tr>
                                    <td class="text-muted ps-3">${st.index + 1}</td>

                                    <td>
                                        <span class="badge-imei">${p.imei}</span>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty p.serialNumber}">
                                                <span class="badge-serial">${p.serialNumber}</span>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${p.status == 'IN_STOCK'}">
                                                <span class="status-pill pill-instock">
                                                    <i class="fas fa-check-circle me-1"></i>Còn hàng
                                                </span>
                                            </c:when>
                                            <c:when test="${p.status == 'SOLD'}">
                                                <span class="status-pill pill-sold">
                                                    <i class="fas fa-shopping-cart me-1"></i>Đã bán
                                                </span>
                                            </c:when>
                                            <c:when test="${p.status == 'RESERVED'}">
                                                <span class="status-pill pill-reserved">
                                                    <i class="fas fa-clock me-1"></i>Giữ chỗ
                                                </span>
                                            </c:when>
                                            <c:when test="${p.status == 'DEFECTIVE'}">
                                                <span class="status-pill pill-defective">
                                                    <i class="fas fa-tools me-1"></i>Lỗi
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-pill pill-other">${p.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-muted small">
                                        <c:choose>
                                            <c:when test="${p.importDate != null}">
                                                <fmt:formatDate value="${p.importDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="text-muted small">
                                        <c:choose>
                                            <c:when test="${p.saleDate != null}">
                                                <fmt:formatDate value="${p.saleDate}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:when>
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

<%@ include file="../common/footer.jsp" %>
