package com.una.ac.cr.gym.controller;

/**
 *
 * @author Amanda
 */

import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.GymClassService;
import com.una.ac.cr.gym.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GymClassController {

    private final GymClassService gymClassService;
    private final UserService userService;

    public GymClassController(GymClassService gymClassService,
            UserService userService) {

        this.gymClassService = gymClassService;
        this.userService = userService;
    }

    @GetMapping("/trainer/classes")
    public String trainerClassesList() {
        return "trainer/classes/list";
    }

    @GetMapping("/admin/classes")
    public String adminClassesList() {
        return "trainer/classes/list";
    }

    @GetMapping("/trainer/classes/form")
    public String trainerClassesForm() {
        return "trainer/classes/form";
    }

    @GetMapping("/admin/classes/form")
    public String adminClassesForm() {
        return "trainer/classes/form";
    }

    @GetMapping("/trainer/classes/form/{idClass}")
    public String trainerClassesEdit(@PathVariable int idClass) {
        return "trainer/classes/form";
    }

    @GetMapping("/trainer/classes/edit/{idClass}")
    public String trainerClassesEditAlias(@PathVariable int idClass) {
        return "trainer/classes/form";
    }

    @GetMapping("/admin/classes/form/{idClass}")
    public String adminClassesEdit(@PathVariable int idClass) {
        return "trainer/classes/form";
    }

    @GetMapping("/admin/classes/edit/{idClass}")
    public String adminClassesEditAlias(@PathVariable int idClass) {
        return "trainer/classes/form";
    }

    @GetMapping("/client/classes")
    public String clientClassesList() {
        return "client/classes/list";
    }

    @ResponseBody
    @GetMapping("/classes/page")
    public Map<String, Object> getClassesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        var classPage = gymClassService.getClassesPage(page, size);

        Map<String, Object> response = new HashMap<>();

        response.put("currentPage", classPage.getNumber() + 1);
        response.put("totalPages", classPage.getTotalPages());

        response.put("classes", classPage.getContent()
                .stream()
                .map(gymClass -> {

                    return toClassMap(gymClass);
                })
                .toList());

        return response;
    }

    @ResponseBody
    @GetMapping("/classes")
    public List<Map<String, Object>> getAllClasses() {

        return gymClassService.getAllClasses()
                .stream()
                .map(gymClass -> {

                    return toClassMap(gymClass);
                })
                .toList();
    }

    @ResponseBody
    @GetMapping("/classes/{idClass}")
    public Map<String, Object> getClassById(@PathVariable int idClass) {
        GymClass gymClass = gymClassService.getClassById(idClass);

        if (gymClass == null) {
            return Map.of("success", false, "message", "Clase no encontrada.");
        }

        Map<String, Object> response = toClassMap(gymClass);
        response.put("success", true);
        return response;
    }

    @ResponseBody
    @PostMapping("/classes")
    public Map<String, Object> saveClass(@RequestBody GymClass gymClass) {
        Map<String, String> fieldErrors = gymClassService.validateFields(gymClass);

        if (!fieldErrors.isEmpty()) {
            return Map.of(
                    "success", false,
                    "fieldErrors", fieldErrors,
                    "message", "No se pudo guardar. Revise los campos marcados."
            );
        }

        GymClass savedClass = gymClassService.saveClass(gymClass);
        return Map.of("success", true, "classId", savedClass.getIdClass());
    }

    @ResponseBody
    @PutMapping("/classes/{idClass}")
    public Map<String, Object> updateClass(@PathVariable int idClass,
            @RequestBody GymClass gymClass) {

        Map<String, String> fieldErrors = gymClassService.validateFields(gymClass);

        if (!fieldErrors.isEmpty()) {
            return Map.of(
                    "success", false,
                    "fieldErrors", fieldErrors,
                    "message", "No se pudo guardar. Revise los campos marcados."
            );
        }

        GymClass savedClass = gymClassService.updateClass(idClass, gymClass);
        return Map.of("success", true, "classId", savedClass.getIdClass());
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

    private Map<String, Object> toClassMap(GymClass gymClass) {
        Map<String, Object> map = new HashMap<>();

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
        map.put("trainerId", gymClass.getTrainer() != null ? gymClass.getTrainer().getUserId() : null);
        map.put("trainerName", gymClass.getTrainer() != null ? gymClass.getTrainer().getFullName() : "Sin entrenador");

        return map;
    }
}
