/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.TicketPrice;
import com.phong.parkingmanagementapp.repositories.PriceRepository;
import com.phong.parkingmanagementapp.services.PriceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PriceServiceImpl implements PriceService{
    private final PriceRepository priceRepo;
    
    @Autowired
    public PriceServiceImpl(PriceRepository priceRepo){
        this.priceRepo = priceRepo;
    }

    @Override
    public List<TicketPrice> findAll() {
        return this.priceRepo.findAll();
    }

    @Override
    public TicketPrice getTicketPriceById(int id) {
        return this.priceRepo.getTicketPriceById(id);
    }
    
}
