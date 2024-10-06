/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.VehicleType;
import com.phong.parkingmanagementapp.services.VehicleTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiVehicleTypeController {
    @Autowired
    private VehicleTypeService typeService;
    
    
    @GetMapping("/vehicle/types")
    public ResponseEntity<List<VehicleType>> getTypeList(){
        return ResponseEntity.ok(this.typeService.findAll());
    }
}
