package com.techshop.model;

import java.time.LocalDateTime;

public class LoyaltyAccount {
    private int accountId;
    private int customerId;
    private int totalPoints;
    private int currentPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin JOIN
    private String customerName;
    private String customerPhone;

    public LoyaltyAccount() {
    }

    public LoyaltyAccount(int accountId, int customerId, int totalPoints, int currentPoints, LocalDateTime createdAt, LocalDateTime updatedAt, String customerName, String customerPhone) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.totalPoints = totalPoints;
        this.currentPoints = currentPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    @Override
    public String toString() {
        return "LoyaltyAccount{" + "accountId=" + accountId + ", customerId=" + customerId + ", totalPoints=" + totalPoints + ", currentPoints=" + currentPoints + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", customerName=" + customerName + ", customerPhone=" + customerPhone + '}';
    }

    
}