/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Receipt;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.repositories.ReceiptRepository;
import com.phong.parkingmanagementapp.repositories.TicketRepository;
import com.phong.parkingmanagementapp.services.ReceiptService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class ReceiptServiceImpl implements ReceiptService{
    @Autowired
    private ReceiptRepository receiptRepo;
    @Autowired
    private TicketRepository ticketRepo;

    @Override
    public Page<Receipt> getReceipt(Pageable pageale, String userName) {
        return this.receiptRepo.getReceipt(pageale, userName);
    }

    @Override
    public boolean saveReceipt(int receiptId) {
        Ticket t = this.ticketRepo.getTicketById(receiptId);
        User owner = t.getUserOwned();
        
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        
        Receipt r = new Receipt();
        r.setTicket(t);
        r.setOwner(owner);
        r.setCreateAt(date);
        r.setTotalAmount(t.getTotalPrice());
        
        this.receiptRepo.save(r);
        
        return true;
    }
    
}
