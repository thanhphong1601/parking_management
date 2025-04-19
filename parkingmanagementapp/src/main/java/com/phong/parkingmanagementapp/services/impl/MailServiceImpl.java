/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.dtos.MailBody;
import com.phong.parkingmanagementapp.services.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class MailServiceImpl implements MailService{
    private final JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username}")
    private String username;
    
    
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendSimpleMail(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom(username);
        message.setSubject((mailBody.subject()));
        message.setText(mailBody.text());
        
        javaMailSender.send(message);
    }
    
}
