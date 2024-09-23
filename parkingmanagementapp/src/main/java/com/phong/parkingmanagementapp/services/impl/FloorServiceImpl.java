/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.repositories.FloorRepository;
import com.phong.parkingmanagementapp.services.FloorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class FloorServiceImpl implements FloorService{
    private final FloorRepository floorRepo;
    
    @Autowired
    public FloorServiceImpl(FloorRepository floorRepo) {
        this.floorRepo = floorRepo;
    }

    @Override
    public List<Floor> findAll() {
        return this.floorRepo.findAll();
    }
    
}
