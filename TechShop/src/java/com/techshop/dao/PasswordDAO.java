package com.techshop.dao;

import com.techshop.dal.DBContext;
import com.techshop.model.User;
import com.techshop.util.AuthenticationUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author justi
 */
public class PasswordDAO extends DBContext {
    //Password
    public boolean hasPassword(int userId) {
        String sql = "SELECT password_hash FROM [User] WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    return hash != null && !hash.trim().isEmpty();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password Plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateWithPassword(String email, String password) {
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.password_hash, u.failed_login_attempts, u.locked_until, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.email = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Check if account is locked
                Timestamp lockedUntilTs = rs.getTimestamp("locked_until");
                if (lockedUntilTs != null) {
                    LocalDateTime lockedUntil = lockedUntilTs.toLocalDateTime();
                    if (LocalDateTime.now().isBefore(lockedUntil)) {
                        System.out.println("Account is locked until: " + lockedUntil);
                        rs.close();
                        ps.close();
                        return null;
                    }
                }
                
                // Check if account is active
                String status = rs.getString("status");
                if (!"ACTIVE".equals(status)) {
                    System.out.println("Account is not active: " + status);
                    rs.close();
                    ps.close();
                    return null;
                }
                
                // Verify password
                String passwordHash = rs.getString("password_hash");
                if (passwordHash == null || passwordHash.trim().isEmpty()) {
                    System.out.println("No password set for this account (OAuth only)");
                    rs.close();
                    ps.close();
                    return null;
                }
                
                if (AuthenticationUtil.verifyPassword(password, passwordHash)) {
                    // Password correct - reset failed attempts and update last login
                    int userId = rs.getInt("user_id");
                    resetFailedLoginAttempts(userId);
                    updateLastLogin(userId);
                    
                    User user = extractUserFromResultSet(rs);
                    rs.close();
                    ps.close();
                    return user;
                } else {
                    // Password incorrect - increment failed attempts
                    int userId = rs.getInt("user_id");
                    int failedAttempts = rs.getInt("failed_login_attempts");
                    incrementFailedLoginAttempts(userId, failedAttempts);
                    
                    rs.close();
                    ps.close();
                    return null;
                }
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.authenticateWithPassword() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Set or update user password
     * @param userId User ID
     * @param newPassword Plain text password (will be hashed)
     * @return true if successful
     */
    public boolean setPassword(int userId, String newPassword) {
        String passwordHash = AuthenticationUtil.hashPassword(newPassword);
        String sql = "UPDATE [User] SET password_hash = ?, updated_at = GETDATE() WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.setPassword() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user phone number
     * @param userId User ID
     * @param phone New phone number
     * @return true if successful
     */
    public boolean updatePhone(int userId, String phone) {
        String sql = "UPDATE [User] SET phone = ?, updated_at = GETDATE() WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.updatePhone() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verify current password before allowing password change
     * @param userId User ID
     * @param currentPassword Current password to verify
     * @return true if current password is correct
     */
    public boolean verifyCurrentPassword(int userId, String currentPassword) {
        String sql = "SELECT password_hash FROM [User] WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                rs.close();
                ps.close();
                
                if (passwordHash == null || passwordHash.trim().isEmpty()) {
                    return false; // No password set
                }
                
                return AuthenticationUtil.verifyPassword(currentPassword, passwordHash);
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.verifyCurrentPassword() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Generate and store password reset token
     * @param email User email
     * @return Reset token if successful, null otherwise
     */
    public String generateResetToken(String email) {
        // First check if user exists and is active
        User user = getByEmail(email);
        if (user == null) {
            return null;
        }
        
        String token = AuthenticationUtil.generateResetToken();
        LocalDateTime expiry = AuthenticationUtil.getTokenExpiryTime();
        
        String sql = "UPDATE [User] SET reset_token = ?, reset_token_expiry = ?, updated_at = GETDATE() " +
                     "WHERE email = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, token);
            ps.setTimestamp(2, Timestamp.valueOf(expiry));
            ps.setString(3, email);
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0 ? token : null;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.generateResetToken() Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verify reset token and get user
     * @param token Reset token
     * @return User object if token is valid, null otherwise
     */
    public User verifyResetToken(String token) {
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.reset_token_expiry, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.reset_token = ? AND u.status = 'ACTIVE'";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Check if token has expired
                Timestamp expiryTs = rs.getTimestamp("reset_token_expiry");
                if (expiryTs == null) {
                    rs.close();
                    ps.close();
                    return null;
                }
                
                LocalDateTime expiry = expiryTs.toLocalDateTime();
                if (AuthenticationUtil.isTokenExpired(expiry)) {
                    System.out.println("Reset token has expired");
                    rs.close();
                    ps.close();
                    return null;
                }
                
                User user = extractUserFromResultSet(rs);
                rs.close();
                ps.close();
                return user;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.verifyResetToken() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Reset password using reset token
     * @param token Reset token
     * @param newPassword New password
     * @return true if successful
     */
    public boolean resetPassword(String token, String newPassword) {
        User user = verifyResetToken(token);
        if (user == null) {
            return false;
        }
        
        // Set new password
        boolean success = setPassword(user.getUserId(), newPassword);
        
        if (success) {
            // Invalidate token after use
            invalidateResetToken(user.getUserId());
        }
        
        return success;
    }
    
    /**
     * Invalidate reset token after use
     * @param userId User ID
     * @return true if successful
     */
    private boolean invalidateResetToken(int userId) {
        String sql = "UPDATE [User] SET reset_token = NULL, reset_token_expiry = NULL, " +
                     "updated_at = GETDATE() WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            
            int rowsAffected = ps.executeUpdate();
            ps.close();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("UserDAO.invalidateResetToken() Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Increment failed login attempts and lock account if needed
     * @param userId User ID
     * @param currentAttempts Current number of failed attempts
     */
    private void incrementFailedLoginAttempts(int userId, int currentAttempts) {
        int newAttempts = currentAttempts + 1;
        
        String sql;
        // Lock account for 15 minutes after 3 failed attempts
        if (newAttempts >= 3) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(15);
            sql = "UPDATE [User] SET failed_login_attempts = ?, locked_until = ?, " +
                  "updated_at = GETDATE() WHERE user_id = ?";
            
            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, newAttempts);
                ps.setTimestamp(2, Timestamp.valueOf(lockUntil));
                ps.setInt(3, userId);
                
                ps.executeUpdate();
                ps.close();
                
                System.out.println("Account locked until: " + lockUntil);
                
            } catch (SQLException e) {
                System.err.println("UserDAO.incrementFailedLoginAttempts() Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            sql = "UPDATE [User] SET failed_login_attempts = ?, updated_at = GETDATE() WHERE user_id = ?";
            
            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, newAttempts);
                ps.setInt(2, userId);
                
                ps.executeUpdate();
                ps.close();
                
            } catch (SQLException e) {
                System.err.println("UserDAO.incrementFailedLoginAttempts() Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Reset failed login attempts after successful login
     * @param userId User ID
     */
    private void resetFailedLoginAttempts(int userId) {
        String sql = "UPDATE [User] SET failed_login_attempts = 0, locked_until = NULL, " +
                     "updated_at = GETDATE() WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            
            ps.executeUpdate();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.resetFailedLoginAttempts() Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update last login timestamp
     * @param userId User ID
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE [User] SET last_login = GETDATE(), updated_at = GETDATE() WHERE user_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            
            ps.executeUpdate();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.updateLastLogin() Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //Helper
    public User getByEmail(String email) {
        String sql = "SELECT u.user_id, u.email, u.full_name, u.phone, " +
                     "       u.role_id, u.branch_id, u.status, " +
                     "       u.created_at, u.updated_at, " +
                     "       r.role_name, " +
                     "       b.branch_name " +
                     "FROM [User] u " +
                     "LEFT JOIN Role r ON u.role_id = r.role_id " +
                     "LEFT JOIN Branch b ON u.branch_id = b.branch_id " +
                     "WHERE u.email = ? AND u.status = 'ACTIVE'";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                rs.close();
                ps.close();
                return user;
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            System.err.println("UserDAO.getByEmail() Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        
        // Handle nullable branch_id
        int branchId = rs.getInt("branch_id");
        if (!rs.wasNull()) {
            user.setBranchId(branchId);
        } else {
            user.setBranchId(null);
        }
        
        user.setStatus(rs.getString("status"));
        
        // Handle timestamp conversion
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            user.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        // JOIN data
        user.setRoleName(rs.getString("role_name"));
        user.setBranchName(rs.getString("branch_name"));
        
        return user;
    }
}
