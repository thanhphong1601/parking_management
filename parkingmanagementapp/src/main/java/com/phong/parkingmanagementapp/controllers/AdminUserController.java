/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.CloudinaryService;
import com.phong.parkingmanagementapp.services.RoleService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.hibernate.annotations.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Controller
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;
    private final TicketService ticketService;
    private final VehicleService vehicleService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    public AdminUserController(UserService userService, RoleService roleService, TicketService ticketService, VehicleService vehicleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.ticketService = ticketService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/users/{role_number}")
    public String userList(@RequestParam Map<String, String> params, @PathVariable("role_number") int roleNumber, Model model) {
//       List<User> userList = this.userService.findUsersByRoleId(roleNumber);

        //del blank vehicles
        this.vehicleService.deleteVehiclesOfBlankUser();
        
        String idNum = params.get("identityNumber");
        String name = params.get("name");

        List<User> userList = this.userService.findUserByIdentityNumberOrNameOrRole(idNum, name, roleNumber);
        model.addAttribute("users", userList);
        model.addAttribute("roleNum", roleNumber);

        return "users";
    }

    @GetMapping("/users/{role_number}/add")
    public String userAdd(@PathVariable("role_number") int roleNumber, Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roleNum", roleNumber);
        model.addAttribute("roles", this.roleService.findAll());
        
        
        
        model.addAttribute("vehicles", this.vehicleService.findVehiclesOfBlankUser());
        
        return "addUser";
    }

    @GetMapping("/users/{role_number}/{uid}/info")
    public String userInfo(@PathVariable Map<String, String> params, Model model) {
        //del blank vehicles
        this.vehicleService.deleteVehiclesOfBlankUser();
        
        int uid = Integer.parseInt(params.get("uid"));
        int roleNum = Integer.parseInt(params.get("role_number"));
       
        model.addAttribute("user", this.userService.getUserById(uid));
        model.addAttribute("roleNum", roleNum);
        model.addAttribute("roles", this.roleService.findAll());
        model.addAttribute("vehicles", this.vehicleService.findVehicleByUserId(uid));
        
        

        return "addUser";
    }

    @PostMapping("/users/{role_number}/add")
    public String addUser(Model model, @ModelAttribute(value = "user") @Valid User u,
            BindingResult rs, @PathVariable("role_number") int roleNum) throws IOException {
        String url = "/users/" + String.valueOf(roleNum);

        if (!u.getFile().isEmpty()) {
            String imageUrl = this.cloudinaryService.uploadImage(u.getFile());
            u.setAvatar(imageUrl);
        }
        if (!rs.hasErrors()) {
            try {

                this.userService.saveUser(u);
                
                User userNew = this.userService.getUserByName(u.getName());
                this.vehicleService.updateNewVehiclesOwner(userNew.getId());

                return "redirect:" + url;
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        System.out.println(rs);
        return "addUser";

    }

    @GetMapping("/users/{role_number}/{id}/delete")
    public String confirmDelete(@PathVariable Map<String, String> params, Model model) {
        int id = Integer.parseInt(params.get("id"));
        User u = this.userService.getUserById(id);
        int roleNum = Integer.parseInt(params.get("role_number"));
        String url = "/users/" + String.valueOf(roleNum);

        if (u == null) {
            return "redirect:" + url;
        }

        model.addAttribute("user", u);
        model.addAttribute("tickets", this.ticketService.findTicketByUserOwnedId(id));
        model.addAttribute("vehicles", this.vehicleService.findVehicleByUserId(id));
        return "confirmDeleteUser";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") int id, @RequestParam("confirm") boolean confirm) {
        User u = this.userService.getUserById(id);
        String url = "/users/" + String.valueOf(u.getRole().getId());
        if (confirm) {
            //del user's tickets
            this.ticketService.deleteTicketsByUserOwnedId(id);
            //delete user's vehicles
            this.vehicleService.deleteVehiclesByUserId(id);
            //del user
            this.userService.deleteUser(u);
        }
        return "redirect:" + url;
    }
}
