/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Otp;
import com.phong.parkingmanagementapp.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer>{
    @Query("SELECT o FROM Otp o WHERE o.otp = :otp AND o.user = :user")
    Optional<Otp> findByOtpAndUser(@Param("otp") int otp, @Param("user") User user);
    
    @Query("SELECT o FROM Otp o WHERE o.user = :user")
    Otp findByUser(@Param("user") User user);
}
