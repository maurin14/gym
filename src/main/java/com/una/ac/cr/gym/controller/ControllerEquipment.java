/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Equipment;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.EquipmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author PC
 */
@Controller
public class ControllerEquipment {
     @GetMapping("/eq")
    public String page(Model model) {
      
        return "equipment/indexEquipment";
    }
   @org.springframework.beans.factory.annotation.Autowired
    private EquipmentService equipmentService;

   @org.springframework.beans.factory.annotation.Autowired
    private BranchService branchServices;
   @GetMapping("/equip")
public String equipment(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Integer id,
        @RequestParam(required = false) Double min,
        @RequestParam(required = false) Double max,
        Model model, HttpSession session,
        @RequestHeader(value = "X-Requested-With", required = false) String ajax) {

    Page<Equipment> data;

    if (id != null) {
        data = equipmentService.findByBranchId(id, PageRequest.of(page, size));
    } else if (min != null && max != null) {
        data = equipmentService.findByCostBetween(min, max, PageRequest.of(page, size));
    } else {
        data = equipmentService.getAll(PageRequest.of(page, size));
    }

    model.addAttribute("equipment", data.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", data.getTotalPages());
    
    User user = (User) session.getAttribute("user");

boolean isAdmin = false;

if (user != null) {
    isAdmin = "administrator".equals(user.getRole());
}

model.addAttribute("isAdmin", isAdmin);

    if ("XMLHttpRequest".equals(ajax)) {
       return "equipment/listEquipment :: tableContent";
    }

    return "equipment/listEquipment";
}

     @GetMapping("/add")
    public String fromEquipment(Model model){
        Equipment e = new Equipment();
        e.setBranch(new Branch());
        model.addAttribute("newEquipment", e);
        model.addAttribute("branches", branchServices.getAll()); 
        
        return("equipment/formEquipment");
    }
     
     @PostMapping("/save")
@ResponseBody
public String save(Equipment newEquipment){

    if (newEquipment.getId() == 0) {
        equipmentService.save(newEquipment);
    } else {
        equipmentService.update(newEquipment.getId(), newEquipment);
    }

    return "ok";
}
    
  @GetMapping("/edit/{id}")
public String editEquipment(@PathVariable("id") int id, Model model) {

    Equipment e = equipmentService.getById(id);

    if (e.getBranch() == null) {
        e.setBranch(new Branch());
    }

    model.addAttribute("newEquipment", e);
    model.addAttribute("branches", branchServices.getAll());

    return "equipment/formEquipment";
}
  @GetMapping("/delete/{id}")
public String delete(@PathVariable("id") int id){

    equipmentService.delete(id);

    return "ok";
}
    
}
