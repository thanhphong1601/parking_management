/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.dtos.AuthenticationRequest;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.AuthenticationService;
import com.phong.parkingmanagementapp.services.JwtService;
import com.phong.parkingmanagementapp.services.RoleService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/auth")
public class ApiUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationService authService;
    @Autowired
    private BCryptPasswordEncoder passEncoder;
    @Autowired
    private RoleService roleService;
    
    
    @PostMapping("/authenticate")
    @CrossOrigin
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest r) {
        if (this.userService.authUser(r.getUsername(), r.getPassword()) == true) {
            String token = this.authService.authenticate(r);

            return new ResponseEntity<>(token, HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }
    
    @PostMapping("/register")
    @CrossOrigin
    public ResponseEntity<String> register(@RequestParam Map<String, String> params) {
        User u = new User();
        u.setName(params.get("name"));
        u.setEmail(params.get("email"));
        u.setUsername(params.get("username"));
        u.setPassword(params.get("password"));
        u.setAddress(params.get("address"));
        String birthday = params.get("birthday");
        u.setBirthday(parseLocalDate.parseDate(birthday));
        u.setIdentityNumber(params.get("identityNumber"));
        u.setPhone(params.get("phone"));
        u.setRole(this.roleService.getRoleById(Integer.parseInt(params.get("role"))));
           
        this.userService.saveUser(u);

        return new ResponseEntity<>("Successfully create", HttpStatus.CREATED);
    }
    
    @GetMapping(path = "/current-user", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<User> getCurrentUser(Principal p) {
        User u = this.userService.getUserByUsername(p.getName());
        return new ResponseEntity<>(u, HttpStatus.OK);
    }
    
   
    
    @GetMapping("/test/")
    @CrossOrigin
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("test", HttpStatus.OK);
    }
}
