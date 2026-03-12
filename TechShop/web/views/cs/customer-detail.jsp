<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Khách Hàng - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4 mb-5">
    
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Hồ Sơ Khách Hàng</h2>
        <a href="${pageContext.request.contextPath}/customer" class="btn btn-outline-secondary">Quay lại danh sách</a>
    </div>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>

    <div class="row mb-4">
        <div class="col-md-8">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-dark text-white fw-bold">Thông Tin Liên Hệ</div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-sm-3 text-muted">Họ và Tên:</div>
                        <div class="col-sm-9 fw-bold fs-5 text-primary">${customer.fullName}</div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-sm-3 text-muted">Số điện thoại:</div>
                        <div class="col-sm-9 fw-bold">${customer.phone}</div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-sm-3 text-muted">Email:</div>
                        <div class="col-sm-9">${empty customer.email ? 'Chưa cập nhật' : customer.email}</div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-sm-3 text-muted">??a ch?:</div>
                        <div class="col-sm-9">${empty customer.address ? 'Chưa cập nhật' : customer.address}</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm h-100 border-warning">
                <div class="card-header bg-warning text-dark fw-bold text-center">Tài Khoản Loyalty</div>
                <div class="card-body d-flex flex-column justify-content-center align-items-center">
                    <h1 class="display-3 text-warning fw-bold mb-0">${loyaltyPoints}</h1>
                    <p class="text-muted fs-5 mt-2">Điểm hiện có</p>
                    <button class="btn btn-outline-warning mt-3 w-100">Xem lịch sử tích điểm</button>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white fw-bold d-flex justify-content-between align-items-center">
            <span>Lịch Sử Mua Hàng</span>
            <span class="badge bg-light text-primary">${purchaseHistory.size()} Đơn hàng</span>
        </div>
        <div class="card-body bg-white">
            
            <form action="${pageContext.request.contextPath}/customer" method="GET" class="row g-3 mb-4 p-3 bg-light rounded border">
                <input type="hidden" name="action" value="detail">
                <input type="hidden" name="id" value="${customer.customerId}">
                
                <div class="col-md-3">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="fromDate" class="form-control" value="${fromDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="toDate" class="form-control" value="${toDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold text-muted small">Trạng thái</label>
                    <select name="status" class="form-select">
                        <option value="">Tất cả trạng thái</option>
                        <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                        <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Chưa xử lý</option>
                        <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                    </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">Lọc Dữ Liệu</button>
                </div>
            </form>

            <div class="table-responsive">
                <table class="table table-hover table-bordered mb-0 align-middle">
                    <thead class="table-light text-center">
                        <tr>
                            <th>Mã Hóa Đơn</th>
                            <th>Ngày Mua</th>
                            <th>Tổng Tiền</th>
                            <th>Giảm Giá</th>
                            <th>Thực Thu</th>
                            <th>Trạng Thái</th>
                            <th>Thao Tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="inv" items="${purchaseHistory}">
                            <tr>
                                <td class="text-center fw-bold text-primary">${inv.invoiceCode}</td>
                                <td class="text-center"><fmt:formatDate value="${inv.legacyInvoiceDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td class="text-end"><fmt:formatNumber value="${inv.totalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                <td class="text-end text-danger"><fmt:formatNumber value="${inv.discountAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                <td class="text-end fw-bold"><fmt:formatNumber value="${inv.finalAmount}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${inv.status == 'COMPLETED'}"><span class="badge bg-success">HOÀN THÀNH</span></c:when>
                                        <c:when test="${inv.status == 'CANCELLED'}"><span class="badge bg-danger">ĐÃ HỦY</span></c:when>
                                        <c:otherwise><span class="badge bg-warning text-dark">${inv.status}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/invoice/print?id=${inv.invoiceId}" target="_blank" class="btn btn-sm btn-outline-info">Xem HĐ</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty purchaseHistory}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-4">
                                    Không tìm thấy lịch sử mua hàng phù hợp với điều kiện lọc.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>                    