/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Otp;
import com.phong.parkingmanagementapp.models.User;
import java.util.Optional;

/**
 *
 * @author Admin
 */
public interface OtpService {
    void save(Otp otp);
    Optional<Otp> findByOtpAndUser(int otp, User user);
    void delete(Otp otp);
    Otp findByUser(User user);
}
