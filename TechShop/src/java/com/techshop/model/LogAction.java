/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.model;

/**
 *
 * @author justi
 */
public enum LogAction {
    LOGIN,
    LOGOUT,
    FAILED_LOGIN,
    CHANGE_PASSWORD,
    REQUEST_PASSWORD_RESET,
    RESET_PASSWORD,
    
    // Nhóm User / Nhân sự
    CREATE_USER,
    UPDATE_USER,
    LOCK_USER,
    UNLOCK_USER,
    
    //Nhóm chi nhánh
    CREATE_BRANCH,
    UPDATE_BRANCH,
    
    // Nhóm Sản phẩm & Kho
    CREATE_CATEGORY,
    UPDATE_CATEGORY,
    CREATE_PRODUCT_MODEL,
    UPDATE_PRODUCT_MODEL,
    CREATE_PRODUCT_VARIANT,
    DELETE_PRODUCT_VARIANT,
    UPDATE_PRODUCT_VARIANT,
    UPDATE_PHYSICAL_PRODUCT,
    CREATE_PRODUCT,
    UPDATE_PRODUCT,
    STOCK_IN,
    STOCK_OUT,
    STOCK_TRANSFER,
    CREATE_STOCK_TRANSFER_REQUEST,
    RECEIVE_STOCK_TRANSFER,
    REJECT_STOCK_TRANSFER,
    APPROVE_STOCK_TRANSFER,
    IMPORT_INVENTORY,
    
    // Nhóm Bán hàng (Invoice)
    CREATE_INVOICE,
    CANCEL_INVOICE,
    EXPORT_INVOICE,
    PRINT_INVOICE,
    
    // Nhóm Báo cáo / Kế toán
    APPROVE_PAYMENT,
    VIEW_FINANCIAL_REPORT,
    EXPORT_FINANCIAL_REPORT,
    CLOSE_ACCOUNTING_PERIOD,
    EXPORT_SALES_HISTORY,
    
    //Nhóm Bảo hành
    UPDATE_WARRANTY_REQUEST,
    CREATE_WARRANTY_REQUEST,
    
    //Nhóm Chăm sóc KH
    UPDATE_CUSTOMER,
    CREATE_CUSTOMER,
    //Nhóm Hệ thống
    EXPORT_AUDIT_LOGS
}
