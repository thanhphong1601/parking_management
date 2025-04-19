/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public interface TicketTypeRepository extends JpaRepository<TicketType, Long>{
    @Override
    List<TicketType> findAll();
    
    @Query("SELECT t FROM TicketType t WHERE t.type = 'Thường'")
    TicketType getNormalType();
    
    @Query("SELECT t FROM TicketType t WHERE t.type = 'VIP'")
    TicketType getVIPType();
    
    @Query("SELECT t FROM TicketType t WHERE t.type = 'Tháng'")
    TicketType getMonthType();
    
    @Query("SELECT t FROM TicketType t WHERE t.id = :id")
    TicketType getTicketTypeById(@Param("id") int typeId);
}
