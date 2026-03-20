<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"   prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Báo cáo Tài chính - TechShop" />
<%@ include file="../common/header.jsp" %>

<%-- Pre-compute totals --%>
<c:set var="sumRevenue" value="0"/>
<c:set var="sumCost"    value="0"/>
<c:set var="sumProfit"  value="0"/>
<c:set var="sumOrders"  value="0"/>
<c:forEach items="${reportData}" var="item">
    <c:set var="sumRevenue" value="${sumRevenue + item.totalRevenue}"/>
    <c:set var="sumCost"    value="${sumCost    + item.totalCost}"/>
    <c:set var="sumProfit"  value="${sumProfit  + item.totalProfit}"/>
    <c:set var="sumOrders"  value="${sumOrders  + item.totalOrders}"/>
</c:forEach>

<div class="container-fluid px-4 py-4">

    <%-- PAGE HEADER --%>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">
            <i class="fas fa-chart-line text-primary me-2"></i>Báo Cáo Tài Chính
        </h2>
        <a id="exportBtn"
           href="${pageContext.request.contextPath}/report/financial?startDate=${startDate}&endDate=${endDate}&action=export"
           class="btn btn-success shadow-sm">
            <i class="fas fa-file-csv me-1"></i> Xuất CSV
        </a>
    </div>

    <%-- FILTER CARD --%>
    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <div class="d-flex flex-wrap gap-2 mb-3">
                <button type="button" class="btn btn-sm btn-outline-secondary quick-btn" data-period="this_month">Tháng này</button>
                <button type="button" class="btn btn-sm btn-outline-secondary quick-btn" data-period="last_month">Tháng trước</button>
                <button type="button" class="btn btn-sm btn-outline-secondary quick-btn" data-period="this_quarter">Quý này</button>
                <button type="button" class="btn btn-sm btn-outline-secondary quick-btn" data-period="this_year">Năm này</button>
                <button type="button" class="btn btn-sm btn-outline-secondary quick-btn" data-period="last_year">Năm ngoái</button>
                <button type="button" class="btn btn-sm btn-outline-secondary quick-btn" data-period="last_30">30 ngày gần nhất</button>
            </div>
            <form action="${pageContext.request.contextPath}/report/financial"
                  method="GET" class="row g-3 align-items-end" id="filterForm">
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="startDate" id="startDate" class="form-control"
                           value="${startDate}" max="${today}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="endDate" id="endDate" class="form-control"
                           value="${endDate}" max="${today}" required>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-filter me-2"></i>Lọc Báo Cáo
                    </button>
                </div>
            </form>
        </div>
    </div>

    <%-- KPI CARDS --%>
    <div class="row g-3 mb-4">

        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-success border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Tổng Doanh Thu</p>
                        <h3 class="mb-1 fw-bold text-success">
                            <fmt:formatNumber value="${sumRevenue}" type="number" pattern="#,##0"/> &#x20AB;
                        </h3>
                        <small class="text-muted">${sumOrders} hóa đơn hoàn thành</small>
                    </div>
                    <div class="bg-success bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-money-bill-wave fa-2x text-success"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-danger border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Tổng Giá Vốn (COGS)</p>
                        <h3 class="mb-1 fw-bold text-danger">
                            <fmt:formatNumber value="${sumCost}" type="number" pattern="#,##0"/> &#x20AB;
                        </h3>
                        <small class="text-muted">
                            <c:choose>
                                <c:when test="${sumRevenue > 0}">
                                    Chiếm <fmt:formatNumber value="${sumCost / sumRevenue * 100}" maxFractionDigits="1"/>% doanh thu
                                </c:when>
                                <c:otherwise>Chưa có doanh thu</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                    <div class="bg-danger bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-boxes fa-2x text-danger"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-primary border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Tổng Lợi Nhuận</p>
                        <h3 class="mb-1 fw-bold text-primary">
                            <fmt:formatNumber value="${sumProfit}" type="number" pattern="#,##0"/> &#x20AB;
                        </h3>
                        <small class="text-muted">
                            Avg/đơn:
                            <c:choose>
                                <c:when test="${sumOrders > 0}">
                                    <fmt:formatNumber value="${sumProfit / sumOrders}" type="number" pattern="#,##0"/> &#x20AB;
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                    <div class="bg-primary bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-chart-line fa-2x text-primary"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="card border-0 shadow-sm h-100 border-start border-warning border-4">
                <div class="card-body d-flex justify-content-between align-items-center">
                    <div>
                        <p class="text-muted mb-1 small fw-bold text-uppercase">Biên Lợi Nhuận</p>
                        <h3 class="mb-1 fw-bold text-warning">
                            <c:choose>
                                <c:when test="${sumRevenue > 0}">
                                    <fmt:formatNumber value="${sumProfit / sumRevenue * 100}" maxFractionDigits="1"/>%
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </h3>
                        <small class="text-muted">
                            <c:choose>
                                <c:when test="${sumRevenue > 0 and sumProfit / sumRevenue >= 0.2}">
                                    <i class="fas fa-thumbs-up text-success me-1"></i>Biên tốt (&ge;20%)
                                </c:when>
                                <c:when test="${sumRevenue > 0}">
                                    <i class="fas fa-exclamation-triangle text-warning me-1"></i>Biên thấp (&lt;20%)
                                </c:when>
                                <c:otherwise>Chưa có dữ liệu</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                    <div class="bg-warning bg-opacity-10 p-3 rounded-circle">
                        <i class="fas fa-percentage fa-2x text-warning"></i>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <%-- CHART (only when there's data) --%>
    <c:if test="${not empty reportData}">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <h6 class="mb-0 fw-bold">
                    <i class="fas fa-chart-bar text-primary me-2"></i>
                    Biểu đồ Doanh thu / Giá vốn / Lợi nhuận
                </h6>
                <div class="btn-group btn-group-sm" id="chartTypeBtns">
                    <button class="btn btn-outline-secondary active" data-type="bar">Cột</button>
                    <button class="btn btn-outline-secondary" data-type="line">Đường</button>
                </div>
            </div>
            <div class="card-body">
                <div style="height:300px; position:relative;">
                    <canvas id="financialChart"></canvas>
                </div>
            </div>
        </div>
    </c:if>

    <%-- DATA TABLE --%>
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h6 class="mb-0 fw-bold">
                <i class="fas fa-table text-secondary me-2"></i>Chi tiết từng ngày
            </h6>
            <small class="text-muted">Kỳ: ${startDate} &rarr; ${endDate}</small>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-primary">
                        <tr>
                            <th class="px-3">Ngày</th>
                            <th class="text-center">Đơn hàng</th>
                            <th class="text-end">Doanh thu</th>
                            <th class="text-end">Giá vốn (COGS)</th>
                            <th class="text-end">Lợi nhuận</th>
                            <th class="text-center" style="width:95px;">Biên LN</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty reportData}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-5">
                                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                        Không có giao dịch hoàn thành trong khoảng thời gian này.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${reportData}" var="item">
                                    <c:set var="rowMargin"
                                           value="${item.totalRevenue > 0 ? item.totalProfit / item.totalRevenue * 100 : -1}"/>
                                    <tr>
                                        <td class="px-3 fw-bold">${item.period}</td>
                                        <td class="text-center">
                                            <span class="badge bg-primary rounded-pill">${item.totalOrders}</span>
                                        </td>
                                        <td class="text-end text-success fw-semibold">
                                            <fmt:formatNumber value="${item.totalRevenue}" type="number" pattern="#,##0"/> &#x20AB;
                                        </td>
                                        <td class="text-end text-danger">
                                            <fmt:formatNumber value="${item.totalCost}" type="number" pattern="#,##0"/> &#x20AB;
                                        </td>
                                        <td class="text-end text-primary fw-semibold">
                                            <fmt:formatNumber value="${item.totalProfit}" type="number" pattern="#,##0"/> &#x20AB;
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${rowMargin >= 20}">
                                                    <span class="badge bg-success">
                                                        <fmt:formatNumber value="${rowMargin}" maxFractionDigits="1"/>%
                                                    </span>
                                                </c:when>
                                                <c:when test="${rowMargin >= 0}">
                                                    <span class="badge bg-warning text-dark">
                                                        <fmt:formatNumber value="${rowMargin}" maxFractionDigits="1"/>%
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">—</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>

                    <c:if test="${not empty reportData}">
                        <tfoot class="table-secondary fw-bold">
                            <tr>
                                <td class="px-3">Tổng cộng</td>
                                <td class="text-center">${sumOrders}</td>
                                <td class="text-end text-success">
                                    <fmt:formatNumber value="${sumRevenue}" type="number" pattern="#,##0"/> &#x20AB;
                                </td>
                                <td class="text-end text-danger">
                                    <fmt:formatNumber value="${sumCost}" type="number" pattern="#,##0"/> &#x20AB;
                                </td>
                                <td class="text-end text-primary">
                                    <fmt:formatNumber value="${sumProfit}" type="number" pattern="#,##0"/> &#x20AB;
                                </td>
                                <td class="text-center">
                                    <c:if test="${sumRevenue > 0}">
                                        <c:set var="totalMargin" value="${sumProfit / sumRevenue * 100}"/>
                                        <span class="badge ${totalMargin >= 20 ? 'bg-success' :
                                                             totalMargin >= 10 ? 'bg-warning text-dark' : 'bg-danger'}">
                                            <fmt:formatNumber value="${totalMargin}" maxFractionDigits="1"/>%
                                        </span>
                                    </c:if>
                                </td>
                            </tr>
                        </tfoot>
                    </c:if>
                </table>
            </div>
        </div>
    </div>

