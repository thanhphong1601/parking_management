/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.dtos.FloorDTO;
import com.phong.parkingmanagementapp.models.Floor;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface FloorService {
    public List<Floor> findAll();
    Floor getFloorById(int id);
    void saveFloor(Floor floor);
    void checkStatus(int floorId);
    boolean deleteFloor(int floorId);
    List<FloorDTO> getAllFloorDTOs();
    List<Floor> findAllFloorAvailable();
}
