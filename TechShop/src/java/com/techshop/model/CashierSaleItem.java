package com.techshop.model;

import java.math.BigDecimal;


public class CashierSaleItem {

    // ── PhysicalProduct ──
    private int    physicalId;   // Chỉ dùng khi checkout (pick từ DB)
    private int    variantId;
    private int    branchId;
    private String branchName;
    private String imei;         // Chỉ dùng khi checkout
    private String serialNumber; // Chỉ dùng khi checkout
    private String status;
    private String importDateStr;
    private String saleDateStr;

    // ── ProductVariant ──
    private String     variantName;
    private String     sku;
    private BigDecimal unitPrice;
    private int        warrantyMonths;
    private String     imageUrl;

    // ── ProductModel ──
    private String modelName;
    private String brand;
    private String modelDesc;

    // ── ProductCategory ──
    private String categoryName;

    // ── Giỏ hàng: số lượng muốn mua ──
    private int quantity = 1;

    // ── Tồn kho: số PhysicalProduct IN_STOCK tại chi nhánh (dùng cho hiển thị) ──
    private int stockCount;

    public CashierSaleItem() {}

    // ── Getters & Setters ──

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

    public String getImportDateStr() { return importDateStr; }
    public void setImportDateStr(String importDateStr) { this.importDateStr = importDateStr; }

    public String getSaleDateStr() { return saleDateStr; }
    public void setSaleDateStr(String saleDateStr) { this.saleDateStr = saleDateStr; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModelDesc() { return modelDesc; }
    public void setModelDesc(String modelDesc) { this.modelDesc = modelDesc; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStockCount() { return stockCount; }
    public void setStockCount(int stockCount) { this.stockCount = stockCount; }

    /** Tổng tiền = unitPrice × quantity */
    public BigDecimal getSubtotal() {
        if (unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}