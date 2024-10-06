/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiPositionController {
    @Autowired
    private PositionService poService;
    
    
    @GetMapping("/position/list")
    public ResponseEntity<?> getLinesByFloorId(@RequestParam(value = "id", required = false) Integer id){
        if (id == null)
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(this.poService.findPositionsByLineId(id));
    }
    
    @GetMapping("/position/list/all")
    public ResponseEntity<?> getAllPositions(){
        return ResponseEntity.ok(this.poService.findAll());
    }
}
