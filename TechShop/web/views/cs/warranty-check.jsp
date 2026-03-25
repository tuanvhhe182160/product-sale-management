<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Tra Cứu Bảo Hành - TechShop" />
<%@ include file="../common/header.jsp" %>

<div class="container-fluid px-4 py-4">

    <h2 class="mb-4"><i class="fas fa-tools text-primary me-2"></i>Tra Cứu & Tiếp Nhận Bảo Hành</h2>

    <%-- Flash messages --%>
    <c:if test="${not empty param.message}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <i class="fas fa-check-circle me-2"></i><strong>Thành công!</strong> ${param.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i><strong>Lỗi:</strong> ${param.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-warning shadow-sm" role="alert">
            <i class="fas fa-search me-2"></i>${error}
        </div>
    </c:if>

    <%-- Search box --%>
    <div class="card shadow-sm mb-4 border-0">
        <div class="card-body bg-light rounded">
            <form action="${pageContext.request.contextPath}/cs/warranty"
                  method="GET" class="row g-2 align-items-end">
                <input type="hidden" name="action" value="check">
                <div class="col-md-6">
                    <label class="form-label fw-bold small text-muted">IMEI sản phẩm</label>
                    <input type="text" name="imei" id="imeiInput"
                           class="form-control form-control-lg"
                           value="${imei}" required
                           placeholder="VD: 359123456789012"
                           autofocus>
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary btn-lg px-4">
                        <i class="fas fa-search me-2"></i>Tra cứu
                    </button>
                </div>
            </form>
        </div>
    </div>

    <%-- Results --%>
    <c:if test="${not empty warrantyInfo}">
        <div class="row g-4">

            <%-- LEFT: product + warranty info --%>
            <div class="col-lg-5">
                <div class="card shadow-sm border-0 h-100">
                    <div class="card-header bg-info text-white fw-bold">
                        <i class="fas fa-info-circle me-2"></i>Thông Tin Sản Phẩm & Bảo Hành
                    </div>
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">Sản phẩm</span>
                            <strong class="text-end">${warrantyInfo.variantName}</strong>
                        </li>
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">IMEI</span>
                            <code>${warrantyInfo.imei}</code>
                        </li>
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">Khách hàng</span>
                            <strong>${warrantyInfo.customerName}</strong>
                        </li>
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">Số điện thoại</span>
                            <span>${warrantyInfo.customerPhone}</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">Mã hóa đơn</span>
                            <span>${warrantyInfo.invoiceCode}</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">Ngày mua</span>
                            <span><fmt:formatDate value="${warrantyInfo.invoiceDate}"
                                                  pattern="dd/MM/yyyy HH:mm"/></span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between">
                            <span class="text-muted">Hết hạn bảo hành</span>
                            <strong><fmt:formatDate value="${warrantyInfo.warrantyEndDate}"
                                                    pattern="dd/MM/yyyy"/></strong>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <span class="text-muted">Trạng thái bảo hành</span>
                            <c:choose>
                                <c:when test="${warrantyInfo.warrantyStatus == 'VALID'}">
                                    <span class="badge bg-success fs-6 px-3 py-2">
                                        <i class="fas fa-check-circle me-1"></i>Còn hiệu lực
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger fs-6 px-3 py-2">
                                        <i class="fas fa-times-circle me-1"></i>Hết hạn
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>
            </div>

            <%-- RIGHT: create warranty form --%>
            <div class="col-lg-7">
                <div class="card shadow-sm border-0 h-100
                    ${warrantyInfo.warrantyStatus == 'EXPIRED' ? 'border-top border-danger border-3' : 'border-top border-primary border-3'}">
                    <div class="card-header fw-bold
                        ${warrantyInfo.warrantyStatus == 'EXPIRED' ? 'bg-secondary' : 'bg-primary'} text-white">
                        <i class="fas fa-clipboard-list me-2"></i>Tạo Phiếu Tiếp Nhận Bảo Hành
                    </div>
                    <div class="card-body">

                        <c:if test="${warrantyInfo.warrantyStatus == 'EXPIRED'}">
                            <div class="alert alert-warning">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Sản phẩm đã <strong>hết hạn bảo hành</strong>.
                                Không thể tạo phiếu tiêu chuẩn. Liên hệ quản lý nếu cần xử lý ngoại lệ.
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/cs/warranty"
                              method="POST"
                              enctype="multipart/form-data"
                              onsubmit="return doSubmit(this);"
                              id="warrantyForm">

                            <input type="hidden" name="action"        value="create">
                            <input type="hidden" name="invoiceId"     value="${warrantyInfo.invoiceId}">
                            <input type="hidden" name="physicalId"    value="${warrantyInfo.physicalId}">
                            <input type="hidden" name="customerId"    value="${warrantyInfo.customerId}">
                            <input type="hidden" name="imei"          value="${warrantyInfo.imei}">
                            <input type="hidden" name="customerEmail" value="${warrantyInfo.customerEmail}">
                            <input type="hidden" name="customerName"  value="${warrantyInfo.customerName}">
                            <input type="hidden" name="variantName"   value="${warrantyInfo.variantName}">

                            <%-- Issue description --%>
                            <div class="mb-3">
                                <label class="form-label fw-bold">
                                    Mô tả lỗi từ khách hàng
                                    <span class="text-danger">*</span>
                                </label>
                                <textarea class="form-control" name="issueDescription"
                                          rows="4" required
                                          placeholder="Ghi rõ tình trạng máy, vết xước (nếu có), lỗi khách báo...&#10;VD: Màn hình bị ố vàng góc trên phải, máy không sạc được."></textarea>
                                <div class="form-text">Mô tả càng chi tiết, kỹ thuật viên xử lý càng nhanh.</div>
                            </div>

                            <%-- Image upload --%>
                            <div class="mb-4">
                                <label class="form-label fw-bold">
                                    Ảnh minh họa
                                    <span class="text-muted fw-normal">(không bắt buộc, tối đa 5 MB)</span>
                                </label>

                                <div id="dropZone"
                                     onclick="document.getElementById('imageInput').click()"
                                     style="border:2px dashed var(--bs-border-color);border-radius:8px;
                                            padding:20px;text-align:center;cursor:pointer;
                                            transition:border-color 0.2s,background 0.2s;"
                                     ondragover="dragOver(event)"
                                     ondragleave="dragLeave(event)"
                                     ondrop="dropFile(event)">
                                    <i class="fas fa-cloud-upload-alt fa-2x text-muted mb-2 d-block"></i>
                                    <span class="text-muted small">Kéo thả ảnh vào đây, hoặc click để chọn file</span>
                                    <br><small class="text-muted" style="font-size:11px;">JPG · PNG · WEBP &nbsp;|&nbsp; Tối đa 5 MB</small>
                                    <input type="file" name="image" id="imageInput"
                                           accept=".jpg,.jpeg,.png,.webp"
                                           style="display:none"
                                           onchange="showPreview(this)">
                                </div>

                                <%-- Preview --%>
                                <div id="previewWrap" class="mt-3 d-none">
                                    <div class="d-flex align-items-center gap-3 p-2 border rounded bg-light">
                                        <img id="previewImg" src="" alt="preview"
                                             style="width:80px;height:80px;object-fit:cover;border-radius:6px;border:1px solid #dee2e6;">
                                        <div class="flex-grow-1 small">
                                            <div id="previewName" class="fw-bold text-truncate" style="max-width:260px;"></div>
                                            <div id="previewSize" class="text-muted"></div>
                                        </div>
                                        <button type="button" class="btn btn-sm btn-outline-danger"
                                                onclick="clearImage()">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                </div>
                                <div id="imgError" class="text-danger small mt-1 d-none"></div>
                            </div>

                            <button type="submit" id="btnSubmit"
                                    class="btn btn-primary w-100 btn-lg"
                                    ${warrantyInfo.warrantyStatus == 'EXPIRED' ? 'disabled' : ''}>
                                <i class="fas fa-clipboard-check me-2"></i>Lập Phiếu Tiếp Nhận
                            </button>

                        </form>
                    </div>
                </div>
            </div>

        </div><%-- /row --%>
    </c:if>

