/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.dtos.TicketDTO;
import com.phong.parkingmanagementapp.models.Ticket;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Override
    List<Ticket> findAll();

    @Query(value = "SELECT t FROM Ticket t WHERE (:name IS NULL OR t.userOwned.name LIKE %:name%)")
    List<Ticket> findTicketByUserOwned(@Param("name") String name);

    public Ticket getTicketById(int id);

    @Query(value = "SELECT t FROM Ticket t WHERE t.userOwned.id = :id")
    List<Ticket> findTicketByUserOwnedId(@Param("id") int id);

    @Query("SELECT t FROM Ticket t WHERE t.vehicle.id = :vehicleId "
            + "AND :currentDate BETWEEN t.startDay AND t.endDay")
    List<Ticket> findValidTicketsByVehicleIdAndDate(@Param("vehicleId") Long vehicleId,
            @Param("currentDate") Date currentDate);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Ticket t WHERE :currentDate BETWEEN t.startDay AND t.endDay")
    boolean existsByDay(@Param("currentDate") Date currentDate);

    @Query("SELECT t FROM Ticket t WHERE t.userOwned.active = true AND (:idNum IS NULL OR t.userOwned.identityNumber LIKE %:idNum%) AND (:name IS NULL OR t.userOwned.name LIKE %:name%)")
    List<Ticket> getTicketsByUserOwnedActive(@Param("idNum") String identityNumber, @Param("name") String name);

    @Query(value = """
                   SELECT t FROM Ticket t 
                    WHERE (:name IS NULL OR t.userOwned.name LIKE %:name%)
                   AND (:status IS NULL OR t.active = :status)
                   """)
    Page<Ticket> findTicketByUserOwnedPageable(@Param("name") String name, @Param("status") Boolean status, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.userOwned.active = true AND (:idNum IS NULL OR t.userOwned.identityNumber LIKE %:idNum%) AND (:name IS NULL OR t.userOwned.name LIKE %:name%)")
    Page<Ticket> getTicketsByUserOwnedActivePageable(@Param("idNum") String identityNumber, @Param("name") String name, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.id = :id AND :currentDate BETWEEN t.startDay AND t.endDay")
    Ticket checkTicketDate(@Param("id") int ticketId, @Param("currentDate") Date currentDate);

    @Query("""
    SELECT new com.phong.parkingmanagementapp.dtos.TicketDTO(
        t.id,
        t.userOwned.name,
        f.floorNumber,
        l.line,
        p.position,
        prc.price,
        t.vehicle.name,
        t.vehicle.plateLicense,
        t.active,
        t.startDay,
        t.endDay,
        t.isPaid,
        t.totalPrice
    )
    FROM Ticket t
    JOIN t.floor f
    JOIN t.line l
    JOIN t.position p
    JOIN t.price prc
    JOIN t.vehicle v
    JOIN t.userOwned u
    WHERE t.userOwned.id = :userId
           AND (:startDay IS NULL OR t.startDay >= :startDay)
           AND (:endDay IS NULL OR t.endDay <= :endDay)
           AND (:isPaid IS NULL OR t.isPaid = :isPaid)
        """)
    Page<TicketDTO> getTicketsInfoByUserId(@Param("userId") int userId, Pageable pageable,
            @Param("startDay") Date startDay, @Param("endDay") Date endDay,
            @Param("isPaid") Boolean isPaid);

    Ticket findTopByOrderByIdDesc();

    Ticket findByTicketId(String ticketId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Ticket t WHERE :id = t.id AND (:currentDate BETWEEN t.startDay AND t.endDay)")
    boolean checkTicketDateValid(@Param("id") int ticketId, @Param("currentDate") Date currentDate);

    //for statistic functions here
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.active = TRUE AND t.userOwned.active = TRUE")
    long countTotalTickets();

    @Query("SELECT t.ticketType.type, COUNT(t) FROM Ticket t GROUP BY t.ticketType.type")
    List<Object[]> countTicketsByType();

    @Query("SELECT t.floor.floorNumber, COUNT(t) FROM Ticket t GROUP BY t.floor.floorNumber ORDER BY t.floor.floorNumber")
    List<Object[]> countTicketsByFloor();
}
