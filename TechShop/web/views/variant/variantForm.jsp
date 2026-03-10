<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="isEdit" value="${not empty variant and not empty variant.variantId}" />

<c:set var="pageTitle"
       value="${isEdit ? 'Edit Product Variant - TechShop' : 'Create Product Variant - TechShop'}" />

<jsp:include page="../common/header.jsp" />

<div class="row">
    <div class="col-12">

        <!-- TITLE -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="page-title">
                <i class="fas ${isEdit ? 'fa-edit' : 'fa-plus-circle'}"></i>
                ${isEdit ? 'Chỉnh sửa Product Variant' : 'Tạo Product Variant mới'}
            </h2>

            <a href="${pageContext.request.contextPath}/variant"
               class="btn btn-outline-primary">
                <i class="fas fa-arrow-left"></i> Quay lại
            </a>
        </div>

        <!-- ERROR -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- FORM -->
        <div class="card shadow-sm">
            <div class="card-body">

                <form action="${pageContext.request.contextPath}/variant/save"
                      method="POST">

                    <!-- HIDDEN ID -->
                    <input type="hidden"
                           name="variantId"
                           value="${isEdit ? variant.variantId : ''}" />

                    <!-- MODEL + SKU -->
                    <div class="row">

                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                Product Model <span class="text-danger">*</span>
                            </label>

                            <select class="form-select"
                                    name="modelId"
                                    required>

                                <option value="">-- Chọn Model --</option>

                                <c:forEach var="model" items="${models}">
                                    <option value="${model.modelId}"
                                        ${isEdit && model.modelId == variant.modelId ? 'selected' : ''}>

                                        ${model.categoryName} - ${model.brand}
                                        - ${model.modelName} (${model.modelCode})

                                    </option>
                                </c:forEach>

                            </select>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                SKU <span class="text-danger">*</span>
                            </label>

                            <input type="text"
                                   class="form-control sku-input"
                                   name="sku"
                                   value="${isEdit ? variant.sku : ''}"
                                   placeholder="VD: IP15PM-256-BLK"
                                   required
                                   pattern="[A-Z0-9\-]+"
                                   title="SKU chỉ chứa chữ in hoa, số và dấu gạch ngang">
                        </div>

                    </div>

                    <!-- VARIANT NAME -->
                    <div class="mb-3">
                        <label class="form-label">
                            Tên Variant <span class="text-danger">*</span>
                        </label>

                        <input type="text"
                               class="form-control"
                               name="variantName"
                               value="${isEdit ? variant.variantName : ''}"
                               placeholder="VD: iPhone 15 Pro Max 256GB Black"
                               required>
                    </div>
                               
                               <!-- ATTRIBUTES -->
<div class="card mt-3 mb-3">

    <div class="card-header bg-light d-flex justify-content-between align-items-center">
        <strong>Variant Attributes</strong>

        <button type="button"
                class="btn btn-sm btn-primary"
                onclick="addAttributeRow()">
            <i class="fas fa-plus"></i> Add Attribute
        </button>
    </div>

    <div class="card-body">

        <table class="table table-bordered align-middle" id="attributeTable">

            <thead>
                <tr>
                    <th style="width:40%">Attribute Name</th>
                    <th style="width:50%">Value</th>
                    <th style="width:10%">Action</th>
                </tr>
            </thead>

            <tbody>

                <!-- EDIT MODE -->
                <c:if test="${isEdit}">
                    <c:forEach var="attr" items="${attributes}">
                        <tr>

                            <td>
                                <input type="text"
                                       class="form-control"
                                       name="attributeName[]"
                                       value="${attr.key}"
                                       required>
                            </td>

                            <td>
                                <input type="text"
                                       class="form-control"
                                       name="attributeValue[]"
                                       value="${attr.value}"
                                       required>
                            </td>

                            <td class="text-center">
                                <button type="button"
                                        class="btn btn-sm btn-danger"
                                        onclick="removeRow(this)">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>

                        </tr>
                    </c:forEach>
                </c:if>

                <!-- CREATE MODE -->
                <c:if test="${not isEdit}">
                    <tr>

                        <td>
                            <input type="text"
                                   class="form-control"
                                   name="attributeName[]"
                                   placeholder="VD: Storage"
                                   required>
                        </td>

                        <td>
                            <input type="text"
                                   class="form-control"
                                   name="attributeValue[]"
                                   placeholder="VD: 256GB"
                                   required>
                        </td>

                        <td class="text-center">
                            <button type="button"
                                    class="btn btn-sm btn-danger"
                                    onclick="removeRow(this)">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>

                    </tr>
                </c:if>

            </tbody>

        </table>

    </div>
