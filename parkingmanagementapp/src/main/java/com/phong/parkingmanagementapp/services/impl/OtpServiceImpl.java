/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Otp;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.repositories.OtpRepository;
import com.phong.parkingmanagementapp.services.OtpService;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class OtpServiceImpl implements OtpService{ 
    private final OtpRepository otpRepo;

    public OtpServiceImpl(OtpRepository otpRepo) {
        this.otpRepo = otpRepo;
    }
    
    @Override
    public void save(Otp otp) {
        this.otpRepo.save(otp);
    }

    @Override
    public Optional<Otp> findByOtpAndUser(int otp, User user) {
        return this.otpRepo.findByOtpAndUser(otp, user);
    }

    @Override
    public void delete(Otp otp) {
        this.otpRepo.delete(otp);
    }

    @Override
    public Otp findByUser(User user) {
        return this.otpRepo.findByUser(user);
    }
    
}
