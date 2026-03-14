<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Chốt Kỳ Kế Toán - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <!-- HEADER -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-lock text-info me-2"></i> Chốt Kỳ Kế Toán
        </h2>
    </div>

    <!-- THÔNG BÁO -->
    <c:if test="${param.success == 'closed'}">
        <div class="alert alert-success shadow-sm">Chốt kỳ thành công!</div>
    </c:if>

    <c:if test="${param.error == 'already_closed'}">
        <div class="alert alert-warning shadow-sm">Kỳ này đã được chốt trước đó.</div>
    </c:if>

    <c:if test="${param.error == 'failed'}">
        <div class="alert alert-danger shadow-sm">Chốt kỳ thất bại.</div>
    </c:if>

    <!-- FORM CHỌN KỲ -->
    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <form action="${pageContext.request.contextPath}/accounting/close-period"
                  method="GET"
                  class="row g-3 align-items-end">

                <input type="hidden" name="action" value="preview"/>
                <input type="hidden" name="branchId" value="${param.branchId}"/>

                <div class="col-md-4">
                    <label for="monthSelect" class="form-label fw-bold text-muted small">Tháng</label>
                    <select id="monthSelect" name="month" class="form-select" required>
                        <option value="">Chọn tháng</option>
                        <option value="1">Tháng 1</option>
                        <option value="2">Tháng 2</option>
                        <option value="3">Tháng 3</option>
                        <option value="4">Tháng 4</option>
                        <option value="5">Tháng 5</option>
                        <option value="6">Tháng 6</option>
                        <option value="7">Tháng 7</option>
                        <option value="8">Tháng 8</option>
                        <option value="9">Tháng 9</option>
                        <option value="10">Tháng 10</option>
                        <option value="11">Tháng 11</option>
                        <option value="12">Tháng 12</option>
                    </select>
                </div>

                <div class="col-md-4">
                    <label for="yearSelect" class="form-label fw-bold text-muted small">Năm</label>
                    <select id="yearSelect" name="year" class="form-select" required id="yearSelect">
                        <option value="">Chọn năm</option>
                    </select>
                </div>

                <div class="col-md-4">
                    <button type="submit"
                            class="btn btn-info text-white w-100">
                        <i class="fas fa-calculator me-2"></i>Xem Trước
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- DANH SÁCH KỲ ĐÃ CHỐT -->
    <div class="card shadow-sm border-0">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Tháng</th>
                            <th>Năm</th>
                            <th class="text-end">Doanh Thu</th>
                            <th class="text-end">Lợi Nhuận</th>
                            <th>Hóa Đơn</th>
                            <th>Ngày Chốt</th>
                            <th class="text-center">Trạng Thái</th>
                        </tr>
                    </thead>
                    <tbody>

                        <c:choose>
                            <c:when test="${empty periodList}">
                                <tr>
                                    <td colspan="7"
                                        class="text-center text-muted py-4">
                                        Chưa có kỳ kế toán nào được chốt.
                                    </td>
                                </tr>
                            </c:when>

                            <c:otherwise>
                                <c:forEach var="p" items="${periodList}">
                                    <tr>
                                        <td>${p.periodMonth}</td>
                                        <td>${p.periodYear}</td>

                                        <td class="text-end fw-bold text-success">
                                            <fmt:formatNumber value="${p.totalRevenue}"
                                                type="number" pattern="#,##0"/> ₫
                                        </td>

                                        <td class="text-end fw-bold text-primary">
                                            <fmt:formatNumber value="${p.totalProfit}"
                                                type="number" pattern="#,##0"/> ₫
                                        </td>

                                        <td>${p.totalInvoices}</td>

                                        <td>
                                            ${p.closedAt.toString().replace('T',' ').substring(0,16)}
                                        </td>

                                        <td class="text-center">
                                            <span class="badge bg-success">
                                                <i class="fas fa-check-circle"></i> ${p.status}
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

<script>
    const startYear = 2000;
    const currentYear = new Date().getFullYear();
    const yearSelect = document.getElementById("yearSelect");

    for (let y = currentYear; y >= startYear; y--) {
        const option = document.createElement("option");
        option.value = y;
        option.textContent = y;
        yearSelect.appendChild(option);
    }
</script>

<%@ include file="../common/footer.jsp" %>