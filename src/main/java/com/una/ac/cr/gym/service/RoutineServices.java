package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.repository.RoutineRepository;
import com.una.ac.cr.gym.repository.RoutineUserRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, String> errors = validateFields(routine);

        if (!errors.isEmpty()) {
            return firstError(errors);
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

    public Map<String, String> validateFields(Routine routine) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (routine == null) {
            errors.put("form", "No se pudo guardar. Revise los campos marcados.");
            return errors;
        }

        if (isBlank(routine.getNameRoutine())) {
            errors.put("nameRoutine", "Este campo es obligatorio.");
        }

        if (isBlank(routine.getDifficultyLevel())) {
            errors.put("difficultyLevel", "Seleccione una opcion.");
        }

        if (isBlank(routine.getRoutineType())) {
            errors.put("routineType", "Seleccione una opcion.");
        }

        if (routine.getEstimatedDuration() == null) {
            errors.put("estimatedDuration", "Este campo es obligatorio.");
        } else if (routine.getEstimatedDuration() <= 0) {
            errors.put("estimatedDuration", "Ingrese un valor valido.");
        }

        if (routine.getQuantityExercises() == null) {
            errors.put("quantityExercises", "Este campo es obligatorio.");
        } else if (routine.getQuantityExercises() <= 0) {
            errors.put("quantityExercises", "Ingrese un valor valido.");
        }

        if (isBlank(routine.getTrainingObjective())) {
            errors.put("trainingObjective", "Seleccione una opcion.");
        }

        if (isBlank(routine.getDescription())) {
            errors.put("description", "Este campo es obligatorio.");
        }

        if (isBlank(routine.getExercises())) {
            errors.put("exercises", "Este campo es obligatorio.");
        }

        return errors;
    }

    private String firstError(Map<String, String> errors) {
        return errors.values().iterator().next();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
