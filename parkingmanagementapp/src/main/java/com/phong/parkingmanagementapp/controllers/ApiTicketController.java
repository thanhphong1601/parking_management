/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.PriceService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiTicketController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private FloorService floorService;
    @Autowired
    private LineService lineService;
    @Autowired
    private PositionService poService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PriceService priceService;

    @GetMapping("/ticket/list")
    public ResponseEntity<List<Ticket>> getActiveTicketList(@RequestParam Map<String, String> params) {
        String ownerIdentityNumber = params.get("identityNum");
        String ownerName = params.get("name");

        return ResponseEntity.ok(this.ticketService.getTicketsByUserOwnedActive(ownerIdentityNumber, ownerName));
    }

    @PostMapping("/ticket/create")
    public ResponseEntity<?> createTicket(@RequestParam Map<String, String> params) {
//        System.out.println(params.get("floorId"));
//        System.out.println(params.get("lineId"));
//        System.out.println(params.get("positionId"));
//        System.out.println(params.get("userCreateId"));
//        System.out.println(params.get("userOwnedId"));
//        System.out.println(params.get("vehicleId"));
//        System.out.println(params.get("startDay"));
//        System.out.println(params.get("endDay"));
//        System.out.println(params.get("priceId"));

        Ticket t = new Ticket();
        String floorId = params.get("floorId");
        t.setFloor(this.floorService.getFloorById(Integer.parseInt(floorId)));
        String lineId = params.get("lineId");
        t.setLine(this.lineService.getLineById(Integer.parseInt(lineId)));
        String positionId = params.get("positionId");
        t.setPosition(this.poService.getPositionById(Integer.parseInt(positionId)));
        String userCreateId = params.get("userCreateId");
        t.setUserCreate(this.userService.getUserById(Integer.parseInt(userCreateId)));
        String userOwnedId = params.get("userOwnedId");
        t.setUserOwned(this.userService.getUserById(Integer.parseInt(userOwnedId)));
        String vehicleId = params.get("vehicleId");
        t.setVehicle(this.vehicleService.getVehicleById(Integer.parseInt(vehicleId)));
        String startDay = params.get("startDay");
        t.setStartDay(parseLocalDate.parseDate(startDay));
        String endDay = params.get("endDay");
        t.setEndDay(parseLocalDate.parseDate(endDay));
        String priceId = params.get("priceId");
        t.setPrice(this.priceService.getTicketPriceById(Integer.parseInt(priceId)));


        this.ticketService.addOrUpdate(t);

        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }
    
    @DeleteMapping("/ticket/{id}/delete")
    public ResponseEntity<?> deleteTicket(@PathVariable("id") int id){
        Ticket t = this.ticketService.getTicketById(id);
        
        this.ticketService.delete(t);
        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

}
