/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.models;

/**
 *
 * @author Admin
 */
public enum PositionStatusEnum {
    AVAILABLE,       // Vị trí đang trống
    RESERVED,        // Vị trí đã được giữ trước (đăng ký vé hoặc chỉ định cho khách mới)
    OCCUPIED,        // Xe đã đậu ở vị trí
    WRONG_POSITION,  // Xe đậu sai vị trí (so với vé đã đăng ký)
    MAINTENANCE,     // Vị trí tạm dừng sử dụng (bảo trì, chặn)
    ERROR,           // Lỗi trạng thái (không xác định, sai luồng)
    MANUAL_OVERRIDE  // Trạng thái do nhân viên thay đổi thủ công
}
