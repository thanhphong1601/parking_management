/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.repositories.TicketRepository;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.PriceService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
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
public class AdminTicketController {

    private final TicketService ticketService;
    private final FloorService floorService;
    private final LineService lineService;
    private final PositionService poService;
    private final UserService userService;
    private final PriceService priceService;

    @Autowired
    public AdminTicketController(TicketService ticketService, FloorService floorService, LineService lineService, PositionService poService, UserService userService, PriceService priceService) {
        this.ticketService = ticketService;
        this.floorService = floorService;
        this.lineService = lineService;
        this.poService = poService;
        this.userService = userService;
        this.priceService = priceService;
    }

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("floors", this.floorService.findAll());
        model.addAttribute("lines", this.lineService.findAll());
        model.addAttribute("positions", this.poService.findAll());
        model.addAttribute("securities", this.userService.findUsersByRoleId(2));
        model.addAttribute("customers", this.userService.findUsersByRoleId(3));
        model.addAttribute("prices", this.priceService.findAll());
    }

    @GetMapping("/tickets")
    public String ticketList(@RequestParam(value = "name", required = false) String name, Model model) {
        model.addAttribute("ticketList", this.ticketService.findTicketByUserOwned(name));
        return "tickets";
    }
    
    @GetMapping("/tickets/add")
    public String ticketAddSite(Model model){
        model.addAttribute("ticket", new Ticket());
        return "addTicket";
    }
    
    @PostMapping("/tickets/add")
    public String addTicket(Model model, @ModelAttribute(value = "ticket") @Valid Ticket t,
            BindingResult rs) {
        
        if (!rs.hasErrors()) {
            try {
                this.ticketService.addOrUpdate(t);
                
                return "redirect:/tickets";
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        return "addTicket";

    }
    
    @GetMapping("/tickets/{id}")
    public String ticketInfo(@PathVariable(value = "id") int id, Model model){
        model.addAttribute("ticket", this.ticketService.getTicketById(id));
        return "addTicket";
    }
    
    @GetMapping("/tickets/{id}/delete")
    public String confirmDelete(@PathVariable("id") int id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return "redirect:/tickets"; // Redirect if ticket not found
        }

        model.addAttribute("ticket", ticket);    
        return "confirmDelete"; // Return to confirmation page
    }
    
    @PostMapping("/tickets/{id}/delete")
    public String deleteTicket(@PathVariable("id") int id, @RequestParam("confirm") boolean confirm) {
        if (confirm) {
            ticketService.delete(ticketService.getTicketById(id));
        }
        return "redirect:/tickets"; // Redirect to ticket list
    }
}
