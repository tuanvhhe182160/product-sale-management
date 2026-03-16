<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Bảo Hành - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container mt-4">
    <h2 class="mb-4 text-primary">Tra Cứu Thông Tin Bảo Hành</h2>
        
        <c:if test="${not empty param.message}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <strong>Thành công!</strong> ${param.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <strong>Lỗi hệ thống:</strong> ${param.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/cs/warranty" method="GET" class="d-flex align-items-center">
                <input type="hidden" name="action" value="check">
                <label for="imei" class="form-label me-3 mb-0 fw-bold">Nhập IMEI Sản phẩm:</label>
                <input type="text" class="form-control w-50 me-3" id="imei" name="imei" value="${imei}" required placeholder="VD: 359123456789012">
                <button type="submit" class="btn btn-primary">Tra cứu</button>
            </form>
        </div>
    </div>

    <c:if test="${not empty warrantyInfo}">
        <div class="row">
            <div class="col-md-6">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-info text-white fw-bold">Thông Tin Đơn Hàng & Bảo Hành</div>
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item"><strong>Sản phẩm:</strong> ${warrantyInfo.variantName}</li>
                        <li class="list-group-item"><strong>IMEI:</strong> ${warrantyInfo.imei}</li>
                        <li class="list-group-item"><strong>Khách hàng:</strong> ${warrantyInfo.customerName} - ${warrantyInfo.customerPhone}</li>
                        <li class="list-group-item"><strong>Mã Hóa Đơn:</strong> ${warrantyInfo.invoiceCode}</li>
                        <li class="list-group-item"><strong>Ngày Mua:</strong> <fmt:formatDate value="${warrantyInfo.invoiceDate}" pattern="dd/MM/yyyy HH:mm"/></li>
                        <li class="list-group-item"><strong>Hạn Bảo Hành:</strong> <fmt:formatDate value="${warrantyInfo.warrantyEndDate}" pattern="dd/MM/yyyy"/></li>
                        <li class="list-group-item">
                            <strong>Trạng Thái:</strong> 
                            <c:choose>
                                <c:when test="${warrantyInfo.warrantyStatus == 'VALID'}">
                                    <span class="badge bg-success">CÒN HẠN BẢO HÀNH</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">HẾT HẠN</span>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card shadow-sm h-100 border-primary">
                    <div class="card-header bg-primary text-white fw-bold">Tạo Yêu Cầu Bảo Hành</div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/cs/warranty" method="POST" onsubmit="return disableSubmit(this);">
                            <input type="hidden" name="action" value="create">
                            <input type="hidden" name="invoiceId" value="${warrantyInfo.invoiceId}">
                            <input type="hidden" name="physicalId" value="${warrantyInfo.physicalId}">
                            <input type="hidden" name="customerId" value="${warrantyInfo.customerId}">
                            <input type="hidden" name="imei" value="${warrantyInfo.imei}">
                            
                            <input type="hidden" name="customerEmail" value="${warrantyInfo.customerEmail}">
                            <input type="hidden" name="customerName" value="${warrantyInfo.customerName}">
                            <input type="hidden" name="variantName" value="${warrantyInfo.variantName}">

                            <div class="mb-3">
                                <label class="form-label fw-bold">Mô tả lỗi từ khách hàng <span class="text-danger">*</span></label>
                                <textarea class="form-control" name="issueDescription" rows="4" required placeholder="Ghi rõ tình trạng máy, vết xước (nếu có), lỗi khách báo..."></textarea>
                            </div>
                            
                            <button type="submit" id="btnSubmitWarranty" class="btn btn-primary w-100" 
                                    ${warrantyInfo.warrantyStatus == 'EXPIRED' ? 'disabled' : ''}>
                                Lập Phiếu Tiếp Nhận
                            </button>
                            <c:if test="${warrantyInfo.warrantyStatus == 'EXPIRED'}">
                                <small class="text-danger d-block mt-2 text-center">Sản phẩm đã hết hạn bảo hành, không thể lập phiếu trên hệ thống.</small>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>
<script>
    function disableSubmit(form) {
        var btn = document.getElementById("btnSubmitWarranty");
        btn.disabled = true; // Khóa nút ngay lập tức
        btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang tạo phiếu...';
        return true; // Cho phép form tiếp tục gửi đi
    }
</script>
<%@ include file="../common/footer.jsp" %>   