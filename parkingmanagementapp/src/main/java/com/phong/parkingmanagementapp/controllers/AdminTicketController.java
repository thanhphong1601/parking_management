/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.PositionStatusEnum;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketPreIdEnum;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.repositories.TicketRepository;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.PriceService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.TicketTypeService;
import com.phong.parkingmanagementapp.services.UserService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private final TicketTypeService typeService;

    @Value("${page_size}")
    private int pageSize;

    @Autowired
    public AdminTicketController(TicketService ticketService, FloorService floorService,
            LineService lineService, PositionService poService,
            UserService userService, PriceService priceService,
            TicketTypeService typeService) {
        this.ticketService = ticketService;
        this.floorService = floorService;
        this.lineService = lineService;
        this.poService = poService;
        this.userService = userService;
        this.priceService = priceService;
        this.typeService = typeService;
    }

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("floors", this.floorService.findAll());
        model.addAttribute("lines", this.lineService.findAll());
        model.addAttribute("positions", this.poService.findAll());
        model.addAttribute("securities", this.userService.findUsersByRoleId(2));
        model.addAttribute("customers", this.userService.findUsersByRoleId(3));
        model.addAttribute("prices", this.priceService.findAll());
        model.addAttribute("types", this.typeService.getAllTypes());
    }

    @GetMapping("/tickets")
    public String ticketList(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model, @RequestParam(required = false) Boolean status) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Ticket> ticketList = this.ticketService.findTicketByUserOwnedPageable(name, status, pageable);
        model.addAttribute("ticketList", ticketList);
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status == null ? "" : status);
        model.addAttribute("name", name);
        model.addAttribute("totalPages", ticketList.getTotalPages());
        return "tickets";
    }

    @GetMapping("/tickets/add")
    public String ticketAddSite(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "addTicket";
    }

    @PostMapping("/tickets/add")
    public String addTicket(Model model, @ModelAttribute(value = "ticket") @Valid Ticket t,
            @RequestParam Map<String, String> params,
            BindingResult rs) {

        if (!rs.hasErrors()) {
            try {
                if (t.getPosition().getTake() == true || t.getPosition().getStatus() != PositionStatusEnum.AVAILABLE) {
                    model.addAttribute("errMsg", "Vị trí này đã được chọn, hãy chọn vị trí khác");
                } else {
                    int typeId = t.getTicketType().getId();
                    int numberOfDays = Integer.parseInt(params.get("numberOfDays"));
                    numberOfDays = (typeId == 3 ? numberOfDays * 30 : numberOfDays);

                    Date endDate = this.ticketService.calculateStartAndEndDate(numberOfDays, t.getStartDay());
                    t.setEndDay(endDate);

                    t.setActive(Boolean.TRUE);
                    
                    t.setLicenseNumber(t.getVehicle().getPlateLicense());

                    this.ticketService.addOrUpdate(t);
                    this.setNewestTicketCreatedId(t.getTicketType().getId());

                    //retake created ticket (newest)
                    Ticket newestCreatedTicket = this.ticketService.findTopByOrderByIdDesc();
                    this.poService.assignPositionToTicket(newestCreatedTicket.getId(),
                            newestCreatedTicket.getPosition().getId());

                    return "redirect:/tickets?page=0";
                }

//                this.poService.UpdateStatus(t.getPosition().getId(), Boolean.TRUE);
//                this.lineService.checkLineStatus(t.getLine().getId());
//                this.floorService.checkStatus(t.getFloor().getId());
            } catch (Exception e) {
                model.addAttribute("errMsg", e.toString());
            }
        }
        return "addTicket";

    }

    @GetMapping("/tickets/{id}")
    public String ticketInfo(@PathVariable(value = "id") int id, Model model) {
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
            Ticket currentTicket = this.ticketService.getTicketById(id);
            this.poService.emptyTakenPosition(currentTicket.getPosition().getId());
            
            ticketService.delete(ticketService.getTicketById(id));
        }
        return "redirect:/tickets?page=0"; // Redirect to ticket list
    }

    @GetMapping("/getLinesByFloorId/{floorId}")
    @ResponseBody
    public List<Line> getLinesByFloorId(@PathVariable("floorId") int floorId) {
        Floor floor = this.floorService.getFloorById(floorId);
        return new ArrayList<>(floor.getLineCollection());
    }

    @GetMapping("/getPositionsByLineId/{lineId}")
    @ResponseBody
    public List<Position> getPositionsByLineId(@PathVariable("lineId") int lineId) {
        Line line = this.lineService.getLineById(lineId);
        return new ArrayList<>(line.getPositionCollection());
    }

    public void setNewestTicketCreatedId(int typeId) {
        Ticket newestTicketCreated = this.ticketService.findTopByOrderByIdDesc();
        String ticketId = switch (typeId) {
            case 1 ->
                String.valueOf(TicketPreIdEnum.NORMAL);
            case 2 ->
                String.valueOf(TicketPreIdEnum.VIP);
            default ->
                String.valueOf(TicketPreIdEnum.MONTH);
        };
        newestTicketCreated.setTicketId(ticketId.concat(String.valueOf(newestTicketCreated.getId()).trim()));

        this.ticketService.addOrUpdate(newestTicketCreated);
    }
}
