package com.techshop.model;

public class Warranties {

    private int warrantyId;
    private int serialId;
    private int userId;
    private String issueDescription;
    private String status;
    private String responseNote;

    public Warranties() {
    }

    public Warranties(int warrantyId, int serialId, int userId,
                      String issueDescription, String status, String responseNote) {
        this.warrantyId = warrantyId;
        this.serialId = serialId;
        this.userId = userId;
        this.issueDescription = issueDescription;
        this.status = status;
        this.responseNote = responseNote;
    }

    public int getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(int warrantyId) {
        this.warrantyId = warrantyId;
    }

    public int getSerialId() {
        return serialId;
    }

    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getResponseNote() {
        return responseNote;
    }

    public void setResponseNote(String responseNote) {
        this.responseNote = responseNote;
    }

    @Override
    public String toString() {
        return "Warranties{" +
                "warrantyId=" + warrantyId +
                ", status='" + status + '\'' +
                '}';
    }
}
