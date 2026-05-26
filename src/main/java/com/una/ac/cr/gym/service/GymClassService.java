package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.repository.BranchRepository;
import com.una.ac.cr.gym.repository.GymClassRepository;
import com.una.ac.cr.gym.repository.UserRepository;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GymClassService {

    private final GymClassRepository gymClassRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    public GymClassService(GymClassRepository gymClassRepository,
            BranchRepository branchRepository,
            UserRepository userRepository) {
        this.gymClassRepository = gymClassRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
    }

    public List<GymClass> getAllClasses() {
        return gymClassRepository.findAll();
    }

    public List<GymClass> getActiveClasses() {
        return gymClassRepository.findByStatusTrue();
    }

    public List<GymClass> getActiveClassesByBranch(int branchId) {
        return gymClassRepository.findActiveByBranchOrTrainerBranch(branchId);
    }

    public List<GymClass> getClassesByTrainer(Integer trainerId) {
        return gymClassRepository.findByTrainer_UserId(trainerId);
    }

    public List<GymClass> getClassesByTrainerAndBranch(Integer trainerId, Integer branchId) {
        if (branchId == null || branchId <= 0) {
            return List.of();
        }

        return gymClassRepository.findByTrainerAndBranch(trainerId, branchId);
    }

    public Page<GymClass> getClassesPage(int page, int size) {
        return gymClassRepository.findAll(PageRequest.of(page, size));
    }

    public Page<GymClass> getActiveClassesPage(int page, int size) {
        return gymClassRepository.findByStatusTrue(PageRequest.of(page, size));
    }

    public Page<GymClass> getTrainerClassesPage(Integer trainerId, int page, int size) {
        return gymClassRepository.findByTrainer_UserId(trainerId, PageRequest.of(page, size));
    }

    public Page<GymClass> getTrainerClassesPage(Integer trainerId, Integer branchId, int page, int size) {
        if (branchId == null || branchId <= 0) {
            return Page.empty(PageRequest.of(page, size));
        }

        return gymClassRepository.findByTrainerAndBranch(trainerId, branchId, PageRequest.of(page, size));
    }

    public boolean classBelongsToTrainerAndBranch(int classId, Integer trainerId, Integer branchId) {
        if (branchId == null || branchId <= 0) {
            return false;
        }

        return gymClassRepository.existsByIdClassAndTrainerAndBranch(classId, trainerId, branchId);
    }

    public GymClass getClassById(int idClass) {
        return gymClassRepository.findById(idClass).orElse(null);
    }

    public GymClass saveClass(GymClass gymClass) {
        Map<String, String> errors = validateFields(gymClass);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.values().iterator().next());
        }

        calculateDuration(gymClass);
        normalizeBranch(gymClass);

        gymClass.setStatus(true);
        gymClass.setEnrolledCount(0);

        return gymClassRepository.save(gymClass);
    }

    public GymClass updateClass(int idClass, GymClass gymClass) {

        GymClass currentClass = gymClassRepository.findById(idClass).orElse(null);

        if (currentClass == null) {
            throw new IllegalArgumentException("La clase que intenta editar no existe.");
        }

        gymClass.setIdClass(idClass);

        Map<String, String> errors = validateFields(gymClass);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.values().iterator().next());
        }

        calculateDuration(gymClass);
        normalizeBranch(gymClass);

        gymClass.setEnrolledCount(currentClass.getEnrolledCount());
        gymClass.setStatus(true);

        return gymClassRepository.save(gymClass);
    }

    public void deleteClass(int idClass) {
        gymClassRepository.deleteById(idClass);
    }

    public Map<String, String> validateFields(GymClass gymClass) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (gymClass == null) {
            errors.put("form", "No se pudo guardar. Revise los campos marcados.");
            return errors;
        }

        if (isBlank(gymClass.getClassType())) {
            errors.put("classType", "Este campo es obligatorio.");
        }

        if (gymClass.getClassDate() == null) {
            errors.put("classDate", "La fecha es obligatoria.");
        }

        if (gymClass.getStartTime() == null) {
            errors.put("startTime", "Este campo es obligatorio.");
        }

        if (gymClass.getEndTime() == null) {
            errors.put("endTime", "Este campo es obligatorio.");
        }

        if (gymClass.getStartTime() != null && gymClass.getEndTime() != null
                && !gymClass.getEndTime().isAfter(gymClass.getStartTime())) {
            errors.put("endTime", "La hora final debe ser mayor que la hora de inicio.");
        }

        if (gymClass.getMaxCapacity() <= 0) {
            errors.put("maxCapacity", "Ingrese un valor válido.");
        }

        if (gymClass.getBranch() == null || gymClass.getBranch().getId() <= 0) {
            errors.put("branchId", "Seleccione una sucursal.");
        } else if (branchRepository.findById(gymClass.getBranch().getId()).isEmpty()) {
            errors.put("branchId", "La sucursal seleccionada no existe.");
        }

        if (gymClass.getTrainer() == null || gymClass.getTrainer().getUserId() == null
                || gymClass.getTrainer().getUserId() <= 0) {
            errors.put("trainerId", "Seleccione una opción.");
        }

        if (gymClass.getTrainer() != null
                && gymClass.getTrainer().getUserId() != null
                && gymClass.getTrainer().getUserId() > 0
                && gymClass.getBranch() != null
                && gymClass.getBranch().getId() > 0) {
            User trainer = userRepository.findById(gymClass.getTrainer().getUserId()).orElse(null);

            if (trainer == null || !"trainer".equals(trainer.getRole())) {
                errors.put("trainerId", "Seleccione un entrenador valido.");
            } else if (trainer.getBranch() != null
                    && trainer.getBranch().getId() != gymClass.getBranch().getId()) {
                errors.put("trainerId", "El entrenador pertenece a otra sucursal.");
            }
        }

        if (gymClass.getEnrolledCount() < 0) {
            errors.put("enrolledCount", "Ingrese un valor válido.");
        } else if (gymClass.getMaxCapacity() > 0 && gymClass.getEnrolledCount() > gymClass.getMaxCapacity()) {
            errors.put("enrolledCount", "La cantidad de inscritos no puede superar la capacidad.");
        }

        if (isBlank(gymClass.getDifficultyLevel())) {
            errors.put("difficultyLevel", "Seleccione una opción.");
        }

        if (isBlank(gymClass.getDescription())) {
            errors.put("description", "Este campo es obligatorio.");
        } else if (gymClass.getDescription().length() > 255) {
            errors.put("description", "Ingrese 255 caracteres o menos.");
        }

        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void calculateDuration(GymClass gymClass) {

        int duration = (int) Duration.between(
                gymClass.getStartTime(),
                gymClass.getEndTime()
        ).toMinutes();

        gymClass.setDuration(duration);
    }

    private void normalizeBranch(GymClass gymClass) {
        if (gymClass.getBranch() == null || gymClass.getBranch().getId() <= 0) {
            gymClass.setBranch(null);
            return;
        }

        gymClass.setBranch(branchRepository.findById(gymClass.getBranch().getId()).orElse(null));
    }
}