</div>

<%-- CHART.JS + LOGIC --%>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
(function () {

    // ── Quick period buttons ────────────────────────────────────────────
    const startInput = document.getElementById('startDate');
    const endInput   = document.getElementById('endDate');

    function fmt(d) { return d.toISOString().slice(0, 10); }

    document.querySelectorAll('.quick-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const p   = this.dataset.period;
            const now = new Date();
            let s, e = now;

            if (p === 'this_month') {
                s = new Date(now.getFullYear(), now.getMonth(), 1);
            } else if (p === 'last_month') {
                s = new Date(now.getFullYear(), now.getMonth() - 1, 1);
                e = new Date(now.getFullYear(), now.getMonth(), 0);
            } else if (p === 'this_quarter') {
                s = new Date(now.getFullYear(), Math.floor(now.getMonth() / 3) * 3, 1);
            } else if (p === 'this_year') {
                s = new Date(now.getFullYear(), 0, 1);
            } else if (p === 'last_year') {
                s = new Date(now.getFullYear() - 1, 0, 1);
                e = new Date(now.getFullYear() - 1, 11, 31);
            } else if (p === 'last_30') {
                s = new Date(now - 29 * 86400000);
            }

            startInput.value = fmt(s);
            endInput.value   = fmt(e);
            document.getElementById('filterForm').submit();
        });
    });

    // ── Keep export URL in sync with date inputs ────────────────────────
    const exportBtn = document.getElementById('exportBtn');
    function syncExport() {
        exportBtn.href = '${pageContext.request.contextPath}/report/financial' +
            '?startDate=' + startInput.value +
            '&endDate='   + endInput.value   +
            '&action=export';
    }
    startInput.addEventListener('change', syncExport);
    endInput.addEventListener('change',   syncExport);

    // ── Chart ───────────────────────────────────────────────────────────
    const canvas = document.getElementById('financialChart');
    if (!canvas) return;

    const labels  = [];
    const revenue = [];
    const cost    = [];
    const profit  = [];

    // Emit data arrays from JSTL
    <c:forEach items="${reportData}" var="row">
    labels.push('${row.period}');
    revenue.push(${row.totalRevenue});
    cost.push(${row.totalCost});
    profit.push(${row.totalProfit});
    </c:forEach>

    function vnd(v) {
        return new Intl.NumberFormat('vi-VN').format(Math.round(v)) + ' \u20AB';
    }

    const yTicks = {
        callback: function (v) {
            if (v >= 1e9) return (v / 1e9).toFixed(1) + ' tỷ';
            if (v >= 1e6) return (v / 1e6).toFixed(0) + ' tr';
            if (v >= 1e3) return (v / 1e3).toFixed(0) + 'k';
            return v;
        }
    };

    const sharedOptions = {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
            legend: { position: 'top' },
            tooltip: {
                callbacks: {
                    label: function (ctx) {
                        return ctx.dataset.label + ': ' + vnd(ctx.parsed.y);
                    }
                }
            }
        },
        scales: { y: { beginAtZero: true, ticks: yTicks } }
    };

    function mkDatasets(type) {
        const isFill = (type === 'line');
        return [
            {
                label: 'Doanh thu',
                data: revenue,
                backgroundColor: 'rgba(25,135,84,0.2)',
                borderColor: 'rgba(25,135,84,1)',
                fill: isFill, borderWidth: 2, tension: 0.3, order: 2
            },
            {
                label: 'Giá vốn',
                data: cost,
                backgroundColor: 'rgba(220,53,69,0.2)',
                borderColor: 'rgba(220,53,69,1)',
                fill: isFill, borderWidth: 2, tension: 0.3, order: 3
            },
            {
                label: 'Lợi nhuận',
                data: profit,
                backgroundColor: 'rgba(13,110,253,0.3)',
                borderColor: 'rgba(13,110,253,1)',
                fill: isFill, borderWidth: 2.5, tension: 0.3, order: 1
            }
        ];
    }

    let chartType = 'bar';
    let chart = new Chart(canvas, {
        type: chartType,
        data: { labels: labels, datasets: mkDatasets(chartType) },
        options: sharedOptions
    });

    document.querySelectorAll('#chartTypeBtns button').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.querySelectorAll('#chartTypeBtns button')
                    .forEach(function (b) { b.classList.remove('active'); });
            this.classList.add('active');
            chartType = this.dataset.type;
            chart.destroy();
            chart = new Chart(canvas, {
                type: chartType,
                data: { labels: labels, datasets: mkDatasets(chartType) },
                options: sharedOptions
            });
        });
    });

}());
</script>

<%@ include file="../common/footer.jsp" %>