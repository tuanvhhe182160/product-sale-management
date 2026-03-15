package com.techshop.model;

import java.time.LocalDateTime;

public class Role {
    private int roleId;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor rỗng
    public Role() {
    }

    // Constructor đầy đủ
    public Role(int roleId, String roleName, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "Role{" + "roleId=" + roleId + ", roleName=" + roleName + ", description=" + description + '}';
    }
}