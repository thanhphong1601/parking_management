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
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
//        User user = userRepo.getUserByUsernameOrEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                user.getAuthorities()
//        );
        return userRepo.getUserByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username/email: " + username));
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
            User currentUser = userRepo.getUserById(user.getId());
            if (currentUser == null) {
                throw new EntityNotFoundException("User not found");
            }

            currentUser.setUsername(
                    Objects.requireNonNullElse(user.getUsername(), currentUser.getUsername())
            );
            currentUser.setPhone(
                    Objects.requireNonNullElse(user.getPhone(), currentUser.getPhone())
            );
            currentUser.setAddress(
                    Objects.requireNonNullElse(user.getAddress(), currentUser.getAddress())
            );

            if (user.getBirthday() != null) {
                currentUser.setBirthday(user.getBirthday());
            }

            if (user.getRole() != null && !user.getRole().equals(currentUser.getRole())) {
                currentUser.setRole(user.getRole());
            }

            
            return this.userRepo.save(currentUser);
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
        u.setBirthday(user.getBirthday());
        u.setName(user.getName());
        u.setPhone(user.getPhone());
        u.setRole(user.getRole());
        u.setIdentityNumber(user.getIdentityNumber());

        if (user.getFile() == null) {
            return userRepo.save(u);
        }

        if (!user.getFile().isEmpty() && user.getAvatar() == null) {
            u.setAvatar(user.getAvatar());
        }

        return userRepo.save(u);
    }

    @Override
    public void deleteUser(User user) {
        if (user == null) {
            return;
        }

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

    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public boolean authUser(String username, String password) {
        User u = this.userRepo.getUserByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));


//        System.out.println(u.getPassword());
//        System.out.println(password);
//        System.out.println(this.passwordEncoder.encode(password));
//        System.out.println(this.passwordEncoder.matches(password, u.getPassword()));
//        
        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public void deactivateUser(int id) {
        User u = this.userRepo.getUserById(id);
        u.setActive(Boolean.FALSE);

        this.userRepo.save(u);
    }

    @Override
    public Page<User> findUserByIdentityNumberOrNameOrRolePageable(String identityNumber, String name, int role, Pageable pageable) {
        return this.userRepo.findUserByIdentityNumberOrNameOrRolePageable(identityNumber, name, role, pageable);
    }

    @Override
    public User getAnonymousUser() {
        return this.userRepo.getAnonymousUser();
    }

    @Override
    public User findByEmail(String email) {
        return this.userRepo.findByEmail(email);
    }

    @Override
    public User findByIdentityNumber(String identityNumber) {
        return this.userRepo.findByIdentityNumber(identityNumber);
    }

    @Override
    public int getAdminId() {
        return this.userRepo.getAdminId();
    }

    @Override
    public Optional<User> getUserByUsernameOrEmail(String username) {
        return this.userRepo.getUserByUsernameOrEmail(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userRepo.existsByUsername(username);
    }

    @Override
    public void save(User u) {
        this.userRepo.save(u);
    }

    @Override
    public long countCustomers() {
        return this.userRepo.countCustomers();
    }

}
