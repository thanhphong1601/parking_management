/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.dtos.TicketDTO;
import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketPreIdEnum;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.PriceService;
import com.phong.parkingmanagementapp.services.TicketPriceService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.TicketTypeService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
public class ApiTicketController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private FloorService floorService;
    @Autowired
    private LineService lineService;
    @Autowired
    private PositionService poService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private TicketPriceService ticketPriceService;
    @Autowired
    private EntryHistoryService entryService;
    @Autowired
    private TicketTypeService ticketTypeService;

    @Value("${page_size}")
    private int pageSize;

    private int normalTicketTypeId = 1;

    @GetMapping("/ticket/list")
    public ResponseEntity<Page<Ticket>> getActiveTicketList(@RequestParam Map<String, String> params) {
        String ownerIdentityNumber = params.get("identityNum");
        String ownerName = params.get("name");
        int page = Integer.parseInt(params.get("page"));

        Pageable pageable = PageRequest.of(page, pageSize);

        return ResponseEntity.ok(this.ticketService.getTicketsByUserOwnedActivePageable(ownerIdentityNumber, ownerName, pageable));
    }

    @PostMapping("/ticket/create")
    public ResponseEntity<?> createTicket(@RequestParam Map<String, String> params) {

        Ticket t = new Ticket();

        //position
        String positionId = params.get("positionId");
        Position posTemp = null;
        if (!positionId.equals("null")) {
            posTemp = this.poService.getPositionById(Integer.parseInt(positionId));
        }
        t.setPosition(posTemp);
        //update status of taken position
        if (posTemp != null) {
            poService.UpdateStatus(Integer.parseInt(positionId), Boolean.TRUE);
        }

        //line
        String lineId = params.get("lineId");
        Line lineTemp = null;
        if (!lineId.equals("null")) {
            lineTemp = this.lineService.getLineById(Integer.parseInt(lineId));
        }
        t.setLine(lineTemp);
        //update line's status
        if (lineTemp != null) {
            this.lineService.checkLineStatus(Integer.parseInt(lineId));
        }

        //floor
        String floorId = params.get("floorId");
        Floor floorTemp = null;
        if (!floorId.equals("null")) {
            floorTemp = this.floorService.getFloorById(Integer.parseInt(floorId));
        }
        t.setFloor(floorTemp);
        //update floor's status
        if (floorTemp != null) {
            this.floorService.checkStatus(Integer.parseInt(floorId));
        }

        String userCreateId = params.getOrDefault("userCreateId", "");
        if (userCreateId.isEmpty() || userCreateId.isBlank()) {
            userCreateId = String.valueOf(this.userService.getAdminId());
        }
        t.setUserCreate(this.userService.getUserById(Integer.parseInt(userCreateId)));
        String userOwnedId = params.get("userOwnedId");
        t.setUserOwned(this.userService.getUserById(Integer.parseInt(userOwnedId)));
        String vehicleId = params.get("vehicleId");
        t.setVehicle(this.vehicleService.getVehicleById(Integer.parseInt(vehicleId)));
        t.setLicenseNumber(t.getVehicle().getPlateLicense());

        String startDay = params.get("startDay");
        t.setStartDay(parseLocalDate.parseDate(startDay));

        //ticket register by day (normal, VIP or Month)
        int typeId = Integer.parseInt(params.get("typeId"));
        if (typeId != 1 || typeId != 2 || typeId != 3) {
            return new ResponseEntity<>("Loại vé không hợp lệ", HttpStatus.NOT_FOUND);
        }

        t.setPrice(typeId == 1 ? this.ticketPriceService.getNormalPrice()
                : typeId == 2 ? this.ticketPriceService.getDiscountPrice()
                        : this.ticketPriceService.getMonthPrice());
        t.setTicketType(this.ticketTypeService.getTicketTypeById(typeId));

        int numberOfDays = Integer.parseInt(params.get("numberOfDays"));
        numberOfDays = (typeId != 1 ? numberOfDays * 30 : numberOfDays);

        Date endDate = this.ticketService.calculateStartAndEndDate(numberOfDays, t.getStartDay());
        t.setEndDay(endDate);

        int totalPrice = this.ticketService.updateTicketTotalPrice(typeId, numberOfDays);
        t.setTotalPrice(totalPrice);

        this.ticketService.addOrUpdate(t);

        this.poService.assignPositionToTicket(this.ticketService.findTopByOrderByIdDesc().getId(), posTemp.getId());

        this.setNewestTicketCreatedId(typeId);

        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }

    //normal func
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

    @DeleteMapping("/ticket/{id}/delete")
    public ResponseEntity<?> deleteTicket(@PathVariable("id") int id) {
        Ticket t = this.ticketService.getTicketById(id);

        this.ticketService.delete(t);
        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    @PostMapping("/ticket/create/anonymous")
    public ResponseEntity<?> createAnonymousTicket(@RequestParam Map<String, String> params)
            throws IOException {
//        System.out.println(params.get("floorId"));
//        System.out.println(params.get("lineId"));
//        System.out.println(params.get("positionId"));
//        System.out.println(params.get("userCreateId"));
//        System.out.println(params.get("userOwnedId"));
//        System.out.println(params.get("vehicleId"));
//        System.out.println(params.get("startDay"));
//        System.out.println(params.get("endDay"));
//        System.out.println(params.get("priceId"));

        Ticket t = new Ticket();

        String userCreateId = params.get("userCreateId");
        t.setUserCreate(this.userService.getUserById(Integer.parseInt(userCreateId)));
        t.setUserOwned(this.userService.getAnonymousUser());
        t.setVehicle(this.vehicleService.getAnonymousVehicle());
        t.setLicenseNumber(t.getVehicle().getPlateLicense());

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        Date startDay = parseLocalDate.convertToDate(today);
        t.setStartDay(startDay);

        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        Date endDay = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        t.setEndDay(endDay);

        t.setPrice(this.ticketPriceService.getNormalPrice());
        t.setTicketType(this.ticketTypeService.getNormalType());

//        Map<String, String> resultsFromRecognizingPlate = new HashMap<>();
//        if (file != null) {
//            resultsFromRecognizingPlate = this.entryService.recognizePlate(file);
//        }
//
//        t.setLicenseNumber(resultsFromRecognizingPlate.isEmpty() ? "" : resultsFromRecognizingPlate.get("plate"));
        //handle license plate img to get its number
//        String licenseNumber = params.getOrDefault("licenseNumber", "0");
//        t.setLicenseNumber(licenseNumber);
        this.ticketService.addOrUpdate(t);

        Ticket newestTicketCreated = this.ticketService.findTopByOrderByIdDesc();
        this.setNewestTicketCreatedId(normalTicketTypeId);

        Position availablePosition = this.poService.findFirstAvailablePositionOrdered().get(0);
        if (availablePosition == null) {
            return new ResponseEntity<>("Hiện bãi đã hết vị trí trống", HttpStatus.NOT_FOUND);
        }

        this.poService.assignPositionToTicket(newestTicketCreated.getId(),
                availablePosition.getId());

        return new ResponseEntity<>("Đã tạo vé mới thành công", HttpStatus.CREATED);
    }

    @GetMapping("/ticket/new/get")
    public ResponseEntity<?> getNewestCreatedTicket() {
        Ticket newestCreatedTicket = this.ticketService.findTopByOrderByIdDesc();

        return ResponseEntity.ok(newestCreatedTicket.getTicketId());
    }

    @GetMapping("/customerr/ticket/{userId}/list")
    public ResponseEntity<Page<TicketDTO>> getCustomerTicketList(@PathVariable(name = "userId") int userId,
            @RequestParam Map<String, String> params) {
        int page = Integer.parseInt(params.get("page"));
        Date startDay = parseLocalDate.parseDate(params.get("startDay"));
        Date endDay = parseLocalDate.parseDate(params.get("endDay"));

        Boolean isPaid;
        String isPaidText = params.get("isPaid");
        if (isPaidText.equalsIgnoreCase("true")) {
            isPaid = Boolean.TRUE;
        } else if (isPaidText.equalsIgnoreCase("false")) {
            isPaid = Boolean.FALSE;
        } else {
            isPaid = null;
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        return new ResponseEntity<>(this.ticketService.getTicketsInfoByUserId(userId, pageable, startDay, endDay, isPaid), HttpStatus.OK);
    }

    //for when admin logged in
    @PutMapping("/ticket/get/all/active/update")
    public ResponseEntity<?> updateStatusAllTicketList() {
        List<Ticket> l = this.ticketService.findAll();
        Date currentDate = new Date();

        if (!l.isEmpty()) {
            for (Ticket t : l) {
                if (this.ticketService.checkTicketDateValid(t.getId(), currentDate)) {
                    t.setActive(Boolean.TRUE);
                } else {
                    t.setActive(Boolean.FALSE);
                }

                this.ticketService.addOrUpdate(t);
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PutMapping("/customer/{customerId}/ticket/get/all/active/update")
    public ResponseEntity<?> updateStatusUserTicketList(@PathVariable("customerId") int customerId) {
        List<Ticket> l = this.ticketService.findTicketByUserOwnedId(customerId);
        Date currentDate = new Date();

        if (!l.isEmpty()) {
            for (Ticket t : l) {
                if (this.ticketService.checkTicketDateValid(t.getId(), currentDate)) {
                    t.setActive(Boolean.TRUE);
                } else {
                    t.setActive(Boolean.FALSE);
                }

                this.ticketService.addOrUpdate(t);
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
    
    @PutMapping("/security/ticket/paid")
    public ResponseEntity<?> payTicketQuick(@RequestParam("ticketId") String ticketId){
        Ticket currentTicket = this.ticketService.findByTicketId(ticketId);
        
        if (currentTicket == null)
            return new ResponseEntity<>("Không tìm thấy vé", HttpStatus.NOT_FOUND);
        
        currentTicket.setIsPaid(Boolean.TRUE);
        this.ticketService.addOrUpdate(currentTicket);
        
        return new ResponseEntity<>("Đã thanh toán vé", HttpStatus.OK);
    }

}
