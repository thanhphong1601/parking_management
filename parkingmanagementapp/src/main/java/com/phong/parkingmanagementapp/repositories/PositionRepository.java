/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Position;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long>{
    @Override
    public List<Position> findAll();
    
    @Query(value = "SELECT p FROM Position p WHERE p.line.id = :id")
    List<Position> findPositionsByLineId(@Param("id") int id);
    
    Position getPositionById(int id);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p WHERE p.line.id = :lineId AND p.take = false AND p.isDeleted = false")
    Boolean existsByStatus(@Param("lineId") int lineId);
    
    @Query("SELECT COUNT(p) FROM Position p WHERE p.line.id = :lineId")
    int countPositionByLineId(@Param("lineId") int lineId);
    
    @Query("""
        SELECT p FROM Position p
        WHERE p.take = false
          AND p.status = 'AVAILABLE'
          AND p.isDeleted = false
          AND p.line.isDeleted = false
          AND p.line.floor.isDeleted = false
        ORDER BY 
            p.line.floor.floorNumber ASC,
            p.line.line ASC,
            p.position ASC
    """)
    List<Position> findFirstAvailablePositionOrdered();
}
