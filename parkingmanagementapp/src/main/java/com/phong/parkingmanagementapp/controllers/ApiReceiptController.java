/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Receipt;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.services.ReceiptService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import jakarta.websocket.server.PathParam;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiReceiptController {

    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;

    @Value("${page_size}")
    private int pageSize;

    @GetMapping("/receipt/list")
    public ResponseEntity<?> getReceipts(@RequestParam Map<String, String> params) {
        String name = params.getOrDefault("name", "");
        String ticketId = params.getOrDefault("ticketId", "");
        int page = Integer.parseInt(params.getOrDefault("page", "0"));

        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(this.receiptService.getReceipt(pageable, name, ticketId));
    }

    @PostMapping("/receipt/create")
    public ResponseEntity<?> createReceipt(@RequestParam Map<String, String> params) {
        int ticketId = Integer.parseInt(params.get("id"));
        Receipt currentReceipt = new Receipt();
        Ticket currentTicket = this.ticketService.getTicketById(ticketId);
        currentReceipt.setTicket(currentTicket);
        String transactionDate = params.get("transactionDate");
        String transactionNumber = params.get("transactionNumber");
        String orderInfo = params.getOrDefault("content", "");
        System.out.println(transactionDate);
                System.out.println(transactionNumber);

                        System.out.println(orderInfo);

        currentReceipt.setTransactionDate(parseLocalDate.parseStringToLocalDateToDate(transactionDate));
        currentReceipt.setTransactionNumber(transactionNumber);
        currentReceipt.setContent(orderInfo);

        this.receiptService.saveReceipt(currentReceipt);

        return ResponseEntity.ok("Done");
    }

    @GetMapping("/customer/{customerId}/receipt/list")
    public ResponseEntity<?> getCustomerReceiptsPageable(@PathVariable("customerId") int customerId,
            @RequestParam Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        Date startDay = parseLocalDate.parseDate(params.get("startDay"));
        Date endDay = parseLocalDate.parseDate(params.get("endDay"));
        
        if (startDay != null && endDay != null && startDay.after(endDay)){
            return new ResponseEntity<>("Ngày nhập vào không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(this.receiptService.getCustomerReceipt(pageable, customerId, startDay, endDay));
    }
}
