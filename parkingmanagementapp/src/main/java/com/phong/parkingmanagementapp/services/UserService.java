/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.User;
import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Admin
 */
public interface UserService {
    User getUserById(int id);
    List<User> findUsersByRoleId(@Param("role") int role);
    User getUserByName(String name);
    List<User> findUserByIdentityNumberOrNameOrRole(String identityNumber, String name, int role);
    UserDetails loadUserByUsername(String username);
    User saveUser(User user);
    void deleteUser(User user);
    List<User> findAll();
    List<User> findAllExceptBlankUser();
    User getUserByUsername(String username);
    boolean authUser(String username, String password);
    void deactivateUser(int id);
}
