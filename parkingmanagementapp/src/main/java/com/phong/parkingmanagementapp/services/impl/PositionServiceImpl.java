/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.repositories.FloorRepository;
import com.phong.parkingmanagementapp.repositories.LineRepository;
import com.phong.parkingmanagementapp.repositories.PositionRepository;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PositionServiceImpl implements PositionService{
    private final PositionRepository positionRepo;
    
    @Autowired
    public PositionServiceImpl(PositionRepository positionRepo) {
        this.positionRepo = positionRepo;
    }

    @Override
    public List<Position> findAll() {
        return this.positionRepo.findAll();
    }
    
}
