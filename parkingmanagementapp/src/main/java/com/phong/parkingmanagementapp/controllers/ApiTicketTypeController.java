/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.TicketPrice;
import com.phong.parkingmanagementapp.models.TicketType;
import com.phong.parkingmanagementapp.services.PriceService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.TicketTypeService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class ApiTicketTypeController {
    @Autowired
    private TicketTypeService ticketTypeService;
    
    @GetMapping("/ticket/type/list")
    public ResponseEntity<List<TicketType>> getTicketPriceType(){
        return ResponseEntity.ok(this.ticketTypeService.getAllTypes());
    }
    
}
