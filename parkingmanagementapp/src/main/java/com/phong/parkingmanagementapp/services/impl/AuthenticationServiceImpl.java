/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.dtos.AuthenticationRequest;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.services.AuthenticationService;
import com.phong.parkingmanagementapp.services.JwtService;
import com.phong.parkingmanagementapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private AuthenticationManager manager;
    
    @Autowired
    private JwtService jwtService;
    
    @Override
    public String authenticate(AuthenticationRequest authenticationRequest) {
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            User userDetails = userRepo.getUserByUsernameOrEmail(authenticationRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
            
            String token = jwtService.generateToken(userDetails);
            return token;

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }
    
}
