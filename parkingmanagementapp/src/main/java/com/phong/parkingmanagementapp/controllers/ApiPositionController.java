/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.services.CloudinaryService;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.utils.DownloadImageAsMultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
public class ApiPositionController {

    @Autowired
    private PositionService poService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private EntryHistoryService entryService;

    @GetMapping("/position/list")
    public ResponseEntity<?> getLinesByFloorId(@RequestParam(value = "id", required = false) Integer id) {
        if (id == null) {
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(this.poService.findPositionsByLineId(id));
    }

    @GetMapping("/position/list/all")
    public ResponseEntity<?> getAllPositions() {
        return ResponseEntity.ok(this.poService.findAll());
    }

    @GetMapping("/ticket/{ticketId}/position/get")
    public ResponseEntity<?> getPositionByTicketId(@PathVariable("ticketId") String ticketId) {
        Ticket currentTicket = this.ticketService.findByTicketId(ticketId);
        if (currentTicket == null) {
            return new ResponseEntity<>("Id vé không hợp lệ", HttpStatus.NOT_FOUND);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("floor", currentTicket.getFloor());
        result.put("line", currentTicket.getLine());
        result.put("position", currentTicket.getPosition());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/position/{positionId}/updateState/in")
    public ResponseEntity<?> updateParkingPositionStateWhenVehicleIn(@RequestParam() Map<String, String> params,
            @PathVariable(value = "positionId") int positionId) {

        Ticket currentTicket = this.ticketService.findByTicketId(params.get("ticketId"));
        if (currentTicket == null) {
            return new ResponseEntity<>("Không tìm thấy vé", HttpStatus.NOT_FOUND);
        }

        String inImgUrl = this.entryService.getEntryHistoryByTicketId(currentTicket.getId()).getPlateImgIn();

        Position currentPosition = this.poService.getPositionById(positionId);
        if (currentPosition == null) {
            return new ResponseEntity<>("Không tìm thấy vị trí hợp lệ", HttpStatus.NOT_FOUND);
        }
        currentPosition.setPlateImgUrl(inImgUrl);
        this.poService.savePosition(currentPosition);

        this.poService.assignPositionToTicket(currentTicket.getId(), positionId);

        return new ResponseEntity<>("Cập nhật trạng thái vị trí thành công", HttpStatus.OK);
    }

    @PostMapping("/position/{positionId}/updateState/out")
    public ResponseEntity<?> updateParkingPositionStateWhenVehicleOut(
            @PathVariable(value = "positionId") int positionId) {
        Position currentPosition = this.poService.getPositionById(positionId);
        if (currentPosition == null) {
            return new ResponseEntity<>("Không tìm thấy vị trí hợp lệ", HttpStatus.NOT_FOUND);
        }

        this.poService.emptyTakenPosition(positionId);

        return new ResponseEntity<>("Cập nhật trạng thái vị trí thành công", HttpStatus.OK);
    }

    @PostMapping("/position/{positionId}/plate/check")
    public ResponseEntity<?> checkLicensePlateFromInput(@PathVariable("positionId") int positionId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, Exception {
        Position currentPosition = this.poService.getPositionById(positionId);
        if (currentPosition == null) {
            return new ResponseEntity<>("Không tìm thấy vị trí hợp lệ", HttpStatus.NOT_FOUND);
        }

        DownloadImageAsMultipartFile downloadFile = new DownloadImageAsMultipartFile();
        String plateImgInUrl = currentPosition.getPlateImgUrl();
        MultipartFile plateImgInUrlFile = downloadFile.downloadImageAsMultipartFile(plateImgInUrl);

        Boolean comparingResult = this.entryService.processComparePlates(plateImgInUrlFile, file);
        
        

        return new ResponseEntity<>(comparingResult, HttpStatus.OK);
    }

}
