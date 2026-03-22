<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.techshop.model.Branch" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% List<Branch> branchList = (List<Branch>) request.getAttribute("branchList"); %>

<jsp:include page="../common/header.jsp"/>

<style>
    /* Applying tabular numbers for better vertical alignment of digits */
    .font-tabular {
        font-variant-numeric: tabular-nums;
    }

    /* Vertical alignment for table cells */
    .table td, .table th {
        vertical-align: middle;
    }
</style>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary">
        <i class="fas fa-building"></i> Branch Management
    </h2>
    <a href="${pageContext.request.contextPath}/branch?action=add" class="btn btn-primary">
        <i class="fas fa-plus"></i> New Branch
    </a>
</div>

<div class="d-flex justify-content-end mb-3">
    <form action="${pageContext.request.contextPath}/branch" method="get" class="d-flex align-items-center gap-2">
        <input type="hidden" name="page" value="1" />
        <label for="pageSizeSelect" class="form-label mb-0 fw-semibold text-primary small">Branches per page</label>
        <select id="pageSizeSelect" name="pageSize" class="form-select form-select-sm w-auto" onchange="this.form.submit()">
            <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
            <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
            <option value="0"  ${pageSize == 0  ? 'selected' : ''}>All</option>
        </select>
    </form>
</div>

<div class="card border-primary">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="text-center">
                    <tr>
                        <th scope="col" class="text-primary text-start">ID</th>
                        <th scope="col" class="text-primary text-start">Code</th>
                        <th scope="col" class="text-primary text-start">Name</th>
                        <th scope="col" class="text-primary text-start">Address</th>
                        <th scope="col" class="text-primary text-start">Phone Number</th>
                        <th scope="col" class="text-primary text-center">Status</th>
                        <th scope="col" class="text-primary text-center">Action</th>
                    </tr>
                </thead>

                <tbody class="font-tabular">
                    <%
                        if (branchList != null && !branchList.isEmpty()) {
                            for (Branch branch : branchList) {
                    %>
                    <tr class="text-secondary">
                        <td class="text-start text-muted">
                            <%= branch.getBranchId() %>
                        </td>
                        <td class="text-start text-primary">
                            <%= branch.getBranchCode() %>
                        </td>
                        <td class="text-start fw-bold">
                            <%= branch.getBranchName() %>
                        </td>
                        <td class="text-start">
                            <%= branch.getAddress() %>
                        </td>
                        <td class="text-start">
                            <%= branch.getPhone() %>
                        </td>
                        <td class="text-center">
                            <%
                                if("Active".equalsIgnoreCase(branch.getStatus())) {
                            %>
                            <span class="badge bg-primary">
                                ACTIVE
                            </span>
                            <%
                                } else {
                            %>
                            <span class="badge bg-secondary">
                                <%= branch.getStatus() %>
                            </span>
                            <%
                                }
                            %>
                        </td>
                        <td class="text-center">
                            <a class="btn btn-sm btn-primary"
                               href="${pageContext.request.contextPath}/branch?action=edit&id=<%= branch.getBranchId() %>">
                                <i class="fas fa-edit"></i> Edit
                            </a>
                        </td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="7" class="text-center text-muted">No branch found</td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>

    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination justify-content-center mb-0">
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}&pageSize=${pageSize}">&laquo;</a>
                </li>

                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:if test="${p >= currentPage - 2 && p <= currentPage + 2}">
                        <li class="page-item ${p == currentPage ? 'active' : ''}">
                            <a class="page-link" href="?page=${p}&pageSize=${pageSize}">${p}</a>
                        </li>
                    </c:if>
                </c:forEach>

                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage + 1}&pageSize=${pageSize}">&raquo;</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp"/>
