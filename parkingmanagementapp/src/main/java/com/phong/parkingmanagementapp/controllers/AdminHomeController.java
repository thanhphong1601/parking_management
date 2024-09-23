/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@Controller
public class AdminHomeController {
    
    @GetMapping("/")
    public String index(Model model){
        //check login user status here
        model.addAttribute("username", "Phong");
        return "index";
    }
    
    @GetMapping("/hi")
    public String hii(){
        return "reallyyy";
    }
}
