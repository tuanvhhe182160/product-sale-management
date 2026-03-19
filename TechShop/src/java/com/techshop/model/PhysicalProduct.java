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
    private String imageUrl;
    private String categoryName;
    private String modelName;

    // Formatted strings dùng cho JSP (tránh dùng fmt:formatDate với LocalDateTime)
    private String importDateStr;
    private String saleDateStr;

    public PhysicalProduct() {}

    public int getPhysicalId() { return physicalId; }
    public void setPhysicalId(int physicalId) { this.physicalId = physicalId; }

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getImportDate() { return importDate; }
    public void setImportDate(LocalDateTime importDate) { this.importDate = importDate; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public String getImportDateStr() { return importDateStr; }
    public void setImportDateStr(String importDateStr) { this.importDateStr = importDateStr; }

    public String getSaleDateStr() { return saleDateStr; }
    public void setSaleDateStr(String saleDateStr) { this.saleDateStr = saleDateStr; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

}