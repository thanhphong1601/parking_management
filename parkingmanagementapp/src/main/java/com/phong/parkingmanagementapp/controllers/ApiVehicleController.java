/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.services.VehicleTypeService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiVehicleController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleTypeService veTypeService;

    @PostMapping("/vehicle/create")
    public ResponseEntity<String> createVehicle(@RequestParam Map<String, String> params) {

        Vehicle v = new Vehicle();
        v.setName(params.get("name"));
        v.setPlateLicense(params.get("plateLicense"));
        v.setType(this.veTypeService.getVehicleTypeById(Integer.parseInt(params.get("type"))));
        this.vehicleService.saveVehicle(v);
        return ResponseEntity.ok("Successfully create");
    }

    @GetMapping("/vehicle/blank/clear")
    public ResponseEntity<?> clearBlankVehicleOwner() {
        this.vehicleService.deleteVehiclesOfBlankUser();
        return new ResponseEntity<>("Cleared", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/vehicle/list/blank")
    public ResponseEntity<List<Vehicle>> getBlankVehicleList() {
        return ResponseEntity.ok(this.vehicleService.findVehiclesOfBlankUser());
    }
    
    @GetMapping("/user/{uid}/vehicle/list")
    public ResponseEntity<List<Vehicle>> getVehicleByUserId(@PathVariable("uid") int uid){
        return ResponseEntity.ok(this.vehicleService.findVehicleByUserId(uid));
    }
    
    @GetMapping("/vehicle/list")
    public ResponseEntity<List<Vehicle>> getVehicle(@RequestParam Map<String, String> params){
        return ResponseEntity.ok(this.vehicleService.findAllByLicensePlate(params.get("plateLicense")));
    }

}
