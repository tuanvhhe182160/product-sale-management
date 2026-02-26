package com.techshop.model;

import java.math.BigDecimal;

/**
 * Đại diện cho 1 sản phẩm vật lý (IMEI) đã được Cashier chọn để bán.
 * Lưu trong session dưới key "saleCart" dưới dạng List<CashierSaleItem>.
 * Đặt tên CashierSaleItem để tránh conflict với CartItem đã có trong project.
 */
public class CashierSaleItem {

    private int physicalId;
    private int variantId;
    private String imei;
    private String serialNumber;
    private String variantName;
    private String sku;
    private String modelName;
    private String brand;
    private String categoryName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private int warrantyMonths;
    private int branchId;

    public CashierSaleItem() {}

    public int getPhysicalId() { return physicalId; }
    public void setPhysicalId(int physicalId) { this.physicalId = physicalId; }

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }
}