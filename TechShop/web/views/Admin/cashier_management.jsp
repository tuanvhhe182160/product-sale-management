<%-- Quản lý Cashier - Admin / Shop Manager --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Quản lý Cashier - TechShop" />
<%@ include file="../common/header.jsp" %>

<style>
.stat-card { border-radius:12px; padding:20px; color:#fff; position:relative; overflow:hidden; }
.stat-card .stat-icon { position:absolute; right:15px; top:15px; font-size:2.5rem; opacity:.2; }
.stat-card .stat-value { font-size:1.8rem; font-weight:800; }
.stat-card .stat-label { font-size:.82rem; opacity:.85; margin-top:2px; }
.bg-gradient-blue { background:linear-gradient(135deg,#667eea,#764ba2); }
.bg-gradient-green { background:linear-gradient(135deg,#11998e,#38ef7d); }
.bg-gradient-orange { background:linear-gradient(135deg,#f2994a,#f2c94c); }
.bg-gradient-pink { background:linear-gradient(135deg,#ee5a6f,#f093fb); }
.cashier-table th { font-size:.78rem; text-transform:uppercase; letter-spacing:.05em; color:#6b7280; }
.cashier-table td { vertical-align:middle; font-size:.88rem; }
.status-active { background:#d1fae5; color:#065f46; font-weight:700; font-size:.75rem; padding:3px 10px; border-radius:20px; }
.status-inactive { background:#fee2e2; color:#991b1b; font-weight:700; font-size:.75rem; padding:3px 10px; border-radius:20px; }
.detail-stats { background:#f8fafc; border:1px solid #e2e8f0; border-radius:12px; padding:20px; }
.detail-stats .ds-value { font-size:1.4rem; font-weight:800; color:#1e293b; }
.detail-stats .ds-label { font-size:.78rem; color:#64748b; }
</style>

<!-- Flash messages -->
<c:if test="${not empty sessionScope.mgmtSuccess}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i>${sessionScope.mgmtSuccess}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="mgmtSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.mgmtError}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i>${sessionScope.mgmtError}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="mgmtError" scope="session"/>
</c:if>

<!-- Page header -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <h4 class="mb-0 fw-bold">
        <i class="fas fa-users-cog text-primary me-2"></i>Quản lý Cashier
    </h4>
    <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addCashierModal">
        <i class="fas fa-user-plus me-1"></i>Thêm Cashier
    </button>
</div>

<!-- Thống kê chi nhánh (nếu có) -->
<c:if test="${not empty branchStats}">
<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="stat-card bg-gradient-blue">
            <div class="stat-icon"><i class="fas fa-users"></i></div>
            <div class="stat-value">${branchStats[3].intValue()}</div>
            <div class="stat-label">Cashier đang hoạt động</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card bg-gradient-green">
            <div class="stat-icon"><i class="fas fa-receipt"></i></div>
            <div class="stat-value">${branchStats[0].intValue()}</div>
            <div class="stat-label">Hóa đơn hôm nay</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card bg-gradient-orange">
            <div class="stat-icon"><i class="fas fa-coins"></i></div>
            <div class="stat-value"><fmt:formatNumber value="${branchStats[1]}" type="number" groupingUsed="true"/>đ</div>
            <div class="stat-label">Doanh thu hôm nay</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-card bg-gradient-pink">
            <div class="stat-icon"><i class="fas fa-chart-line"></i></div>
            <div class="stat-value"><fmt:formatNumber value="${branchStats[2]}" type="number" groupingUsed="true"/>đ</div>
            <div class="stat-label">Doanh thu tháng này</div>
        </div>
    </div>
</div>
</c:if>

<!-- Bộ lọc -->
<div class="card mb-4">
    <div class="card-body py-3">
        <form method="get" action="${pageContext.request.contextPath}/admin/cashier-mgmt" class="row g-2 align-items-end">
            <c:if test="${userRole == 'Admin'}">
            <div class="col-md-3">
                <label class="form-label small fw-bold">Chi nhánh</label>
                <select name="branchId" class="form-select form-select-sm">
                    <option value="">-- Tất cả --</option>
                    <c:forEach var="b" items="${branches}">
                        <option value="${b.branchId}" <c:if test="${b.branchId == viewBranchId}">selected</c:if>>${b.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            </c:if>
            <div class="col-md-3">
                <label class="form-label small fw-bold">Tìm kiếm</label>
                <input type="text" name="search" class="form-control form-control-sm"
                       placeholder="Tên, email, SĐT..." value="${search}">
            </div>
            <div class="col-md-2">
                <label class="form-label small fw-bold">Trạng thái</label>
                <select name="status" class="form-select form-select-sm">
                    <option value="ALL" <c:if test="${statusFilter == 'ALL'}">selected</c:if>>Tất cả</option>
                    <option value="ACTIVE" <c:if test="${statusFilter == 'ACTIVE'}">selected</c:if>>Hoạt động</option>
                    <option value="INACTIVE" <c:if test="${statusFilter == 'INACTIVE'}">selected</c:if>>Vô hiệu</option>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary btn-sm w-100">
                    <i class="fas fa-search me-1"></i>Lọc
                </button>
            </div>
        </form>
    </div>
</div>

<div class="row g-4">
<!-- Danh sách cashier -->
<div class="col-lg-8">
<div class="card">
    <div class="card-header bg-white py-3">
        <span class="fw-bold"><i class="fas fa-list me-1"></i>Danh sách Cashier (${cashiers.size()})</span>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty cashiers}">
                <p class="text-muted text-center py-4">Không tìm thấy cashier nào.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover cashier-table mb-0">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>SĐT</th>
                                <th>Chi nhánh</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${cashiers}" varStatus="st">
                            <tr>
                                <td>${st.index + 1}</td>
                                <td class="fw-semibold">${c.fullName}</td>
                                <td>${c.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty c.phone}">${c.phone}</c:when>
                                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty c.branchName}">${c.branchName}</c:when>
                                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${c.status == 'ACTIVE'}">
                                            <span class="status-active">Hoạt động</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-inactive">Vô hiệu</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/admin/cashier-mgmt?cashierId=${c.userId}&branchId=${viewBranchId}&search=${search}&status=${statusFilter}"
                                       class="btn btn-outline-primary btn-sm me-1" title="Xem thống kê">
                                        <i class="fas fa-chart-bar"></i>
                                    </a>
                                    <c:choose>
                                        <c:when test="${c.status == 'ACTIVE'}">
                                            <a href="${pageContext.request.contextPath}/admin/cashier-mgmt?action=toggle&userId=${c.userId}&newStatus=INACTIVE"
                                               class="btn btn-outline-danger btn-sm"
                                               onclick="return confirm('Vô hiệu hóa tài khoản ${c.fullName}?')"
                                               title="Vô hiệu hóa">
                                                <i class="fas fa-user-slash"></i>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/admin/cashier-mgmt?action=toggle&userId=${c.userId}&newStatus=ACTIVE"
                                               class="btn btn-outline-success btn-sm"
                                               onclick="return confirm('Kích hoạt lại tài khoản ${c.fullName}?')"
                                               title="Kích hoạt">
                                                <i class="fas fa-user-check"></i>
                                            </a>
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
</div>

<!-- Panel thống kê cashier được chọn -->
<div class="col-lg-4">
<c:choose>
    <c:when test="${selectedCashierId > 0 and not empty selectedStats}">
        <%-- Tìm tên cashier được chọn --%>
        <c:set var="selectedName" value="Cashier"/>
        <c:forEach var="c" items="${cashiers}">
            <c:if test="${c.userId == selectedCashierId}">
                <c:set var="selectedName" value="${c.fullName}"/>
            </c:if>
        </c:forEach>

        <div class="detail-stats mb-3">
            <h6 class="fw-bold mb-3">
                <i class="fas fa-chart-pie text-primary me-1"></i>Thống kê: ${selectedName}
            </h6>

            <!-- Filter tháng/năm -->
            <form method="get" action="${pageContext.request.contextPath}/admin/cashier-mgmt" class="row g-2 mb-3">
                <input type="hidden" name="cashierId" value="${selectedCashierId}">
                <input type="hidden" name="branchId" value="${viewBranchId}">
                <input type="hidden" name="search" value="${search}">
                <input type="hidden" name="status" value="${statusFilter}">
                <div class="col-5">
                    <select name="month" class="form-select form-select-sm">
                        <option value="">Tháng hiện tại</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" <c:if test="${filterMonth == m}">selected</c:if>>Tháng ${m}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-4">
                    <select name="year" class="form-select form-select-sm">
                        <option value="">Năm</option>
                        <c:forEach var="y" begin="2024" end="2026">
                            <option value="${y}" <c:if test="${filterYear == y}">selected</c:if>>${y}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-3">
                    <button type="submit" class="btn btn-sm btn-primary w-100">Xem</button>
                </div>
            </form>

            <div class="row g-3">
                <div class="col-6">
                    <div class="ds-value">${selectedStats[0].intValue()}</div>
                    <div class="ds-label">HĐ hôm nay</div>
                </div>
                <div class="col-6">
                    <div class="ds-value"><fmt:formatNumber value="${selectedStats[1]}" type="number" groupingUsed="true"/>đ</div>
                    <div class="ds-label">DT hôm nay</div>
                </div>
                <div class="col-6">
                    <div class="ds-value">${selectedStats[2].intValue()}</div>
                    <div class="ds-label">HĐ trong tháng</div>
                </div>
                <div class="col-6">
                    <div class="ds-value"><fmt:formatNumber value="${selectedStats[3]}" type="number" groupingUsed="true"/>đ</div>
                    <div class="ds-label">DT trong tháng</div>
                </div>
                <div class="col-12">
                    <hr class="my-2">
                    <div class="ds-value">${selectedStats[4].intValue()}</div>
                    <div class="ds-label">Tổng HĐ (tất cả thời gian)</div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="detail-stats text-center text-muted py-5">
            <i class="fas fa-chart-bar fa-3x mb-3 opacity-25"></i>
            <p class="mb-0">Chọn một cashier để xem thống kê</p>
        </div>
    </c:otherwise>
</c:choose>
</div>
</div>


<!-- Modal Thêm Cashier -->
<div class="modal fade" id="addCashierModal" tabindex="-1" aria-labelledby="addCashierModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/cashier-mgmt?action=add">
                <div class="modal-header">
                    <h5 class="modal-title" id="addCashierModalLabel">
                        <i class="fas fa-user-plus me-1"></i>Thêm Cashier mới
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Email <span class="text-danger">*</span></label>
                        <input type="email" name="email" class="form-control" required placeholder="cashier@example.com">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Họ tên <span class="text-danger">*</span></label>
                        <input type="text" name="fullName" class="form-control" required placeholder="Nguyễn Văn A">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Số điện thoại</label>
                        <input type="text" name="phone" class="form-control" placeholder="0901234567">
                    </div>
                    <c:if test="${userRole == 'Admin'}">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Chi nhánh <span class="text-danger">*</span></label>
                        <select name="addBranchId" class="form-select" required>
                            <option value="">-- Chọn chi nhánh --</option>
                            <c:forEach var="b" items="${branches}">
                                <option value="${b.branchId}">${b.branchName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    </c:if>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i>Thêm
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
