/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.techshop.servlet;
import com.techshop.util.ValidationUtil;
import com.techshop.dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;

import com.techshop.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author kminh
 */

/**
 * Sử dụng đường dẫn techshop/user để truy cập
 */

public class UserManageServlet extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getRequestDispatcher("/views/user.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
                user.setStatus("Inactive");
                boolean success = userDAO.update(user);
                
                HttpSession session = request.getSession();
                if (success) {
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

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
