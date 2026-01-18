package com.techshop.model;

import java.time.LocalDateTime;

public class PhysicalProduct {
    private int physicalId;
    private int variantId;
    private int branchId;
    private String imei;
    private String serialNumber;
    private String status; // IN_STOCK, SOLD, RESERVED, DEFECTIVE, IN_TRANSFER, WARRANTY
    private LocalDateTime importDate;
    private LocalDateTime saleDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin JOIN
    private String variantName;
    private String branchName;
    private String sku;

    public PhysicalProduct() {
    }

    public PhysicalProduct(int physicalId, int variantId, int branchId, String imei, String serialNumber, String status, LocalDateTime importDate, LocalDateTime saleDate, LocalDateTime createdAt, LocalDateTime updatedAt, String variantName, String branchName, String sku) {
        this.physicalId = physicalId;
        this.variantId = variantId;
        this.branchId = branchId;
        this.imei = imei;
        this.serialNumber = serialNumber;
        this.status = status;
        this.importDate = importDate;
        this.saleDate = saleDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.variantName = variantName;
        this.branchName = branchName;
        this.sku = sku;
    }

    public int getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(int physicalId) {
        this.physicalId = physicalId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
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

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "PhysicalProduct{" + "physicalId=" + physicalId + ", variantId=" + variantId + ", branchId=" + branchId + ", imei=" + imei + ", serialNumber=" + serialNumber + ", status=" + status + ", importDate=" + importDate + ", saleDate=" + saleDate + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", variantName=" + variantName + ", branchName=" + branchName + ", sku=" + sku + '}';
    }

    
}