<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp" />

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="text-primary mb-0">
        <i class="fas fa-barcode"></i> Import Products
    </h2>
    <span class="badge bg-primary fs-6">
        <i class="fas fa-building"></i> ${sessionScope.branchName}
    </span>
</div>

<%-- Variant context card --%>
<div class="card border-primary mb-3 mx-auto" style="max-width: 800px;">
    <div class="card-header bg-primary text-white fw-semibold">
        <i class="fas fa-barcode"></i> Step 2 of 2 — Enter IMEIs
    </div>
    <div class="card-body py-3">
        <div class="d-flex gap-4">
            <div>
                <span class="text-muted small">Variant</span><br>
                <strong>${variant.variantName}</strong>
            </div>
            <div>
                <span class="text-muted small">SKU</span><br>
                <strong>${variant.sku}</strong>
            </div>
            <div>
                <span class="text-muted small">Units to import</span><br>
                <strong>${quantity}</strong>
            </div>
        </div>
    </div>
</div>

<div class="card border-primary mx-auto" style="max-width: 800px;">
    <div class="card-body">

        <c:if test="${dateError != null}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> ${dateError}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/inventory/import/imei">
            <input type="hidden" name="variantId" value="${variant.variantId}">
            <input type="hidden" name="quantity"  value="${quantity}">

            <div class="mb-4" style="max-width: 220px;">
                <label class="form-label fw-semibold">
                    Import Date <span class="text-danger">*</span>
                </label>
                <input type="date" class="form-control" name="importDate"
                       id="importDate" value="${importDate}" required>
            </div>

            <table class="table table-bordered align-middle mb-4">
                <thead class="table-primary">
                    <tr>
                        <th style="width: 48px;">#</th>
                        <th>IMEI <span class="text-danger">*</span></th>
                        <th>Serial Number <span class="text-muted fw-normal">(optional)</span></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach begin="0" end="${quantity - 1}" var="i">
                        <c:set var="hasError" value="${errors[i] != null && not empty errors[i]}" />
                        <tr class="${hasError ? 'table-danger' : ''}">
                            <td class="text-muted fw-semibold text-center">${i + 1}</td>
                            <td>
                                <input type="text" id="imei_${i}"
                                       class="form-control form-control-sm ${hasError ? 'is-invalid' : ''}"
                                       name="imei_${i}" value="${imeis[i]}"
                                       placeholder="Enter or scan IMEI" autocomplete="off">
                                <c:if test="${hasError}">
                                    <div class="invalid-feedback">${errors[i]}</div>
                                </c:if>
                            </td>
                            <td>
                                <input type="text"
                                       class="form-control form-control-sm"
                                       name="serial_${i}" value="${serials[i]}"
                                       placeholder="Optional">
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-success">
                    <i class="fas fa-check"></i> Confirm Import
                </button>
                <a href="${pageContext.request.contextPath}/inventory/import" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left"></i> Back
                </a>
            </div>
        </form>

    </div>
</div>

<script>
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth()+1; //January is 0!
    var yyyy = today.getFullYear();
    if(dd<10){
        dd='0'+dd
    }
    if(mm<10){
        mm='0'+mm
    }

    today = yyyy+'-'+mm+'-'+dd;
    // Set today as the max date for import date picker
    document.getElementById("importDate").setAttribute("max", today);

    // Auto-advance to next IMEI field on Enter key
    var imeiInputs = Array.from(document.querySelectorAll('input[name^="imei_"]'));
    imeiInputs.forEach(function(input, index) {
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                var next = imeiInputs[index + 1];
                if (next) {
                    next.focus();
                } else {
                    document.querySelector('[type="submit"]').focus();
                }
            }
        });
    });

    // Auto-focus the first empty IMEI field
    var firstEmpty = imeiInputs.find(function(input) { return !input.value; });
    if (firstEmpty) firstEmpty.focus();
</script>

<jsp:include page="../common/footer.jsp" />
