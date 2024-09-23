/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Ticket;
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
public interface TicketRepository extends JpaRepository<Ticket, Long>{
    @Override
    List<Ticket> findAll();
    
    @Query(value = "SELECT t FROM Ticket t WHERE (:name IS NULL OR t.userOwned.name LIKE %:name%)")
    List<Ticket> findTicketByUserOwned(@Param("name") String name);
    
    public Ticket getTicketById(int id);
    
    @Query(value = "SELECT t FROM Ticket t WHERE t.userOwned.id = :id")
    List<Ticket> findTicketByUserOwnedId(@Param("id") int id);
}
