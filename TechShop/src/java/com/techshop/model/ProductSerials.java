package com.techshop.model;

import java.sql.Timestamp;

public class ProductSerials {

    private int serialId;
    private int productId;
    private String serialNumber;
    private String status;
    private Integer orderId;
    private Timestamp warrantyStartDate;
    private Timestamp warrantyEndDate;

    public ProductSerials() {
    }

    public ProductSerials(int serialId, int productId, String serialNumber, String status,
                          Integer orderId, Timestamp warrantyStartDate, Timestamp warrantyEndDate) {
        this.serialId = serialId;
        this.productId = productId;
        this.serialNumber = serialNumber;
        this.status = status;
        this.orderId = orderId;
        this.warrantyStartDate = warrantyStartDate;
        this.warrantyEndDate = warrantyEndDate;
    }

    public int getSerialId() {
        return serialId;
    }

    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Timestamp getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyStartDate(Timestamp warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public Timestamp getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Timestamp warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    @Override
    public String toString() {
        return "ProductSerials{" +
                "serialId=" + serialId +
                ", serialNumber='" + serialNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
