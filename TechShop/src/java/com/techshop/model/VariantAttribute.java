package com.techshop.model;

import java.time.LocalDateTime;

public class VariantAttribute {
    private int attributeId;
    private int variantId;
    private String attributeName;
    private String attributeValue;
    private LocalDateTime createdAt;

    public VariantAttribute() {
    }

    public VariantAttribute(int attributeId, int variantId, String attributeName, String attributeValue, LocalDateTime createdAt) {
        this.attributeId = attributeId;
        this.variantId = variantId;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.createdAt = createdAt;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "VariantAttribute{" + "attributeId=" + attributeId + ", variantId=" + variantId + ", attributeName=" + attributeName + ", attributeValue=" + attributeValue + ", createdAt=" + createdAt + '}';
    }

    
}