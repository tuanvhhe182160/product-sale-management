<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Theo dõi chi tiết đơn bảo hành đã tạo" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Chi Tiết Yêu Cầu #${reqDetail.requestCode}</h2>
        <a href="${pageContext.request.contextPath}/cs/warranty/list" class="btn btn-outline-secondary">Quay lại danh sách</a>
    </div>

    <div class="row">
        <div class="col-md-5">
            <div class="card shadow-sm mb-4 h-100">
                <div class="card-header bg-dark text-white fw-bold">Hồ Sơ Yêu Cầu</div>
                <div class="card-body">
                    <h5 class="text-primary border-bottom pb-2">Khách Hàng</h5>
                    <p class="mb-1"><strong>Họ tên:</strong> ${reqDetail.customerName}</p>
                    <p class="mb-1"><strong>Điện thoại:</strong> <span class="text-danger fw-bold">${reqDetail.customerPhone}</span></p>
                    <p class="mb-3"><strong>Email:</strong> ${reqDetail.customerEmail}</p>

                    <h5 class="text-primary border-bottom pb-2 mt-4">Sản Phẩm & Lỗi</h5>
                    <p class="mb-1"><strong>Tên máy:</strong> ${reqDetail.variantName}</p>
                    <p class="mb-1"><strong>IMEI:</strong> ${reqDetail.imei}</p>
                    <p class="mb-1"><strong>Hóa đơn:</strong> ${reqDetail.invoiceCode} (<fmt:formatDate value="${reqDetail.legacyInvoiceDate}" pattern="dd/MM/yyyy"/>)</p>
                    <div class="mt-3 p-3 bg-light border rounded">
                        <strong>Khách báo lỗi:</strong><br>
                        <span class="text-danger">${reqDetail.issueDescription}</span>
                    </div>
                    <%-- Ảnh minh họa lỗi do CS chụp khi tiếp nhận --%>
                    <c:if test="${not empty reqDetail.imageUrl}">
                        <div class="row mt-3">
                            <div class="col-sm-12 text-muted mb-1 fw-bold">
                                <i class="fas fa-image me-1"></i>Ảnh minh họa lỗi:
                            </div>
                            <div class="col-sm-12">
                                <a href="${pageContext.request.contextPath}/${reqDetail.imageUrl}"
                                   target="_blank">
                                    <img src="${pageContext.request.contextPath}/${reqDetail.imageUrl}"
                                         alt="Ảnh lỗi"
                                         class="img-fluid rounded border"
                                         style="max-height:300px;object-fit:contain;cursor:zoom-in;">
                                </a>
                                <div class="text-muted small mt-1">
                                    Nhấn vào ảnh để xem toàn màn hình
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <div class="col-md-7">
            <div class="card shadow-sm mb-4 border-primary">
                <div class="card-body text-center">
                    <h5 class="text-muted mb-2">Trạng Thái Hiện Tại</h5>
                    <c:choose>
                        <c:when test="${reqDetail.status == 'PENDING'}"><h2 class="text-warning mb-0">CHỜ KỸ THUẬT TIẾP NHẬN</h2></c:when>
                        <c:when test="${reqDetail.status == 'IN_PROGRESS'}"><h2 class="text-info mb-0">ĐANG SỬA CHỮA</h2></c:when>
                        <c:when test="${reqDetail.status == 'COMPLETED'}">
                            <h2 class="text-success mb-2">ĐÃ HOÀN THÀNH</h2>
                            <p class="mb-0 text-muted"><strong>Kết luận:</strong> ${reqDetail.resolution}</p>
                        </c:when>
                        <c:when test="${reqDetail.status == 'REJECTED'}">
                            <h2 class="text-danger mb-2">TỪ CHỐI BẢO HÀNH</h2>
                            <p class="mb-0 text-muted"><strong>Lý do:</strong> ${reqDetail.resolution}</p>
                        </c:when>
                    </c:choose>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-header bg-secondary text-white fw-bold">Tiến Độ Xử Lý Kỹ Thuật</div>
                <div class="card-body p-0">
                    <table class="table table-hover table-striped mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Thời gian</th>
                                <th>Cập nhật bởi</th>
                                <th>Nội dung / Ghi chú</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="hist" items="${historyList}">
                                <tr>
                                    <td class="text-nowrap"><fmt:formatDate value="${hist.legacyUpdatedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><strong>${hist.updatedByName}</strong><br><small class="text-muted">${hist.status}</small></td>
                                    <td>${hist.note}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty historyList}">
                                <tr><td colspan="3" class="text-center text-muted">Chưa có cập nhật nào.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>