<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.techshop.util.NumberUtil" %>
<jsp:include page="../common/header.jsp" />

<style>
/* ===== BLUE THEME ===== */
.variant-img {
    width: 55px;
    height: 55px;
    object-fit: cover;
    border-radius: 6px;
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

<div class="row">
    <div class="col-12">

        <!-- TITLE -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-primary">
                <i class="fas fa-box"></i> Product Variants
            </h2>
            <a href="${pageContext.request.contextPath}/variant/form" class="btn btn-primary">
                <i class="fas fa-plus"></i> New Variant
            </a>
        </div>

        <!-- SUCCESS / ERROR -->
        <c:if test="${param.success != null}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle"></i> Action completed successfully!
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
                <form method="GET" action="${pageContext.request.contextPath}/variant" class="row g-3 align-items-end">

                    <!-- SEARCH -->
                    <div class="col-md-4">
                        <label class="form-label fw-semibold text-primary">Search</label>
                        <input type="text"
                               class="form-control"
                               name="search"
                               placeholder="Name, SKU..."
                               value="${searchValue}">
                    </div>

                    <!-- CATEGORY -->
                    <div class="col-md-3">
                        <label class="form-label fw-semibold text-primary">Category</label>
                        <select class="form-select" name="categoryId" id="categoryFilter">
                            <option value="">-- All Categories --</option>
                            <c:forEach var="c" items="${categories}">
                                <option value="${c.categoryId}"
                                        ${c.categoryId == selectedCategoryId ? 'selected' : ''}>
                                    ${c.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- MODEL -->
                    <div class="col-md-3">
                        <label class="form-label fw-semibold text-primary">Model</label>
                        <select class="form-select" name="modelId" id="modelFilter">
                            <option value="">-- All Models --</option>
                            <c:forEach var="m" items="${models}">
                                <option value="${m.modelId}"
                                        data-category="${m.categoryId}"
                                        ${m.modelId == selectedModelId ? 'selected' : ''}>
                                    ${m.brand} - ${m.modelName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- BUTTONS -->
                    <div class="col-md-2 d-flex gap-2">
                        <button class="btn btn-primary flex-fill">
                            <i class="fas fa-search"></i> Search
                        </button>
                        <c:if test="${not empty searchValue or selectedModelId != null or selectedCategoryId != null}">
                            <a href="${pageContext.request.contextPath}/variant"
                               class="btn btn-outline-secondary flex-fill">
                                <i class="fas fa-times"></i>
                            </a>
                        </c:if>
                    </div>

                </form>
            </div>
        </div>

        <!-- TABLE -->
        <div class="card border-primary">
            <div class="card-body">

                <c:if test="${empty variants}">
                    <p class="text-muted text-center py-4">
                        No variants found.
                    </p>
                </c:if>

                <c:if test="${not empty variants}">
                    <table class="table table-hover table-striped">
                        <thead class="table-primary">
                            <tr>
                                <th>Variant</th>
                                <th>SKU</th>
                                <th>Model</th>
                                <th>Category</th>
                                <th>Brand</th>
                                <th>Cost Price</th>
                                <th>Sale Price</th>
                                <th>Warranty Duration</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="v" items="${variants}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <img src="${not empty v.imageUrl ? v.imageUrl : pageContext.request.contextPath.concat('/assets/images/no-image.png')}"
                                                 class="variant-img">
                                            <span>${v.variantName}</span>
                                        </div>
                                    </td>

                                    <td><strong>${v.sku}</strong></td>
                                    <td>${v.modelName}</td>
                                    <td>
                                        <span class="badge bg-secondary">
                                            ${v.categoryName}
                                        </span>
                                    </td>
                                    <td>${v.brand}</td>
                                    <td>
                                        ${v.costPrice != null ? NumberUtil.formatCurrency(v.costPrice) : '-'}
                                    </td>
                                    <td class="text-primary fw-semibold">
                                        ${NumberUtil.formatCurrency(v.basePrice)}
                                    </td>
                                    <td>${v.warrantyMonths} months</td>
                                    <td>
                                        <span class="badge ${v.status == 'ACTIVE' ? 'bg-primary' : 'bg-secondary'}">
                                            ${v.status}
                                        </span>
                                    </td>
                                    <td>
                                        <a class="btn btn-sm btn-outline-primary"
                                           href="${pageContext.request.contextPath}/variant/form?id=${v.variantId}">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <a class="btn btn-sm btn-outline-danger"
                                           href="${pageContext.request.contextPath}/variant/delete?id=${v.variantId}"
                                           onclick="return confirm('Delete this variant?')">
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


<script>

document.addEventListener("DOMContentLoaded", function(){

    const category = document.getElementById("categoryFilter");
    const model = document.getElementById("modelFilter");

    function filterModels(){

        const selectedCategory = category.value;

        Array.from(model.options).forEach(function(option){

            const optionCategory = option.dataset.category;

            if(!optionCategory){
                return;
            }

            if(selectedCategory === "" || optionCategory === selectedCategory){
                option.style.display = "block";
            }else{
                option.style.display = "none";
            }

        });
    }

    category.addEventListener("change", filterModels);

    filterModels();

});

</script>
<jsp:include page="../common/footer.jsp" />
