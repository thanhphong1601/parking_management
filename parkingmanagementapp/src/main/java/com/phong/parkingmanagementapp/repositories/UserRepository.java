/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.repositories;

import com.phong.parkingmanagementapp.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    //find by id, find all, find by role, 
    public User getUserById(int id);

    @Query("SELECT u FROM User u WHERE u.role.id = :role")
    List<User> findUsersByRoleId(@Param("role") int role);

    public User getUserByName(String name);

    @Query("SELECT u FROM User u WHERE (:idNum IS NULL OR u.identityNumber LIKE %:idNum%) AND (:name IS NULL OR u.name LIKE %:name%) AND (:role IS NULL OR u.role.id = :role)")
    public List<User> findUserByIdentityNumberOrNameOrRole(@Param("idNum") String identityNumber, @Param("name") String name, @Param("role") int role);

    Optional<User> findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    boolean existsByUsername(String username);

    boolean existsByIdentityNumber(String identityNumber);
    
    @Override
    List<User> findAll();
    
    @Query(value = "SELECT u FROM User u WHERE u.name != 'BlankUser'")
    List<User> findAllExceptBlankUser();
    
    User getUserByUsername(String username);
    
}
