<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.techshop.util.NumberUtil" %>
<%@ page import="com.techshop.util.DateTimeUtil" %>
<jsp:include page="../common/header.jsp" />

<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="fas fa-box"></i> Quản lý Product Variants</h2>
            <a href="${pageContext.request.contextPath}/variant/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Tạo Variant mới
            </a>
        </div>

        <c:if test="${param.success != null}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <c:choose>
                    <c:when test="${param.success == 'create'}">
                        <i class="fas fa-check-circle"></i> Tạo variant thành công!
                    </c:when>
                    <c:when test="${param.success == 'update'}">
                        <i class="fas fa-check-circle"></i> Cập nhật variant thành công!
                    </c:when>
                    <c:when test="${param.success == 'delete'}">
                        <i class="fas fa-check-circle"></i> Xóa variant thành công!
                    </c:when>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Search and Filter Form -->
        <div class="card mb-3">
            <div class="card-body">
                <form method="GET" action="${pageContext.request.contextPath}/variant" class="row g-3">
                    <div class="col-md-5">
                        <label for="search" class="form-label">Tìm kiếm</label>
                        <input type="text" class="form-control" id="search" name="search" 
                               placeholder="Tìm theo SKU, tên variant, model, brand..." 
                               value="${searchValue != null ? searchValue : ''}">
                    </div>
                    <div class="col-md-5">
                        <label for="modelId" class="form-label">Lọc theo Model</label>
                        <select class="form-select" id="modelId" name="modelId">
                            <option value="">-- Tất cả Models --</option>
                            <c:forEach var="model" items="${models}">
                                <option value="${model.modelId}" ${model.modelId == selectedModelId ? 'selected' : ''}>
                                    ${model.categoryName} - ${model.brand} - ${model.modelName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                    </div>
                    <c:if test="${searchValue != null && !empty searchValue || selectedModelId != null}">
                        <div class="col-12">
                            <a href="${pageContext.request.contextPath}/variant" class="btn btn-outline-secondary btn-sm">
                                <i class="fas fa-times"></i> Xóa bộ lọc
                            </a>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <c:choose>
                    <c:when test="${variants == null || empty variants}">
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <p class="text-muted">Chưa có variant nào trong hệ thống.</p>
                            <a href="${pageContext.request.contextPath}/variant/create" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Tạo Variant đầu tiên
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover table-striped">
                                <thead class="table-dark">
                                    <tr>
                                        <th>ID</th>
                                        <th>SKU</th>
                                        <th>Tên Variant</th>
                                        <th>Model</th>
                                        <th>Category</th>
                                        <th>Brand</th>
                                        <th>Giá bán</th>
                                        <th>Giá vốn</th>
                                        <th>Bảo hành (tháng)</th>
                                        <th>Trạng thái</th>
                                        <th>Ngày tạo</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="variant" items="${variants}">
                                        <tr>
                                            <td>${variant.variantId}</td>
                                            <td><strong>${variant.sku}</strong></td>
                                            <td>${variant.variantName}</td>
                                            <td>${variant.modelName}</td>
                                            <td><span class="badge bg-secondary">${variant.categoryName}</span></td>
                                            <td>${variant.brand}</td>
                                            <td><strong class="text-success">${NumberUtil.formatCurrency(variant.basePrice)}</strong></td>
                                            <td>
                                                <c:if test="${variant.costPrice != null}">
                                                    ${NumberUtil.formatCurrency(variant.costPrice)}
                                                </c:if>
                                                <c:if test="${variant.costPrice == null}">
                                                    <span class="text-muted">-</span>
                                                </c:if>
                                            </td>
                                            <td>${variant.warrantyMonths}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${variant.status == 'ACTIVE'}">
                                                        <span class="badge bg-success">ACTIVE</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">INACTIVE</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${DateTimeUtil.formatDateOnly(variant.createdAt)}</td>
                                            <td>
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <a href="${pageContext.request.contextPath}/variant/edit?id=${variant.variantId}" 
                                                       class="btn btn-outline-primary" title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/variant/delete?id=${variant.variantId}" 
                                                       class="btn btn-outline-danger" 
                                                       onclick="return confirm('Bạn có chắc chắn muốn xóa variant này?')" 
                                                       title="Xóa">
                                                        <i class="fas fa-trash"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<style>
    .table-responsive {
        max-height: 600px;
        overflow-y: auto;
    }
    .table-responsive thead {
        position: sticky;
        top: 0;
        z-index: 10;
        background-color: #212529;
    }
    .table-responsive table {
        margin-bottom: 0;
    }
</style>

<jsp:include page="../common/footer.jsp" />