</div>

<script>
const MAX_MB = 5;

function showPreview(input) {
    const file    = input.files[0];
    const errEl   = document.getElementById('imgError');
    const wrap    = document.getElementById('previewWrap');
    errEl.classList.add('d-none');

    if (!file) { wrap.classList.add('d-none'); return; }

    if (file.size > MAX_MB * 1024 * 1024) {
        errEl.textContent = 'Ảnh vượt quá ' + MAX_MB + ' MB.';
        errEl.classList.remove('d-none');
        input.value = '';
        wrap.classList.add('d-none');
        return;
    }

    const reader = new FileReader();
    reader.onload = e => {
        document.getElementById('previewImg').src  = e.target.result;
        document.getElementById('previewName').textContent = file.name;
        document.getElementById('previewSize').textContent =
            (file.size / 1024 / 1024).toFixed(2) + ' MB';
        wrap.classList.remove('d-none');
    };
    reader.readAsDataURL(file);
}

function clearImage() {
    document.getElementById('imageInput').value = '';
    document.getElementById('previewWrap').classList.add('d-none');
}

function dragOver(e) {
    e.preventDefault();
    const z = document.getElementById('dropZone');
    z.style.borderColor = '#0d6efd';
    z.style.background  = '#e8f0fe';
}
function dragLeave(e) {
    const z = document.getElementById('dropZone');
    z.style.borderColor = '';
    z.style.background  = '';
}
function dropFile(e) {
    e.preventDefault();
    dragLeave(e);
    const file = e.dataTransfer.files[0];
    if (!file) return;
    const input = document.getElementById('imageInput');
    const dt = new DataTransfer();
    dt.items.add(file);
    input.files = dt.files;
    showPreview(input);
}

function doSubmit(form) {
    const btn = document.getElementById('btnSubmit');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang tạo phiếu...';
    return true;
}
</script>

<%@ include file="../common/footer.jsp" %>
