/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.dtos.MailBody;

/**
 *
 * @author Admin
 */
public interface MailService {
    public void sendSimpleMail(MailBody mailBody);
}
