<%-- 
    Document   : adminModelForm
    Created on : Feb 2, 2026, 4:22:12 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="${pageTitle}" />
<%@ include file="../common/header.jsp" %>

<style>
    .page-wrap { max-width: 1080px; margin: 0 auto; }
    .form-card { border: 1px solid rgba(0,0,0,.08); border-radius: .5rem; }
    .form-card .card-header { background: #fff; border-bottom: 1px solid rgba(0,0,0,.06); }
    .form-label .req { color: #dc3545; font-weight: 700; }
</style>

<div class="page-wrap">
    <div class="card shadow-sm form-card">

        <div class="card-header py-3 d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center gap-2">
                <i class="fas ${mode == 'edit' ? 'fa-pen' : 'fa-plus'} text-primary"></i>
                <h5 class="mb-0 fw-bold">
                    <c:choose>
                        <c:when test="${mode == 'edit'}">Edit Model</c:when>
                        <c:otherwise>Create Model</c:otherwise>
                    </c:choose>
                </h5>
            </div>

            <a class="btn btn-outline-secondary btn-sm"
               href="${pageContext.request.contextPath}/model?categoryId=${categoryId}">
                Back
            </a>
        </div>

        <div class="card-body p-4">

            <!-- CHỈ 1 form dùng cho cả create + edit -->
            <form method="post" action="${pageContext.request.contextPath}/model/form">
                <input type="hidden" name="modelId" value="${model.modelId}" />
                <input type="hidden" name="categoryId" value="${categoryId}" />

                <div class="mb-3">
                    <label class="form-label fw-semibold">
                        Model Code <span class="req">*</span>
                    </label>
                    <input type="text" class="form-control" name="modelCode"
                           value="${model.modelCode}" maxlength="50" required />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">
                        Model Name <span class="req">*</span>
                    </label>
                    <input type="text" class="form-control" name="modelName"
                           value="${model.modelName}" maxlength="100" required />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Brand</label>
                    <input type="text" class="form-control" name="brand"
                           value="${model.brand}" maxlength="50" />
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Description</label>
                    <textarea class="form-control" name="description"
                              rows="4" maxlength="500">${model.description}</textarea>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">Status</label>
                    <select name="status" class="form-select">
                        <option value="ACTIVE" ${model.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                        <option value="INACTIVE" ${model.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                    </select>
                </div>

                <div class="d-flex justify-content-end gap-2">
                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/model?categoryId=${categoryId}">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>
                        <c:choose>
                            <c:when test="${mode == 'edit'}">Update</c:when>
                            <c:otherwise>Create</c:otherwise>
                        </c:choose>
                    </button>
                </div>

            </form>
        </div>

    </div>
</div>

<%@ include file="../common/footer.jsp" %>

