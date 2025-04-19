/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.repositories.VehicleRepository;
import com.phong.parkingmanagementapp.services.VehicleService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class VehicleServiceImpl implements VehicleService{
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    
    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepo, UserRepository userRepo){
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Vehicle> findVehicleByUserId(int id) {
        return this.vehicleRepo.findVehicleByUserId(id);
    }

    @Override
    public boolean deleteVehiclesByUserId(int userId) {
        if (this.userRepo.getUserById(userId) == null)
            return false;
        if (this.vehicleRepo.findVehicleByUserId(userId).isEmpty()){
            return false;
        }
        
        this.vehicleRepo.deleteVehiclesByUserId(userId);
        return true;
    }

    @Override
    public void saveVehicle(Vehicle v) {
        if (v == null)
            return;
        
        v.setUser(this.userRepo.getUserByName("BlankUser"));
        this.vehicleRepo.save(v);
    }

    @Override
    public List<Vehicle> findVehiclesOfBlankUser() {
        return this.vehicleRepo.findVehiclesOfBlankUser();
    }

    @Override
    public void deleteVehiclesOfBlankUser() {
        this.vehicleRepo.deleteVehiclesOfBlankUser();
    }

    @Override
    public void updateNewVehiclesOwner(int uid) {
        User u = this.userRepo.getUserById(uid);
        if (u == null)
            return;
        
        //add vehicles from blank user then delete
        List<Vehicle> vehicles = this.vehicleRepo.findVehiclesOfBlankUser();
        if (vehicles.isEmpty())
            return;
        
        for (Vehicle v : vehicles){
            v.setUser(u);
        }
        
        this.vehicleRepo.saveAll(vehicles);
    }

    @Override
    public void saveVehicleForCreatedUser(int uid, Vehicle v) {
        User u = this.userRepo.getUserById(uid);
        v.setUser(u);
        
        this.vehicleRepo.save(v);
    }

    @Override
    public Optional<Vehicle> findByLicensePlateNumberIgnoreCase(String plateLicense) {
        return this.vehicleRepo.findByLicensePlateNumberIgnoreCase(plateLicense);
    }

    @Override
    public Vehicle getVehicleById(int id) {
        return this.vehicleRepo.getVehicleById(id);
    }

    @Override
    public List<Vehicle> findAllByLicensePlate(String plateLicense) {
        return this.vehicleRepo.findAllByLicensePlate(plateLicense);
    }

    @Override
    public Vehicle getAnonymousVehicle() {
        return this.vehicleRepo.getAnonymousVehicle();
    }

}
