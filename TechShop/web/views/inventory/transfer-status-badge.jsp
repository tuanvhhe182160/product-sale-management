<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="s" value="${param.status}" />
<c:choose>
    <c:when test="${s == 'PENDING'}">
        <span class="badge bg-warning text-dark">PENDING</span>
    </c:when>
    <c:when test="${s == 'APPROVED'}">
        <span class="badge bg-primary">APPROVED</span>
    </c:when>
    <c:when test="${s == 'COMPLETED'}">
        <span class="badge bg-success">COMPLETED</span>
    </c:when>
    <c:when test="${s == 'REJECTED'}">
        <span class="badge bg-danger">REJECTED</span>
    </c:when>
    <c:otherwise>
        <span class="badge bg-secondary">${s}</span>
    </c:otherwise>
</c:choose>
