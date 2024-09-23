/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query(value = "SELECT v FROM Vehicle v WHERE v.user.id = :id")
    public List<Vehicle> findVehicleByUserId(@Param("id") int id);

    @Modifying
    @Query("DELETE FROM Vehicle v WHERE v.user.id = :userId")
    void deleteVehiclesByUserId(@Param("userId") int userId);
    
    @Query("SELECT v FROM Vehicle v WHERE v.user.username = 'blankuser'")
    List<Vehicle> findVehiclesOfBlankUser();
    
    @Modifying
    @Query("DELETE FROM Vehicle v WHERE v.user.name = 'blankuser'")
    void deleteVehiclesOfBlankUser();
}
