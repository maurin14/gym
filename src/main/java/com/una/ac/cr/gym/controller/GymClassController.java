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
import com.una.ac.cr.gym.service.GymClassService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classes")
public class GymClassController {

    private final GymClassService gymClassService;

    public GymClassController(GymClassService gymClassService) {
        this.gymClassService = gymClassService;
    }

    @GetMapping
    public List<GymClass> getAllClasses() {
        return gymClassService.getAllClasses();
    }

    @GetMapping("/{idClass}")
    public GymClass getClassById(@PathVariable int idClass) {
        return gymClassService.getClassById(idClass);
    }

    @PostMapping
    public GymClass saveClass(@RequestBody GymClass gymClass) {
        return gymClassService.saveClass(gymClass);
    }

    @PutMapping("/{idClass}")
    public GymClass updateClass(@PathVariable int idClass, @RequestBody GymClass gymClass) {
        return gymClassService.updateClass(idClass, gymClass);
    }

    @DeleteMapping("/{idClass}")
    public void deleteClass(@PathVariable int idClass) {
        gymClassService.deleteClass(idClass);
    }
}
