<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Xem Trước Chốt Kỳ - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-eye text-info me-2"></i> Xem Trước Kỳ Kế Toán
        </h2>
    </div>

    <c:set var="p" value="${previewPeriod}" />

    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">

            <div class="row text-center">

                <div class="col-md-4 mb-3">
                    <h6 class="text-muted">Tổng Hóa Đơn</h6>
                    <h4 class="fw-bold">${p.totalInvoices}</h4>
                </div>

                <div class="col-md-4 mb-3">
                    <h6 class="text-muted">Doanh Thu</h6>
                    <h4 class="fw-bold text-success">
                        <fmt:formatNumber value="${p.totalRevenue}"
                            type="number" pattern="#,##0"/> ₫
                    </h4>
                </div>

                <div class="col-md-4 mb-3">
                    <h6 class="text-muted">Lợi Nhuận</h6>
                    <h4 class="fw-bold text-primary">
                        <fmt:formatNumber value="${p.totalProfit}"
                            type="number" pattern="#,##0"/> ₫
                    </h4>
                </div>

            </div>
        </div>
    </div>

    <!-- CONFIRM BUTTON -->
    <div class="d-flex justify-content-between">

        <a href="${pageContext.request.contextPath}/accounting/close-period?branchId=${p.branchId}"
           class="btn btn-secondary">
            <i class="fas fa-arrow-left me-2"></i>Quay Lại
        </a>

        <form action="${pageContext.request.contextPath}/accounting/close-period"
              method="POST">

            <input type="hidden" name="action" value="close"/>
            <input type="hidden" name="branchId" value="${p.branchId}"/>
            <input type="hidden" name="month" value="${p.periodMonth}"/>
            <input type="hidden" name="year" value="${p.periodYear}"/>

            <button type="submit"
                    class="btn btn-success">
                <i class="fas fa-lock me-2"></i>Xác Nhận Chốt Kỳ
            </button>
        </form>

    </div>

</div>

<%@ include file="../common/footer.jsp" %>