<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Bảo Hành - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4">
    <h2 class="text-primary mb-4">Danh Sách Yêu Cầu Bảo Hành (Kỹ Thuật Viên)</h2>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">${param.error}</div>
    </c:if>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/tech/warranty/list" method="GET" class="row g-3">
                <div class="col-md-3">
                    <label class="form-label fw-bold">Trạng thái</label>
                    <select name="status" class="form-select">
                        <option value="">Tất cả</option>
                        <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Chờ tiếp nhận</option>
                        <option value="IN_PROGRESS" ${param.status == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                        <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold">Từ ngày</label>
                    <input type="date" name="fromDate" class="form-control" value="${param.fromDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold">Đến ngày</label>
                    <input type="date" name="toDate" class="form-control" value="${param.toDate}">
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">Lọc Dữ Liệu</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover table-bordered mb-0 align-middle">
                <thead class="table-dark text-center">
                    <tr>
                        <th>Mã Yêu Cầu</th>
                        <th>Ngày Tạo</th>
                        <th>Khách Hàng</th>
                        <th>Mô Tả Lỗi</th>
                        <th>Trạng Thái</th>
                        <th>Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="req" items="${requestList}">
                        <tr>
                            <td class="text-center fw-bold">${req.requestCode}</td>
                            <td class="text-center"><fmt:formatDate value="${req.requestDate}" pattern="dd/MM/yyyy"/></td>
                            <td>${req.customerName}</td>
                            <td><span class="d-inline-block text-truncate" style="max-width: 200px;">${req.issueDescription}</span></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${req.status == 'PENDING'}"><span class="badge bg-warning text-dark">CHỜ TIẾP NHẬN</span></c:when>
                                    <c:when test="${req.status == 'IN_PROGRESS'}"><span class="badge bg-info text-dark">ĐANG XỬ LÝ</span></c:when>
                                    <c:when test="${req.status == 'COMPLETED'}"><span class="badge bg-success">HOÀN THÀNH</span></c:when>
                                    <c:when test="${req.status == 'REJECTED'}"><span class="badge bg-danger">TỪ CHỐI</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">${req.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/tech/warranty/detail?id=${req.requestId}" class="btn btn-sm btn-primary">Xử Lý</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty requestList}">
                        <tr><td colspan="6" class="text-center text-muted py-4">Không có yêu cầu bảo hành nào.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>                  