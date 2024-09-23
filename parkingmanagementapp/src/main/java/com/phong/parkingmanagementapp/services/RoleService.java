/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Role;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface RoleService {
    List<Role> findAll();
    Role getRoleById(int id);
}
