<%-- 
    Document   : categoryEdit
    Created on : Jan 21, 2026, 5:52:15 PM
    Author     : Admin
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Edit Category - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="card border-0 shadow-sm">
    <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
        <div>
            <h5 class="mb-0">Edit Category</h5>
            <small class="text-muted">Update category information</small>
        </div>

        <a class="btn btn-outline-secondary"
           href="${pageContext.request.contextPath}/admin/category">
            Back
        </a>
    </div>

    <div class="card-body">
        <c:if test="${not empty error}">
            <div class="alert alert-danger mb-3">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/ProductCategory/edit">
            <input type="hidden" name="categoryId" value="${category.categoryId}" />

            <div class="mb-3">
                <label class="form-label">Category Code</label>
                <input type="text" class="form-control" name="categoryCode"
                       value="${category.categoryCode}" maxlength="20" required />
            </div>

            <div class="mb-3">
                <label class="form-label">Category Name</label>
                <input type="text" class="form-control" name="categoryName"
                       value="${category.categoryName}" maxlength="100" required />
            </div>

            <div class="mb-3">
                <label class="form-label">Description</label>
                <textarea class="form-control" name="description"
                          rows="3" maxlength="255">${category.description}</textarea>
            </div>

            <div class="mb-3">
                <label class="form-label">Status</label>
                <select name="status" class="form-select">
                    <option value="ACTIVE" ${category.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                    <option value="INACTIVE" ${category.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                </select>
            </div>

            <div class="d-flex justify-content-end gap-2 pt-2">
                <a class="btn btn-outline-secondary"
                   href="${pageContext.request.contextPath}/admin/category">
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary">Update</button>
            </div>
        </form>

    </div>
</div>

<%@ include file="../common/footer.jsp" %>
