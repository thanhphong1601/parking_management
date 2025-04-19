/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.models;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Entity
@Data
@Table(name = "entry_history")
@NoArgsConstructor
@AllArgsConstructor
public class EntryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "time_in", nullable = true)
    private Date timeIn;
    @Basic(optional = true)
    @Column(name = "plate_img_in")
    private String plateImgIn;
    
    @Column(name = "time_out", nullable = true)
    private Date timeOut;
    @Basic(optional = true)
    @Column(name = "plate_img_out")
    private String plateImgOut;
    
    @Basic(optional = true)
    @Column(name = "person_img_in")
    private String personImgIn;
    
    @Basic(optional = true)
    @Column(name = "person_img_out")
    private String personImgOut;
    
    //khach hang
    @JoinColumn(name = "vehicle_owner_id")
    @ManyToOne()
    @NotNull(message = "{entry.user.notNull}")
    private User owner;
    
    //nhân viên
    @JoinColumn(name = "creator_id")
    @ManyToOne()
    @NotNull(message = "{entry.user.notNull}")
    private User creator;
    
    @JoinColumn(name = "vehicle_id")
    @ManyToOne()
    @NotNull(message = "{entry.vehicle.notNull}")
    private Vehicle vehicle;
    
    @JoinColumn(name = "ticket_id")
    @ManyToOne()
    private Ticket ticket;
    
    @Transient
    private MultipartFile file;
}
