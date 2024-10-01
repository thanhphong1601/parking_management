/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiCustomerController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/customer/list")
    public ResponseEntity<List<User>> getCustomerList(@RequestParam Map<String, String> params){
        String name = params.get("name");
        String identityNumber = params.get("identityNum");
        return ResponseEntity.ok(this.userService.findUserByIdentityNumberOrNameOrRole(identityNumber, name, 3));
    }
    
    @PostMapping("/customer/{id}")
    public ResponseEntity<?> uploadCustomerInfo(@RequestParam Map<String, String> params,
            @PathVariable("id") int id){
        User u = this.userService.getUserById(id);
        if (params.get("name") != null)
            u.setName(params.get("name"));
        if (params.get("email") != null)
            u.setEmail(params.get("email"));
        if (params.get("address") != null)
            u.setAddress(params.get("address"));
        if (params.get("phone") != null)
            u.setPhone(params.get("phone"));
        if (params.get("identityNumber") != null)
            u.setIdentityNumber(params.get("identityNumber"));
        
        this.userService.saveUser(u);
        return null;
    }
}
