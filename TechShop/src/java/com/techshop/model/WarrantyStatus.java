/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techshop.model;

/**
 *
 * @author justi
 */
public enum WarrantyStatus {
    PENDING,      // Chờ tiếp nhận (CS vừa tạo phiếu)
    IN_PROGRESS,  // Đang xử lý (Kỹ thuật viên đã tiếp nhận/đang sửa)
    COMPLETED,    // Hoàn thành (Đã sửa xong/đổi trả xong)
    REJECTED,     // Từ chối (Lỗi người dùng, rớt nước, hết hạn...)
    CANCELLED
}
