package com.techshop.model;

import java.time.LocalDateTime;

public class SystemLog {
    private int logId;
    private Integer userId;
    private String action;
    private String entityType; // User, Product, Invoice, etc.
    private Integer entityId;
    private String ipAddress;
    private String details;
    private LocalDateTime createdAt;
    
    // Thông tin JOIN
    private String userName;

    public SystemLog() {
    }

    public SystemLog(int logId, Integer userId, String action, String entityType, Integer entityId, String ipAddress, String details, LocalDateTime createdAt, String userName) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.ipAddress = ipAddress;
        this.details = details;
        this.createdAt = createdAt;
        this.userName = userName;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "SystemLog{" + "logId=" + logId + ", userId=" + userId + ", action=" + action + ", entityType=" + entityType + ", entityId=" + entityId + ", ipAddress=" + ipAddress + ", details=" + details + ", createdAt=" + createdAt + ", userName=" + userName + '}';
    }

    
}