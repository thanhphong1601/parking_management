/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
public class AdminAreaController {

    @Autowired
    private FloorService floorService;
    @Autowired
    private LineService lineService;
    @Autowired
    private PositionService poService;

    @GetMapping("/area")
    public String mainPage(Model model) {
        model.addAttribute("floors", this.floorService.findAll());
        model.addAttribute("lines", this.lineService.findAll());
        model.addAttribute("positions", this.poService.findAll());
        return "areamanagement";
    }

    @GetMapping("/addFloor")
    public String floorAddingPage(Model model, @RequestParam(value = "floorId", required = false) String id) {
        if (id != null) {
            model.addAttribute("floor", this.floorService.getFloorById(Integer.parseInt(id)));
        } else {
            model.addAttribute("floor", new Floor());
        }
        return "addFloor";
    }

    @PostMapping("/addFloor")
    public String addFloor(Model model, @RequestParam("floorId") int id, @ModelAttribute(value = "floor") @Valid Floor f,
            BindingResult rs) {
        if (!rs.hasErrors()) {
            try {
                this.floorService.saveFloor(f);
                return "redirect:/area";
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        return "addFloor";
    }
    
    @GetMapping("/addLine")
    public String lineAddingPage(Model model, @RequestParam(value = "lineId", required = false) String id) {
        if (id != null) {
            model.addAttribute("line", this.lineService.getLineById(Integer.parseInt(id)));
        } else {
            model.addAttribute("line", new Line());
        }
        model.addAttribute("floors", this.floorService.findAll());
        return "addLine";
    }
    
    @PostMapping("/addLine")
    public String addLine(Model model, @RequestParam("lineId") int id, @ModelAttribute(value = "line") @Valid Line l,
            BindingResult rs) {
        if (!rs.hasErrors()) {
            try {
                this.lineService.saveLine(l);
                return "redirect:/area";
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        return "addLine";
    }
    
    @GetMapping("/addPosition")
    public String positionAddingPage(Model model, @RequestParam(value = "positionId", required = false) String id) {
        if (id != null) {
            model.addAttribute("position", this.poService.getPositionById(Integer.parseInt(id)));
        } else {
            model.addAttribute("position", new Position());
        }
        model.addAttribute("lines", this.lineService.findAll());
        return "addPosition";
    }
    
    @PostMapping("/addPosition")
    public String addPosition(Model model, @RequestParam("positionId") int id, @ModelAttribute(value = "position") @Valid Position p,
            BindingResult rs) {
        if (!rs.hasErrors()) {
            try {
                this.poService.savePosition(p);
                return "redirect:/area";
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        return "addPosition";
    }
}