</div>

                    <!-- PRICE -->
                    <div class="row">

                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                Giá bán (VNĐ) <span class="text-danger">*</span>
                            </label>

                            <input type="number"
                                   class="form-control"
                                   name="basePrice"
                                   value="${isEdit ? variant.basePrice.intValue() : ''}"
                                   min="0"
                                   step="1000"
                                   required>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                Giá vốn (VNĐ)
                            </label>

                            <input type="number"
                                   class="form-control"
                                   name="costPrice"
                                   value="${isEdit && variant.costPrice != null ? variant.costPrice.intValue() : ''}"
                                   min="0"
                                   step="1000">
                        </div>

                    </div>

                    <!-- WARRANTY + STATUS -->
                    <div class="row">

                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                Bảo hành (tháng) <span class="text-danger">*</span>
                            </label>

                            <input type="number"
                                   class="form-control"
                                   name="warrantyMonths"
                                   value="${isEdit ? variant.warrantyMonths : 12}"
                                   min="0"
                                   required>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                Trạng thái <span class="text-danger">*</span>
                            </label>

                            <select class="form-select"
                                    name="status"
                                    required>

                                <option value="ACTIVE"
                                    ${isEdit && variant.status == 'ACTIVE' ? 'selected' : ''}>
                                    ACTIVE
                                </option>

                                <option value="INACTIVE"
                                    ${isEdit && variant.status == 'INACTIVE' ? 'selected' : ''}>
                                    INACTIVE
                                </option>

                            </select>
                        </div>

                    </div>

                    <!-- IMAGE -->
                    <div class="mb-3">
                        <label class="form-label">
                            URL Hình ảnh
                        </label>

                        <input type="url"
                               class="form-control"
                               name="imageUrl"
                               value="${isEdit && variant.imageUrl != null ? variant.imageUrl : ''}"
                               placeholder="https://example.com/image.jpg">
                    </div>

                    <!-- INFO (EDIT ONLY) -->
                    <c:if test="${isEdit}">
                        <div class="mb-3">
                            <small class="text-muted">
                                <i class="fas fa-info-circle"></i>
                                Ngày tạo: ${variant.createdAt} |
                                Cập nhật lần cuối: ${variant.updatedAt}
                            </small>
                        </div>
                    </c:if>

                    <!-- ACTION -->
                    <div class="text-end">

                        <a href="${pageContext.request.contextPath}/variant"
                           class="btn btn-secondary">
                            <i class="fas fa-times"></i> Hủy
                        </a>

                        <button type="submit"
                                class="btn btn-primary ms-2">

                            <i class="fas fa-save"></i>
                            ${isEdit ? 'Cập nhật Variant' : 'Lưu Variant'}

                        </button>

                    </div>

                </form>

            </div>
        </div>
    </div>
</div>

<!-- STYLE -->
<style>

.page-title{
    color:#0d6efd;
    font-weight:600;
}

.page-title i{
    color:#0d6efd;
}

.card{
    border:1px solid rgba(13,110,253,.25);
    border-radius:10px;
}

.form-control:focus,
.form-select:focus{
    border-color:#0d6efd;
    box-shadow:0 0 0 .2rem rgba(13,110,253,.25);
}

.btn-primary{
    background-color:#0d6efd;
    border-color:#0d6efd;
}

.btn-outline-primary{
    color:#0d6efd;
    border-color:#0d6efd;
}

.btn-outline-primary:hover{
    background-color:#0d6efd;
    color:#fff;
}

</style>

<script>

document.querySelectorAll('.sku-input').forEach(function(input){
    input.addEventListener('input', function(e){
        e.target.value = e.target.value.toUpperCase();
    });
});

</script>

<script>

function addAttributeRow(){

    const table = document.getElementById("attributeTable")
                          .getElementsByTagName('tbody')[0];

    const row = table.insertRow();

    row.innerHTML = `
        <td>
            <input type="text"
                   class="form-control"
                   name="attributeName[]"
                   placeholder="VD: Color"
                   required>
        </td>

        <td>
            <input type="text"
                   class="form-control"
                   name="attributeValue[]"
                   placeholder="VD: Black"
                   required>
        </td>

        <td class="text-center">
            <button type="button"
                    class="btn btn-sm btn-danger"
                    onclick="removeRow(this)">
                <i class="fas fa-trash"></i>
            </button>
        </td>
    `;
}

function removeRow(btn){

    const row = btn.closest("tr");
    row.remove();

}

</script>

<jsp:include page="../common/footer.jsp" />