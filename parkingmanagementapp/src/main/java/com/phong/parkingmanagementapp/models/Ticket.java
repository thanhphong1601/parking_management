/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Admin
 */
@Entity
@Data
@Table(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
//    @Id
//    @Basic(optional = false)
//    @Column(name = "id")
//    private String id;
    
    @Basic(optional = true)
    @Column(name = "ticket_id")
    private String ticketId;
    
    //không nhất thiết chọn tầng -> chỉ bắt buộc khi đăng ký vé -> xét ở FE và controller
    @ToString.Exclude
    @JoinColumn(name = "floor_id")
    @ManyToOne(optional = true)
    private Floor floor;
    @ToString.Exclude
    @JoinColumn(name = "line_id")
    @ManyToOne(optional = true)
    private Line line;
    @ToString.Exclude
    @JoinColumn(name = "position_id")
    @ManyToOne(optional = true)
    private Position position;
    //
    
    @ToString.Exclude
    @JoinColumn(name = "ticket_price_id")
    @ManyToOne(optional = false)
    private TicketPrice price;

    @ToString.Exclude
    @JoinColumn(name = "user_id_create")
    @ManyToOne(optional = false)
    private User userCreate;

    @ToString.Exclude
    @JoinColumn(name = "user_id_owned")
    @ManyToOne(optional = false)
    private User userOwned;
    
    @ToString.Exclude
    @JoinColumn(name = "vehicle_id")
    @ManyToOne(optional = true)
    private Vehicle vehicle;
    
    @Column(name = "start_day", nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDay;
    
    @Column(name = "end_day", nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDay;
    
    @Column(name = "total_price", nullable = true)
    private Integer totalPrice;
    
    @Column(name = "is_paid", nullable = true)
    private Boolean isPaid = false;
    
    @JoinColumn(name = "ticket_type")
    @ManyToOne(optional = true)
    private TicketType ticketType;
    
    @Column(name = "license_number", nullable = true)
    private String licenseNumber;
    
    @Column(name = "active", nullable = true)
    private Boolean active;

//    @JsonIgnore
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ticket")
//    private Collection<Report> reportCollection;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ticket")
    private Collection<EntryHistory> entryCollection;
    
    
    @Override
    public String toString() {
        return "Ticket{id=" + id + ", price=" + price + ", userOwned=" + (userOwned != null ? userOwned.getId() : null)
                + ", userCreate=" + (userCreate != null ? userCreate.getId() : null) + '}';
    }
}
