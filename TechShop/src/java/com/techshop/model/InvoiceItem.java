package com.techshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceItem {
    private int itemId;
    private int invoiceId;
    private int physicalId;
    private int variantId;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;
    private int warrantyMonths;
    private LocalDateTime createdAt;
    
    // Thông tin JOIN
    private String variantName;
    private String sku;
    private String imei;

    public InvoiceItem() {
    }

    public InvoiceItem(int itemId, int invoiceId, int physicalId, int variantId, BigDecimal unitPrice, int quantity, BigDecimal subtotal, int warrantyMonths, LocalDateTime createdAt, String variantName, String sku, String imei) {
        this.itemId = itemId;
        this.invoiceId = invoiceId;
        this.physicalId = physicalId;
        this.variantId = variantId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.warrantyMonths = warrantyMonths;
        this.createdAt = createdAt;
        this.variantName = variantName;
        this.sku = sku;
        this.imei = imei;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "InvoiceItem{" + "itemId=" + itemId + ", invoiceId=" + invoiceId + ", physicalId=" + physicalId + ", variantId=" + variantId + ", unitPrice=" + unitPrice + ", quantity=" + quantity + ", subtotal=" + subtotal + ", warrantyMonths=" + warrantyMonths + ", createdAt=" + createdAt + ", variantName=" + variantName + ", sku=" + sku + ", imei=" + imei + '}';
    }

    
}