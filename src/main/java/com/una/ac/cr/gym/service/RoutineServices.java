/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.repository.RoutineRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author alira
 */
@Service
public class RoutineServices {

    @Autowired
    private RoutineRepository routineRepository;

    public List<Routine> getRoutines() {
        return routineRepository.findAll();
    }

    public Routine getRoutine(int idRoutine) {
        if (idRoutine <= 0) {
            return null;
        }
        return routineRepository.findById(idRoutine).orElse(null);
    }

    public String saveRoutine(Routine routine) {
        String validation = validateRoutine(routine);

        if (!validation.isEmpty()) {
            return validation;
        }

        routineRepository.save(routine);
        return "";
    }

    public String deleteRoutine(int idRoutine) {
        if (idRoutine <= 0) {
            return "ID de rutina inválido";
        }

        Routine routine = getRoutine(idRoutine);

        if (routine == null) {
            return "Rutina no encontrada";
        }

        // Eliminación lógica
        routine.setState(false);
        routineRepository.save(routine);

        return "";
    }

    private String validateRoutine(Routine routine) {

        if (routine == null) {
            return "La rutina es nula";
        }

        if (routine.getNameRoutine() == null || routine.getNameRoutine().isEmpty()) {
            return "El nombre de la rutina es obligatorio";
        }

        if (routine.getDifficultyLevel() == null || routine.getDifficultyLevel().isEmpty()) {
            return "Debe seleccionar un nivel de dificultad";
        }

        if (routine.getRoutineType() == null || routine.getRoutineType().isEmpty()) {
            return "Debe seleccionar un tipo de rutina";
        }

        if (routine.getEstimatedDuration() <= 0) {
            return "La duración estimada debe ser mayor a 0";
        }

        if (routine.getQuantityExercises() <= 0) {
            return "La cantidad de ejercicios debe ser mayor a 0";
        }

        if (routine.getExercises() == null || routine.getExercises().isEmpty()) {
            return "Debe ingresar la descripción de los ejercicios";
        }

        return "";
    }
    
    public Page<Routine> getFilteredRoutines(String difficultyLevel, String routineType, int page) {

        Pageable pageable = PageRequest.of(page, 5);

        if ((difficultyLevel == null || difficultyLevel.isEmpty()) &&
            (routineType == null || routineType.isEmpty())) {
            return routineRepository.findAll(pageable);
        }

        if (difficultyLevel != null && !difficultyLevel.isEmpty() &&
            routineType != null && !routineType.isEmpty()) {
            return routineRepository.findByDifficultyLevelAndRoutineType(difficultyLevel, routineType, pageable);
        }

        if (difficultyLevel != null && !difficultyLevel.isEmpty()) {
            return routineRepository.findByDifficultyLevel(difficultyLevel, pageable);
        }

        return routineRepository.findByRoutineType(routineType, pageable);
    }

    public Page<Routine> getClientRoutines(String difficultyLevel, String routineType, Pageable pageable) {
        if (difficultyLevel == null) {
            difficultyLevel = "";
        }

        if (routineType == null) {
            routineType = "";
        }

        return routineRepository.findActiveRoutinesForClient(
                difficultyLevel.trim(),
                routineType.trim(),
                pageable
        );
    }
}
