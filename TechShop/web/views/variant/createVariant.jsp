<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp" />

<div class="row">
    <div class="col-12">

        <!-- TITLE -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="page-title">
                <i class="fas fa-plus-circle"></i> Tạo Product Variant mới
            </h2>
            <a href="${pageContext.request.contextPath}/variant?action=list" class="btn btn-outline-primary">
                <i class="fas fa-arrow-left"></i> Quay lại
            </a>
        </div>

        <!-- ERROR -->
        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- FORM CARD -->
        <div class="card shadow-sm">
            <div class="card-body">

                <form action="${pageContext.request.contextPath}/variant/create"
                      method="POST" id="variantForm">

                    <!-- MODEL + SKU -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Product Model <span class="text-danger">*</span></label>
                            <select class="form-select" name="modelId" required>
                                <option value="">-- Chọn Model --</option>
                                <c:forEach var="model" items="${models}">
                                    <option value="${model.modelId}">
                                        ${model.categoryName} - ${model.brand} - ${model.modelName} (${model.modelCode})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label">SKU <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="sku"
                                   placeholder="VD: IP15PM-256-BLK"
                                   required pattern="[A-Z0-9\-]+"
                                   title="SKU chỉ chứa chữ in hoa, số và dấu gạch ngang">
                        </div>
                    </div>

                    <!-- VARIANT NAME -->
                    <div class="mb-3">
                        <label class="form-label">Tên Variant <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="variantName"
                               placeholder="VD: iPhone 15 Pro Max 256GB Black" required>
                    </div>

                    <!-- PRICE -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" name="basePrice"
                                   min="0" step="1000" required>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giá vốn (VNĐ)</label>
                            <input type="number" class="form-control" name="costPrice"
                                   min="0" step="1000">
                        </div>
                    </div>

                    <!-- WARRANTY + STATUS -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Bảo hành (tháng) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control"
                                   name="warrantyMonths" value="12" min="0" required>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label">Trạng thái <span class="text-danger">*</span></label>
                            <select class="form-select" name="status" required>
                                <option value="ACTIVE" selected>ACTIVE</option>
                                <option value="INACTIVE">INACTIVE</option>
                            </select>
                        </div>
                    </div>

                    <!-- IMAGE -->
                    <div class="mb-3">
                        <label class="form-label">URL Hình ảnh</label>
                        <input type="url" class="form-control"
                               name="imageUrl"
                               placeholder="https://example.com/image.jpg">
                    </div>

                    <!-- ACTION -->
                    <div class="text-end">
                        <a href="${pageContext.request.contextPath}/variant?action=list"
                           class="btn btn-secondary">
                            <i class="fas fa-times"></i> Hủy
                        </a>
                        <button type="submit" class="btn btn-primary ms-2">
                            <i class="fas fa-save"></i> Lưu Variant
                        </button>
                    </div>

                </form>
            </div>
        </div>
    </div>
</div>

<!-- BLUE THEME -->
<style>
.page-title {
    color: #0d6efd;
    font-weight: 600;
}

.page-title i {
    color: #0d6efd;
}

.card {
    border: 1px solid rgba(13,110,253,.25);
    border-radius: 10px;
}

.form-control:focus,
.form-select:focus {
    border-color: #0d6efd;
    box-shadow: 0 0 0 0.2rem rgba(13,110,253,.25);
}

.btn-primary {
    background-color: #0d6efd;
    border-color: #0d6efd;
}

.btn-outline-primary {
    border-color: #0d6efd;
    color: #0d6efd;
}

.btn-outline-primary:hover {
    background-color: #0d6efd;
    color: #fff;
}
</style>

<script>
document.querySelector('input[name="sku"]').addEventListener('input', function(e) {
    e.target.value = e.target.value.toUpperCase();
});
</script>

<jsp:include page="../common/footer.jsp" />
