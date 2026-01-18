package com.techshop.model;

import java.time.LocalDateTime;

public class LoyaltyTransaction {
    private int transactionId;
    private int accountId;
    private String transactionType; // EARN, REDEEM, EXPIRE, ADJUST
    private int points;
    private Integer referenceId; // Invoice ID
    private String description;
    private LocalDateTime transactionDate;

    public LoyaltyTransaction() {
    }

    public LoyaltyTransaction(int transactionId, int accountId, String transactionType, int points, Integer referenceId, String description, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.points = points;
        this.referenceId = referenceId;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "LoyaltyTransaction{" + "transactionId=" + transactionId + ", accountId=" + accountId + ", transactionType=" + transactionType + ", points=" + points + ", referenceId=" + referenceId + ", description=" + description + ", transactionDate=" + transactionDate + '}';
    }

    
}