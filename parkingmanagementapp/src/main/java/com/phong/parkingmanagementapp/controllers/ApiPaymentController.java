/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.services.PaymentService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @GetMapping("/successPayment")
    public ResponseEntity<String> getPage(@RequestParam Map<String, String> params){
        String status = this.paymentService.handleVnpayReturn(params);
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/payment/createPayment")
    public ResponseEntity<String> test() throws Exception{
        String url = this.paymentService.createPaymentUrl(100000, "Xin chào");
        
        return ResponseEntity.ok(url);
    }
    
    
}
