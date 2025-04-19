/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Receipt;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Admin
 */
@Controller
public interface ReceiptRepository extends JpaRepository<Receipt, Long>{
    @Query("SELECT r FROM Receipt r WHERE (r.owner.name LIKE %:userName% OR :userName IS NULL)")
    Page<Receipt> getReceipt(Pageable pageale, @Param("userName") String userName);
    
    @Query("SELECT r FROM Receipt r WHERE r.ticket.id = :id")
    Receipt getReceiptByTicketId(@Param("id") int id);
    
    @Query("""
           SELECT new com.phong.parkingmanagementapp.dtos.ReceiptDTO(
           r.transactionNumber,
           r.transactionDate,
           t.id,
           tType.type,
           t.isPaid,
           t.totalPrice
           )
           FROM Receipt r 
           JOIN r.ticket t
           JOIN t.ticketType tType
           WHERE r.owner.id = :userId
           AND (:startDay IS NULL OR :endDay IS NULL OR (r.transactionDate BETWEEN :startDay AND :endDay))
           ORDER BY r.transactionDate DESC
           """)
    Page<Receipt> getCustomerReceipt(Pageable pageale, @Param("userId") int userId,
            @Param("startDay") Date startDay, @Param("endDay") Date endDay);
}
