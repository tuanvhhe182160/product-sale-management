<%@ page import="java.util.List" %>
<%@ page import="com.techshop.model.Branch" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% 

    List<String> errors = (List<String>) request.getAttribute("errorsList"); 
   
    Branch branch = new Branch();
    
if(request.getAttribute("branch") != null) {
branch = (Branch) request.getAttribute("branch");
    }
%>

<jsp:include page="../common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary">
        <i class="fas fa-plus-circle"></i>Branch Form
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
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="name" class="form-label">Branch Name<span class="text-danger">*</span></label>
                    <input type="text"
                           class="form-control"
                           id="name"
                           name="name"
                           value="<%= branch != null ? branch.getBranchName() : "" %>"
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
                           value="<%= branch != null ? branch.getBranchCode() : "" %>"
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
                       value="<%= branch != null ? branch.getAddress() : "" %>"
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
                           value="<%= branch != null ? branch.getPhone() : "" %>"
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
                        <option value="ACTIVE"
                                <%
                                    if (branch != null && "ACTIVE".equals(branch.getStatus())) {
                                %>
                                selected<% } %>>ACTIVE
                        </option>
                        <option value="INACTIVE"
                                <%
                                    if (branch != null && "INACTIVE".equals(branch.getStatus())) {
                                %>
                                selected<% } %>>INACTIVE
                        </option>
                    </select>
                </div>
            </div>

            <div class="text-end">
                <a href="${pageContext.request.contextPath}/branch"
                   class="btn btn-secondary">
                    <i class="fas fa-times"></i> Cancel
                </a>
                <button type="submit" class="btn btn-primary ms-2">
                    <i class="fas fa-save"></i> Submit
                </button>
            </div>
        </form>
    </div>
</div>

<script>

</script>

<jsp:include page="../common/footer.jsp"/>