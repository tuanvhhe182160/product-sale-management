<%-- 
    Document   : cart
    Created on : Feb 25, 2026, 7:11:53 PM
    Author     : Admin
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Giỏ hàng - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    table thead th {
        font-size: .78rem;
        text-transform: uppercase;
        letter-spacing: .5px;
        color: #6c757d;
        border-bottom: 2px solid #dee2e6;
        white-space: nowrap;
    }
    tbody tr:hover { background: rgba(13,110,253,.04); }

    .badge-imei {
        font-family: monospace;
        font-size: .82rem;
        background: #e8f0fe;
        color: #1a56db;
        padding: 3px 9px;
        border-radius: 4px;
        font-weight: 600;
    }
    .badge-sku {
        font-family: monospace;
        font-size: .75rem;
        background: #f1f5f9;
        color: #475569;
        padding: 2px 7px;
        border-radius: 4px;
    }
    .total-row td {
        font-size: 1.05rem;
        font-weight: 700;
        border-top: 2px solid #dee2e6;
    }
    .product-thumb {
        width: 44px; height: 44px;
        object-fit: contain;
        border-radius: 6px;
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        padding: 3px;
    }
    .no-thumb {
        width: 44px; height: 44px;
        border-radius: 6px;
        background: #f1f5f9;
        border: 1px solid #e9ecef;
        display: flex; align-items: center; justify-content: center;
        color: #ced4da; font-size: 1.1rem;
    }
</style>

<!-- ===== Header ===== -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h4 class="mb-1 fw-bold">
            <i class="fas fa-shopping-cart text-primary me-2"></i>Giỏ hàng
        </h4>
        <span class="text-muted small">
            <strong>${cart.size()}</strong> sản phẩm đã chọn
        </span>
    </div>
    <a href="${pageContext.request.contextPath}/cashier"
       class="btn btn-outline-secondary btn-sm">
        <i class="fas fa-plus me-1"></i>Thêm sản phẩm
    </a>
</div>

<!-- Flash messages -->
<c:if test="${not empty cartSuccess}">
    <div class="alert alert-success alert-dismissible fade show py-2" role="alert">
        <i class="fas fa-check-circle me-2"></i>${cartSuccess}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>
