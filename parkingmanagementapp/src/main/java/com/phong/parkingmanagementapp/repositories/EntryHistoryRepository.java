/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.EntryHistory;
import java.util.Date;
import java.util.List;
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
public interface EntryHistoryRepository extends JpaRepository<EntryHistory, Long> {
    @Query("SELECT COUNT(e) > 0 FROM EntryHistory e WHERE e.vehicle.plateLicense = :licensePlateNumber AND e.timeOut IS NULL AND e.plateImgOut IS NULL")
    boolean existsByLicensePlateNumberAndTimeOutAndPlateImgOutNull(@Param("licensePlateNumber") String licensePlateNumber);
    
    @Query("SELECT e FROM EntryHistory e WHERE e.vehicle.plateLicense = :licensePlateNumber AND e.timeOut IS NULL AND e.plateImgOut IS NULL")
    List<EntryHistory> findEntryHistoriesByLicensePlateNumberAndTimeOutAndPlateImgOut(@Param("licensePlateNumber") String licensePlateNumber);
    
    @Query("SELECT e FROM EntryHistory e WHERE (:licensePlate IS NULL OR e.vehicle.plateLicense LIKE %:licensePlate%) AND e.timeOut IS NULL AND e.plateImgOut IS NULL")
    List<EntryHistory> findEntryHistoriesByTimeOutAndPlateImgOut(@Param("licensePlate") String licensePlate);
    
    EntryHistory getEntryHistoryById(int id);
    
    @Query("SELECT e FROM EntryHistory e WHERE (e.vehicle.plateLicense = :plateLicense) AND e.timeOut IS NULL AND e.plateImgOut IS NULL")
    EntryHistory getEntryHistoryByVehiclePlateLicense(@Param("plateLicense") String plateLicense);
    
    @Query("SELECT COUNT(e) FROM EntryHistory e WHERE DATE(e.timeIn) = ?1")
    long countByDate(Date date);

    @Query("SELECT COUNT(e) FROM EntryHistory e WHERE MONTH(e.timeIn) = ?1 AND YEAR(e.timeIn) = ?2")
    long countByMonth(int month, int year);

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, e.timeIn, e.timeOut)) FROM EntryHistory e WHERE e.timeOut IS NOT NULL")
    Double findAverageParkingDuration();
    
    @Query("""
           SELECT e FROM EntryHistory e 
           WHERE (e.vehicle.user.name LIKE %:name% OR :name IS NULL)
           AND (:start IS NULL OR e.timeIn > :start)
           AND (:end IS NULL OR e.timeOut < :end)
           AND (:plate IS NULL OR e.ticket.licenseNumber LIKE %:plate%)
           """)
    Page<EntryHistory> findAllByName(Pageable pageable, @Param("name") String name,
            @Param("start") Date startFilter,@Param("end") Date endFilter,
            @Param("plate") String plateNumber);
    
    Long countByTimeInBetween(Date start, Date end);
    Long countByTimeOutBetween(Date start, Date end);
    
    @Query("SELECT e FROM EntryHistory e WHERE (e.ticket.id = :ticketId AND (e.personImgOut IS NULL AND e.plateImgOut IS NULL AND e.timeOut IS NULL))")
    EntryHistory getEntryHistoryByTicketId(@Param("ticketId") int id);
    
    @Query("""
           SELECT e FROM EntryHistory e
           WHERE (:licensePlateNumber IS NULL OR e.licenseNumber LIKE %:licensePlateNumber%)
           AND e.timeOut IS NULL AND e.plateImgOut IS NULL
           """)
    Page<EntryHistory> getEntryHistoryListByLicensePlateNumberPageable(@Param("licensePlateNumber") String licensePlateNumber, Pageable pageable);
    
    @Query("""
           SELECT e FROM EntryHistory e
           WHERE (:licensePlateNumber IS NULL OR e.licenseNumber LIKE %:licensePlateNumber%)
           """)
    Page<EntryHistory> getAllEntryHistoryListByLicensePlateNumberPageable(@Param("licensePlateNumber") String licensePlateNumber, Pageable pageable);
    
}
