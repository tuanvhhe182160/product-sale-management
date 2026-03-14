<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Danh sách Hóa Đơn - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-receipt text-info me-2"></i> Đối Soát Hóa Đơn
        </h2>
        <a href="${pageContext.request.contextPath}/accounting/invoices?startDate=${startDate}&endDate=${endDate}&action=export" 
           class="btn btn-success shadow-sm">
            <i class="fas fa-file-excel fa-sm text-white-50 me-1"></i> Xuất Dữ Liệu
        </a>
    </div>

    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <form action="${pageContext.request.contextPath}/accounting/invoices" method="GET" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="startDate" class="form-control" value="${startDate}" max="${today}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="endDate" class="form-control" value="${endDate}" max="${today}" required>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-info px-4 w-100 text-white">
                        <i class="fas fa-search me-2"></i>Tìm Kiếm
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Mã Hóa Đơn</th>
                            <th>Ngày Giao Dịch</th>
                            <th>Chi Nhánh</th>
                            <th>Thu Ngân</th>
                            <th>Khách Hàng</th>
                            <th>Hình Thức</th>
                            <th class="text-end">Thực Thu</th>
                            <th class="text-center">Trạng Thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty invoices}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted py-4">
                                        Không tìm thấy hóa đơn nào trong thời gian này.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${invoices}" var="inv">
                                    <tr>
                                        <td class="fw-bold">
                                            <a href="${pageContext.request.contextPath}/accounting/invoice-detail?id=${inv.invoiceId}"
                                                class="text-primary text-decoration-none">
                                                ${inv.invoiceCode}
                                            </a>
                                        </td>
                                        <td>
                                            ${inv.invoiceDate.toString().replace('T', ' ').substring(0, 16)}
                                        </td>
                                        <td>${not empty inv.branchName ? inv.branchName : 'N/A'}</td>
                                        <td>${not empty inv.cashierName ? inv.cashierName : 'N/A'}</td>
                                        <td>${not empty inv.customerName ? inv.customerName : 'Khách lẻ'}</td>
                                        <td>
                                            <span class="badge bg-secondary">${inv.paymentMethod}</span>
                                        </td>
                                        <td class="text-end fw-bold text-success">
                                            <fmt:formatNumber value="${inv.finalAmount}" type="number" pattern="#,##0"/> ₫
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${inv.status == 'COMPLETED'}">
                                                    <span class="badge bg-success"><i class="fas fa-check-circle"></i> Hoàn thành</span>
                                                </c:when>
                                                <c:when test="${inv.status == 'CANCELLED'}">
                                                    <span class="badge bg-danger"><i class="fas fa-times-circle"></i> Đã hủy</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark">${inv.status}</span>
                                                </c:otherwise>
                                            </c:choose>
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

<%@ include file="../common/footer.jsp" %>