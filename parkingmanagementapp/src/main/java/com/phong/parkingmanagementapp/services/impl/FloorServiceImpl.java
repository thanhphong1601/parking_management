/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.dtos.FloorDTO;
import com.phong.parkingmanagementapp.dtos.LineDTO;
import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.repositories.FloorRepository;
import com.phong.parkingmanagementapp.repositories.LineRepository;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepo;
    private final LineRepository lineRepo;
    private final LineService lineService;

    @Autowired
    public FloorServiceImpl(FloorRepository floorRepo, LineRepository lineRepo,
            @Lazy LineService lineService) {
        this.floorRepo = floorRepo;
        this.lineRepo = lineRepo;
        this.lineService = lineService;
    }

    @Override
    public List<Floor> findAll() {
        return this.floorRepo.findAll();
    }

    @Override
    public Floor getFloorById(int id) {
        return this.floorRepo.getFloorById(id);
    }

    @Override
    public void saveFloor(Floor floor) {
        this.floorRepo.save(floor);
    }

    @Override
    public void checkStatus(int floorId) {
        Boolean check = this.lineRepo.existsByStatus(floorId);
        Floor f = this.floorRepo.getFloorById(floorId);

        if (!check) { //full
            f.setFull(true);

        } else {
            f.setFull(false);
        }

        this.floorRepo.save(f);

    }

    @Override
    public boolean deleteFloor(int floorId) {
        Floor currentFloor = this.floorRepo.getFloorById(floorId);

        if (currentFloor == null) {
            return false;
        }

        currentFloor.setIsDeleted(Boolean.TRUE);
        this.floorRepo.save(currentFloor);

        for (Line l : currentFloor.getLineCollection()) {
            this.lineService.deleteLine(l.getId());
        }

        return true;
    }

    @Override
    public List<FloorDTO> getAllFloorDTOs() {
        List<Floor> floors = floorRepo.findAll();
        return floors.stream().map(floor -> new FloorDTO(
                floor.getId(),
                floor.getFloorNumber(),
                floor.getLineCollection().stream()
                        .map(line -> new LineDTO(
                        line.getId(),
                        line.getLine(),
                        line.getPositionCollection()
                ))
                        .collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

}
