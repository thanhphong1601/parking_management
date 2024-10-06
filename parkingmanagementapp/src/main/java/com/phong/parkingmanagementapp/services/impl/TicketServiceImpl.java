/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.repositories.TicketRepository;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.services.TicketService;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class TicketServiceImpl implements TicketService{
    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepo) {
        this.ticketRepo = ticketRepository;
        this.userRepo = userRepo;
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepo.findAll();
    }

    @Override
    public List<Ticket> findTicketByUserOwned(String name) {
        return ticketRepo.findTicketByUserOwned(name);
    }

    @Override
    public void addOrUpdate(Ticket ticket) {
        ticket.setIsPaid(Boolean.FALSE);
        ticket.setTotalPrice(this.totalPriceCal(ticket.getStartDay(), ticket.getEndDay(), ticket.getPrice().getPrice()));
        
        ticketRepo.save(ticket);
    }

    @Override
    public Ticket getTicketById(int id) {
        return this.ticketRepo.getTicketById(id);
    }

    @Override
    public boolean delete(Ticket ticket) {
        if (ticket == null)
            return false;
        
        ticketRepo.delete(ticket);
        return true;
    }

    @Override
    public List<Ticket> findTicketByUserOwnedId(int id) {
        return this.ticketRepo.findTicketByUserOwnedId(id);
    }

    @Override
    public boolean deleteTicketsByUserOwnedId(int id) {
        List<Ticket> ticketList = this.ticketRepo.findTicketByUserOwnedId(id);
        
        if (this.userRepo.getUserById(id) == null)
            return false;
        
        if (ticketList.isEmpty())
            return false;
        
//        for(Ticket t : ticketList){
//            this.ticketRepo.delete(t);
//        }
        this.ticketRepo.deleteAllInBatch(ticketList);
        
        return true;
    }

    @Override
    public int totalPriceCal(Date startDay, Date endDay, int pricePerDay) {
        long daysBetween = ChronoUnit.DAYS.between(startDay.toInstant(), endDay.toInstant());
        
        return (int) daysBetween * pricePerDay;
    }

    @Override
    public List<Ticket> findValidTicketsByVehicleIdAndDate(Long vehicleId, Date currentDate) {
        return this.ticketRepo.findValidTicketsByVehicleIdAndDate(vehicleId, currentDate);
    }

    @Override
    public List<Ticket> getTicketsByUserOwnedActive(String identityNumber, String name) {
        return this.ticketRepo.getTicketsByUserOwnedActive(identityNumber, name);
    }
    
}
