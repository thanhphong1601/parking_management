/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Position;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface PositionService {
    public List<Position> findAll();
    List<Position> findPositionsByLineId(int id);
    void savePosition(Position position);
    Position getPositionById(int id);
    void UpdateStatus(int positionId, Boolean status);
    boolean deletePosition(int positionId);
    int countPositionByLineId(int lineId);
    Boolean existsByStatus(int lineId);
    List<Position> findFirstAvailablePositionOrdered();
    void assignPositionToTicket(int ticketId, int positionId);
    void emptyTakenPosition(int positionId);
    void emptyTakenPositionOfDeactiveTicket(int ticketId);
    long countAvailablePositions();
    long countTakenPositions();
    
}
