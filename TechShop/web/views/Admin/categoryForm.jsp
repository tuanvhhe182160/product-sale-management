<%-- 
    Document   : categoryForm
    Created on : Jan 29, 2026, 10:11:39 AM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="isEdit" value="${not empty category and not empty category.categoryId}" />

<c:set var="pageTitle" value="${isEdit ? 'Edit Category - TechShop' : 'Create Category'}" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4">
    <div class="card shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
                <i class="fas ${isEdit ? 'fa-edit' : 'fa-plus'} text-primary me-2"></i>
                ${isEdit ? 'Edit Category' : 'Create New Category'}
            </h5>

            <a class="btn btn-outline-secondary"
               href="${pageContext.request.contextPath}/category">
                Back
            </a>
        </div>

        <div class="card-body">

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/category/save">

                <input type="hidden" name="categoryId" value="${isEdit ? category.categoryId : ''}" />

                <div class="mb-3">
                    <label class="form-label">Category Code *</label>
                    <input type="text" name="categoryCode" class="form-control"
                           value="${isEdit ? category.categoryCode : categoryCode}"
                           maxlength="20" required />
                </div>

                <div class="mb-3">
                    <label class="form-label">Category Name *</label>
                    <input type="text" name="categoryName" class="form-control"
                           value="${isEdit ? category.categoryName : categoryName}"
                           maxlength="100" required />
                </div>

                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="form-control" rows="3" maxlength="255"
                    >${isEdit ? category.description : description}</textarea>
                </div>

                <c:choose>
                    <c:when test="${isEdit}">
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select">
                                <option value="ACTIVE" ${category.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                <option value="INACTIVE" ${category.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                            </select>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="status" value="ACTIVE"/>
                    </c:otherwise>
                </c:choose>

                <div class="d-flex justify-content-end gap-2">
                    <a href="${pageContext.request.contextPath}/category"
                       class="btn btn-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i>
                        ${isEdit ? 'Update' : 'Create'}
                    </button>
                </div>
            </form>

        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>

