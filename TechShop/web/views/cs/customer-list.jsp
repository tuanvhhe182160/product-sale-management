<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:set var="pageTitle" value="Khách hàng - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Quản Lý Khách Hàng</h2>
        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addCustomerModal">
            + Thêm Khách Hàng
        </button>
    </div>

    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">${param.error}</div>
    </c:if>
    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>

    <div class="card mb-4 shadow-sm">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/customer" method="GET" class="d-flex">
                <input type="text" name="search" class="form-control me-2" value="${searchKeyword}" placeholder="Tìm kiếm theo Tên, SĐT, Email...">
                <button type="submit" class="btn btn-primary px-4">Tìm</button>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover table-bordered mb-0">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Họ Tên</th>
                        <th>SĐT</th>
                        <th>Email</th>
                        <th>Ngày Tham Gia</th>
                        <th class="text-center">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cus" items="${customers}">
                        <tr>
                            <td>${cus.customerId}</td>
                            <td class="fw-bold">${cus.fullName}</td>
                            <td>${cus.phone}</td>
                            <td>${cus.email}</td>
                            <td>${cus.createdAt}</td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/customer?action=detail&id=${cus.customerId}" class="btn btn-sm btn-info text-white">Xem Chi Tiết</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty customers}">
                        <tr>
                            <td colspan="6" class="text-center text-muted py-3">Không có dữ liệu khách hàng.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="addCustomerModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/customer/save" method="POST">
          <div class="modal-header bg-success text-white">
            <h5 class="modal-title">Thêm Khách Hàng Mới</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                    <input type="text" name="phone" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Họ và Tên <span class="text-danger">*</span></label>
                    <input type="text" name="fullName" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control">
                </div>
                <div class="mb-3">
                    <label class="form-label">Ngày sinh</label>
                    <input type="date" name="dateOfBirth" class="form-control">
                </div>
                <div class="mb-3">
                    <label class="form-label">Địa chỉ</label>
                    <input type="text" name="address" class="form-control">
                </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="submit" class="btn btn-success">Lưu Khách Hàng</button>
          </div>
      </form>
    </div>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>