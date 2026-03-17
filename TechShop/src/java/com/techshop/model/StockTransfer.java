package com.techshop.model;

import java.time.LocalDateTime;

public class StockTransfer {
    private int transferId;
    private String transferCode;
    private int fromBranchId;
    private int toBranchId;
    private int variantId;
    private int requestedQuantity;
    private int requestedBy;
    private Integer approvedBy;
    private Integer receivedBy;
    private String status;
    private String note;
    private LocalDateTime requestDate;
    private LocalDateTime approvalDate;
    private LocalDateTime completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Join fields
    private String fromBranchName;
    private String toBranchName;
    private String requestedByName;
    private String approvedByName;
    private String variantName;
    private String sku;
    private int itemCount;

    public StockTransfer() {}

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(int requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getFromBranchName() { return fromBranchName; }
    public void setFromBranchName(String fromBranchName) { this.fromBranchName = fromBranchName; }

    public String getToBranchName() { return toBranchName; }
    public void setToBranchName(String toBranchName) { this.toBranchName = toBranchName; }

    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public int getTransferId() { return transferId; }
    public void setTransferId(int transferId) { this.transferId = transferId; }

    public String getTransferCode() { return transferCode; }
    public void setTransferCode(String transferCode) { this.transferCode = transferCode; }

    public int getFromBranchId() { return fromBranchId; }
    public void setFromBranchId(int fromBranchId) { this.fromBranchId = fromBranchId; }

    public int getToBranchId() { return toBranchId; }
    public void setToBranchId(int toBranchId) { this.toBranchId = toBranchId; }

    public int getRequestedBy() { return requestedBy; }
    public void setRequestedBy(int requestedBy) { this.requestedBy = requestedBy; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public Integer getReceivedBy() { return receivedBy; }
    public void setReceivedBy(Integer receivedBy) { this.receivedBy = receivedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "StockTransfer{transferId=" + transferId + ", transferCode='" + transferCode + "', status='" + status + "'}";
    }
}
