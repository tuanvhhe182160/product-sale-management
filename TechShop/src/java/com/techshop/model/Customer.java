package com.techshop.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Customer {
    private int customerId;
    private String phone;
    private String fullName;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Customer() {
    }

    public Customer(int customerId, String phone, String fullName, String email, String address, LocalDate dateOfBirth, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.customerId = customerId;
        this.phone = phone;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    @Override
    public String toString() {
        return "Customer{" + "customerId=" + customerId + ", phone=" + phone + ", fullName=" + fullName + ", email=" + email + ", address=" + address + ", dateOfBirth=" + dateOfBirth + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }

    
}