/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Entity
@Data
@Table(name = "position")
@NoArgsConstructor
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "position_number")
    private int position;

    //position's status indicating whether its taken or not
    @Column(name = "take")
    private Boolean take = false;

    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "line_id")
    @ManyToOne(optional = true)
    private Line line;

    @Column(name = "is_deleted")
    @Basic(optional = true)
    private Boolean isDeleted = false;

    @Column(name = "parking_status")
    @Enumerated(EnumType.STRING)
    private PositionStatusEnum status;

    @Column(name = "current_ticket_id")
    private Long currentTicketId;
    
    @Basic(optional = true)
    @Column(name = "plate_img_url")
    private String plateImgUrl;
    
    @Transient
    private MultipartFile file;

}
