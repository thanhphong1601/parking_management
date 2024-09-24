/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Role;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.RoleService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class TestController {
    private final UserService userService;
    private final TicketService ticketService;
    private final RoleService roleService;
    private final VehicleService vehicleService;
    
    @Autowired
    public TestController(UserService userService, TicketService ticketService, RoleService roleService, VehicleService vehicleService){
        this.userService = userService;
        this.ticketService = ticketService;
        this.roleService = roleService;
        this.vehicleService = vehicleService;
    }
    
    @GetMapping("/test/")
    public String test(){
        Dotenv env = Dotenv.load();
        return env.get("API_KEY");
    }
}
