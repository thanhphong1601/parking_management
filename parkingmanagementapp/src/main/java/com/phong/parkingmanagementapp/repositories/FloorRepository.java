/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.Floor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    @Override
    public List<Floor> findAll();

    public Floor getFloorById(int id);

//    @Query("""
//           SELECT com.phong.parkingmanagementapp.dtos.FloorDTO(
//           t.id,
//           t.floorNumber,
//           t.lineCollection
//           )
//           FROM Floor t
//           """)
//    List<Floor> findAllFloorDTO();
    @Query("SELECT f FROM Floor f LEFT JOIN FETCH f.lineCollection l LEFT JOIN FETCH l.positionCollection")
    List<Floor> findAllWithLinesAndPositions();
}
