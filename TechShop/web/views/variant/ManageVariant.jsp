<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.NumberUtil" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>
<jsp:include page="../common/header.jsp" />

<div class="row">
    <div class="col-12">

        <!-- TITLE -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-primary">
                <i class="fas fa-box"></i> Quản lý Product Variants
            </h2>
            <a href="${pageContext.request.contextPath}/variant/create"
               class="btn btn-primary">
                <i class="fas fa-plus"></i> Tạo Variant mới
            </a>
        </div>

        <!-- SUCCESS / ERROR -->
        <c:if test="${param.success != null}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i> Thao tác thành công!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- SEARCH + FILTER -->
        <div class="card mb-3 border-primary">
            <div class="card-body">
                <form method="GET"
                      action="${pageContext.request.contextPath}/variant"
                      class="row g-3">

                    <div class="col-md-5">
                        <label class="form-label fw-semibold text-primary">
                            Tìm kiếm
                        </label>
                        <input type="text"
                               class="form-control"
                               name="search"
                               value="${searchValue}">
                    </div>

                    <div class="col-md-5">
                        <label class="form-label fw-semibold text-primary">
                            Lọc theo Model
                        </label>
                        <select class="form-select" name="modelId">
                            <option value="">-- Tất cả Models --</option>
                            <c:forEach var="m" items="${models}">
                                <option value="${m.modelId}"
                                        ${m.modelId == selectedModelId ? 'selected' : ''}>
                                    ${m.categoryName} - ${m.brand} - ${m.modelName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-2 d-flex align-items-end">
                        <button class="btn btn-primary w-100">
                            <i class="fas fa-search"></i> Tìm
                        </button>
                    </div>

                    <c:if test="${searchValue != null || selectedModelId != null}">
                        <div class="col-12">
                            <a href="${pageContext.request.contextPath}/variant"
                               class="btn btn-outline-primary btn-sm">
                                <i class="fas fa-times"></i> Xóa bộ lọc
                            </a>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>

        <!-- TABLE -->
        <div class="card border-primary">
            <div class="card-body">

                <c:if test="${empty variants}">
                    <p class="text-muted text-center py-4">
                        Không có dữ liệu
                    </p>
                </c:if>

                <c:if test="${not empty variants}">
                    <table class="table table-hover table-striped">
                        <thead class="table-primary">
                            <tr>
                                <th>ID</th>
                                <th>Ảnh</th>
                                <th>SKU</th>
                                <th>Tên</th>
                                <th>Model</th>
                                <th>Category</th>
                                <th>Brand</th>
                                <th>Giá bán</th>
                                <th>Giá vốn</th>
                                <th>BH</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="v" items="${variants}">
                                <tr>
                                    <td>${v.variantId}</td>

                                    <td>
                                        <img src="${pageContext.request.contextPath}${v.imageUrl != null ? v.imageUrl : '/assets/images/no-image.png'}"
                                             class="variant-img">
                                    </td>

                                    <td><strong>${v.sku}</strong></td>
                                    <td>${v.variantName}</td>
                                    <td>${v.modelName}</td>
                                    <td>
                                        <span class="badge bg-secondary">
                                            ${v.categoryName}
                                        </span>
                                    </td>
                                    <td>${v.brand}</td>
                                    <td class="text-primary fw-semibold">
                                        ${NumberUtil.formatCurrency(v.basePrice)}
                                    </td>
                                    <td>
                                        ${v.costPrice != null ? NumberUtil.formatCurrency(v.costPrice) : '-'}
                                    </td>
                                    <td>${v.warrantyMonths}</td>
                                    <td>
                                        <span class="badge ${v.status == 'ACTIVE' ? 'bg-primary' : 'bg-secondary'}">
                                            ${v.status}
                                        </span>
                                    </td>
                                    <td>
                                        ${DateTimeUtil.formatDateOnly(v.createdAt)}
                                    </td>
                                    <td>
                                        <a class="btn btn-sm btn-outline-primary"
                                           href="${pageContext.request.contextPath}/variant/edit?id=${v.variantId}">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <a class="btn btn-sm btn-outline-danger"
                                           href="${pageContext.request.contextPath}/variant/delete?id=${v.variantId}"
                                           onclick="return confirm('Xóa variant này?')">
                                            <i class="fas fa-trash"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <!-- PAGINATION -->
                    <nav>
                        <ul class="pagination justify-content-center mt-3">

                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="?page=${currentPage-1}&search=${searchValue}&modelId=${selectedModelId}">
                                    &laquo;
                                </a>
                            </li>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link"
                                       href="?page=${i}&search=${searchValue}&modelId=${selectedModelId}">
                                        ${i}
                                    </a>
                                </li>
                            </c:forEach>

                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="?page=${currentPage+1}&search=${searchValue}&modelId=${selectedModelId}">
                                    &raquo;
                                </a>
                            </li>

                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>
</div>

<style>
/* ===== BLUE THEME ===== */
.variant-img {
    width: 55px;
    height: 55px;
    object-fit: cover;
    border-radius: 6px;
    border: 1px solid #0d6efd;
}

.form-control:focus,
.form-select:focus {
    border-color: #0d6efd;
    box-shadow: 0 0 0 .2rem rgba(13,110,253,.25);
}

.pagination .page-link {
    color: #0d6efd;
}
.pagination .page-item.active .page-link {
    background-color: #0d6efd;
    border-color: #0d6efd;
}

.table-primary th {
    background-color: #0d6efd;
    color: #fff;
}
</style>

<jsp:include page="../common/footer.jsp" />
