package com.techshop.model;

import java.time.LocalDateTime;

public class StockTransfer {
    private int transferId;
    private String transferCode;
    private int fromBranchId;
    private int toBranchId;
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

    public StockTransfer() {}

    public StockTransfer(int transferId, String transferCode, int fromBranchId, int toBranchId,
                         int requestedBy, Integer approvedBy, Integer receivedBy, String status,
                         String note, LocalDateTime requestDate, LocalDateTime approvalDate,
                         LocalDateTime completionDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.transferId = transferId;
        this.transferCode = transferCode;
        this.fromBranchId = fromBranchId;
        this.toBranchId = toBranchId;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.receivedBy = receivedBy;
        this.status = status;
        this.note = note;
        this.requestDate = requestDate;
        this.approvalDate = approvalDate;
        this.completionDate = completionDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
