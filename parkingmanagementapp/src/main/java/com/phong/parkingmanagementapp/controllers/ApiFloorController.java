/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.services.FloorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiFloorController {
    @Autowired
    private FloorService floorService;
    
    @GetMapping("/floor/list")
    public ResponseEntity<List<Floor>> getFloorList(){
        return ResponseEntity.ok(floorService.findAll());
    }
    
    @GetMapping("/floorDTO/list")
    public ResponseEntity<?> getFloorDTOList(){
        return new ResponseEntity<>(this.floorService.getAllFloorDTOs(), HttpStatus.OK);
    }
}
