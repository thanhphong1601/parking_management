/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.dtos.TicketDTO;
import com.phong.parkingmanagementapp.models.Receipt;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketPrice;
import com.phong.parkingmanagementapp.repositories.PriceRepository;
import com.phong.parkingmanagementapp.repositories.ReceiptRepository;
import com.phong.parkingmanagementapp.repositories.TicketPriceRepository;
import com.phong.parkingmanagementapp.repositories.TicketRepository;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class TicketServiceImpl implements TicketService{
    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final ReceiptRepository receiptRepo;
    private final TicketPriceRepository ticketPriceRepo;
    private final PriceRepository priceRepo;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepo,
            ReceiptRepository receiptRepo, TicketPriceRepository ticketPriceRepo,
            PriceRepository priceRepo) {
        this.ticketRepo = ticketRepository;
        this.userRepo = userRepo;
        this.receiptRepo = receiptRepo;
        this.ticketPriceRepo = ticketPriceRepo;
        this.priceRepo = priceRepo;
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
        if (ticket.getIsPaid() == null)
            ticket.setIsPaid(Boolean.FALSE);
        if (ticket.getTotalPrice() == null){
            ticket.setTotalPrice(this.totalPriceCal(ticket.getStartDay(), ticket.getEndDay(), ticket.getPrice().getPrice()));
        }
        
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
        
//        Receipt r = this.receiptRepo.getReceiptByTicketId(ticket.getId());
//        if (r != null)
//            this.receiptRepo.delete(r);
        ticket.setActive(Boolean.FALSE);
        this.ticketRepo.save(ticket);
        
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
    public int totalPriceCal(int numberOfDays, int priceId) {
        TicketPrice priceInstance = this.priceRepo.getTicketPriceById(priceId);
        int ticketPrice = priceInstance.getPrice();
        
        return (int) numberOfDays * ticketPrice;     
    }

    @Override
    public List<Ticket> findValidTicketsByVehicleIdAndDate(Long vehicleId, Date currentDate) {
        return this.ticketRepo.findValidTicketsByVehicleIdAndDate(vehicleId, currentDate);
    }

    @Override
    public List<Ticket> getTicketsByUserOwnedActive(String identityNumber, String name) {
        return this.ticketRepo.getTicketsByUserOwnedActive(identityNumber, name);
    }

    @Override
    public Page<Ticket> findTicketByUserOwnedPageable(String name, Boolean status, Pageable pageable) {
        return this.ticketRepo.findTicketByUserOwnedPageable(name, status, pageable);
    }

    @Override
    public Page<Ticket> getTicketsByUserOwnedActivePageable(String identityNumber, String name, Pageable pageable) {
        return this.ticketRepo.getTicketsByUserOwnedActivePageable(identityNumber, name, pageable);
    }

    @Override
    public Ticket checkTicketDate(int ticketId, Date currentDate) {
        Ticket t = this.ticketRepo.checkTicketDate(ticketId, currentDate);
        
        return t;
    }

    @Override
    public void updateLicenseField(int ticketId, String plateNumber){
        Ticket t = this.ticketRepo.getTicketById(ticketId);
        t.setLicenseNumber(plateNumber);
        
        this.ticketRepo.save(t);
    }

    @Override
    public Date calculateStartAndEndDate(int numberOfDays, Date startDate) {              
        // Chuyển đổi từ Date sang LocalDate
        LocalDate startLocalDate = startDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        
        
        LocalDate endLocalDate = startLocalDate.plusDays(numberOfDays);

        // Chuyển đổi LocalDate về Date (lúc bắt đầu của ngày)
        Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                
        
        return endDate;
    }

    @Override
    public int updateTicketTotalPrice(int typeId, int numberOfDays) {
        
        int totalPrice = 0;
        
        if (typeId == 1) { //normal type, 15k/day
            totalPrice = numberOfDays * this.ticketPriceRepo.getNormalPrice().getPrice();
        }
        
        if (typeId == 2) { //month, 7k/day
            totalPrice = numberOfDays * this.ticketPriceRepo.getMonthPrice().getPrice();
        }
        
        if (typeId == 3) { //discount type, 10k/day
            totalPrice = numberOfDays * this.ticketPriceRepo.getDiscountPrice().getPrice();
        }
                
        return totalPrice;
    }

    @Override
    public Page<TicketDTO> getTicketsInfoByUserId(int userId, Pageable pageable, Date startDay, Date endDay, Boolean isPaid) {
        return this.ticketRepo.getTicketsInfoByUserId(userId, pageable, startDay, endDay, isPaid);
    }

    @Override
    public Ticket findTopByOrderByIdDesc() {
        return this.ticketRepo.findTopByOrderByIdDesc();
    }

    @Override
    public Ticket findByTicketId(String ticketId) {
        return this.ticketRepo.findByTicketId(ticketId);
    }

    @Override
    public boolean checkTicketDateValid(int ticketId, Date currentDate) {
        return this.ticketRepo.checkTicketDateValid(ticketId, currentDate);
    }

    @Override
    public long countTotalTickets() {
        return this.ticketRepo.countTotalTickets();
    }

    @Override
    public List<Object[]> countTicketsByType() {
        return this.ticketRepo.countTicketsByType();
    }

    @Override
    public List<Object[]> countTicketsByFloor() {
        return this.ticketRepo.countTicketsByFloor();
    }

    
    
}
