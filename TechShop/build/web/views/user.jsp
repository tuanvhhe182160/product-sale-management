<%@page import="com.techshop.model.User"%>
<%@page import="com.techshop.model.Branch"%>
<%@page import="com.techshop.model.Role"%>
<%@page import="com.techshop.dao.BranchDAO"%>
<%@page import="com.techshop.dao.RoleDAO"%>
<%@page import="com.techshop.dao.UserDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ include file="common/header.jsp" %>

<%
    UserDAO userDAO = new UserDAO();
    BranchDAO branchDAO = new BranchDAO();
    RoleDAO roleDAO = new RoleDAO();

    List<User> userList = userDAO.getAll();
    List<Branch> branchList = branchDAO.getAll();
    List<Role> roleList = roleDAO.getAll();
%>
<!-- Check permission -->
<c:if test="${true}">
    <!-- Hiển thị thông báo -->
    <%
        String message = (String) session.getAttribute("message");
        String messageType = (String) session.getAttribute("messageType");
        if (message != null) {
    %>
        <div class="alert alert-<%= messageType %> alert-dismissible fade show" role="alert">
            <%= message %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <%
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
    %>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary"><i class="fas fa-users-cog"></i> User Management</h2>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addEmployeeModal">
            <i class="fas fa-plus"></i> New Employee
        </button>
    </div>

    <div class="card border-primary">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th class="text-primary">ID</th>
                            <th class="text-primary">Full Name</th>
                            <th class="text-primary">Role</th>
                            <th class="text-primary">Branch</th>
                            <th class="text-primary">Phone Number</th>
                            <th class="text-primary">Status</th>
                            <th class="text-center text-primary">Operation</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (userList != null && !userList.isEmpty()) {
                                for (User user : userList) {
                        %>
                        <tr class="text-secondary">
                            <td class="text-secondary"><%= user.getUserId() %></td>
                            <td class="text-secondary"><%= user.getFullName() %></td>
                            <td class="text-secondary"><%= user.getRoleName() != null ? user.getRoleName() : "N/A" %></td>
                            <td class="text-secondary"><%= user.getBranchName() != null ? user.getBranchName() : "N/A" %></td>
                            <td class="text-secondary"><%= user.getPhone() != null ? user.getPhone() : "N/A" %></td>
                            <td>
                                <% if ("Active".equalsIgnoreCase(user.getStatus())) { %>
                                    <span class="badge bg-primary">Active</span>
                                <% } else { %>
                                    <span class="badge bg-secondary">Inactive</span>
                                <% } %>
                            </td>
                            <td class="text-center">
                                <button class="btn btn-sm btn-info me-1" 
                                        title="View" 
                                        onclick="viewUser(<%= user.getUserId() %>, '<%= user.getFullName() %>', '<%= user.getEmail() %>', '<%= user.getRoleName() != null ? user.getRoleName() : "N/A" %>', '<%= user.getBranchName() != null ? user.getBranchName() : "N/A" %>', '<%= user.getPhone() != null ? user.getPhone() : "N/A" %>', '<%= user.getStatus() %>', '<%= user.getCreatedAt() != null ? user.getCreatedAt().toString() : "N/A" %>', '<%= user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : "N/A" %>')">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="btn btn-sm btn-warning me-1" 
                                        title="Edit"
                                        onclick="editUser(<%= user.getUserId() %>, '<%= user.getFullName() %>', '<%= user.getEmail() %>', <%= user.getRoleId() %>, <%= user.getBranchId() != null ? user.getBranchId() : "null" %>, '<%= user.getPhone() != null ? user.getPhone() : "" %>', '<%= user.getStatus() %>')">
                                    <i class="fas fa-edit"></i>
                                </button>
                                </button>
                                <button class="btn btn-sm btn-danger" 
                                        title="Delete" 
                                        onclick="deleteUser(<%= user.getUserId() %>, '<%= user.getFullName() %>')">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="7" class="text-center text-muted">No users found</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal Add Employee -->
    <div class="modal fade" id="addEmployeeModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-user-plus"></i> Add New Employee</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="addEmployeeForm" method="POST" action="${pageContext.request.contextPath}/user">
                        <input type="hidden" name="action" value="add">
                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Role</label>
                            <select class="form-select" id="roleId" name="roleId" required>
                                <option value="">Select role...</option>
                                <%
                                    if (roleList != null && !roleList.isEmpty()) {
                                        for (Role role : roleList) {
                                %>
                                    <option value="<%= role.getRoleId() %>"><%= role.getRoleName() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Branch</label>
                            <select class="form-select" id="branchId" name="branchId" required>
                                <option value="">Select branch...</option>
                                <%
                                    if (branchList != null && !branchList.isEmpty()) {
                                        for (Branch branch : branchList) {
                                            if ("Active".equalsIgnoreCase(branch.getStatus())) {
                                %>
                                    <option value="<%= branch.getBranchId() %>"><%= branch.getBranchName() %></option>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Phone Number</label>
                            <input type="tel" class="form-control" id="phone" name="phone" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <select class="form-select" id="status" name="status" required>
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="saveEmployee()">Save</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Modal Edit Employee -->
    <div class="modal fade" id="editEmployeeModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-user-edit"></i> Edit Employee</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="editEmployeeForm" method="POST" action="${pageContext.request.contextPath}/user">
                        <input type="hidden" name="action" value="edit">
                        <input type="hidden" id="editUserId" name="userId">
                        
                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="editFullName" name="fullName" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" id="editEmail" name="email" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Role</label>
                            <select class="form-select" id="editRoleId" name="roleId" required>
                                <option value="">Select role...</option>
                                <%
                                    if (roleList != null && !roleList.isEmpty()) {
                                        for (Role role : roleList) {
                                %>
                                    <option value="<%= role.getRoleId() %>"><%= role.getRoleName() %></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Branch</label>
                            <select class="form-select" id="editBranchId" name="branchId" required>
                                <option value="">Select branch...</option>
                                <%
                                    if (branchList != null && !branchList.isEmpty()) {
                                        for (Branch branch : branchList) {
                                            if ("Active".equalsIgnoreCase(branch.getStatus())) {
                                %>
                                    <option value="<%= branch.getBranchId() %>"><%= branch.getBranchName() %></option>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Phone Number</label>
                            <input type="tel" class="form-control" id="editPhone" name="phone" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <select class="form-select" id="editStatus" name="status" required>
                                <option value="Active">Active</option>
                                <option value="Inactive">Inactive</option>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="updateEmployee()">Update</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Modal View Employee -->
    <div class="modal fade" id="viewEmployeeModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title"><i class="fas fa-user"></i> Employee Details</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">ID:</div>
                        <div class="col-8" id="viewUserId"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Full Name:</div>
                        <div class="col-8" id="viewFullName"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Email:</div>
                        <div class="col-8" id="viewEmail"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Role:</div>
                        <div class="col-8" id="viewRole"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Branch:</div>
                        <div class="col-8" id="viewBranch"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Phone Number:</div>
                        <div class="col-8" id="viewPhone"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Status:</div>
                        <div class="col-8" id="viewStatus"></div>
                    </div>
                    <hr>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Created At:</div>
                        <div class="col-8" id="viewCreatedAt"></div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-4 fw-bold">Updated At:</div>
                        <div class="col-8" id="viewUpdatedAt"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Reset form when modal is closed
        document.getElementById('addEmployeeModal').addEventListener('hidden.bs.modal', function () {
            document.getElementById('addEmployeeForm').reset();
        });

        // View user details
        function viewUser(userId, fullName, email, roleName, branchName, phone, status, createdAt, updatedAt) {
            document.getElementById('viewUserId').textContent = userId;
            document.getElementById('viewFullName').textContent = fullName;
            document.getElementById('viewEmail').textContent = email;
            document.getElementById('viewRole').textContent = roleName;
            document.getElementById('viewBranch').textContent = branchName;
            document.getElementById('viewPhone').textContent = phone;
            
            // Status with badge
            const statusHtml = status === 'Active' 
                ? '<span class="badge bg-success">Active</span>' 
                : '<span class="badge bg-secondary">Inactive</span>';
            document.getElementById('viewStatus').innerHTML = statusHtml;
            
            // Format datetime
            document.getElementById('viewCreatedAt').textContent = formatDateTime(createdAt);
            document.getElementById('viewUpdatedAt').textContent = formatDateTime(updatedAt);
            
            // Show modal
            var viewModal = new bootstrap.Modal(document.getElementById('viewEmployeeModal'));
            viewModal.show();
        }

        // Format datetime helper function
        function formatDateTime(dateTimeStr) {
            if (!dateTimeStr || dateTimeStr === 'N/A') {
                return 'N/A';
            }
            try {
                const date = new Date(dateTimeStr);
                return date.toLocaleString('en-GB', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });
            } catch (e) {
                return dateTimeStr;
            }
        }

        // Edit user - populate form with user data
        function editUser(userId, fullName, email, roleId, branchId, phone, status) {
            document.getElementById('editUserId').value = userId;
            document.getElementById('editFullName').value = fullName;
            document.getElementById('editEmail').value = email;
            document.getElementById('editRoleId').value = roleId;
            document.getElementById('editBranchId').value = branchId || '';
            document.getElementById('editPhone').value = phone;
            document.getElementById('editStatus').value = status;
            
            // Show modal
            var editModal = new bootstrap.Modal(document.getElementById('editEmployeeModal'));
            editModal.show();
        }

        // Update employee
        function updateEmployee() {
            const form = document.getElementById('editEmployeeForm');
            if (form.checkValidity()) {
                form.submit();
            } else {
                form.reportValidity();
            }
        }

        // Reset edit form when modal is closed
        document.getElementById('editEmployeeModal').addEventListener('hidden.bs.modal', function () {
            document.getElementById('editEmployeeForm').reset();
        });

        // Save employee (you can implement AJAX call here)
        function saveEmployee() {
            const form = document.getElementById('addEmployeeForm');
            if (form.checkValidity()) {
                form.submit(); // Submit form
            } else {
                form.reportValidity();
            }
        }

        // Delete user (set status to Inactive)
        function deleteUser(userId, fullName) {
            if (confirm('Are you sure you want to deactivate user: ' + fullName + '?')) {
                // Tạo form ẩn để submit
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/user';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';
                
                const userIdInput = document.createElement('input');
                userIdInput.type = 'hidden';
                userIdInput.name = 'userId';
                userIdInput.value = userId;
                
                form.appendChild(actionInput);
                form.appendChild(userIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</c:if>
<%@ include file="common/footer.jsp" %>