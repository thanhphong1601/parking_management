/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.dtos;

import com.phong.parkingmanagementapp.models.Vehicle;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Admin
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class TicketDTO {
    private int ticketId;
    private String userOwnedName;
    private int floorNumber;
    private String line;
    private int position;
    private int ticketPrice;
    private String vehicleName;
    private String vehicleLicensePlate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDay;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDay;
    
    private Boolean isPaid;
    private Integer totalPrice;
    
}
