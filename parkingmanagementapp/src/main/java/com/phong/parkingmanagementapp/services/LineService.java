/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Line;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface LineService {
    public List<Line> findAll();
    List<Line> findLinesByFloorId(int id);
    Line getLineById(int id);
    void saveLine(Line line);
}
