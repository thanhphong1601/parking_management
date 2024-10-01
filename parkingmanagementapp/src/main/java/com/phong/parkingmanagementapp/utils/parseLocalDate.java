/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.utils;

import java.time.LocalDate;
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

    // Chuyển đổi LocalDate sang Date
    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // Hàm parse chính để chuyển từ chuỗi "yyyy-MM-dd" sang Date
    public static Date parseDate(String dateString) {
        LocalDate localDate = parseLocalDate(dateString);
        if (localDate != null) {
            return convertToDate(localDate);
        }
        return null;
    }
}
