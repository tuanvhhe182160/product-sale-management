<%-- 
    Document   : adminEditModel
    Created on : Jan 20, 2026, 9:31:21 PM
    Author     : Admin
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form method="post" action="${pageContext.request.contextPath}/ProductModel/edit">
    <input type="hidden" name="modelId" value="${model.modelId}" />
    <input type="hidden" name="categoryId" value="${model.categoryId}" />

    <div class="mb-3">
        <label class="form-label">Model Code</label>
        <input type="text" class="form-control" name="modelCode"
               value="${model.modelCode}" maxlength="50" required />
    </div>

    <div class="mb-3">
        <label class="form-label">Model Name</label>
        <input type="text" class="form-control" name="modelName"
               value="${model.modelName}" maxlength="100" required />
    </div>

    <div class="mb-3">
        <label class="form-label">Brand</label>
        <input type="text" class="form-control" name="brand"
               value="${model.brand}" maxlength="50" />
    </div>

    <div class="mb-3">
        <label class="form-label">Description</label>
        <textarea class="form-control" name="description"
                  rows="3" maxlength="500">${model.description}</textarea>
    </div>

    <div class="mb-3">
        <label class="form-label">Status</label>
        <select name="status" class="form-select">
            <option value="ACTIVE" ${model.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
            <option value="INACTIVE" ${model.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
        </select>
    </div>

    <div class="d-flex justify-content-end gap-2 pt-2">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
        <button type="submit" class="btn btn-primary">Update</button>
    </div>
</form>

