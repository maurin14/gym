/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.controller;

/**
 *
 * @author Amanda
 */

import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.GymClassService;
import com.una.ac.cr.gym.service.UserService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GymClassController {

    private final GymClassService gymClassService;
    private final UserService userService;

    public GymClassController(GymClassService gymClassService, UserService userService) {
        this.gymClassService = gymClassService;
        this.userService = userService;
    }

    @GetMapping("/trainer/classes")
    public String trainerClassesList() {
        return "trainer/classes/list";
    }

    @GetMapping("/trainer/classes/form")
    public String trainerClassesForm() {
        return "trainer/classes/form";
    }

    @GetMapping("/trainer/classes/form/{idClass}")
    public String trainerClassesEdit(@PathVariable int idClass) {
        return "trainer/classes/form";
    }


    @GetMapping("/client/classes")
    public String clientClassesList() {
        return "client/classes/list";
    }

    @ResponseBody
    @GetMapping("/classes")
    public List<GymClass> getAllClasses() {
        return gymClassService.getAllClasses();
    }

    @ResponseBody
    @GetMapping("/classes/{idClass}")
    public GymClass getClassById(@PathVariable int idClass) {
        return gymClassService.getClassById(idClass);
    }

    @ResponseBody
    @PostMapping("/classes")
    public GymClass saveClass(@RequestBody GymClass gymClass) {
        return gymClassService.saveClass(gymClass);
    }

    @ResponseBody
    @PutMapping("/classes/{idClass}")
    public GymClass updateClass(@PathVariable int idClass, @RequestBody GymClass gymClass) {
        return gymClassService.updateClass(idClass, gymClass);
    }

    @ResponseBody
    @DeleteMapping("/classes/{idClass}")
    public void deleteClass(@PathVariable int idClass) {
        gymClassService.deleteClass(idClass);
    }

    @ResponseBody
    @GetMapping("/classes/trainers")
    public List<User> getTrainers() {
        return userService.filterUsers(null, "trainer");
    }
}