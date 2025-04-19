/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Line;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.PositionStatusEnum;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.repositories.FloorRepository;
import com.phong.parkingmanagementapp.repositories.LineRepository;
import com.phong.parkingmanagementapp.repositories.PositionRepository;
import com.phong.parkingmanagementapp.services.FloorService;
import com.phong.parkingmanagementapp.services.LineService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.TicketService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepo;
    private final LineService lineService;
    private final FloorService floorService;
    private final TicketService ticketService;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepo,
            LineService lineService, FloorService floorService,
            TicketService ticketService) {
        this.positionRepo = positionRepo;
        this.lineService = lineService;
        this.floorService = floorService;
        this.ticketService = ticketService;
    }

    @Override
    public List<Position> findAll() {
        return this.positionRepo.findAll();
    }

    @Override
    public List<Position> findPositionsByLineId(int id) {
        return this.positionRepo.findPositionsByLineId(id);
    }

    @Override
    public void savePosition(Position position) {
        this.positionRepo.save(position);
    }

    @Override
    public Position getPositionById(int id) {
        return this.positionRepo.getPositionById(id);
    }

    @Override
    public void UpdateStatus(int positionId, Boolean status) {
        Position pos = this.positionRepo.getPositionById(positionId);
        pos.setTake(status);

        this.positionRepo.save(pos);
    }

    @Override
    public boolean deletePosition(int positionId) {
        Position currentPosition = this.positionRepo.getPositionById(positionId);

        if (currentPosition == null) {
            return false;
        }

        currentPosition.setIsDeleted(Boolean.TRUE);
        currentPosition.setTake(Boolean.TRUE);
        this.positionRepo.save(currentPosition);

        this.lineService.checkLineStatus(currentPosition.getLine().getId());

        this.floorService.checkStatus(currentPosition.getLine().getFloor().getId());

        return true;
    }

    @Override
    public int countPositionByLineId(int lineId) {
        return this.positionRepo.countPositionByLineId(lineId);
    }

    @Override
    public Boolean existsByStatus(int lineId) {
        return this.positionRepo.existsByStatus(lineId);
    }

    @Override
    public List<Position> findFirstAvailablePositionOrdered() {
        return this.positionRepo.findFirstAvailablePositionOrdered();
    }

    @Override
    public void assignPositionToTicket(int ticketId, int positionId) {
        Position currentPosition = this.positionRepo.getPositionById(positionId);
        if (currentPosition != null) {
            currentPosition.setStatus(PositionStatusEnum.OCCUPIED);
            currentPosition.setCurrentTicketId((long) ticketId);
            currentPosition.setTake(true); // đồng bộ trạng thái vé đã đăng ký

            this.positionRepo.save(currentPosition);

            this.lineService.checkLineStatus(currentPosition.getLine().getId());

            this.floorService.checkStatus(currentPosition.getLine().getFloor().getId());
        }

        Ticket currentTicket = this.ticketService.getTicketById(ticketId);
        currentTicket.setPosition(currentPosition);
        currentTicket.setLine(currentPosition.getLine());
        currentTicket.setFloor(currentPosition.getLine().getFloor());

        this.ticketService.addOrUpdate(currentTicket);
    }
    
    //xử lý chức năng gán vị trí vào vé, xử lý mô hình bãi xe ở frontend
    //bấm vào 1 vị trí và nhập id vé => gửi id vé và id position dùng hàm assignPositionToTicket()
    //kiểm tra full trc khi gọi hàm trên

    @Override
    public void emptyTakenPosition(int positionId) {
        Position currentPosition = this.positionRepo.getPositionById(positionId);
        if (currentPosition != null){
            //update ticket's license field for further uses
            Ticket currentTicket = this.ticketService.getTicketById(Integer.parseInt(
                    currentPosition.getCurrentTicketId().toString()));
            currentTicket.setLicenseNumber("0");
            this.ticketService.addOrUpdate(currentTicket);
            
            currentPosition.setStatus(PositionStatusEnum.AVAILABLE);
            currentPosition.setCurrentTicketId(null);
            currentPosition.setTake(Boolean.FALSE);
            currentPosition.setPlateImgUrl(null);
            
            this.positionRepo.save(currentPosition);

            this.lineService.checkLineStatus(currentPosition.getLine().getId());

            this.floorService.checkStatus(currentPosition.getLine().getFloor().getId());
        }
    }

}
