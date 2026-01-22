<%@ page import="java.util.List" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% List<String> errors = (List<String>) request.getAttribute("errorsList"); %>

<jsp:include page="../common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary">
        <i class="fas fa-plus-circle"></i> Add New Branch
    </h2>
    <a href="${pageContext.request.contextPath}/branch"
       class="btn btn-primary">
        <i class="fas fa-arrow-left"></i> Return to Branch List
    </a>
</div>

<!-- Server-side validation message -->
<%
    if (errors != null && !errors.isEmpty()) {
%>
<div class="alert alert-danger alert-dismissible fade show" role="alert">
    <%
        for (String err : errors) {
    %>
    <div><i class="fas fa-exclamation-circle"></i> <%= err %>
    </div>
    <%
        }
    %>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<%
        request.removeAttribute("errorsList");
    }
%>

<div class="card border-primary">
    <div class="card-body">
        <form id="addBranchForm" method="post" action="branch"<%--onsubmit="return validateForm()"--%>>
            <input type="hidden" name="action" value="insert">

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="name" class="form-label">Branch Name<span class="text-danger">*</span></label>
                    <input type="text"
                           class="form-control"
                           id="name"
                           name="name"
                           required
                           maxlength="100"
                           placeholder="Enter branch name">
                </div>

                <div class="col-md-6 mb-3">
                    <label for="code" class="form-label">Branch Code<span class="text-danger">*</span></label>
                    <input type="text"
                           class="form-control"
                           id="code"
                           name="code"
                           required
                           maxlength="20"
                           placeholder="ABC123">
                </div>
            </div>

            <div class="mb-3">
                <label for="address" class="form-label">Address<span class="text-danger">*</span></label>
                <input type="text"
                       class="form-control"
                       id="address"
                       name="address"
                       required
                       maxlength="255"
                       placeholder="123 Main Street">
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="phone" class="form-label">Phone Number<span class="text-danger">*</span></label>
                    <input type="tel"
                           class="form-control"
                           id="phone"
                           name="phone"
                           required
                           pattern="[0-9]{10,11}"
                           maxlength="11"
                           placeholder="0123456789">
                </div>

                <div class="col-md-6 mb-3">
                    <label for="status" class="form-label">Status<span class="text-danger">*</span></label>
                    <select class="form-select"
                            id="status"
                            name="status"
                            required>
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE</option>
                    </select>
                </div>
            </div>

            <div class="text-end">
                <a href="${pageContext.request.contextPath}/branch"
                   class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
                <button type="submit" class="btn btn-primary ms-2">
                    <i class="fas fa-save"></i> Create Branch
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    // function validateForm() {
    //
    //     let name = document.forms[0]["name"].value.trim();
    //     let address = document.forms[0]["address"].value.trim();
    //     let phone = document.forms[0]["phone"].value.trim();
    //
    //     if (name.length < 3) {
    //         alert("Branch name must be at least 3 characters");
    //         return false;
    //     }
    //
    //     if (address.length < 5) {
    //         alert("Address must be at least 5 characters");
    //         return false;
    //     }
    //
    //     if (!/^[0-9]{9,11}$/.test(phone)) {
    //         alert("Phone number must contain 9 to 11 digits");
    //         return false;
    //     }
    //
    //     return true;
    // }
</script>

<jsp:include page="../common/footer.jsp"/>