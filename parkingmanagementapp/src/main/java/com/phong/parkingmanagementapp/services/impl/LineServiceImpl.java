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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class LineServiceImpl implements LineService {

    private final LineRepository lineRepo;
    private final PositionService posService;
    private final FloorService floorService;

    @Autowired
    public LineServiceImpl(LineRepository lineRepo,
            @Lazy PositionService posService, FloorService floorService) {
        this.lineRepo = lineRepo;
        this.posService = posService;
        this.floorService = floorService;
    }

    @Override
    public List<Line> findAll() {
        return this.lineRepo.findAll();
    }

    @Override
    public List<Line> findLinesByFloorId(int id) {
        return this.lineRepo.findLinesByFloorId(id);
    }

    @Override
    public Line getLineById(int id) {
        return this.lineRepo.getLineById(id);
    }

    @Override
    public void saveLine(Line line) {
        this.lineRepo.save(line);
    }

    @Override
    public void checkLineStatus(int lineId) {
        Boolean check = this.posService.existsByStatus(lineId);
        Line l = this.lineRepo.getLineById(lineId);
        if (!check) {
            l.setFull(true);

        } else {
            l.setFull(false);
        }

        this.lineRepo.save(l);

    }

    @Override
    public boolean deleteLine(int lineId) {
        Line currentLine = this.lineRepo.getLineById(lineId);

        if (currentLine == null) {
            return false;
        }

        currentLine.setIsDeleted(Boolean.TRUE);
//        Collection<Position> positionCollection = currentLine.getPositionCollection();
//        for (Position p : positionCollection){
//            this.posService.deletePosition(p.getId());
//        }
        List<Position> positions = new ArrayList<>(currentLine.getPositionCollection());
        for (Position p : positions) {
            this.posService.deletePosition(p.getId());
        }

        this.lineRepo.save(currentLine);

        this.floorService.checkStatus(currentLine.getFloor().getId());

        return true;
    }

    @Override
    public int countLineByFloorId(int floorId) {
        return this.lineRepo.countLineByFloorId(floorId);
    }

}
