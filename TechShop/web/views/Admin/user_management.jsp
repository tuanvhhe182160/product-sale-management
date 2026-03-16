<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Quản lý Nhân viên - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
.user-table th { font-size:.78rem; text-transform:uppercase; letter-spacing:.05em; color:#6b7280; }
.user-table td { vertical-align:middle; font-size:.88rem; }
.status-active { background:#d1fae5; color:#065f46; font-weight:700; font-size:.75rem; padding:3px 10px; border-radius:20px; }
.status-inactive { background:#fee2e2; color:#991b1b; font-weight:700; font-size:.75rem; padding:3px 10px; border-radius:20px; }
</style>

<!-- Flash messages -->
<c:if test="${not empty sessionScope.userMgmtSuccess}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i>${sessionScope.userMgmtSuccess}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div><c:remove var="userMgmtSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.userMgmtError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i>${sessionScope.userMgmtError}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div><c:remove var="userMgmtError" scope="session"/>
</c:if>

<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <h4 class="mb-0 fw-bold"><i class="fas fa-users text-primary me-2"></i>Quản lý Nhân viên</h4>
    <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addUserModal">
        <i class="fas fa-user-plus me-1"></i>Thêm Nhân viên
    </button>
</div>

<!-- Bộ lọc -->
<div class="card mb-4">
    <div class="card-body py-3">
        <form method="get" action="${pageContext.request.contextPath}/user" class="row g-2 align-items-end">
            <div class="col-md-3">
                <label class="form-label small fw-bold">Tìm kiếm</label>
                <input type="text" name="search" class="form-control form-control-sm"
                       placeholder="Tên, email, SĐT..." value="${search}">
            </div>
            <div class="col-md-2">
                <label class="form-label small fw-bold">Vai trò</label>
                <select name="roleId" class="form-select form-select-sm">
                    <option value="">Tất cả</option>
                    <c:forEach var="r" items="${roles}">
                        <option value="${r.roleId}" <c:if test="${roleFilter eq r.roleId}">selected</c:if>>${r.roleName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small fw-bold">Chi nhánh</label>
                <select name="branchId" class="form-select form-select-sm">
                    <option value="">Tất cả</option>
                    <c:forEach var="b" items="${branches}">
                        <option value="${b.branchId}" <c:if test="${branchFilter eq b.branchId}">selected</c:if>>${b.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small fw-bold">Trạng thái</label>
                <select name="status" class="form-select form-select-sm">
                    <option value="ALL" <c:if test="${statusFilter == 'ALL'}">selected</c:if>>Tất cả</option>
                    <option value="ACTIVE" <c:if test="${statusFilter == 'ACTIVE'}">selected</c:if>>Hoạt động</option>
                    <option value="INACTIVE" <c:if test="${statusFilter == 'INACTIVE'}">selected</c:if>>Vô hiệu</option>
                </select>
            </div>
            <div class="col-md-1">
                <button type="submit" class="btn btn-primary btn-sm w-100"><i class="fas fa-search"></i></button>
            </div>
        </form>
    </div>
</div>

<!-- Bảng nhân viên -->
<div class="card">
    <div class="card-header bg-white py-3">
        <span class="fw-bold"><i class="fas fa-list me-1"></i>Danh sách (${users.size()} nhân viên)</span>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty users}">
                <p class="text-muted text-center py-4">Không tìm thấy nhân viên nào.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover user-table mb-0">
                        <thead><tr>
                            <th>#</th><th>Họ tên</th><th>Email</th><th>SĐT</th>
                            <th>Vai trò</th><th>Chi nhánh</th><th>Trạng thái</th><th class="text-center">Thao tác</th>
                        </tr></thead>
                        <tbody>
                        <c:forEach var="u" items="${users}" varStatus="st">
                            <tr>
                                <td>${st.index + 1}</td>
                                <td class="fw-semibold">${u.fullName}</td>
                                <td>${u.email}</td>
                                <td><c:choose><c:when test="${not empty u.phone}">${u.phone}</c:when><c:otherwise><span class="text-muted">—</span></c:otherwise></c:choose></td>
                                <td><span class="badge bg-secondary">${u.roleName}</span></td>
                                <td><c:choose><c:when test="${not empty u.branchName}">${u.branchName}</c:when><c:otherwise><span class="text-muted">—</span></c:otherwise></c:choose></td>
                                <td><c:choose>
                                    <c:when test="${u.status == 'ACTIVE'}"><span class="status-active">Hoạt động</span></c:when>
                                    <c:otherwise><span class="status-inactive">Vô hiệu</span></c:otherwise>
                                </c:choose></td>
                                <td class="text-center">
                                    <button class="btn btn-outline-primary btn-sm me-1" title="Sửa"
                                            onclick="openEdit(${u.userId},'${u.email}','${u.fullName}','${u.phone}',${u.roleId},${u.branchId != null ? u.branchId : 0},'${u.status}')">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <c:choose>
                                        <c:when test="${u.status == 'ACTIVE'}">
                                            <a href="${pageContext.request.contextPath}/user?action=toggle&userId=${u.userId}&newStatus=INACTIVE"
                                               class="btn btn-outline-danger btn-sm" onclick="return confirm('Vô hiệu hóa ${u.fullName}?')"><i class="fas fa-user-slash"></i></a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/user?action=toggle&userId=${u.userId}&newStatus=ACTIVE"
                                               class="btn btn-outline-success btn-sm" onclick="return confirm('Kích hoạt ${u.fullName}?')"><i class="fas fa-user-check"></i></a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Modal Thêm Nhân viên -->
<div class="modal fade" id="addUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/user?action=add">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-user-plus me-1"></i>Thêm Nhân viên</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Email <span class="text-danger">*</span></label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Họ tên <span class="text-danger">*</span></label>
                        <input type="text" name="fullName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">SĐT</label>
                        <input type="text" name="phone" class="form-control">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Vai trò <span class="text-danger">*</span></label>
                        <select name="roleId" class="form-select" required>
                            <c:forEach var="r" items="${roles}">
                                <option value="${r.roleId}">${r.roleName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Chi nhánh</label>
                        <select name="branchId" class="form-select">
                            <option value="">-- Không (Admin) --</option>
                            <c:forEach var="b" items="${branches}">
                                <option value="${b.branchId}">${b.branchName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i>Thêm</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Sửa Nhân viên -->
<div class="modal fade" id="editUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/user?action=edit">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-edit me-1"></i>Sửa Nhân viên</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="userId" id="editUserId">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Email</label>
                        <input type="email" name="email" id="editEmail" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Họ tên</label>
                        <input type="text" name="fullName" id="editFullName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">SĐT</label>
                        <input type="text" name="phone" id="editPhone" class="form-control">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Vai trò</label>
                        <select name="roleId" id="editRoleId" class="form-select" required>
                            <c:forEach var="r" items="${roles}">
                                <option value="${r.roleId}">${r.roleName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Chi nhánh</label>
                        <select name="branchId" id="editBranchId" class="form-select">
                            <option value="">-- Không --</option>
                            <c:forEach var="b" items="${branches}">
                                <option value="${b.branchId}">${b.branchName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Trạng thái</label>
                        <select name="status" id="editStatus" class="form-select">
                            <option value="ACTIVE">Hoạt động</option>
                            <option value="INACTIVE">Vô hiệu</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i>Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function openEdit(id, email, name, phone, roleId, branchId, status) {
    document.getElementById('editUserId').value = id;
    document.getElementById('editEmail').value = email;
    document.getElementById('editFullName').value = name;
    document.getElementById('editPhone').value = phone || '';
    document.getElementById('editRoleId').value = roleId;
    document.getElementById('editBranchId').value = branchId > 0 ? branchId : '';
    document.getElementById('editStatus').value = status;
    new bootstrap.Modal(document.getElementById('editUserModal')).show();
}
</script>

<%@ include file="../common/footer.jsp" %>
