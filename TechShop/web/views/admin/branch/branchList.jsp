<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../../common/header.jsp"/>

<style>
    body {
        font-family: Arial;
        background: #f5f9ff;
    }

    .container {
        width: 85%;
        margin: auto;
        background: white;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 0 10px #ddd;
    }

    h2 {
        color: #0a58ca;
    }

    table {
        width: 100%;
        border-collapse: collapse;
    }

    th {
        background: #0a58ca;
        color: white;
        padding: 10px;
    }

    td {
        padding: 10px;
        border-bottom: 1px solid #ddd;
    }

    .btn {
        background: #0a58ca;
        color: white;
        padding: 8px 15px;
        text-decoration: none;
        border-radius: 5px;
    }

    .edit {
        color: #0a58ca;
        font-weight: bold;
    }

    .delete {
        color: red;
        margin-left: 10px;
    }

    input {
        width: 100%;
        padding: 8px;
        margin: 6px 0;
    }

    button {
        background: #0a58ca;
        color: white;
        padding: 10px;
        border: none;
        border-radius: 5px;
    }

    .btn-back {
        margin-left: 10px;
    }
</style>

<div class="container">

    <h2>Branch Management</h2>

    <a class="btn" href="branch?action=add">+ Add Branch</a>

    <table>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Code</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Action</th>
        </tr>

        <c:forEach items="${list}" var="b">
            <tr>
                <td>${b.branchId}</td>
                <td>${b.branchName}</td>
                <td>${b.branchCode}</td>
                <td>${b.phone}</td>
                <td>${b.address}</td>
                <td>
                    <a class="edit" href="branch?action=edit&id=${b.branchId}">Edit</a>
                </td>
            </tr>
        </c:forEach>

    </table>

</div>

<jsp:include page="../../common/footer.jsp"/>