/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.services.VehicleTypeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Admin
 */
@Controller
public class AdminVehicleController {

    @Autowired
    private VehicleTypeService veTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/users/{roleNum}/vehicles/add")
    public String showPage(Model model, @PathVariable("roleNum") int roleNum, HttpSession session) {

        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("types", this.veTypeService.findAll());
        model.addAttribute("roleNum", roleNum);
        model.addAttribute("uid", null);
        return "addVehicle";
    }
    
    @GetMapping("/users/{roleNum}/{uid}/vehicles/add")
    public String createdUserVehiclePage(Model model, @PathVariable Map<String, String> params, HttpSession session) {
        int roleNum = Integer.parseInt(params.get("roleNum"));
        int uid = Integer.parseInt(params.get("uid"));
        
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("types", this.veTypeService.findAll());
        model.addAttribute("roleNum", roleNum);
        model.addAttribute("uid", uid);
        return "addVehicle";
    }
    

    @PostMapping("/users/{roleNum}/vehicles/add")
    public String addVehicle(@PathVariable("roleNum") int roleNum, @ModelAttribute(value = "vehicle") @Valid Vehicle v,
            BindingResult rs, Model model, HttpSession session) {
        String url = "/users/" + roleNum + "/add";

        if (!rs.hasErrors()) {
            try {
                this.vehicleService.saveVehicle(v);

                return "redirect:" + url;
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }

        return "addVehicle";
    }
    
    @PostMapping("/users/{roleNum}/{uid}/vehicles/add")
    public String createdUserAddVehicle(@PathVariable Map<String, String> params, @ModelAttribute(value = "vehicle") @Valid Vehicle v,
            BindingResult rs, Model model, HttpSession session) {
        int roleNum = Integer.parseInt(params.get("roleNum"));
        int uid = Integer.parseInt(params.get("uid"));
        
        String urlSuccess = "/users/" + roleNum + "/" + uid + "/info";
        ///users/{roleNum}/{uid}/vehicles/add
        String urlFailed = "/users/" + roleNum + "/" + uid + "/vehicles/add";

        if (!rs.hasErrors()) {
            try {
                //new func
                this.vehicleService.saveVehicleForCreatedUser(uid, v);

                return "redirect:" + urlSuccess;
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }

        return "redirect:" + urlFailed;
    }
}
