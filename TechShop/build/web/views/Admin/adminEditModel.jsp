<%-- 
    Document   : adminEditModel
    Created on : Jan 20, 2026, 9:31:21 PM
    Author     : Admin
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Edit Model - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
    .page-wrap {
        max-width: 1080px;
        margin: 0 auto;
    }
    .form-card {
        border: 1px solid rgba(0,0,0,.08);
        border-radius: .5rem;
    }
    .form-card .card-header {
        background: #fff;
        border-bottom: 1px solid rgba(0,0,0,.06);
    }
    .form-label .req {
        color: #dc3545;
        font-weight: 700;
    }
</style>

<div class="page-wrap">

    <div class="card shadow-sm form-card">
        <!-- Header giống ảnh: icon + title bên trái, Back bên phải -->
        <div class="card-header py-3 d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-2">
                <i class="fas fa-pen text-primary"></i>
                <h5 class="mb-0 fw-bold">Edit Model</h5>
            </div>

            <a class="btn btn-outline-secondary btn-sm"
               href="${pageContext.request.contextPath}/ProductModel?categoryId=${model.categoryId}">
                Back
            </a>
        </div>

        <div class="card-body p-4">
            <form method="post" action="${pageContext.request.contextPath}/ProductModel/edit">

                <input type="hidden" name="modelId" value="${model.modelId}" />
                <input type="hidden" name="categoryId" value="${model.categoryId}" />

                <!-- Model Code -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">
                        Model Code <span class="req">*</span>
                    </label>
                    <input type="text"
                           class="form-control"
                           name="modelCode"
                           value="${model.modelCode}"
                           maxlength="50"
                           required />
                </div>

                <!-- Model Name -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">
                        Model Name <span class="req">*</span>
                    </label>
                    <input type="text"
                           class="form-control"
                           name="modelName"
                           value="${model.modelName}"
                           maxlength="100"
                           required />
                </div>

                <!-- Brand -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Brand</label>
                    <input type="text"
                           class="form-control"
                           name="brand"
                           value="${model.brand}"
                           maxlength="50" />
                </div>

                <!-- Description -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Description</label>
                    <textarea class="form-control"
                              name="description"
                              rows="4"
                              maxlength="500">${model.description}</textarea>
                </div>

                <!-- Status -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">Status</label>
                    <select name="status" class="form-select">
                        <option value="ACTIVE" ${model.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                        <option value="INACTIVE" ${model.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                    </select>
                </div>

                <!-- Footer buttons giống ảnh: Cancel xám + Update xanh, góc phải -->
                <div class="d-flex justify-content-end gap-2">
                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/ProductModel?categoryId=${model.categoryId}">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i> Update
                    </button>
                </div>

            </form>
        </div>
    </div>

</div>

<%@ include file="../common/footer.jsp" %>
