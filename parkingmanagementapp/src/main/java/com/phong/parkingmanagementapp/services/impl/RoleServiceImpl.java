/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.models.Role;
import com.phong.parkingmanagementapp.repositories.RoleRepository;
import com.phong.parkingmanagementapp.services.RoleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepo;
    
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepo){
        this.roleRepo = roleRepo;
    }

    @Override
    public List<Role> findAll() {
        return this.roleRepo.findAll();
    }

    @Override
    public Role getRoleById(int id) {
        return this.roleRepo.getRoleById(id);
    }
    
}
