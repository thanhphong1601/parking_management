/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.TicketPrice;
import com.phong.parkingmanagementapp.repositories.TicketPriceRepository;
import com.phong.parkingmanagementapp.repositories.TicketTypeRepository;
import com.phong.parkingmanagementapp.services.TicketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class TicketPriceServiceImpl implements TicketPriceService{
    @Autowired
    private TicketPriceRepository ticketPriceRepo;

    @Override
    public TicketPrice getNormalPrice() {
        return this.ticketPriceRepo.getNormalPrice();
    }

    @Override
    public TicketPrice getDiscountPrice() {
        return this.ticketPriceRepo.getDiscountPrice();
    }

    @Override
    public TicketPrice getMonthPrice() {
        return this.ticketPriceRepo.getMonthPrice();
    }
    

    
    
}
