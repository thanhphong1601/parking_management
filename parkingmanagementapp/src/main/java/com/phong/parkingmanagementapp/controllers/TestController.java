/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.dtos.TicketDTO;
import com.phong.parkingmanagementapp.models.EntryHistory;
import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.PositionStatusEnum;
import com.phong.parkingmanagementapp.models.Role;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketPreIdEnum;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.repositories.EntryHistoryRepository;
import com.phong.parkingmanagementapp.repositories.LineRepository;
import com.phong.parkingmanagementapp.repositories.TicketRepository;
import com.phong.parkingmanagementapp.services.CloudinaryService;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PaymentService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.ReceiptService;
import com.phong.parkingmanagementapp.services.RoleService;
import com.phong.parkingmanagementapp.services.TextExtractorService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.TicketTypeService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final UserService userService;
    private final TicketService ticketService;
    private final RoleService roleService;
    private final VehicleService vehicleService;
    
    @Autowired
    private CloudinaryService cloudinary;

    @Autowired
    private TextExtractorService textExtractor;

    @Autowired
    private TicketTypeService ticketTypeService;

    @Value("${page_size}")
    private int pageSize;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FloorService floorService;
    @Autowired
    private LineService lineService;
    @Autowired
    private LineRepository lineRepo;
    @Autowired
    private EntryHistoryService entryService;
    @Autowired
    private PositionService poService;
    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private EntryHistoryRepository entryRepo;

    @Autowired
    public TestController(UserService userService, TicketService ticketService, RoleService roleService, VehicleService vehicleService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.roleService = roleService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/test/")
    public ResponseEntity<String> test() throws Exception {
        Date start = new Date(2024 - 1900, 8, 21); // Tháng bắt đầu từ 0, tháng 9 là 8
        Date end = new Date(2024 - 1900, 9, 21);   // Tháng 10 là 9

        String url = this.paymentService.createPaymentUrl(100000, "Xin chào", 12);

        return ResponseEntity.ok(url);
    }

    @GetMapping("/test2/{userId}")
    public ResponseEntity<Page<TicketDTO>> test2(@PathVariable(name = "userId") int userId) {
        Pageable pageable = PageRequest.of(0, pageSize);

        return new ResponseEntity<>(this.ticketRepo.getTicketsInfoByUserId(userId, pageable, null, null, Boolean.FALSE), HttpStatus.OK);
    }

    @PostMapping("/test3")
    public ResponseEntity<?> test3(@RequestParam Map<String, String> params,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Map<String, String> plateRecognizeResult = this.entryService.recognizePlate(file);

            //check if it's a riel plate img
            if (plateRecognizeResult.get("plate") == null) {
                return new ResponseEntity<>(plateRecognizeResult,
                        HttpStatus.NOT_FOUND);
            }
        return new ResponseEntity<>(plateRecognizeResult, HttpStatus.OK);
    }

    @GetMapping("/testRequestParams/")
    public ResponseEntity<?> testAuth(@RequestParam(value = "id", required = false) Integer id) {
        if (id == null) {
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.ok("Success" + id);
        }
    }

    @GetMapping("/testPage")
    public ResponseEntity<?> testPageable(@RequestParam Map<String, String> params) {
        int page = Integer.parseInt(params.get("page"));

        Pageable pageable = PageRequest.of(page, pageSize);
        String name = params.get("name");
        String identityNumber = params.get("identityNum");
        Page<User> userList = this.userService.findUserByIdentityNumberOrNameOrRolePageable(identityNumber, name, 3, pageable);

        return ResponseEntity.ok(userList);
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> ticketList(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "page", defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Ticket> ticketList = this.ticketService.findTicketByUserOwnedPageable(name, true, pageable);

        return ResponseEntity.ok(ticketList);
    }

    @GetMapping("/line/status/test")
    public ResponseEntity<String> lineStatus(@RequestParam(value = "floorId") String id) {
        int floorId = Integer.parseInt(id);
//        Boolean check = this.lineService.checkLineStatus(lineId);
//        this.floorService.checkStatus(floorId);
        Boolean check = this.lineRepo.existsByStatus(floorId);
        this.lineService.checkLineStatus(floorId);
        return ResponseEntity.ok(check.toString());
    }

    @GetMapping("/faces/comparing/test")
    public ResponseEntity<?> updateDeleted() {
        List<Position> l = this.poService.findPositionsByLineId(7);
        for (Position p : l) {
            p.setIsDeleted(Boolean.FALSE);
            this.poService.savePosition(p);
        }

        Line line = this.lineService.getLineById(7);
        line.setIsDeleted(Boolean.FALSE);
        this.lineService.saveLine(line);

        return ResponseEntity.ok(l);
    }
}
