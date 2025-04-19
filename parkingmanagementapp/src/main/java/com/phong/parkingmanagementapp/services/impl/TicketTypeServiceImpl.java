/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketType;
import com.phong.parkingmanagementapp.repositories.TicketTypeRepository;
import com.phong.parkingmanagementapp.services.TicketTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class TicketTypeServiceImpl implements TicketTypeService{
    @Autowired
    private TicketTypeRepository ticketTypeRepo;
    

    @Override
    public TicketType getNormalType() {
        return this.ticketTypeRepo.getNormalType();
    }

    @Override
    public TicketType getVIPType() {
        return this.ticketTypeRepo.getVIPType();
    }

    @Override
    public TicketType getMonthType() {
        return this.ticketTypeRepo.getMonthType();
    }

    @Override
    public List<TicketType> getAllTypes() {
        return this.ticketTypeRepo.findAll();
    }

    @Override
    public TicketType getTicketTypeById(int typeId) {
        return this.ticketTypeRepo.getTicketTypeById(typeId);
    }

    
}
