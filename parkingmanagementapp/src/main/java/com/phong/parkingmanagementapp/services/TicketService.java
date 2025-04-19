/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.dtos.TicketDTO;
import com.phong.parkingmanagementapp.models.Ticket;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface TicketService {
    List<Ticket> findAll();
    List<Ticket> findTicketByUserOwned(String name);
    void addOrUpdate(Ticket ticket);
    boolean delete(Ticket ticket);
    Ticket getTicketById(int id);
    List<Ticket> findTicketByUserOwnedId(int id);
    boolean deleteTicketsByUserOwnedId(int id);
    int totalPriceCal(Date startDay, Date endDay, int pricePerDay);
    int totalPriceCal(int numberOfDays, int priceId);
    List<Ticket> findValidTicketsByVehicleIdAndDate(Long vehicleId, 
                                                    Date currentDate);
    List<Ticket> getTicketsByUserOwnedActive(String identityNumber, String name);
    Page<Ticket> findTicketByUserOwnedPageable(String name, Pageable pageable);
    Page<Ticket> getTicketsByUserOwnedActivePageable(String identityNumber,String name, Pageable pageable);
    Ticket checkTicketDate(int ticketId, Date currentDate);
    void updateLicenseField(int ticketId, String plateNumber);
    Date calculateStartAndEndDate(int numberOfDays, Date startDate);
    int updateTicketTotalPrice(int typeId, int numberOfDays);
    Page<TicketDTO> getTicketsInfoByUserId(int userId, Pageable pageable,
            Date startDay, Date endDay, Boolean isPaid);
    Ticket findTopByOrderByIdDesc();
    Ticket findByTicketId(String ticketId);
    boolean checkTicketDateValid(int ticketId, Date currentDate);

}
