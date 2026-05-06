/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

/**
 *
 * @author Amanda
 */

import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.repository.GymClassRepository;
import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GymClassService {

    private final GymClassRepository gymClassRepository;

    public GymClassService(GymClassRepository gymClassRepository) {
        this.gymClassRepository = gymClassRepository;
    }

    public List<GymClass> getAllClasses() {
        return gymClassRepository.findAll();
    }

    public GymClass getClassById(int idClass) {
        return gymClassRepository.findById(idClass).orElse(null);
    }

    public GymClass saveClass(GymClass gymClass) {
        calculateDuration(gymClass);
        gymClass.setStatus(true);
        return gymClassRepository.save(gymClass);
    }

    public GymClass updateClass(int idClass, GymClass gymClass) {
        gymClass.setIdClass(idClass);
        calculateDuration(gymClass);
        return gymClassRepository.save(gymClass);
    }

    public void deleteClass(int idClass) {
        gymClassRepository.deleteById(idClass);
    }

    private void calculateDuration(GymClass gymClass) {
        int duration = (int) Duration.between(
                gymClass.getStartTime(),
                gymClass.getEndTime()
        ).toMinutes();

        gymClass.setDuration(duration);
    }
}
