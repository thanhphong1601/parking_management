/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.TicketType;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface TicketTypeService {
    TicketType getNormalType();
    TicketType getVIPType();
    TicketType getMonthType();
    List<TicketType> getAllTypes();
    TicketType getTicketTypeById(int typeId);

}
