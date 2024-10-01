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
}
