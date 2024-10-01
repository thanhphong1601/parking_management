/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.Role;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PaymentService;
import com.phong.parkingmanagementapp.services.RoleService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private PaymentService paymentService;
    
    @Autowired
    private FloorService floorService;
    @Autowired
    private LineService lineService;
    @Autowired
    public TestController(UserService userService, TicketService ticketService, RoleService roleService, VehicleService vehicleService){
        this.userService = userService;
        this.ticketService = ticketService;
        this.roleService = roleService;
        this.vehicleService = vehicleService;
    }
    
    @GetMapping("/test/")
    public ResponseEntity<String> test() throws Exception{
        Date start = new Date(2024 - 1900, 8, 21); // Tháng bắt đầu từ 0, tháng 9 là 8
        Date end = new Date(2024 - 1900, 9, 21);   // Tháng 10 là 9
        
        String url = this.paymentService.createPaymentUrl(100000, "Xin chào");
        
        return ResponseEntity.ok(url);
    }
    
    @GetMapping("/test2/")
    public ResponseEntity<String> test2(@RequestBody Map<String, String> user){
        String name = user.get("username");
        String pass = user.get("password");
        return ResponseEntity.ok("username: " + name + "- password:" + pass);
    }
    
    @GetMapping("/test3/")
    public ResponseEntity<?> test3(){
        Date date = new Date();
        return ResponseEntity.ok(this.ticketService.findValidTicketsByVehicleIdAndDate((long) 11, date));
    }
    
    @GetMapping("/testRequestParams/")
    public ResponseEntity<?> testAuth(@RequestParam(value = "id", required = false) Integer id){
        if (id == null)
            return ResponseEntity.ok("Success");
        else
            return ResponseEntity.ok("Success" + id);
    }
}
