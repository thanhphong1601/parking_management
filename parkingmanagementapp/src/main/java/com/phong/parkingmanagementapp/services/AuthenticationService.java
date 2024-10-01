/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.dtos.AuthenticationRequest;

/**
 *
 * @author Admin
 */
public interface AuthenticationService {
    String authenticate(AuthenticationRequest authenticationRequest);
//
//    AuthenticationResponse register(RegisterRequest registerRequest);
//
//    AuthenticationResponse loginWithGoogle(LoginWithGoogleRequest data);
}
