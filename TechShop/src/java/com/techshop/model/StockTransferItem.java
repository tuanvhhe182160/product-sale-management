package com.techshop.model;

import java.time.LocalDateTime;

public class StockTransferItem {
    private int itemId;
    private int transferId;
    private int physicalId;
    private LocalDateTime createdAt;

    public StockTransferItem() {}

    public StockTransferItem(int itemId, int transferId, int physicalId, LocalDateTime createdAt) {
        this.itemId = itemId;
        this.transferId = transferId;
        this.physicalId = physicalId;
        this.createdAt = createdAt;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getTransferId() { return transferId; }
    public void setTransferId(int transferId) { this.transferId = transferId; }

    public int getPhysicalId() { return physicalId; }
    public void setPhysicalId(int physicalId) { this.physicalId = physicalId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "StockTransferItem{itemId=" + itemId + ", transferId=" + transferId + ", physicalId=" + physicalId + "}";
    }
}
