
package com.techshop.servlet;
import com.techshop.dao.BranchDAO;
import com.techshop.dao.RoleDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.util.ValidationUtil;
import com.techshop.dao.UserDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import java.io.IOException;

import com.techshop.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "UserManageServlet", urlPatterns = "/user")
public class UserManageServlet extends HttpServlet {

    private SystemLogDAO logDAO = new SystemLogDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        BranchDAO branchDAO = new BranchDAO();
        RoleDAO roleDAO = new RoleDAO();       

        request.setAttribute("userList", userDAO.getAll());
        request.setAttribute("branchList", branchDAO.getAll());
        request.setAttribute("roleList", roleDAO.getAll());
        request.getRequestDispatcher("/views/user/user-management.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addUser(request, response);
        } else if ("edit".equals(action)) {
            editUser(request, response);
        } else if ("delete".equals(action)) {
            deleteUser(request, response);
        }
    }
    
    private void addUser(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();

            // Lấy dữ liệu từ form
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            int roleId = Integer.parseInt(request.getParameter("roleId"));
            String branchIdStr = request.getParameter("branchId");
            Integer branchId = (branchIdStr != null && !branchIdStr.isEmpty()) 
                              ? Integer.parseInt(branchIdStr) 
                              : null;
            String status = request.getParameter("status");
            
                        // Validate input
            if (!ValidationUtil.isNotEmpty(fullName) || fullName.length() < 2 || fullName.length() > 100) {
                session.setAttribute("message", "Full name must be 2-100 characters!");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/user");
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                session.setAttribute("message", "Invalid email format!");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/user");
                return;
            }

            if (!ValidationUtil.isValidPhone(phone)) {
                session.setAttribute("message", "Phone number must be 10-11 digits!");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/user");
                return;
            }
            // Tạo đối tượng User mới
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRoleId(roleId);
            newUser.setBranchId(branchId);
            newUser.setStatus(status);

            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.insert(newUser);
            if (success) {
                // --- GHI LOG ---
                User adminUser = (User) session.getAttribute("user");
                Integer adminId = (adminUser != null) ? adminUser.getUserId() : null;
                
                logDAO.logAction(
                    adminId, 
                    LogAction.CREATE_USER, 
                    EntityType.USER, 
                    null, 
                    request.getRemoteAddr(), 
                    "Thêm mới tài khoản nhân sự: " + fullName + " (" + email + ")"
                );
                // -----------------------
                session.setAttribute("message", "User added successfully!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Failed to add user!");
                session.setAttribute("messageType", "danger");
            }

            // Redirect về trang user
            response.sendRedirect(request.getContextPath() + "/user");
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("message", "Error: " + e.getMessage());
            session.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/user");
        }
    }
    
    private void editUser(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();

            // Lấy dữ liệu từ form
            int userId = Integer.parseInt(request.getParameter("userId"));
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            int roleId = Integer.parseInt(request.getParameter("roleId"));
            String branchIdStr = request.getParameter("branchId");
            Integer branchId = (branchIdStr != null && !branchIdStr.isEmpty()) 
                              ? Integer.parseInt(branchIdStr) 
                              : null;
            String status = request.getParameter("status");
            
            // Validate input
            if (!ValidationUtil.isNotEmpty(fullName) || fullName.length() < 2 || fullName.length() > 100) {
                session.setAttribute("message", "Full name must be 2-100 characters!");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/user");
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                session.setAttribute("message", "Invalid email format!");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/user");
                return;
            }

            if (!ValidationUtil.isValidPhone(phone)) {
                session.setAttribute("message", "Phone number must be 10-11 digits!");
                session.setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/user");
                return;
            }
            // Tạo đối tượng User để update
            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRoleId(roleId);
            user.setBranchId(branchId);
            user.setStatus(status);
            
            // Update database
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.update(user);
            
            // Set message
            if (success) {
                // --- GHI LOG ---
                User adminUser = (User) session.getAttribute("user");
                Integer adminId = (adminUser != null) ? adminUser.getUserId() : null;
                
                logDAO.logAction(
                    adminId, 
                    LogAction.UPDATE_USER, 
                    EntityType.USER, 
                    userId, 
                    request.getRemoteAddr(), 
                    "Cập nhật thông tin tài khoản nhân sự: " + email
                );
                // -----------------------
                session.setAttribute("message", "User updated successfully!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Failed to update user!");
                session.setAttribute("messageType", "danger");
            }
            
            response.sendRedirect(request.getContextPath() + "/user");
            
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("message", "Error: " + e.getMessage());
            session.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/user");
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            
            UserDAO userDAO = new UserDAO();
            // Lấy thông tin user hiện tại
            User user = userDAO.getById(userId);
            
            if (user != null) {
                // Chỉ cập nhật status thành Inactive
                user.setStatus("INACTIVE");
                boolean success = userDAO.update(user);
                
                HttpSession session = request.getSession();
                if (success) {
                    // --- GHI LOG ---
                    User adminUser = (User) session.getAttribute("user");
                    Integer adminId = (adminUser != null) ? adminUser.getUserId() : null;
                    
                    logDAO.logAction(
                        adminId, 
                        LogAction.LOCK_USER, 
                        EntityType.USER, 
                        userId, 
                        request.getRemoteAddr(), 
                        "Vô hiệu hóa tài khoản nhân sự: " + user.getEmail()
                    );
                    // -----------------------
                    session.setAttribute("message", "User deactivated successfully!");
                    session.setAttribute("messageType", "success");
                } else {
                    session.setAttribute("message", "Failed to deactivate user!");
                    session.setAttribute("messageType", "danger");
                }
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("message", "User not found!");
                session.setAttribute("messageType", "danger");
            }
            
            response.sendRedirect(request.getContextPath() + "/user");
            
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("message", "Error: " + e.getMessage());
            session.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/user");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
