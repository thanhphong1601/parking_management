/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Line;
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
public interface LineRepository extends JpaRepository<Line, Long>{
    @Override
    public List<Line> findAll();
    
    @Query(value = "SELECT l FROM Line l WHERE l.floor.id = :id AND (l.full = false AND l.isDeleted = false)")
    List<Line> findLinesByFloorId(@Param("id")int id);
    
    Line getLineById(int id);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Line l WHERE l.floor.id = :floorId AND l.full = false AND l.isDeleted = false")
    Boolean existsByStatus(@Param("floorId") int floorId);
    
    @Query("SELECT COUNT(l) FROM Line l WHERE l.floor.id = :floorId")
    int countLineByFloorId(@Param("floorId") int floorId);

}
