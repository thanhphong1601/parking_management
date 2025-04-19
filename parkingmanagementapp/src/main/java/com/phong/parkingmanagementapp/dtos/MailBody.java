/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.dtos;

import lombok.Builder;

/**
 *
 * @author Admin
 */
@Builder
public record MailBody(String to, String subject, String text) {
    
}