<c:if test="${not empty cartError}">
    <div class="alert alert-danger alert-dismissible fade show py-2" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i>${cartError}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:choose>
    <c:when test="${empty cart}">
        <!-- Giỏ trống -->
        <div class="card border-0 shadow-sm">
            <div class="card-body text-center py-5 text-muted">
                <i class="fas fa-shopping-cart fa-2x mb-3 d-block"></i>
                <p class="mb-3">Giỏ hàng trống. Hãy tìm và chọn sản phẩm.</p>
                <a href="${pageContext.request.contextPath}/cashier"
                   class="btn btn-primary btn-sm">
                    <i class="fas fa-search me-1"></i>Tìm sản phẩm
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">

            <!-- ===== Bảng sản phẩm ===== -->
            <div class="col-lg-8">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-2 d-flex justify-content-between align-items-center">
                        <h6 class="mb-0 fw-semibold">
                            <i class="fas fa-list text-primary me-2"></i>Danh sách IMEI đã chọn
                        </h6>
                        <a href="${pageContext.request.contextPath}/cart?action=clear"
                           class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Xóa toàn bộ giỏ hàng?')">
                            <i class="fas fa-trash me-1"></i>Xóa tất cả
                        </a>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table align-middle mb-0">
                                <thead class="bg-light">
                                    <tr>
                                        <th class="ps-3" style="width:50px">#</th>
                                        <th style="width:54px"></th>
                                        <th>Sản phẩm</th>
                                        <th style="width:170px">IMEI</th>
                                        <th style="width:130px">Đơn giá</th>
                                        <th style="width:60px" class="text-center">Xóa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${cart}" var="item" varStatus="st">
                                        <tr>
                                            <td class="text-muted ps-3">${st.index + 1}</td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty item.imageUrl}">
                                                        <img src="${pageContext.request.contextPath}/${item.imageUrl}"
                                                             class="product-thumb"
                                                             alt="${item.variantName}"
                                                             onerror="this.style.display='none';this.nextElementSibling.style.display='flex';" />
                                                        <div class="no-thumb" style="display:none;">
                                                            <i class="fas fa-image"></i>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="no-thumb">
                                                            <i class="fas fa-image"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td>
                                                <div class="fw-semibold small">${item.variantName}</div>
                                                <div class="text-muted" style="font-size:.75rem;">
                                                    ${item.brand}
                                                    <c:if test="${not empty item.brand}"> · </c:if>
                                                    ${item.modelName}
                                                </div>
                                                <span class="badge-sku">${item.sku}</span>
                                            </td>

                                            <td>
                                                <span class="badge-imei">${item.imei}</span>
                                                <c:if test="${not empty item.serialNumber}">
                                                    <div class="text-muted" style="font-size:.72rem;font-family:monospace;">
                                                        S/N: ${item.serialNumber}
                                                    </div>
                                                </c:if>
                                            </td>

                                            <td class="fw-semibold text-primary">
                                                <fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/>đ
                                            </td>

                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/cart?action=remove&physicalId=${item.physicalId}"
                                                   class="btn btn-sm btn-outline-danger"
                                                   onclick="return confirm('Xóa IMEI ${item.imei} khỏi giỏ?')"
                                                   title="Xóa">
                                                    <i class="fas fa-times"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                                <tfoot>
                                    <tr class="total-row">
                                        <td colspan="4" class="ps-3 text-end text-muted">Tổng cộng:</td>
                                        <td class="text-primary">
                                            <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/>đ
                                        </td>
                                        <td></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ===== Panel thanh toán ===== -->
            <div class="col-lg-4">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-2">
                        <h6 class="mb-0 fw-semibold">
                            <i class="fas fa-receipt text-primary me-2"></i>Thông tin thanh toán
                        </h6>
                    </div>
                    <div class="card-body">

                        <form method="get"
                              action="${pageContext.request.contextPath}/invoice/create">

                            <!-- Thông tin khách hàng -->
                            <div class="mb-3">
                                <label class="form-label small fw-semibold">
                                    Số điện thoại khách hàng <span class="text-danger">*</span>
                                </label>
                                <div class="input-group input-group-sm">
                                    <span class="input-group-text bg-white">
                                        <i class="fas fa-phone text-muted"></i>
                                    </span>
                                    <input type="text" class="form-control" name="phone"
                                           placeholder="VD: 0901234567" required
                                           pattern="[0-9]{9,11}"
                                           title="Nhập số điện thoại 9-11 chữ số" />
                                </div>
                                <div class="form-text">Để tra cứu hoặc tạo khách hàng mới.</div>
                            </div>

                            <!-- Phương thức thanh toán -->
                            <div class="mb-3">
                                <label class="form-label small fw-semibold">Phương thức thanh toán</label>
                                <select class="form-select form-select-sm" name="paymentMethod">
                                    <option value="CASH">💵 Tiền mặt</option>
                                    <option value="CARD">💳 Thẻ ngân hàng</option>
                                    <option value="TRANSFER">📱 Chuyển khoản</option>
                                    <option value="MIXED">🔀 Kết hợp</option>
                                </select>
                            </div>

                            <!-- Ghi chú -->
                            <div class="mb-4">
                                <label class="form-label small fw-semibold">Ghi chú</label>
                                <textarea class="form-control form-control-sm" name="note"
                                          rows="2" placeholder="Ghi chú cho đơn hàng..."></textarea>
                            </div>

                            <!-- Tổng tiền hiển thị -->
                            <div class="d-flex justify-content-between align-items-center mb-3 px-1">
                                <span class="text-muted">Tổng tiền hàng:</span>
                                <span class="fw-bold text-primary fs-5">
                                    <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/>đ
                                </span>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-check-circle me-2"></i>Xác nhận thanh toán
                                </button>
                                <a href="${pageContext.request.contextPath}/cashier"
                                   class="btn btn-outline-secondary btn-sm">
                                    <i class="fas fa-plus me-1"></i>Tiếp tục thêm sản phẩm
                                </a>
                            </div>

                        </form>
                    </div>
                </div>
            </div>

        </div>
    </c:otherwise>
</c:choose>

<%@ include file="../common/footer.jsp" %>
