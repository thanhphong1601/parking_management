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
import org.springframework.web.bind.annotation.PathVariable;
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
    public String addFloor(Model model, @ModelAttribute(value = "floor") @Valid Floor f,
            BindingResult rs) {
        if (!rs.hasErrors()) {
            try {
                if (f.getId() == null) { //new floor
                    f.setFull(Boolean.TRUE);
                    f.setIsDeleted(false);
                } else {
                    Floor floorTemp = this.floorService.getFloorById(f.getId());
                    f.setIsDeleted(floorTemp.getIsDeleted());
                }
                
                System.out.println(f.getFull());

                this.floorService.saveFloor(f);
                if (f.getId() != null) {
                    this.floorService.checkStatus(f.getId());
                }

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
            model.addAttribute("lineNew", this.lineService.getLineById(Integer.parseInt(id)));
        } else {
            model.addAttribute("lineNew", new Line());
        }
        model.addAttribute("floors", this.floorService.findAll());
        return "addLine";
    }

    @PostMapping("/addLine")
    public String addLine(Model model, @ModelAttribute(value = "lineNew") @Valid Line l,
            BindingResult rs) {
        if (!rs.hasErrors()) {
            try {
                if (l.getId() == null){ //new line
                    l.setFull(Boolean.TRUE);
                    l.setIsDeleted(false);
                } else {
                    Line lineTemp = this.lineService.getLineById(l.getId());
                    l.setIsDeleted(lineTemp.getIsDeleted());
                }
                
                this.lineService.saveLine(l);

                this.floorService.checkStatus(l.getFloor().getId());

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
            model.addAttribute("positionNew", this.poService.getPositionById(Integer.parseInt(id)));
        } else {
            model.addAttribute("positionNew", new Position());
        }
        model.addAttribute("lines", this.lineService.findAll());
        return "addPosition";
    }

    @PostMapping("/addPosition")
    public String addPosition(Model model, @ModelAttribute(value = "positionNew") @Valid Position p,
            BindingResult rs) {
        if (!rs.hasErrors()) {
            try {
                if (p.getId() == null){ //new position
                    p.setTake(Boolean.FALSE);
                    p.setIsDeleted(false);
                }
                else {
                    Position posTemp = this.poService.getPositionById(p.getId());
                    p.setIsDeleted(posTemp.getIsDeleted());
                }
                this.poService.savePosition(p);

                this.lineService.checkLineStatus(p.getLine().getId());
                this.floorService.checkStatus(p.getLine().getFloor().getId());

                return "redirect:/area";
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        return "addPosition";
    }

    @GetMapping("/area/position/{id}/delete")
    public String confirmDeletePosition(Model model, @PathVariable("id") int id) {
        Position currentPosition = this.poService.getPositionById(id);

        if (currentPosition == null) {
            return "redirect:/area"; // Redirect if position not found
        }
        model.addAttribute("position", currentPosition);

        return "confirmDeletePosition";
    }

    @PostMapping("/area/position/{id}/delete")
    public String deletePosition(Model model, @PathVariable("id") int id, @RequestParam("confirm") boolean confirm) {
        if (confirm) {
            this.poService.deletePosition(id);
            //inform user about the delete
        }
        return "redirect:/area";
    }

    @GetMapping("/area/line/{id}/delete")
    public String confirmDeleteLine(Model model, @PathVariable("id") int id) {
        Line currentLine = this.lineService.getLineById(id);

        if (currentLine == null) {
            return "redirect:/area"; // Redirect if position not found
        }
        model.addAttribute("line", currentLine);
        model.addAttribute("positions", currentLine.getPositionCollection());
        model.addAttribute("positionAmount", this.poService.countPositionByLineId(id));

        return "confirmDeleteLine";
    }

    @PostMapping("/area/line/{id}/delete")
    public String deleteLine(Model model, @PathVariable("id") int id, @RequestParam("confirm") boolean confirm) {
        if (confirm) {
            this.lineService.deleteLine(id);
            //inform user about the delete
        }
        return "redirect:/area";
    }

    @GetMapping("/area/floor/{id}/delete")
    public String confirmDeleteFloor(Model model, @PathVariable("id") int id) {
        Floor currentFloor = this.floorService.getFloorById(id);

        if (currentFloor == null) {
            return "redirect:/area"; // Redirect if position not found
        }
        model.addAttribute("floor", currentFloor);
        model.addAttribute("lines", currentFloor.getLineCollection());
        model.addAttribute("lineAmount", this.lineService.countLineByFloorId(id));

        return "confirmDeleteFloor";
    }

    @PostMapping("/area/floor/{id}/delete")
    public String deleteFloor(Model model, @PathVariable("id") int id, @RequestParam("confirm") boolean confirm) {
        if (confirm) {
            this.floorService.deleteFloor(id);
            //inform user about the delete
        }
        return "redirect:/area";
    }
}
