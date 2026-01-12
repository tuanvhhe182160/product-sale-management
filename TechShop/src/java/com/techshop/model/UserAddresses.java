package com.techshop.model;

public class UserAddresses {

    private int addressId;
    private int userId;
    private String fullAddress;
    private String phone;
    private boolean isDefault;

    public UserAddresses() {
    }

    public UserAddresses(int addressId, int userId, String fullAddress, String phone, boolean isDefault) {
        this.addressId = addressId;
        this.userId = userId;
        this.fullAddress = fullAddress;
        this.phone = phone;
        this.isDefault = isDefault;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "UserAddresses{" +
                "addressId=" + addressId +
                ", userId=" + userId +
                ", fullAddress='" + fullAddress + '\'' +
                ", phone='" + phone + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
