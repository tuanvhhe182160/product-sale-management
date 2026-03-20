<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Chốt Kỳ Kế Toán - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <%-- ===== PAGE HEADER ===== --%>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-lock text-info me-2"></i> Chốt Kỳ Kế Toán
            <small class="text-muted fs-6 ms-2">${branchName}</small>
        </h2>
    </div>

    <%-- ===== FLASH MESSAGES ===== --%>
    <c:if test="${param.success == 'closed'}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="fas fa-check-circle me-2"></i>
            <strong>Chốt kỳ thành công!</strong>
            Tháng ${param.month}/${param.year} đã được khoá và lưu vào lịch sử.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.error == 'already_closed'}">
        <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
            Tháng ${param.month}/${param.year} đã được chốt trước đó.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.error == 'future_month'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="fas fa-ban me-2"></i>
            Không thể chốt tháng hiện tại hoặc tháng trong tương lai.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${param.error == 'db_failed'}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="fas fa-times-circle me-2"></i>
            Lỗi hệ thống khi lưu dữ liệu. Vui lòng thử lại.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="row">

        <%-- ===== LEFT COLUMN: FORM + PREVIEW ===== --%>
        <div class="col-lg-5 mb-4">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-header bg-info text-white py-3">
                    <h5 class="mb-0"><i class="fas fa-calculator me-2"></i>Chọn kỳ cần chốt</h5>
                </div>
                <div class="card-body">

                    <%-- Hướng dẫn --%>
                    <div class="alert alert-light border mb-4 small">
                        <i class="fas fa-info-circle text-info me-2"></i>
                        Chỉ có thể chốt các <strong>tháng đã qua</strong>.
                        Mỗi kỳ chỉ được chốt <strong>một lần</strong> và không thể hoàn tác.
                    </div>

                    <%-- Selector --%>
                    <div class="row g-3 mb-4">
                        <div class="col-6">
                            <label class="form-label fw-bold text-muted small">Tháng</label>
                            <select id="monthSel" class="form-select">
                                <c:forEach begin="1" end="12" var="m">
                                    <option value="${m}"
                                        <c:if test="${m == defaultMonth}">selected</c:if>>
                                        Tháng ${m}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-bold text-muted small">Năm</label>
                            <select id="yearSel" class="form-select">
                                <%-- JS will populate --%>
                            </select>
                        </div>
                    </div>

                    <button type="button" id="btnPreview"
                            class="btn btn-info text-white w-100 mb-3">
                        <i class="fas fa-eye me-2"></i>Xem Trước Dữ Liệu
                    </button>

                    <%-- Preview panel (hidden until AJAX loads) --%>
                    <div id="previewPanel" class="d-none">

                        <%-- Already-closed warning --%>
                        <div id="alreadyClosedAlert"
                             class="alert alert-warning d-none mb-3">
                            <i class="fas fa-lock me-2"></i>
                            Kỳ này <strong>đã được chốt</strong> trước đó.
                        </div>

                        <%-- Validation error from servlet --%>
                        <div id="validationAlert"
                             class="alert alert-danger d-none mb-3">
                        </div>

                        <%-- Data cards --%>
                        <div id="previewCards" class="d-none">
                            <h6 class="text-muted fw-bold mb-3">
                                <i class="fas fa-chart-bar me-1"></i>
                                Dữ liệu tháng <span id="previewLabel"></span>
                            </h6>
                            <div class="row g-2 mb-3">
                                <div class="col-4">
                                    <div class="card bg-light border-0 text-center p-2">
                                        <div class="small text-muted">Hóa đơn</div>
                                        <div class="fw-bold fs-5" id="pvInvoices">—</div>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="card bg-success bg-opacity-10 border-0 text-center p-2">
                                        <div class="small text-muted">Doanh thu</div>
                                        <div class="fw-bold text-success" id="pvRevenue">—</div>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="card bg-primary bg-opacity-10 border-0 text-center p-2">
                                        <div class="small text-muted">Lợi nhuận</div>
                                        <div class="fw-bold text-primary" id="pvProfit">—</div>
                                    </div>
                                </div>
                            </div>

                            <%-- Confirm form --%>
                            <form method="POST"
                                  action="${pageContext.request.contextPath}/accounting/close-period"
                                  id="closeForm">
                                <input type="hidden" name="action" value="close">
                                <input type="hidden" name="month" id="hiddenMonth">
                                <input type="hidden" name="year"  id="hiddenYear">

                                <div class="alert alert-warning small mb-3">
                                    <i class="fas fa-exclamation-triangle me-1"></i>
                                    Hành động này <strong>không thể hoàn tác</strong>.
                                    Xác nhận sau khi đã kiểm tra lại số liệu.
                                </div>

                                <button type="submit" class="btn btn-success w-100">
                                    <i class="fas fa-lock me-2"></i>Xác Nhận Chốt Kỳ
                                </button>
                            </form>
                        </div>
                    </div>

                    <%-- Loading spinner --%>
                    <div id="previewSpinner" class="text-center d-none py-3">
                        <div class="spinner-border text-info" role="status">
                            <span class="visually-hidden">Đang tải...</span>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <%-- ===== RIGHT COLUMN: HISTORY TABLE ===== --%>
        <div class="col-lg-7 mb-4">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="mb-0 fw-bold">
                        <i class="fas fa-history text-secondary me-2"></i>Lịch sử kỳ đã chốt
                    </h5>
                    <span class="badge bg-secondary">${history.size()} kỳ</span>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th class="px-3">Kỳ</th>
                                    <th class="text-center">Hóa đơn</th>
                                    <th class="text-end">Doanh thu</th>
                                    <th class="text-end">Lợi nhuận</th>
                                    <th>Người chốt</th>
                                    <th>Ngày chốt</th>
                                    <th class="text-center">TT</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty history}">
                                        <tr>
                                            <td colspan="7" class="text-center text-muted py-5">
                                                <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                                Chưa có kỳ nào được chốt.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="p" items="${history}">
                                            <tr>
                                                <td class="px-3 fw-bold">
                                                    Tháng ${p.periodMonth}/${p.periodYear}
                                                </td>
                                                <td class="text-center">
                                                    <span class="badge bg-primary rounded-pill">
                                                        ${p.totalInvoices}
                                                    </span>
                                                </td>
                                                <td class="text-end fw-semibold text-success">
                                                    <fmt:formatNumber value="${p.totalRevenue}"
                                                                      type="number"
                                                                      pattern="#,##0"/> &#x20AB;
                                                </td>
                                                <td class="text-end fw-semibold text-primary">
                                                    <fmt:formatNumber value="${p.totalProfit}"
                                                                      type="number"
                                                                      pattern="#,##0"/> &#x20AB;
                                                </td>
                                                <td class="text-muted small">
                                                    ${empty p.closedByName ? 'N/A' : p.closedByName}
                                                </td>
                                                <td class="text-muted small">
                                                    <c:if test="${p.closedAt != null}">
                                                        <fmt:formatDate
                                                            value="${p.closedAtAsDate}"
                                                            pattern="dd/MM/yyyy HH:mm"/>
                                                    </c:if>
                                                </td>
                                                <td class="text-center">
                                                    <span class="badge bg-success">
                                                        <i class="fas fa-lock me-1"></i>Đã chốt
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    </div><%-- /row --%>
</div><%-- /container --%>

