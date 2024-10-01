/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Admin
 */
public interface VehicleService {
    List<Vehicle> findVehicleByUserId(int id);
    boolean deleteVehiclesByUserId(@Param("userId") int userId);
    void saveVehicle(Vehicle v);
    List<Vehicle> findVehiclesOfBlankUser();
    void deleteVehiclesOfBlankUser();
    void updateNewVehiclesOwner(int uid);
    void saveVehicleForCreatedUser(int uid, Vehicle v);
    Optional<Vehicle> findByLicensePlateNumberIgnoreCase(String plateLicense);
    Vehicle getVehicleById(int id);
}
