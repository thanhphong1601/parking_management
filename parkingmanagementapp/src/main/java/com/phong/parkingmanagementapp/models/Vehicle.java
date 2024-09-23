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
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Entity
@Data
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @NotNull
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    //other class
    @JoinColumn(name = "type_id")
    @ManyToOne(optional = false)
    private VehicleType type;
    @NotNull
    @Basic(optional = false)
    @Column(name = "plate_license")
    private String plateLicense;
    
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = true)
    private User user;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicle")
    private Collection<EntryHistory> entryHistoryCollection;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicle")
    private Collection<Ticket> ticketCollection;
        
    @Override
    public String toString(){
        return "Vehicle: " + id + ", name= " + name + '\'' + '}';
    }
}
