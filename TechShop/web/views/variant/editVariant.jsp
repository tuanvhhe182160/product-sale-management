<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp" />

<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="fas fa-edit"></i> Chỉnh sửa Product Variant</h2>
            <a href="${pageContext.request.contextPath}/variant?action=list" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay lại
            </a>
        </div>

        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${variant == null}">
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i> Không tìm thấy variant!
            </div>
        </c:if>

        <c:if test="${variant != null}">
            <div class="card">
                <div class="card-body">
                        <form action="${pageContext.request.contextPath}/variant/edit" method="POST" id="variantForm">
                        <input type="hidden" name="variantId" value="${variant.variantId}">

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="modelId" class="form-label">Product Model <span class="text-danger">*</span></label>
                                    <select class="form-select" id="modelId" name="modelId" required>
                                        <option value="">-- Chọn Model --</option>
                                        <c:forEach var="model" items="${models}">
                                            <option value="${model.modelId}" 
                                                    ${model.modelId == variant.modelId ? 'selected' : ''}>
                                                ${model.categoryName} - ${model.brand} - ${model.modelName} (${model.modelCode})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="sku" class="form-label">SKU <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="sku" name="sku" 
                                           value="${variant.sku}" required 
                                           pattern="[A-Z0-9\-]+" 
                                           title="SKU chỉ chứa chữ in hoa, số và dấu gạch ngang">
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="variantName" class="form-label">Tên Variant <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="variantName" name="variantName" 
                                   value="${variant.variantName}" required>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="basePrice" class="form-label">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="basePrice" name="basePrice" 
                                           value="${variant.basePrice.intValue()}" min="0" step="1000" required>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="costPrice" class="form-label">Giá vốn (VNĐ)</label>
                                    <input type="number" class="form-control" id="costPrice" name="costPrice" 
                                           value="${variant.costPrice != null ? variant.costPrice.intValue() : ''}" 
                                           min="0" step="1000">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="warrantyMonths" class="form-label">Thời gian bảo hành (tháng) <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="warrantyMonths" name="warrantyMonths" 
                                           value="${variant.warrantyMonths}" min="0" required>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                    <select class="form-select" id="status" name="status" required>
                                        <option value="ACTIVE" ${variant.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                        <option value="INACTIVE" ${variant.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="imageUrl" class="form-label">URL Hình ảnh</label>
                            <input type="url" class="form-control" id="imageUrl" name="imageUrl" 
                                   value="${variant.imageUrl != null ? variant.imageUrl : ''}" 
                                   placeholder="https://example.com/image.jpg">
                        </div>

                        <div class="mb-3">
                            <small class="text-muted">
                                <i class="fas fa-info-circle"></i> 
                                Ngày tạo: ${variant.createdAt != null ? variant.createdAt : 'N/A'} | 
                                Cập nhật lần cuối: ${variant.updatedAt != null ? variant.updatedAt : 'N/A'}
                            </small>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/variant?action=list" class="btn btn-secondary">
                                <i class="fas fa-times"></i> Hủy
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Cập nhật Variant
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</div>

<script>
    // Format SKU to uppercase on input
    document.getElementById('sku').addEventListener('input', function(e) {
        e.target.value = e.target.value.toUpperCase();
    });
</script>

<jsp:include page="../common/footer.jsp" />

