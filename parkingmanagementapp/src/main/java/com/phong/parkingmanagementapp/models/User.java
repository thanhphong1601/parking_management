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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Entity
@Data
@Table(name = "user")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
    @Column(name = "identity_number", unique = true)
    @NotNull
    private String identityNumber;
    @Column(name = "phone")
    private String phone;
    @Column(name = "address")
    @Basic(optional = true)
    private String address;
    @Column(name = "birthday")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    @Size(max = 50, message = "{user.email.lenghtErr}")
    @NotEmpty(message = "{user.email.nullErr}")
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "username", unique = true)
    private String username;
    @Basic(optional = false)
    @NotNull(message = "{user.pass.nullErr}")
    @Size(min = 1, max = 100, message = "{user.pass.lenghtErr}")
    @Column(name = "password")
    private String password;

    @ToString.Exclude
    @JoinColumn(name = "role_id")
    @ManyToOne(optional = false)
    private Role role;

    @Basic(optional = true)
    @Column(name = "avatar")
    private String avatar;

    @Transient
    private MultipartFile file;

    @Column(name = "active")
    private Boolean active = false;
    
    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Otp otp;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userCreate")
    private Collection<Ticket> ticketCreateCollection;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userOwned")
    private Collection<Ticket> ticketOwnedCollection;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Vehicle> vehicleCollection;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private Collection<EntryHistory> entryHistoryCollection;

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + '\'' + '}';
    }
    
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.role.getRole());
        authorities.add(authority);
        return authorities;
    }
}
