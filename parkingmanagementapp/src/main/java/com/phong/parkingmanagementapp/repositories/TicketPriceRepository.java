/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;


import com.phong.parkingmanagementapp.models.TicketPrice;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Long>{
    @Override
    List<TicketPrice> findAll();
    
    @Query("SELECT p FROM TicketPrice p WHERE p.id = 1")
    TicketPrice getNormalPrice();
    
    @Query("SELECT p FROM TicketPrice p WHERE p.id = 2")
    TicketPrice getDiscountPrice();
    
    @Query("SELECT p FROM TicketPrice p WHERE p.id = 3")
    TicketPrice getMonthPrice();
}
