package com.techshop.model;

import java.time.LocalDateTime;

public class WarrantyHistory {
    private int historyId;
    private int requestId;
    private String status;
    private String note;
    private int updatedBy;
    private LocalDateTime updatedAt;
    
    // Thông tin JOIN
    private String updatedByName;

    public WarrantyHistory() {
    }

    public WarrantyHistory(int historyId, int requestId, String status, String note, int updatedBy, LocalDateTime updatedAt, String updatedByName) {
        this.historyId = historyId;
        this.requestId = requestId;
        this.status = status;
        this.note = note;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
        this.updatedByName = updatedByName;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    @Override
    public String toString() {
        return "WarrantyHistory{" + "historyId=" + historyId + ", requestId=" + requestId + ", status=" + status + ", note=" + note + ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt + ", updatedByName=" + updatedByName + '}';
    }

    
}