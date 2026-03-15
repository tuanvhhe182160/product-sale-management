package com.techshop.model;

import java.time.LocalDateTime;

public class InventoryTransaction {
    private int transactionId;
    private String transactionType; // IMPORT, TRANSFER_OUT, TRANSFER_IN, SALE, RETURN, ADJUST
    private int physicalId;
    private Integer fromBranchId;
    private Integer toBranchId;
    private int quantity;
    private Integer referenceId; // Invoice ID or Transfer ID
    private int performedBy;
    private String note;
    private LocalDateTime transactionDate;
    
    // Thông tin JOIN
    private String performedByName;
    private String fromBranchName;
    private String toBranchName;

    public InventoryTransaction() {
    }

    public InventoryTransaction(int transactionId, String transactionType, int physicalId, Integer fromBranchId, Integer toBranchId, int quantity, Integer referenceId, int performedBy, String note, LocalDateTime transactionDate, String performedByName, String fromBranchName, String toBranchName) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.physicalId = physicalId;
        this.fromBranchId = fromBranchId;
        this.toBranchId = toBranchId;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.performedBy = performedBy;
        this.note = note;
        this.transactionDate = transactionDate;
        this.performedByName = performedByName;
        this.fromBranchName = fromBranchName;
        this.toBranchName = toBranchName;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(int physicalId) {
        this.physicalId = physicalId;
    }

    public Integer getFromBranchId() {
        return fromBranchId;
    }

    public void setFromBranchId(Integer fromBranchId) {
        this.fromBranchId = fromBranchId;
    }

    public Integer getToBranchId() {
        return toBranchId;
    }

    public void setToBranchId(Integer toBranchId) {
        this.toBranchId = toBranchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public int getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(int performedBy) {
        this.performedBy = performedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public void setPerformedByName(String performedByName) {
        this.performedByName = performedByName;
    }

    public String getFromBranchName() {
        return fromBranchName;
    }

    public void setFromBranchName(String fromBranchName) {
        this.fromBranchName = fromBranchName;
    }

    public String getToBranchName() {
        return toBranchName;
    }

    public void setToBranchName(String toBranchName) {
        this.toBranchName = toBranchName;
    }

    @Override
    public String toString() {
        return "InventoryTransaction{" + "transactionId=" + transactionId + ", transactionType=" + transactionType + ", physicalId=" + physicalId + ", fromBranchId=" + fromBranchId + ", toBranchId=" + toBranchId + ", quantity=" + quantity + ", referenceId=" + referenceId + ", performedBy=" + performedBy + ", note=" + note + ", transactionDate=" + transactionDate + ", performedByName=" + performedByName + ", fromBranchName=" + fromBranchName + ", toBranchName=" + toBranchName + '}';
    }

    
}