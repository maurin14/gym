/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Schedule;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
 @RequestMapping("/schedule")
public class ScheduleController {
  
    @GetMapping("/sche")
    public String page(Model model) {
      
        return "schedule/indexSchedule";
    }
 @Autowired
    private ScheduleService scheduleServices;

    @Autowired
    private BranchService branchServices;

   

 @GetMapping
   public String listSchedules(
        @RequestParam(required = false) Integer branchId,
        @RequestParam(required = false) String start,
        @RequestParam(required = false) String end,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model, HttpSession session,
        @RequestHeader(value = "X-Requested-With", required = false) String ajax) {

    Page<Schedule> data;

    if (branchId != null) {
       
 data = scheduleServices.getbyId(branchId, PageRequest.of(page, size));
    } else if (start != null && end != null) {
     data = scheduleServices.findByBranchIdAndStartTimeBetween(
    LocalTime.parse(start),
    LocalTime.parse(end),
    PageRequest.of(page, size)
);

    } else {
       data = scheduleServices.getAll(PageRequest.of(page, size));
    } 

    model.addAttribute("schedules", data.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", data.getTotalPages());
    User user = (User) session.getAttribute("user");

boolean isAdmin = false;

if (user != null) {
    isAdmin = "administrator".equals(user.getRole());
}

model.addAttribute("isAdmin", isAdmin);

    if ("XMLHttpRequest".equals(ajax)) {
      return "schedule/listSchedule :: tableContainer";
    }

    return "schedule/listSchedule";
}


    @GetMapping("/add")
    public String addForm(Model model) {
       
        Schedule schedule = new Schedule();

        schedule.setBranch(new Branch());

        var branches = branchServices.getAll();
        if (branches == null) {
            branches = new ArrayList<>();
        }

        model.addAttribute("schedule", schedule);
        model.addAttribute("branches", branches);

        return "schedule/formSchedule";
    }


    @PostMapping("/save")
    @ResponseBody
    public String save(@ModelAttribute Schedule schedule) {

   
        if (schedule.getEndTime().isBefore(schedule.getStartTime())) {
            return "error";
        }

        if (schedule.getId() == 0) {
            scheduleServices.save(schedule);
        } else {
            scheduleServices.update(schedule.getId(), schedule);
        }

        return "ok";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") int id, Model model) {

        Schedule schedule = scheduleServices.getById(id);

        if (schedule == null) {
            return "redirect:/listSchedule";
        }

        model.addAttribute("schedule", schedule);
        model.addAttribute("branches", branchServices.getAll());

        return "schedule/formSchedule";
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    
    public String delete(@PathVariable("id") int id) {
        scheduleServices.delete(id);
        return "ok";
    }
}