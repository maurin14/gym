package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.repository.RoutineRepository;
import com.una.ac.cr.gym.repository.RoutineUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class RoutineServices {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineUserRepository routineUserRepository;

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

    @Transactional
    public String deleteRoutine(int idRoutine) {
        if (idRoutine <= 0) {
            return "ID de rutina invalido";
        }

        if (!routineRepository.existsById(idRoutine)) {
            return "Rutina no encontrada";
        }

        try {
            routineUserRepository.deleteByIdRoutine(idRoutine);
            routineRepository.deleteById(idRoutine);
            routineRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "No se puede eliminar la rutina porque tiene registros relacionados.";
        }

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
            return "La duracion estimada debe ser mayor a 0";
        }

        if (routine.getQuantityExercises() <= 0) {
            return "La cantidad de ejercicios debe ser mayor a 0";
        }

        if (routine.getExercises() == null || routine.getExercises().isEmpty()) {
            return "Debe ingresar la descripcion de los ejercicios";
        }

        return "";
    }

    public Page<Routine> getFilteredRoutines(String difficultyLevel, String routineType, int page) {
        Pageable pageable = PageRequest.of(page, 5);

        if ((difficultyLevel == null || difficultyLevel.isEmpty())
                && (routineType == null || routineType.isEmpty())) {
            return routineRepository.findAll(pageable);
        }

        if (difficultyLevel != null && !difficultyLevel.isEmpty()
                && routineType != null && !routineType.isEmpty()) {
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
