/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.TicketPrice;
import com.phong.parkingmanagementapp.services.PriceService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sound.midi.SysexMessage;
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
public class ApiTicketPriceController {
    @Autowired
    private PriceService priceService;
    @Autowired
    private TicketService ticketService;
    
    @GetMapping("/ticket/price/list")
    public ResponseEntity<List<TicketPrice>> getTicketPriceType(){
        return ResponseEntity.ok(this.priceService.findAll());
    }
    
    @GetMapping("/ticket/price/totalPrice")
    public ResponseEntity<?> calTotalPrice(@RequestParam Map<String, String> params){
        String startDayString = params.get("startDay");
        Date startDay = parseLocalDate.parseDate(startDayString);
        String endDayString = params.get("numberOfDays");
        int numberOfDays = Integer.parseInt(endDayString);
        String priceId = params.get("id");
        if (priceId.isBlank() || priceId.isEmpty())
            priceId = "1";
        
        int price = this.ticketService.totalPriceCal(numberOfDays, Integer.parseInt(priceId));
 
        return ResponseEntity.ok(price);
    }
}
