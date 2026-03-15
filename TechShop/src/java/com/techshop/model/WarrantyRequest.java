package com.techshop.model;

import java.time.LocalDateTime;

public class WarrantyRequest {
    private int requestId;
    private String requestCode;
    private int invoiceId;
    private int physicalId;
    private int customerId;
    private String issueDescription;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, REJECTED, CANCELLED
    private Integer technicianId;
    private int customerServiceId;
    private String resolution;
    private LocalDateTime requestDate;
    private LocalDateTime completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin JOIN
    private String customerName;
    private String customerPhone;
    private String variantName;
    private String imei;
    private String technicianName;
    private String customerServiceName;

    public WarrantyRequest() {
    }

    public WarrantyRequest(int requestId, String requestCode, int invoiceId, int physicalId, int customerId, String issueDescription, String status, Integer technicianId, int customerServiceId, String resolution, LocalDateTime requestDate, LocalDateTime completionDate, LocalDateTime createdAt, LocalDateTime updatedAt, String customerName, String customerPhone, String variantName, String imei, String technicianName, String customerServiceName) {
        this.requestId = requestId;
        this.requestCode = requestCode;
        this.invoiceId = invoiceId;
        this.physicalId = physicalId;
        this.customerId = customerId;
        this.issueDescription = issueDescription;
        this.status = status;
        this.technicianId = technicianId;
        this.customerServiceId = customerServiceId;
        this.resolution = resolution;
        this.requestDate = requestDate;
        this.completionDate = completionDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.variantName = variantName;
        this.imei = imei;
        this.technicianName = technicianName;
        this.customerServiceName = customerServiceName;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Integer technicianId) {
        this.technicianId = technicianId;
    }

    public int getCustomerServiceId() {
        return customerServiceId;
    }

    public void setCustomerServiceId(int customerServiceId) {
        this.customerServiceId = customerServiceId;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
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

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getCustomerServiceName() {
        return customerServiceName;
    }

    public void setCustomerServiceName(String customerServiceName) {
        this.customerServiceName = customerServiceName;
    }

    @Override
    public String toString() {
        return "WarrantyRequest{" + "requestId=" + requestId + ", requestCode=" + requestCode + ", invoiceId=" + invoiceId + ", physicalId=" + physicalId + ", customerId=" + customerId + ", issueDescription=" + issueDescription + ", status=" + status + ", technicianId=" + technicianId + ", customerServiceId=" + customerServiceId + ", resolution=" + resolution + ", requestDate=" + requestDate + ", completionDate=" + completionDate + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", customerName=" + customerName + ", customerPhone=" + customerPhone + ", variantName=" + variantName + ", imei=" + imei + ", technicianName=" + technicianName + ", customerServiceName=" + customerServiceName + '}';
    }

    
}