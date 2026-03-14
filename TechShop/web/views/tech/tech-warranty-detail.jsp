<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Bảo Hành - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Chi Tiết Yêu Cầu #${reqDetail.requestCode}</h2>
        <a href="${pageContext.request.contextPath}/tech/warranty/list" class="btn btn-outline-secondary">Quay lại danh sách</a>
    </div>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">${param.error}</div>
    </c:if>

    <div class="row">
        <div class="col-md-8">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-dark text-white fw-bold">Thông Tin Khách Hàng & Sản Phẩm</div>
                <div class="card-body">
                    <div class="row mb-2">
                        <div class="col-sm-3 text-muted">Khách hàng:</div>
                        <div class="col-sm-9 fw-bold">${reqDetail.customerName} - ${reqDetail.customerPhone}</div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-sm-3 text-muted">Email:</div>
                        <div class="col-sm-9">${reqDetail.customerEmail}</div>
                    </div>
                    <hr>
                    <div class="row mb-2">
                        <div class="col-sm-3 text-muted">Sản phẩm:</div>
                        <div class="col-sm-9 fw-bold text-primary">${reqDetail.variantName}</div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-sm-3 text-muted">IMEI:</div>
                        <div class="col-sm-9 fw-bold">${reqDetail.imei}</div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-sm-3 text-muted">Hóa đơn gốc:</div>
                        <div class="col-sm-9"><a href="#" class="text-decoration-none">${reqDetail.invoiceCode}</a> (Mua ngày: <fmt:formatDate value="${reqDetail.invoiceDate}" pattern="dd/MM/yyyy"/>)</div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-sm-12 text-muted mb-1">Mô tả lỗi từ khách hàng (CS ghi nhận):</div>
                        <div class="col-sm-12 p-3 bg-light border rounded text-danger fw-bold">
                            ${reqDetail.issueDescription}
                        </div>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-header bg-secondary text-white fw-bold">Lịch Sử Cập Nhật</div>
                <div class="card-body p-0">
                    <table class="table table-sm table-striped mb-0">
                        <thead>
                            <tr>
                                <th>Thời gian</th>
                                <th>Trạng thái</th>
                                <th>Người cập nhật</th>
                                <th>Ghi chú</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="hist" items="${historyList}">
                                <tr>
                                    <td><fmt:formatDate value="${hist.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><span class="badge bg-dark">${hist.status}</span></td>
                                    <td>${hist.updatedByName}</td>
                                    <td>${hist.note}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-primary">
                <div class="card-header bg-primary text-white fw-bold text-center">Thao Tác Kỹ Thuật</div>
                <div class="card-body">
                    
                    <form action="${pageContext.request.contextPath}/tech/warranty" method="POST">
                        <input type="hidden" name="requestId" value="${reqDetail.requestId}">
                        <input type="hidden" name="requestCode" value="${reqDetail.requestCode}">
                        <input type="hidden" name="customerEmail" value="${reqDetail.customerEmail}">
                        <input type="hidden" name="customerName" value="${reqDetail.customerName}">

                        <c:if test="${reqDetail.status == 'PENDING'}">
                            <div class="alert alert-warning text-center">Yêu cầu này chưa có người xử lý.</div>
                            <div class="mb-3">
                                <label class="form-label">Ghi chú ban đầu (Tuỳ chọn)</label>
                                <textarea name="note" class="form-control" rows="2" placeholder="VD: Đã nhận máy, chuẩn bị bung seal..."></textarea>
                            </div>
                            <button type="submit" name="action" value="accept" class="btn btn-warning w-100 fw-bold">Tiếp Nhận Xử Lý</button>
                        </c:if>

                        <c:if test="${reqDetail.status == 'IN_PROGRESS'}">
                            <div class="alert alert-info text-center">Bạn đang xử lý yêu cầu này.</div>
                            
                            <div class="mb-4 p-3 border rounded bg-light">
                                <label class="form-label fw-bold text-primary">Cập nhật tiến độ (Gửi Email cho khách)</label>
                                <textarea name="note" class="form-control mb-2" rows="2" placeholder="VD: Đang chờ linh kiện màn hình về..."></textarea>
                                <button type="submit" name="action" value="update" class="btn btn-sm btn-outline-primary w-100">Ghi nhận tiến độ</button>
                            </div>

                            <hr>
                            
                            <div class="mb-3">
                                <label class="form-label fw-bold text-success">Hướng giải quyết cuối cùng (Sẽ lưu vào hồ sơ)</label>
                                <textarea name="resolution" class="form-control" rows="3" required placeholder="VD: Đã thay Mainboard mới. Test OK."></textarea>
                            </div>
                            <div class="d-flex gap-2">
                                <button type="submit" name="action" value="complete" class="btn btn-success flex-grow-1 fw-bold">Hoàn Thành</button>
                                <button type="submit" name="action" value="reject" class="btn btn-danger flex-grow-1 fw-bold" onclick="return confirm('Bạn chắc chắn muốn TỪ CHỐI bảo hành ca này?');">Từ Chối</button>
                            </div>
                        </c:if>

                        <c:if test="${reqDetail.status == 'COMPLETED' || reqDetail.status == 'REJECTED'}">
                            <div class="alert alert-secondary text-center mb-0">
                                Yêu cầu này đã đóng.<br>
                                <strong>Kết luận:</strong> ${reqDetail.resolution}
                            </div>
                        </c:if>
                    </form>

                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>  