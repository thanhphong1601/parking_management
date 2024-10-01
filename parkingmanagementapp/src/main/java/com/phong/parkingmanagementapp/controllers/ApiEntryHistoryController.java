/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.exceptions.VehicleNotFoundException;
import com.phong.parkingmanagementapp.models.EntryHistory;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.CloudinaryService;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiEntryHistoryController {

    @Autowired
    private EntryHistoryService entryService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private CloudinaryService cloudService;

    @GetMapping(path = "/recognize/in")
    @CrossOrigin
    public ResponseEntity<?> getRecognizePlateIn(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(required = false) Map<String, String> params) throws IOException {

        Map<String, Object> result = new HashMap<>();

        //from result got from recognizing -> get vehicle
        Map<String, String> resultsFromRecognizingPlate = new HashMap<>();

        if (file != null) {
            resultsFromRecognizingPlate = this.entryService.recognizePlate(file);

            if (resultsFromRecognizingPlate.get("plate") == null) {
                return new ResponseEntity<>("Cannot get plate license from image!", HttpStatus.NOT_FOUND);
            }

            result.put("plate", resultsFromRecognizingPlate.get("plate"));
            result.put("timeIn", resultsFromRecognizingPlate.get("timestamp"));

            String plateLicense = resultsFromRecognizingPlate.get("plate");
            Vehicle currentVehicle = this.vehicleService.findByLicensePlateNumberIgnoreCase(plateLicense)
                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with plate: " + plateLicense));

            result.put("vehicle", currentVehicle);

            //from vehicle -> get user
            User user = currentVehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.NOT_FOUND);
            }

            result.put("owner", user);

            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.NOT_FOUND);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);
        } else {
            Vehicle currentVehicle = this.vehicleService.getVehicleById(Integer.parseInt(params.get("vehicleId")));
            result.put("plate", currentVehicle.getPlateLicense());
            result.put("timeIn", LocalDateTime.now());
            result.put("vehicle", currentVehicle);

            User user = currentVehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.NOT_FOUND);
            }

            result.put("owner", user);

            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.NOT_FOUND);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping(path = "/recognize/in")
    @CrossOrigin
    public ResponseEntity<?> PostRecognizePlateIn(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam() Map<String, String> params) throws IOException {
        //call cloudinary to upload the image and get back url to save
        if (file != null) {
            String inImgUrl = this.cloudService.uploadImage(file);
            params.put("inImgUrl", inImgUrl);
        }

        this.entryService.saveHistoryIn(params);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping(path = "/recognize/out")
    @CrossOrigin
    public ResponseEntity<?> recognizePlateOut(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "id", required = false) Integer id) throws IOException {
        Map<String, Object> result = new HashMap<>();
        //from result got from recognizing -> get vehicle
        Map<String, String> resultsFromRecognizingPlate = this.entryService.recognizePlate(file);

        if (file != null) {
            if (resultsFromRecognizingPlate.get("plate") == null) {
                return new ResponseEntity<>("Cannot get plate license from image!", HttpStatus.NOT_FOUND);
            }
            result.put("plate", resultsFromRecognizingPlate.get("plate"));
            result.put("timeOut", resultsFromRecognizingPlate.get("timestamp"));

            String plateLicense = resultsFromRecognizingPlate.get("plate");
            Vehicle currentVehicle = this.vehicleService.findByLicensePlateNumberIgnoreCase(plateLicense)
                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with plate: " + plateLicense));

            //check if vehicle is correct comparing to the one in In entry history
            if (this.entryService.existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(plateLicense)) {
                result.put("vehicle", currentVehicle);
                result.put("licensePlateNum", plateLicense);
            } else {
                return new ResponseEntity<>("No entered vehicle found!", HttpStatus.NOT_FOUND);
            }

            //from vehicle -> get user
            User user = currentVehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.NOT_FOUND);
            }

            result.put("owner", user);

            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.NOT_FOUND);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);

        }
        else {
            int historyId = id;
            EntryHistory history = this.entryService.getEntryHistoryById(historyId);
            result.put("plate", history.getVehicle().getPlateLicense());
            result.put("timeIn", history.getTimeIn());
           
            result.put("timeOut", LocalDateTime.now());
            
            Vehicle vehicle = history.getVehicle();
            result.put("vehicle", vehicle);
            result.put("licensePlateNum", vehicle.getPlateLicense());
            
            //from vehicle -> get user
            User user = vehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.NOT_FOUND);
            }

            result.put("owner", user);

            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) vehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.NOT_FOUND);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);
            
            result.put("ticket", currentTicket);

            
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @PostMapping(path = "/recognize/out")
    @CrossOrigin
    public ResponseEntity<?> PostRecognizePlateOut(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam() Map<String, String> params) throws IOException {
        //call cloudinary to upload the image and get back url to save
        if (file != null) {
            String outImgUrl = this.cloudService.uploadImage(file);
            params.put("outImgUrl", outImgUrl);
        }
        
        //kiểm tra xác thực xe

        this.entryService.saveHistoryOut(params);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping(path = "/recognize/out/list")
    @CrossOrigin
    public ResponseEntity<?> inEntryList(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(this.entryService.findEntryHistoriesByTimeOutAndPlateImgOut());
    }

    @GetMapping(path = "/recognize/test")
    @CrossOrigin
    public ResponseEntity<?> test(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Map<String, String> resultsFromRecognizingPlate = new HashMap<>();
        resultsFromRecognizingPlate = this.entryService.recognizePlate(file);
        return ResponseEntity.ok(resultsFromRecognizingPlate);
    }

}
