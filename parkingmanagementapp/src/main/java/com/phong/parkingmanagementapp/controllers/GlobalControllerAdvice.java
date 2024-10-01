/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Admin
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private UserService userService;
    
    @ModelAttribute
    public void commonAttributes(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();        
        //User u = this.userService.getUserByUsername(userDetails.getUsername());
        User u = new User();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            u = this.userService.getUserByUsername(userDetails.getUsername());
        } else if (authentication != null) {
            u = this.userService.getUserByUsername(authentication.getName());
        }

        model.addAttribute("current_user", u);
    }
}
