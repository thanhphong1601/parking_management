/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class parseLocalDate {
    // Định dạng ngày tháng dạng "yyyy-MM-dd"

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

// Hàm chuyển đổi chuỗi ngày sang LocalDate
    public static LocalDate parseLocalDate(String dateString) {
        try {
            // Chuyển đổi chuỗi ngày sang LocalDate
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Định dạng ngày không hợp lệ: " + e.getMessage());
            return null;
        }
    }

// Chuyển đổi LocalDate sang Date với thời gian bắt đầu của ngày (00:00:00)
    public static Date convertToDate(LocalDate localDate) {
        // Chuyển LocalDate thành LocalDateTime tại thời điểm bắt đầu của ngày
        LocalDateTime localDateTime = localDate.atStartOfDay();
        // Chuyển đổi từ LocalDateTime sang Date
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

// Hàm parse chính để chuyển từ chuỗi "yyyy-MM-dd" sang Date với thời gian bắt đầu
    public static Date parseDate(String dateString) {
        LocalDate localDate = parseLocalDate(dateString);
        if (localDate != null) {
            return convertToDate(localDate);
        }
        return null;
    }
    
    public static Date parseStringToDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        java.sql.Date date = java.sql.Date.valueOf(localDate);
        
        return date;
    }
    
    public static Date parseStringToLocalDateToDate(String dateStr){
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter2);
        
        Date currentDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return currentDate;
    }

}
