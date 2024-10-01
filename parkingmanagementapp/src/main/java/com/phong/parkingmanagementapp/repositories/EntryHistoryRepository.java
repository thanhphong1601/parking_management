/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.EntryHistory;
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
    
    @Query("SELECT e FROM EntryHistory e WHERE e.timeOut IS NULL AND e.plateImgOut IS NULL")
    List<EntryHistory> findEntryHistoriesByTimeOutAndPlateImgOut();
    
    EntryHistory getEntryHistoryById(int id);
    
}
