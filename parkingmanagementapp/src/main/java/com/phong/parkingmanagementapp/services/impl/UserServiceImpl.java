/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.repositories.VehicleRepository;
import com.phong.parkingmanagementapp.services.CloudinaryService;
import com.phong.parkingmanagementapp.services.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final VehicleRepository vehicleRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Autowired
    public UserServiceImpl(UserRepository userRepo, VehicleRepository vehicleRepo) {
        this.userRepo = userRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public User getUserById(int id) {
        return this.userRepo.getUserById(id);
    }

    @Override
    public List<User> findUsersByRoleId(int role) {
        return this.userRepo.findUsersByRoleId(role);
    }

    @Override
    public User getUserByName(String name) {
        return this.userRepo.getUserByName(name);
    }

    @Override
    public List<User> findUserByIdentityNumberOrNameOrRole(String identityNumber, String name, int role) {
        return this.userRepo.findUserByIdentityNumberOrNameOrRole(identityNumber, name, role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }

    @Override
    public User saveUser(User user) {
//        if (!user.getFile().isEmpty() && user.getAvatar() == null) {
//            try {
//                //            try {
////                Map res = this.cloudinary.uploader().upload(user.getFile().getBytes(), ObjectUtils.asMap("resource_type", "auto"));
////                user.setAvatar(res.get("secure_url").toString());
////
////                
////            } catch (IOException ex) {
////                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
////            }
//                String imageUrl = this.cloudinaryService.uploadImage(user.getFile());
//                user.setAvatar(imageUrl);
//            } catch (IOException ex) {
//                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        if (user.getId() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setActive(Boolean.TRUE);
            return this.userRepo.save(user);
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User u = new User();
        u.setUsername(user.getUsername());
        u.setPassword(encodedPassword);
        u.setEmail(user.getEmail());
        u.setActive(Boolean.TRUE);
        u.setAddress(user.getAddress());
        u.setBirthday(u.getBirthday());
        u.setName(user.getName());
        u.setPhone(user.getPhone());
        u.setRole(user.getRole());
        u.setIdentityNumber(user.getIdentityNumber());
        
        if (!user.getFile().isEmpty() && user.getAvatar() == null)
            u.setAvatar(user.getAvatar());

        
        return userRepo.save(u);
    }

    @Override
    public void deleteUser(User user) {
        if (user == null)
            return;
        
        this.userRepo.delete(user);
    }

    @Override
    public List<User> findAll() {
        return this.userRepo.findAll();
    }

    @Override
    public List<User> findAllExceptBlankUser() {
        return this.userRepo.findAllExceptBlankUser();
    }

}
