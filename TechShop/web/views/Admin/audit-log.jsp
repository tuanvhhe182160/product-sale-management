<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" />
<c:set var="pageTitle" value="Audit Logs - TechShop Admin" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0 text-gray-800">
            <i class="fas fa-shield-alt text-primary me-2"></i>
            Nhật Ký Hệ Thống (Audit Logs)
        </h2>
        <a href="${pageContext.request.contextPath}/admin/audit-logs?action=export&startDate=${startDate}&endDate=${endDate}&actionFilter=${actionFilter}&searchKeyword=${searchKeyword}"
           class="btn btn-success shadow-sm">
            <i class="fas fa-file-csv fa-sm text-white-50 me-1"></i>
            Xuất CSV
        </a>
    </div>


    <!-- FILTER CARD -->
    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">

            <form action="${pageContext.request.contextPath}/admin/audit-logs" method="GET" class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label fw-bold text-muted small">Từ ngày</label>
                    <input type="date" name="startDate" class="form-control" value="${startDate}" max="${today}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold text-muted small">Đến ngày</label>
                    <input type="date" name="endDate" class="form-control" value="${endDate}" max="${today}">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-bold text-muted small">Loại Hành Động</label>
                    <select name="actionFilter" class="form-select">
                        <option value="ALL">-- Tất cả --</option>
                        <c:forEach items="${actionList}" var="act">
                            <option value="${act}" ${actionFilter == act ? 'selected' : ''}>${act}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold text-muted small">Từ khóa</label>
                    <input type="text" name="searchKeyword" class="form-control"
                           placeholder="Tên user, entity, chi tiết..." value="${searchKeyword}">
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- LOG TABLE -->
    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th class="ps-3">ID</th>
                            <th>Thời Gian</th>
                            <th>Người Thực Hiện</th>
                            <th>Hành Động</th>
                            <th>Đối Tượng</th>
                            <th>IP Address</th>
                            <th>Chi Tiết</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty logs}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-4">
                                        <i class="fas fa-folder-open fa-2x mb-2 text-secondary d-block"></i>
                                        Không tìm thấy nhật ký nào phù hợp.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${logs}" var="log">
                                    <tr>
                                        <td class="ps-3 fw-bold text-muted">#${log.logId}</td>
                                        <td>${log.createdAt.toString().replace('T',' ').substring(0,19)}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty log.userName}">
                                                    <span class="fw-bold text-primary">${log.userName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted fst-italic">Hệ thống / Khách</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span class="badge bg-info text-dark border">${log.action}</span></td>
                                        <td>
                                            <c:if test="${not empty log.entityType}">
                                                ${log.entityType}
                                                <c:if test="${not empty log.entityId}">
                                                    <span class="text-muted">(ID: ${log.entityId})</span>
                                                </c:if>
                                            </c:if>
                                        </td>
                                        <td class="small font-monospace">${log.ipAddress}</td>
                                        <td class="small text-truncate" style="max-width:250px" title="${log.details}">${log.details}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- PAGINATION -->
        <c:if test="${totalPages > 1}">
            <div class="card-footer bg-white">
                <c:set var="pageSize" value="20"/>
                <c:set var="startLog" value="${(currentPage-1)*pageSize + 1}" />
                <c:set var="endLog" value="${currentPage*pageSize}" />
                <c:if test="${endLog > totalLogs}"><c:set var="endLog" value="${totalLogs}" /></c:if>
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted small">
                        Showing <strong>${startLog}-${endLog}</strong> of <strong>${totalLogs}</strong> logs (Page ${currentPage}/${totalPages})
                    </div>
                    <nav>
                        <ul class="pagination pagination-sm mb-0">
                            <c:if test="${currentPage > 1}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${currentPage-1}&startDate=${startDate}&endDate=${endDate}&actionFilter=${actionFilter}&searchKeyword=${searchKeyword}">
                                        <i class="fas fa-chevron-left"></i>
                                    </a>
                                </li>
                            </c:if>
                            <c:set var="startPage" value="${currentPage - 2}" />
                            <c:set var="endPage" value="${currentPage + 2}" />
                            <c:if test="${startPage < 1}"><c:set var="startPage" value="1"/><c:set var="endPage" value="5"/></c:if>
                            <c:if test="${endPage > totalPages}"><c:set var="endPage" value="${totalPages}"/><c:set var="startPage" value="${totalPages-4}"/></c:if>
                            <c:if test="${startPage < 1}"><c:set var="startPage" value="1"/></c:if>
                            <c:forEach begin="${startPage}" end="${endPage}" var="p">
                                <li class="page-item ${p == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="?page=${p}&startDate=${startDate}&endDate=${endDate}&actionFilter=${actionFilter}&searchKeyword=${searchKeyword}">${p}</a>
                                </li>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${currentPage+1}&startDate=${startDate}&endDate=${endDate}&actionFilter=${actionFilter}&searchKeyword=${searchKeyword}">
                                        <i class="fas fa-chevron-right"></i>
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </div>
            </div>
        </c:if>                    
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
