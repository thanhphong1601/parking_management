/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.models.VehicleType;
import com.phong.parkingmanagementapp.repositories.VehicleTypeRepository;
import com.phong.parkingmanagementapp.services.VehicleTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class VehicleTypeServiceImpl implements VehicleTypeService{
    private final VehicleTypeRepository veTypeRepo;
    
    @Autowired
    public VehicleTypeServiceImpl(VehicleTypeRepository veTypeRepo){
        this.veTypeRepo = veTypeRepo;
    }

    @Override
    public List<VehicleType> findAll() {
        return this.veTypeRepo.findAll();
    }

    @Override
    public VehicleType getVehicleTypeById(int id) {
        return this.veTypeRepo.getVehicleTypeById(id);
    }
    
}
