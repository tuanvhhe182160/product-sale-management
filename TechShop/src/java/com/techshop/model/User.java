package com.techshop.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String email;
    private String fullName;
    private String phone;
    private int roleId;
    private Integer branchId; // Integer để có thể null (Admin)
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String avatarUrl;
    
    // Thông tin JOIN (không có trong DB)
    private String roleName;
    private String branchName;

    public User() {
    }

    public User(int userId, String email, String fullName, String phone, int roleId, Integer branchId, String status, LocalDateTime createdAt, LocalDateTime updatedAt, String roleName, String branchName, String avatarUrl) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.roleId = roleId;
        this.branchId = branchId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roleName = roleName;
        this.branchName = branchName;
        this.avatarUrl = avatarUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", email=" + email + ", fullName=" + fullName + ", phone=" + phone + ", roleId=" + roleId + ", branchId=" + branchId + ", status=" + status + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", avatarUrl=" + avatarUrl + ", roleName=" + roleName + ", branchName=" + branchName + '}';
    }
    
    
}