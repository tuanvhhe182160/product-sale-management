

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

    /* Cancel banner */
    .cancel-banner {
        background: #fff5f5;
        border: 1px solid #fecaca;
        border-radius: 10px;
        padding: 14px 18px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        flex-wrap: wrap;
        margin-bottom: 20px;
    }
    .cancel-banner .cancel-text {
        font-size: .88rem;
        color: #7f1d1d;
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
<c:if test="${not empty cancelSuccess}">
    <div class="alert alert-warning alert-dismissible fade show py-2" role="alert">
        <i class="fas fa-ban me-2"></i>${cancelSuccess}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:choose>
    <c:when test="${empty cart}">
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

        <!-- ===== Banner hủy đơn ===== -->
        <div class="cancel-banner">
            <div class="cancel-text">
                <i class="fas fa-exclamation-triangle me-2 text-danger"></i>
                Cần hủy toàn bộ đơn hàng này? Tất cả sản phẩm sẽ được trả về danh sách chờ bán.
            </div>
            <button type="button" class="btn btn-danger btn-sm"
                    data-bs-toggle="modal" data-bs-target="#cancelModal">
                <i class="fas fa-ban me-1"></i>Hủy đơn hàng
            </button>
        </div>

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
                                                        <img src="${item.imageUrl}"
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

                            <div class="mb-3">
                                <label class="form-label small fw-semibold">Phương thức thanh toán</label>
                                <select class="form-select form-select-sm" name="paymentMethod">
                                    <option value="CASH">💵 Tiền mặt</option>
                                    <option value="CARD">💳 Thẻ ngân hàng</option>
                                    <option value="TRANSFER">📱 Chuyển khoản</option>
                                    <option value="MIXED">🔀 Kết hợp</option>
                                </select>
                            </div>

                            <div class="mb-4">
                                <label class="form-label small fw-semibold">Ghi chú</label>
                                <textarea class="form-control form-control-sm" name="note"
                                          rows="2" placeholder="Ghi chú cho đơn hàng..."></textarea>
                            </div>

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

<!-- ===== Modal Hủy Đơn ===== -->
<div class="modal fade" id="cancelModal" tabindex="-1" aria-labelledby="cancelModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">

            <div class="modal-header bg-danger text-white py-3">
                <h6 class="modal-title fw-bold mb-0" id="cancelModalLabel">
                    <i class="fas fa-ban me-2"></i>Xác nhận hủy đơn hàng
                </h6>
                <button type="button" class="btn-close btn-close-white"
                        data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <!-- Tóm tắt đơn -->
                <div class="bg-light rounded p-3 mb-3">
                    <div class="small text-muted mb-1">Đơn hàng sẽ bị hủy</div>
                    <div class="d-flex justify-content-between">
                        <span class="small"><strong>${cart.size()}</strong> sản phẩm</span>
                        <span class="small fw-bold text-danger">
                            <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/>đ
                        </span>
                    </div>
                </div>

                <div class="alert alert-warning py-2 small mb-3">
                    <i class="fas fa-info-circle me-1"></i>
                    Sau khi hủy, tất cả sản phẩm sẽ được trả về danh sách tồn kho.
                    Hành động này <strong>không thể hoàn tác</strong>.
                </div>

                <!-- Lý do hủy -->
                <label class="form-label small fw-semibold">
                    Lý do hủy <span class="text-danger">*</span>
                </label>
                <div class="d-flex flex-column gap-2 mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason"
                               id="r1" value="Khách đổi ý, không mua nữa" checked>
                        <label class="form-check-label small" for="r1">Khách đổi ý, không mua nữa</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason"
                               id="r2" value="Chọn nhầm sản phẩm">
                        <label class="form-check-label small" for="r2">Chọn nhầm sản phẩm</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason"
                               id="r3" value="Khách không đủ tiền thanh toán">
                        <label class="form-check-label small" for="r3">Khách không đủ tiền thanh toán</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="cancelReason"
                               id="r4" value="other">
                        <label class="form-check-label small" for="r4">Lý do khác...</label>
                    </div>
                </div>

                <!-- Ô nhập lý do khác -->
                <div id="otherReasonBox" style="display:none;">
                    <textarea class="form-control form-control-sm" id="otherReasonText"
                              rows="2" placeholder="Nhập lý do hủy..."></textarea>
                </div>
            </div>

            <div class="modal-footer py-2 gap-2">
                <button type="button" class="btn btn-outline-secondary btn-sm"
                        data-bs-dismiss="modal">
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
    document.querySelectorAll('input[name="cancelReason"]').forEach(function(radio) {
        radio.addEventListener('change', function() {
            document.getElementById('otherReasonBox').style.display =
                this.value === 'other' ? 'block' : 'none';
        });
    });

    // Xác nhận hủy → redirect clear
    document.getElementById('confirmCancelBtn').addEventListener('click', function() {
        var selected = document.querySelector('input[name="cancelReason"]:checked');
        var reason = selected ? selected.value : '';

        if (reason === 'other') {
            reason = document.getElementById('otherReasonText').value.trim();
            if (!reason) {
                document.getElementById('otherReasonText').focus();
                document.getElementById('otherReasonText').classList.add('is-invalid');
                return;
            }
        }

        // Redirect sang cart?action=cancel với lý do
        window.location.href = '${pageContext.request.contextPath}/cart?action=cancel&reason='
            + encodeURIComponent(reason);
    });
</script>

<%@ include file="../common/footer.jsp" %>

