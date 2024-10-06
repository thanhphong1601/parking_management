/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Ticket;
import java.util.Date;
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
    
    @Query("SELECT t FROM Ticket t WHERE t.vehicle.id = :vehicleId " +
           "AND :currentDate BETWEEN t.startDay AND t.endDay")
    List<Ticket> findValidTicketsByVehicleIdAndDate(@Param("vehicleId") Long vehicleId, 
                                                    @Param("currentDate") Date currentDate);
    
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Ticket t WHERE :currentDate BETWEEN t.startDay AND t.endDay")
    boolean existsByDay(@Param("currentDate") Date currentDate);
    
    @Query("SELECT t FROM Ticket t WHERE t.userOwned.active = true AND (:idNum IS NULL OR t.userOwned.identityNumber LIKE %:idNum%) AND (:name IS NULL OR t.userOwned.name LIKE %:name%)")
    List<Ticket> getTicketsByUserOwnedActive(@Param("idNum") String identityNumber, @Param("name") String name);

}
