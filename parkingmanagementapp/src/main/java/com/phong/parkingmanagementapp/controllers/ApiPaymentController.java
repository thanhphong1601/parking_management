/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.services.PaymentService;
import com.phong.parkingmanagementapp.services.TicketService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApiPaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private TicketService ticketService;
    
    @PostMapping("/successPayment")
    public ResponseEntity<String> getPage(@RequestParam Map<String, String> params){
        int ticketId = Integer.parseInt(params.get("id"));
        Ticket ticket = this.ticketService.getTicketById(ticketId);
        ticket.setIsPaid(Boolean.TRUE);
        this.ticketService.addOrUpdate(ticket);
        
        return ResponseEntity.ok("Done");
    }
    
    @PostMapping("/payment/createPayment")
    public ResponseEntity<String> createPayment(@RequestParam Map<String, String> params) throws Exception{
        String orderInfo = params.getOrDefault("orderInfo", "Thông tin giao dịch");
        long price = Long.parseLong(params.get("price"));
        int ticketId = Integer.parseInt(params.get("ticketId"));
       
        String url = this.paymentService.createPaymentUrl(price, orderInfo, ticketId);
        
        return ResponseEntity.ok(url);
    }
    
    
}
