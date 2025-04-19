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
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    @Autowired
    private VehicleService vehicleService;

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
        String email = params.get("email");
        if (this.userService.findByEmail(email) != null) {
            return new ResponseEntity<>("Email đã có người sử dụng! Hãy dùng một email khác để đăng ký", HttpStatus.CONFLICT);
        }
        String username = params.get("username");
        if (this.userService.getUserByUsername(username) != null) {
            return new ResponseEntity<>("Tài khoản đã có người sử dụng! Hãy dùng một tài khoản khác để đăng ký", HttpStatus.CONFLICT);
        }

        User u = new User();
        u.setName(params.get("name"));
        u.setEmail(email);
        u.setUsername(username);
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
        User u = this.userService.getUserByUsernameOrEmail(p.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping("/test/")
    @CrossOrigin
    public ResponseEntity<String> test(@RequestParam Map<String, String> params) {
        User u = new User();
        String birthday = params.get("birthday");
        u.setBirthday(parseLocalDate.parseDate(birthday));
//        System.out.println("=======================================");
//        System.out.println("Chuỗi đầu vào: [" + birthday + "]");
//        System.out.println(u.getBirthday());
//                System.out.println("=======================================");

        return new ResponseEntity<>("test", HttpStatus.OK);
    }

    @GetMapping("/user/{email}/activate")
    public ResponseEntity<String> activeAccount(@PathVariable(value = "email") String email) {
        User currentUser = this.userService.findByEmail(email);
        if (currentUser == null) {
            return new ResponseEntity<>("Không tồn tại người dùng", HttpStatus.CONFLICT);
        }

        if (currentUser.getActive() == false) {
            currentUser.setActive(Boolean.TRUE);
        }
        this.userService.save(currentUser);

        return new ResponseEntity<>("Kích hoạt thành công", HttpStatus.OK);
    }

    @GetMapping("/user/check/{email}/{identityNumber}/{username}")
    public ResponseEntity<String> checkMailAndIdentityNumberForRegister(@PathVariable(value = "email") String email,
            @PathVariable(value = "identityNumber") String identityNumber,
            @PathVariable(value = "username") String username) {
        User currentUserWithMail = this.userService.findByEmail(email);
        if (currentUserWithMail != null) {
            return new ResponseEntity<>("Email này đã có tài khoản khác sử dụng!", HttpStatus.CONFLICT);
        }
        User currentUserWithUsername = this.userService.getUserByUsername(username);
        if (currentUserWithUsername != null) {
            return new ResponseEntity<>("Tài khoản đã tồn tại!", HttpStatus.CONFLICT);
        }
        User currentUserWithIdentityNumber = this.userService.findByIdentityNumber(identityNumber);
        if (currentUserWithIdentityNumber != null) {
            return new ResponseEntity<>("Căn cước công dân này đã có tài khoản khác sử dụng!", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Thành công", HttpStatus.OK);
    }

    @GetMapping("/user/email/check")
    public ResponseEntity<?> checkMail(@RequestParam Map<String, String> params) {
        String email = params.get("email");
        User currentUserWithEmail = this.userService.findByEmail(email);
        if (currentUserWithEmail == null) {
            return new ResponseEntity<>("Email này hiện chưa có tài khoản đăng ký", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Thành công", HttpStatus.OK);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestParam Map<String, String> params) {
        System.out.println("Changing passworddddddddddddddddddddddddd");
        String email = params.get("email");
        if (email == null) {
            return new ResponseEntity<>("Không tìm thấy email hợp lệ", HttpStatus.NOT_FOUND);
        }

        User currentUser = this.userService.findByEmail(email);
        if (currentUser == null) {
            return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.NOT_FOUND);
        }

        currentUser.setPassword(passEncoder.encode(params.get("password")));

        this.userService.save(currentUser);

        return new ResponseEntity<>("Cài lại mật khẩu thành công", HttpStatus.OK);
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(@AuthenticationPrincipal OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        User user = userService.findByEmail(email);

        if (user == null) {
            if (this.userService.existsByUsername(email)){
                return new ResponseEntity<>("Đã tồn tại tài khoản này", HttpStatus.CONFLICT);
            }
            User newUser = new User();
            newUser.setEmail(email);      
            newUser.setUsername(email); // dùng email làm username
            newUser.setName(name);
            newUser.setPassword(passEncoder.encode(UUID.randomUUID().toString())); // dummy password
            newUser.setRole(this.roleService.getRoleById(3));
            newUser.setIdentityNumber("0000000000000000");
            newUser.setActive(true);
            newUser.setAddress(null);

            this.userService.saveUser(newUser);
        }

        //after create new user with email
        user = userService.findByEmail(email);

        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", jwt));
    }
    
    @GetMapping("/account/active/check")
    public ResponseEntity<?> checkAccountActive(@RequestParam(value = "username") String username) {
        if (username.isEmpty() || username.isBlank())
            return new ResponseEntity<>("Email không xác định", HttpStatus.BAD_REQUEST);
        
        User currentUser = this.userService.getUserByUsername(username);
        
        if (currentUser.getActive() == true)
            return new ResponseEntity<>("Activated", HttpStatus.OK);
        
        return new ResponseEntity<>(currentUser.getEmail(), HttpStatus.OK);
    }
}
