/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.models.EntryHistory;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
public class AdminEntryHistoryController {
    @Autowired
    private EntryHistoryService entryService;
    
    @Value("${page_size_history}")
    private int pageSize;
    
    @GetMapping("/history")
    public String historyList(Model model, @RequestParam Map<String, String> params){
        int page = Integer.parseInt(params.get("page"));      
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());
        
        String name = params.get("name");
        Page<EntryHistory> entryList = this.entryService.findAllByName(pageable, name);
        model.addAttribute("history", entryList.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", entryList.getTotalPages());
        return "history";
    }
}
