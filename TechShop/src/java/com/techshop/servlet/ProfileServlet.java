package com.techshop.servlet;

import com.techshop.dao.PasswordDAO;
import com.techshop.dao.SystemLogDAO;
import com.techshop.dao.UserDAO;
import com.techshop.model.EntityType;
import com.techshop.model.LogAction;
import com.techshop.model.User;
import com.techshop.util.AuthenticationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

@WebServlet("/profile")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 5   // 5MB
)

public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private PasswordDAO passDAO = new PasswordDAO();
    private SystemLogDAO logDAO = new SystemLogDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy user từ session
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }
        // Lấy lại từ DB để đảm bảo data mới nhất
        User user = userDAO.getById(currentUser.getUserId());
        request.setAttribute("user", user);
        request.getRequestDispatcher("/views/user/profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        User currentUser = (User) request.getSession().getAttribute("user");

        if ("updatePhone".equals(action)) {
            String phone = request.getParameter("phone");
            if (passDAO.updatePhone(currentUser.getUserId(), phone)) {
                // --- GHI LOG ĐỔI SỐ ĐIỆN THOẠI ---
                logDAO.logAction(
                    currentUser.getUserId(), 
                    LogAction.UPDATE_USER, 
                    EntityType.USER, 
                    currentUser.getUserId(), 
                    request.getRemoteAddr(), 
                    "Người dùng cập nhật số điện thoại cá nhân"
                );
                // ---------------------------------
                request.setAttribute("message", "Cập nhật số điện thoại thành công!");
            }
        } else if ("changePassword".equals(action)) {
            handlePasswordChange(request, currentUser.getUserId());
        } else if("changeAvatar".equals(action)){
            handleUploadAvatar(request, currentUser);
        }
        doGet(request, response);
    }
    
    private void handlePasswordChange(HttpServletRequest request, int userId) {
        String currentPass = request.getParameter("currentPassword");
        String newPass = request.getParameter("newPassword");
        String confirmPass = request.getParameter("confirmPassword");

        // 1. Kiểm tra mật khẩu mới và xác nhận có khớp nhau không
        if (!newPass.equals(confirmPass)) {
            request.setAttribute("error", "Mật khẩu mới và xác nhận không khớp!");
            return;
        }

        // 2. Kiểm tra mật khẩu mới
        if (!AuthenticationUtil.isPasswordStrong(newPass)) { 
            String feedback = AuthenticationUtil.getPasswordStrengthFeedback(newPass);
            request.setAttribute("error", feedback);
            return;
        }

        // 3. Kiểm tra mật khẩu hiện tại có đúng không
        boolean alreadyHasPassword = passDAO.hasPassword(userId);
        if(alreadyHasPassword){
            if (!passDAO.verifyCurrentPassword(userId, currentPass)) {
                request.setAttribute("error", "Mật khẩu hiện tại không chính xác!");
                return;
            }
            if (currentPass.equals(newPass)) {
                request.setAttribute("error", "Mật khẩu mới không được giống mật khẩu cũ!");
                return;
            }
        }             

        // 5. Thực hiện cập nhật
        if (passDAO.setPassword(userId, newPass)) {
            // --- GHI LOG ĐỔI MẬT KHẨU ---
            logDAO.logAction(
                userId, 
                LogAction.CHANGE_PASSWORD, 
                EntityType.USER, 
                userId, 
                request.getRemoteAddr(), 
                "Người dùng tự thay đổi mật khẩu cá nhân"
            );
            // ----------------------------
            request.setAttribute("message", "Đổi mật khẩu thành công!");
            
        } else {
            request.setAttribute("error", "Có lỗi xảy ra khi cập nhật mật khẩu.");
        }
    }
    
    private void handleUploadAvatar(HttpServletRequest request, User currentUser)
        throws ServletException, IOException {

        Part part = request.getPart("avatar"); 
        if (part == null || part.getSize() == 0) {
            request.setAttribute("error", "Vui lòng chọn ảnh.");
            return;
        }
        
        if(!part.getContentType().endsWith("jpeg") && !part.getContentType().endsWith("png")){
            request.setAttribute("error", "Vui lòng chọn ảnh đuôi JPEG hoặc PNG.");
            return;
        }
        
        InputStream input = part.getInputStream(); //đọc dữ liệu dạng byte
        BufferedImage image = ImageIO.read(input); //đọc từ inputstream, chuyển thành BufferedImage (đối tượng ảnh trong Java) nếu ảnh hợp lệ

        if(image == null) {
            request.setAttribute("error", "Ảnh không hợp lệ");
            return;
        }
        
        String fileName = Paths.get(part.getSubmittedFileName())
                           .getFileName()
                           .toString();

        String newFileName = System.currentTimeMillis() + "_" + fileName;

        String uploadPath = getServletContext().getRealPath("/uploads");
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        part.write(uploadPath + File.separator + newFileName);

        // update DB
        passDAO.updateAvatar(currentUser.getUserId(), newFileName);
        // --- GHI LOG ĐỔI AVATAR ---
        logDAO.logAction(
            currentUser.getUserId(), 
            LogAction.UPDATE_USER, 
            EntityType.USER, 
            currentUser.getUserId(), 
            request.getRemoteAddr(), 
            "Người dùng cập nhật ảnh đại diện"
        );
        // --------------------------

        // update session
        currentUser.setAvatarUrl(newFileName);
        request.getSession().setAttribute("user", currentUser);

        request.setAttribute("message", "Cập nhật ảnh đại diện thành công!");
    }
}