/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.model;

import java.sql.Timestamp;

/**
 *
 * @author justi
 */
public class WarrantyCheckDTO {
    private String imei;
    private String variantName;
    private String invoiceCode;
    private Timestamp invoiceDate;
    private String customerName;
    private String customerPhone;
    private int customerId;
    private int invoiceId;
    private int physicalId;
    private int warrantyMonths;
    private Timestamp warrantyEndDate;
    private String warrantyStatus;
    private String customerEmail;

    public WarrantyCheckDTO() {
    }

    public WarrantyCheckDTO(String imei, String variantName, String invoiceCode, Timestamp invoiceDate, String customerName, String customerPhone, int customerId, int invoiceId, int physicalId, int warrantyMonths, Timestamp warrantyEndDate, String warrantyStatus, String customerEmail) {
        this.imei = imei;
        this.variantName = variantName;
        this.invoiceCode = invoiceCode;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerId = customerId;
        this.invoiceId = invoiceId;
        this.physicalId = physicalId;
        this.warrantyMonths = warrantyMonths;
        this.warrantyEndDate = warrantyEndDate;
        this.warrantyStatus = warrantyStatus;
        this.customerEmail = customerEmail;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public Timestamp getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Timestamp invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Timestamp getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Timestamp warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public String getWarrantyStatus() {
        return warrantyStatus;
    }

    public void setWarrantyStatus(String warrantyStatus) {
        this.warrantyStatus = warrantyStatus;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }    

    @Override
    public String toString() {
        return "WarrantyCheckDTO{" + "imei=" + imei + ", variantName=" + variantName + ", invoiceCode=" + invoiceCode + ", invoiceDate=" + invoiceDate + ", customerName=" + customerName + ", customerPhone=" + customerPhone + ", customerId=" + customerId + ", invoiceId=" + invoiceId + ", physicalId=" + physicalId + ", warrantyMonths=" + warrantyMonths + ", warrantyEndDate=" + warrantyEndDate + ", warrantyStatus=" + warrantyStatus + ", customerEmail=" + customerEmail + '}';
    }
    
    
}