<script>
(function () {
    const ctx        = '${pageContext.request.contextPath}';
    const monthSel   = document.getElementById('monthSel');
    const yearSel    = document.getElementById('yearSel');
    const btnPreview = document.getElementById('btnPreview');

    const previewPanel    = document.getElementById('previewPanel');
    const previewSpinner  = document.getElementById('previewSpinner');
    const previewCards    = document.getElementById('previewCards');
    const alreadyAlert    = document.getElementById('alreadyClosedAlert');
    const validationAlert = document.getElementById('validationAlert');
    const previewLabel    = document.getElementById('previewLabel');

    const pvInvoices = document.getElementById('pvInvoices');
    const pvRevenue  = document.getElementById('pvRevenue');
    const pvProfit   = document.getElementById('pvProfit');
    const hiddenMonth = document.getElementById('hiddenMonth');
    const hiddenYear  = document.getElementById('hiddenYear');

    // ── Populate year dropdown ──────────────────────────────────────────
    const currentYear  = ${currentYear};
    const defaultMonth = ${defaultMonth};
    const defaultYear  = ${defaultYear};

    for (let y = currentYear; y >= 2020; y--) {
        const opt = document.createElement('option');
        opt.value = y;
        opt.textContent = y;
        if (y === defaultYear) opt.selected = true;
        yearSel.appendChild(opt);
    }

    // ── Format numbers ──────────────────────────────────────────────────
    function formatVND(n) {
        return new Intl.NumberFormat('vi-VN').format(Math.round(n)) + ' ₫';
    }

    // ── Preview button click ────────────────────────────────────────────
    btnPreview.addEventListener('click', function () {
        const month = monthSel.value;
        const year  = yearSel.value;
        if (!month || !year) return;

        // Show spinner, hide everything else
        previewPanel.classList.remove('d-none');
        previewSpinner.classList.remove('d-none');
        previewCards.classList.add('d-none');
        alreadyAlert.classList.add('d-none');
        validationAlert.classList.add('d-none');

        fetch(ctx + '/accounting/close-period?action=preview-json&month=' + month + '&year=' + year)
            .then(function (r) { return r.json(); })
            .then(function (data) {
                previewSpinner.classList.add('d-none');

                // Server-side validation error (e.g. future month)
                if (data.error) {
                    validationAlert.textContent = data.error;
                    validationAlert.classList.remove('d-none');
                    return;
                }

                // Fill data
                previewLabel.textContent = 'Tháng ' + data.month + '/' + data.year;
                pvInvoices.textContent   = data.totalInvoices;
                pvRevenue.textContent    = formatVND(data.totalRevenue);
                pvProfit.textContent     = formatVND(data.totalProfit);

                hiddenMonth.value = data.month;
                hiddenYear.value  = data.year;

                previewCards.classList.remove('d-none');

                if (data.alreadyClosed) {
                    alreadyAlert.classList.remove('d-none');
                    // Hide confirm button
                    document.getElementById('closeForm')
                             .querySelector('button[type="submit"]')
                             .setAttribute('disabled', 'disabled');
                } else {
                    alreadyAlert.classList.add('d-none');
                    document.getElementById('closeForm')
                             .querySelector('button[type="submit"]')
                             .removeAttribute('disabled');
                }
            })
            .catch(function () {
                previewSpinner.classList.add('d-none');
                validationAlert.textContent = 'Lỗi kết nối. Vui lòng thử lại.';
                validationAlert.classList.remove('d-none');
            });
    });

}());
</script>

<%@ include file="../common/footer.jsp" %>