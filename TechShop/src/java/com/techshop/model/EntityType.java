/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.model;

/**
 *
 * @author justi
 */
public enum EntityType {
    USER,
    ROLE,
    BRANCH,
    CATEGORY,
    PRODUCT_MODEL,
    PRODUCT_VARIANT,
    INVOICE,
    INVENTORY_TRANSACTION,
    WARRANTY_REQUEST,
    ACCOUNTING,
    CUSTOMER,
    REPORT, // Dành cho các hành động liên quan đến xuất báo cáo
    SYSTEM  // Dành cho các hành động chung chung như Login/Logout
}
