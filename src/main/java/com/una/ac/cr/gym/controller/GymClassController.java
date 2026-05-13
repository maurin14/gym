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
import java.util.Map;
import java.util.HashMap;

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
    public List<java.util.Map<String, Object>> getAllClasses() {

        return gymClassService.getAllClasses()
                .stream()
                .map(gymClass -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();

                    map.put("idClass", gymClass.getIdClass());
                    map.put("classType", gymClass.getClassType());
                    map.put("classDate", gymClass.getClassDate());
                    map.put("startTime", gymClass.getStartTime());
                    map.put("endTime", gymClass.getEndTime());
                    map.put("maxCapacity", gymClass.getMaxCapacity());
                    map.put("enrolledCount", gymClass.getEnrolledCount());
                    map.put("difficultyLevel", gymClass.getDifficultyLevel());
                    map.put("description", gymClass.getDescription());
                    map.put("duration", gymClass.getDuration());
                    map.put("status", gymClass.isStatus());

                    if (gymClass.getTrainer() != null) {
                        map.put("trainerName", gymClass.getTrainer().getFullName());
                    } else {
                        map.put("trainerName", "Sin entrenador");
                    }

                    return map;
                })
                .toList();
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