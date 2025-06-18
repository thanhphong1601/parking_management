/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.RoleService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api")
@CrossOrigin
public class ApiCustomerController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private TicketService ticketService;

    @Value("${page_size}")
    private int pageSize;

    @GetMapping("/customer/list")
    public ResponseEntity<Page<User>> getCustomerList(@RequestParam Map<String, String> params) {
        String name = params.get("name");
        String identityNumber = params.get("identityNum");
        int page = Integer.parseInt(params.get("page"));

        Pageable pageable = PageRequest.of(page, pageSize);

        return ResponseEntity.ok(this.userService.findUserByIdentityNumberOrNameOrRolePageable(identityNumber, name, 3, pageable));
    }

    @PostMapping("/customer/{id}")
    public ResponseEntity<?> uploadCustomerInfo(@RequestParam Map<String, String> params,
            @PathVariable("id") int id) {
        User u = this.userService.getUserById(id);
        if (params.get("name") != null) {
            u.setName(params.get("name"));
        }
        if (params.get("email") != null) {
            u.setEmail(params.get("email"));
        }
        if (params.get("address") != null) {
            u.setAddress(params.get("address"));
        }
        if (params.get("phone") != null) {
            u.setPhone(params.get("phone"));
        }
        if (params.get("identityNumber") != null) {
            u.setIdentityNumber(params.get("identityNumber"));
        }

        this.userService.saveUser(u);
        return null;
    }

    @PostMapping("/customer/create")
    @CrossOrigin
    public ResponseEntity<String> createNewUser(@RequestParam Map<String, String> params) {
        if (params.get("id") == null || params.get("id").isBlank()) {
            User u = new User();
            u.setName(params.get("name"));
            u.setEmail(params.get("email"));
            u.setUsername(params.get("username"));
            u.setPassword(params.get("password"));
            u.setAddress(params.get("address"));
            String birthday = params.get("birthday");

            u.setBirthday(parseLocalDate.parseStringToDate(birthday));
            u.setIdentityNumber(params.get("identityNumber"));
            u.setPhone(params.get("phone"));

            u.setRole(this.roleService.getRoleById(Integer.parseInt(params.get("role"))));

            this.userService.saveUser(u);

            User currentUser = this.userService.getUserByUsername(params.get("username"));
            this.vehicleService.updateNewVehiclesOwner(currentUser.getId());
        } else {
            User currentUser = this.userService.getUserById(Integer.parseInt(params.get("id")));
            this.vehicleService.updateNewVehiclesOwner(currentUser.getId());
        }

        return new ResponseEntity<>("Successfully created", HttpStatus.CREATED);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("id") int id) {
        User u = this.userService.getUserById(id);
        List<Vehicle> vehicleList = this.vehicleService.findVehicleByUserId(id);
        List<Ticket> ticketList = this.ticketService.findTicketByUserOwnedId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("user", u);
        result.put("vehicles", vehicleList);
        result.put("tickets", ticketList);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/customer/{id}/deactivate")
    @CrossOrigin
    public ResponseEntity<?> deactivateUserInfo(@PathVariable("id") int id) {
        this.userService.deactivateUser(id);

        return new ResponseEntity<>("Successfully deleted", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/security/list")
    public ResponseEntity<List<User>> getSecurityGuardList() {
        return ResponseEntity.ok(this.userService.findUsersByRoleId(2));
    }
    
    @GetMapping("/customer/list/all")
    public ResponseEntity<?> getAllCustomers(@RequestParam Map<String, String> params){
        String name = params.get("name");
        String identityNumber = params.get("identityNum");
        
        return ResponseEntity.ok(this.userService.findUserByIdentityNumberOrNameOrRole(identityNumber, name, 3));
    }

}
