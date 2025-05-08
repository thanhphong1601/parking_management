/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.ReceiptService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Admin
 */
@Controller
public class AdminStatController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private UserService userService;
    @Autowired
    private PositionService posService;

    @GetMapping("/stat")
    public String statPage(Model model) {

        model.addAttribute("ticketCount", this.ticketService.countTotalTickets());
        model.addAttribute("totalRevenue", this.receiptService.sumTotalRevenue());
        model.addAttribute("totalCustomer", this.userService.countCustomers());
        model.addAttribute("availablePositions", this.posService.countAvailablePositions());

        //datas for charts
        //revenue by month - receipt
        List<Object[]> revenueData = this.receiptService.getMonthlyRevenue();
        List<String> months = new ArrayList<>();
        List<Long> revenues = new ArrayList<>();
        for (Object[] obj : revenueData) {
            int monthNumber = (Integer) obj[0];
            months.add("Tháng " + monthNumber);
            revenues.add((Long) obj[1]);
        }
        model.addAttribute("months", months);
        model.addAttribute("monthlyRevenue", revenues);
        
        //counts ticket by type - ticket
        List<Object[]> typeData = this.ticketService.countTicketsByType();
        List<String> ticketTypes = new ArrayList<>();
        List<Long> typeCounts = new ArrayList<>();
        for (Object[] obj : typeData) {
            ticketTypes.add((String) obj[0]);
            typeCounts.add((Long) obj[1]);
        }
        model.addAttribute("ticketTypeLabels", ticketTypes);
        model.addAttribute("ticketTypeCounts", typeCounts);
        
        //counts ticket by floor - ticket
        List<Object[]> floorData = this.ticketService.countTicketsByFloor();
        List<String> floorLabels = new ArrayList<>();
        List<Long> floorCounts = new ArrayList<>();
        for (Object[] obj : floorData) {
            floorLabels.add("Tầng " + obj[0]);
            floorCounts.add((Long) obj[1]);
        }
        model.addAttribute("floorLabels", floorLabels);
        model.addAttribute("floorTicketCounts", floorCounts);

        return "stat";
    }
}
