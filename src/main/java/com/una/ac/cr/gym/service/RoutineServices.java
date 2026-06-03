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
            return "message.routine.invalidId";
        }

        if (!routineRepository.existsById(idRoutine)) {
            return "message.routine.notFound";
        }

        try {
            routineUserRepository.deleteByIdRoutine(idRoutine);
            routineRepository.deleteById(idRoutine);
            routineRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "message.routine.relatedRecords";
        }

        return "";
    }

    public Map<String, String> validateFields(Routine routine) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (routine == null) {
            errors.put("form", "message.form.review");
            return errors;
        }

        if (isBlank(routine.getNameRoutine())) {
            errors.put("nameRoutine", "message.validation.required");
        }

        if (isBlank(routine.getDifficultyLevel())) {
            errors.put("difficultyLevel", "message.validation.select");
        }

        if (isBlank(routine.getRoutineType())) {
            errors.put("routineType", "message.validation.select");
        }

        if (routine.getEstimatedDuration() == null) {
            errors.put("estimatedDuration", "message.validation.required");
        } else if (routine.getEstimatedDuration() <= 0) {
            errors.put("estimatedDuration", "message.validation.value");
        }

        if (routine.getQuantityExercises() == null) {
            errors.put("quantityExercises", "message.validation.required");
        } else if (routine.getQuantityExercises() <= 0) {
            errors.put("quantityExercises", "message.validation.value");
        }

        if (isBlank(routine.getTrainingObjective())) {
            errors.put("trainingObjective", "message.validation.select");
        }

        if (isBlank(routine.getDescription())) {
            errors.put("description", "message.validation.required");
        }

        if (isBlank(routine.getExercises())) {
            errors.put("exercises", "message.validation.required");
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

    public Page<Routine> getRoutinesAssignedToClients(List<Integer> clientIds,
            String difficultyLevel, String routineType, Pageable pageable) {

        if (clientIds == null || clientIds.isEmpty()) {
            return Page.empty(pageable);
        }

        if (difficultyLevel == null) {
            difficultyLevel = "";
        }

        if (routineType == null) {
            routineType = "";
        }

        return routineRepository.findRoutinesAssignedToClients(
                clientIds,
                difficultyLevel.trim(),
                routineType.trim(),
                pageable
        );
    }

    public boolean routineBelongsToClients(int routineId, List<Integer> clientIds) {
        return clientIds != null
                && !clientIds.isEmpty()
                && routineRepository.existsAssignedToClients(routineId, clientIds);
    }
}
