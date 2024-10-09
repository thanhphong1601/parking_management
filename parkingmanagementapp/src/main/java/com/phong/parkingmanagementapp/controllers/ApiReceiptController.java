/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.ReceiptService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ApiReceiptController {
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    
    @Value("${page_size}")
    private int pageSize;
    
    @GetMapping("/receipt/list")
    public ResponseEntity<?> getReceipts(@RequestParam Map<String, String> params){
        String name = params.getOrDefault("name", "");
        int page = Integer.parseInt(params.getOrDefault("page", "0"));

        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(this.receiptService.getReceipt(pageable, name));
    }
    
    @PostMapping("/receipt/create")
    public ResponseEntity<?> createReceipt(@RequestParam Map<String, String> params){
        int ticketId = Integer.parseInt(params.get("id"));
        this.receiptService.saveReceipt(ticketId);
        
        return ResponseEntity.ok("Done");
    }
}
