<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:include page="../common/header.jsp"/>

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

    <h2>Branch Form</h2>

    <form action="branch" method="post">

        <input type="hidden" name="branchId" value="${branch.branchId}">

        <label>Branch Name</label>
        <input type="text" name="branchName"
               value="${branch.branchName}" required>

        <label>Branch Code</label>
        <input type="text" name="branchCode"
               value="${branch.branchCode}" required>

        <label>Phone</label>
        <input type="text" name="phone"
               value="${branch.phone}">

        <label>Address</label>
        <input type="text" name="address"
               value="${branch.address}">

        <button type="submit">Save</button>
        <a class="btn-back" href="branch">Back</a>

    </form>

</div>

<jsp:include page="../common/footer.jsp"/>