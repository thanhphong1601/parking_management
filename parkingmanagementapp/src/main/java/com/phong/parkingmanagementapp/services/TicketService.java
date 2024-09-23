/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Ticket;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface TicketService {
    List<Ticket> findAll();
    List<Ticket> findTicketByUserOwned(String name);
    void addOrUpdate(Ticket ticket);
    boolean delete(Ticket ticket);
    Ticket getTicketById(int id);
    List<Ticket> findTicketByUserOwnedId(int id);
    boolean deleteTicketsByUserOwnedId(int id);
}
