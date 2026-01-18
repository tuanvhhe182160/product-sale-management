package com.techshop.model;

import java.time.LocalDateTime;

public class ProductModel {
    private int modelId;
    private int categoryId;
    private String modelCode;
    private String modelName;
    private String brand;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin JOIN
    private String categoryName;

    public ProductModel() {
    }

    public ProductModel(int modelId, int categoryId, String modelCode, String modelName, String brand, String description, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.modelId = modelId;
        this.categoryId = categoryId;
        this.modelCode = modelCode;
        this.modelName = modelName;
        this.brand = brand;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ProductModel{" + "modelId=" + modelId + ", modelCode=" + modelCode + ", modelName=" + modelName + ", brand=" + brand + '}';
    }
}