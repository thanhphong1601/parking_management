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
import java.util.List;
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
    public Page<Receipt> getReceipt(Pageable pageale, String userName, String ticketId) {
        return this.receiptRepo.getReceipt(pageale, userName, ticketId);
    }

    @Override
    public boolean saveReceipt(Receipt receipt) {
        User owner = receipt.getTicket().getUserOwned();
        
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        
        receipt.setOwner(owner);
        receipt.setCreateAt(date);
        receipt.setTotalAmount(receipt.getTicket().getTotalPrice());
        
        this.receiptRepo.save(receipt);
        
        return true;
    }

    @Override
    public Receipt getReceiptByTicketId(int id) {
        return this.receiptRepo.getReceiptByTicketId(id);
    }

    @Override
    public Page<Receipt> getCustomerReceipt(Pageable pageale, int userId, Date startDay, Date endDay) {
        return this.receiptRepo.getCustomerReceipt(pageale, userId, startDay, endDay);
    }

    @Override
    public long sumTotalRevenue() {
        return this.receiptRepo.sumTotalRevenue();
    }

    @Override
    public List<Object[]> getMonthlyRevenue() {
        return this.receiptRepo.getMonthlyRevenue();
    }
    
}
