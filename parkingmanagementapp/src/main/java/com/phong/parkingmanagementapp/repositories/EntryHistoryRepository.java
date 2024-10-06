/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.EntryHistory;
import java.util.Date;
import java.util.List;
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
}
