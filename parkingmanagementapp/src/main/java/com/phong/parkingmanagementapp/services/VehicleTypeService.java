/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.models.VehicleType;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface VehicleTypeService {
    List<VehicleType> findAll();
    VehicleType getVehicleTypeById(int id);
}
