<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Theo dõi các đơn bảo hành đã tạo" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid mt-4 px-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Tra Cứu Tiến Độ Bảo Hành</h2>
        <a href="${pageContext.request.contextPath}/cs/warranty" class="btn btn-success">+ Lập Phiếu Mới (Check IMEI)</a>
    </div>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/cs/warranty/list" method="GET" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label fw-bold">Tìm kiếm nhanh</label>
                    <input type="text" name="search" class="form-control" value="${param.search}" placeholder="Nhập SĐT khách, IMEI hoặc Mã phiếu...">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-bold">Trạng thái</label>
                    <select name="status" class="form-select">
                        <option value="">Tất cả</option>
                        <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Chờ tiếp nhận</option>
                        <option value="IN_PROGRESS" ${param.status == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                        <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Đã xong (Chờ trả)</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-bold">Từ ngày</label>
                    <input type="date" name="fromDate" class="form-control" value="${param.fromDate}">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-bold">Đến ngày</label>
                    <input type="date" name="toDate" class="form-control" value="${param.toDate}">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">Tra cứu</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover table-bordered mb-0 align-middle">
                <thead class="table-dark text-center">
                    <tr>
                        <th>Mã Phiếu</th>
                        <th>Ngày Nhận</th>
                        <th>Khách Hàng (SĐT)</th>
                        <th>Sản Phẩm (IMEI)</th>
                        <th>Tình Trạng Lỗi</th>
                        <th>Tiến Độ Hiện Tại</th>
                        <th>Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="req" items="${warrantyList}">
                        <tr>
                            <td class="text-center fw-bold text-primary">${req.requestCode}</td>
                            <td class="text-center"><fmt:formatDate value="${req.legacyRequestDate}" pattern="dd/MM/yyyy"/></td>
                            <td>
                                <span class="fw-bold">${req.customerName}</span><br>
                                <span class="text-danger fw-bold">📞 ${req.customerPhone}</span>
                            </td>
                            <td>
                                ${req.variantName}<br>
                                <small class="text-muted">IMEI: ${req.imei}</small>
                            </td>
                            <td><span class="d-inline-block text-truncate" style="max-width: 200px;">${req.issueDescription}</span></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${req.status == 'PENDING'}"><span class="badge bg-warning text-dark fs-6">CHỜ KỸ THUẬT NHẬN</span></c:when>
                                    <c:when test="${req.status == 'IN_PROGRESS'}"><span class="badge bg-info text-dark fs-6">ĐANG SỬA CHỮA</span></c:when>
                                    <c:when test="${req.status == 'COMPLETED'}"><span class="badge bg-success fs-6">ĐÃ SỬA XONG</span></c:when>
                                    <c:when test="${req.status == 'REJECTED'}"><span class="badge bg-danger fs-6">BỊ TỪ CHỐI</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">${req.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/cs/warranty/detail?id=${req.requestId}" class="btn btn-sm btn-info text-white">Xem Chi Tiết</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty warrantyList}">
                        <tr><td colspan="7" class="text-center text-muted py-4">Không tìm thấy yêu cầu bảo hành nào khớp với thông tin tra cứu.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>