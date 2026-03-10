package com.techshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Invoice {
    private int invoiceId;
    private String invoiceCode;
    private int customerId;
    private int branchId;
    private int cashierId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentMethod; // CASH, CARD, TRANSFER, MIXED
    private String status; // COMPLETED, CANCELLED
    private String note;
    private LocalDateTime invoiceDate;
    private LocalDateTime createdAt;
    
    // Thông tin JOIN
    private String customerName;
    private String customerPhone;
    private String branchName;
    private String cashierName;
    private int itemCount;

    public Invoice() {
    }

    public Invoice(int invoiceId, String invoiceCode, int customerId, int branchId, int cashierId, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal finalAmount, String paymentMethod, String status, String note, LocalDateTime invoiceDate, LocalDateTime createdAt, String customerName, String customerPhone, String branchName, String cashierName) {
        this.invoiceId = invoiceId;
        this.invoiceCode = invoiceCode;
        this.customerId = customerId;
        this.branchId = branchId;
        this.cashierId = cashierId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.note = note;
        this.invoiceDate = invoiceDate;
        this.createdAt = createdAt;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.branchName = branchName;
        this.cashierName = cashierName;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getInvoiceDateFormatted() {
        if (invoiceDate == null) return "";
        return invoiceDate.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    public String getInvoiceDateShort() {
        if (invoiceDate == null) return "";
        return invoiceDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String toString() {
        return "Invoice{" + "invoiceId=" + invoiceId + ", invoiceCode=" + invoiceCode + ", customerId=" + customerId + ", branchId=" + branchId + ", cashierId=" + cashierId + ", totalAmount=" + totalAmount + ", discountAmount=" + discountAmount + ", finalAmount=" + finalAmount + ", paymentMethod=" + paymentMethod + ", status=" + status + ", note=" + note + ", invoiceDate=" + invoiceDate + ", createdAt=" + createdAt + ", customerName=" + customerName + ", customerPhone=" + customerPhone + ", branchName=" + branchName + ", cashierName=" + cashierName + '}';
    }

    
}