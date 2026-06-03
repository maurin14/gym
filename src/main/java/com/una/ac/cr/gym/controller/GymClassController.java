package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.GymClassService;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Controller
public class GymClassController {

    private final GymClassService gymClassService;
    private final UserService userService;
    private final BranchService branchService;

    public GymClassController(GymClassService gymClassService,
            UserService userService,
            BranchService branchService) {
        this.gymClassService = gymClassService;
        this.userService = userService;
        this.branchService = branchService;
    }

    @GetMapping("/trainer/classes")
    public String trainerClassesList(Model model) {
        model.addAttribute("classBasePath", "/trainer/classes");
        model.addAttribute("canManageClasses", false);
        return "trainer/classes/list";
    }

    @GetMapping("/admin/classes")
    public String adminClassesList(Model model) {
        model.addAttribute("classBasePath", "/admin/classes");
        model.addAttribute("canManageClasses", true);
        return "trainer/classes/list";
    }

    @GetMapping("/trainer/classes/form")
    public String trainerClassesForm(Model model) {
        return "redirect:/trainer/classes";
    }

    @GetMapping("/admin/classes/form")
    public String adminClassesForm(Model model) {
        model.addAttribute("classBasePath", "/admin/classes");
        return "trainer/classes/form";
    }

    @GetMapping("/trainer/classes/form/{idClass}")
    public String trainerClassesEdit(@PathVariable int idClass, Model model) {
        return "redirect:/trainer/classes";
    }

    @GetMapping("/trainer/classes/edit/{idClass}")
    public String trainerClassesEditAlias(@PathVariable int idClass, Model model) {
        return "redirect:/trainer/classes";
    }

    @GetMapping("/admin/classes/form/{idClass}")
    public String adminClassesEdit(@PathVariable int idClass, Model model) {
        model.addAttribute("classBasePath", "/admin/classes");
        return "trainer/classes/form";
    }

    @GetMapping("/admin/classes/edit/{idClass}")
    public String adminClassesEditAlias(@PathVariable int idClass, Model model) {
        model.addAttribute("classBasePath", "/admin/classes");
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
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer branchId,
            HttpSession session) {

        int currentPage = Math.max(page, 0);

        Page<GymClass> classPage =
                getClassesPageForSession(session, currentPage, size, branchId);

        if (currentPage >= classPage.getTotalPages()
                && classPage.getTotalPages() > 0) {

            currentPage = classPage.getTotalPages() - 1;

            classPage =
                    getClassesPageForSession(session, currentPage, size, branchId);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("currentPage", classPage.getNumber() + 1);
        response.put("totalPages", classPage.getTotalPages());

        response.put("classes", classPage.getContent()
                .stream()
                .map(this::toClassMap)
                .toList());

        return response;
    }

    @ResponseBody
    @GetMapping("/classes")
    public List<Map<String, Object>> getAllClasses(HttpSession session) {

        User user = currentUser(session);

        if ("client".equals(user.getRole())) {
            return gymClassService
                    .getActiveClassesPage(0, 100)
                    .getContent()
                    .stream()
                    .map(this::toClassMap)
                    .toList();
        }

        return getClassesForSession(session)
                .stream()
                .map(this::toClassMap)
                .toList();
    }

    @ResponseBody
    @GetMapping("/classes/{idClass}")
    public Map<String, Object> getClassById(@PathVariable int idClass,
            HttpSession session) {

        GymClass gymClass = gymClassService.getClassById(idClass);

        if (gymClass == null) {
            return Map.of(
                    "success", false,
                    "message", "Clase no encontrada."
            );
        }

        User user = currentUser(session);

        if ("trainer".equals(user.getRole())
                && !isAssignedTrainer(gymClass, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Map<String, Object> response = toClassMap(gymClass);
        response.put("success", true);

        return response;
    }

    @ResponseBody
    @PostMapping("/classes")
    public ResponseEntity<Map<String, Object>> saveClass(
            @RequestBody GymClass gymClass,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "success", false,
                            "message", "Solo administradores pueden guardar clases."
                    ));
        }

        Map<String, String> fieldErrors =
                gymClassService.validateFields(gymClass);

        if (!fieldErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "fieldErrors", fieldErrors,
                    "message", "No se pudo guardar. Revise los campos marcados."
            ));
        }

        GymClass savedClass = gymClassService.saveClass(gymClass);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "classId", savedClass.getIdClass()
        ));
    }

    @ResponseBody
    @PutMapping("/classes/{idClass}")
    public ResponseEntity<Map<String, Object>> updateClass(
            @PathVariable int idClass,
            @RequestBody GymClass gymClass,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "success", false,
                            "message", "Solo administradores pueden editar clases."
                    ));
        }

        if (gymClassService.getClassById(idClass) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "La clase que intenta editar no existe."
            ));
        }

        Map<String, String> fieldErrors =
                gymClassService.validateFields(gymClass);

        if (!fieldErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "fieldErrors", fieldErrors,
                    "message", "No se pudo guardar. Revise los campos marcados."
            ));
        }

        GymClass savedClass =
                gymClassService.updateClass(idClass, gymClass);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "classId", savedClass.getIdClass()
        ));
    }

    @ResponseBody
    @DeleteMapping("/classes/{idClass}")
    public ResponseEntity<Void> deleteClass(@PathVariable int idClass,
            HttpSession session) {

        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        gymClassService.deleteClass(idClass);

        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @GetMapping("/classes/trainers")
    public List<Map<String, Object>> getTrainers(
            @RequestParam(required = false) Integer branchId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return List.of();
        }

        List<User> trainers;

        if (branchId != null && branchId > 0) {
            trainers = userService.getTrainersByBranch(branchId);
        } else {
            trainers = userService.filterUsers(null, "trainer");
        }

        return trainers.stream()
                .map(this::toUserSimpleMap)
                .toList();
    }

    @ResponseBody
    @GetMapping("/classes/branches")
    public List<Map<String, Object>> getBranches(HttpSession session) {

        if (!isAdmin(session)) {
            return List.of();
        }

        return branchService.getActiveBranches()
                .stream()
                .map(branch -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", branch.getId());
                    map.put("name", branch.getName());
                    return map;
                })
                .toList();
    }

    private Page<GymClass> getClassesPageForSession(
            HttpSession session,
            int page,
            int size,
            Integer branchId) {

        User user = currentUser(session);

        if ("trainer".equals(user.getRole())) {

            if (branchId != null && branchId > 0) {
                return gymClassService.getTrainerClassesPage(
                        user.getUserId(),
                        branchId,
                        page,
                        size
                );
            }

            return gymClassService.getTrainerClassesPage(
                    user.getUserId(),
                    getCurrentBranchId(user),
                    page,
                    size
            );
        }

        if ("client".equals(user.getRole())) {

            if (branchId != null && branchId > 0) {
                return gymClassService.getActiveClassesByBranchPage(
                        branchId,
                        page,
                        size
                );
            }

            return gymClassService.getActiveClassesPage(page, size);
        }

        if (branchId != null && branchId > 0) {
            return gymClassService.getClassesByBranchPage(
                    branchId,
                    page,
                    size
            );
        }

        return gymClassService.getClassesPage(page, size);
    }

    private List<GymClass> getClassesForSession(HttpSession session) {

        User user = currentUser(session);

        if ("trainer".equals(user.getRole())) {
            return gymClassService.getClassesByTrainerAndBranch(
                    user.getUserId(),
                    getCurrentBranchId(user)
            );
        }

        if ("client".equals(user.getRole())) {
            return gymClassService
                    .getActiveClassesPage(0, 100)
                    .getContent();
        }

        return gymClassService.getAllClasses();
    }

    private boolean isAdmin(HttpSession session) {
        User user = currentUser(session);
        return "administrator".equals(user.getRole());
    }

    private User currentUser(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
    }

    private boolean isAssignedTrainer(GymClass gymClass, User user) {
        return gymClassService.classBelongsToTrainerAndBranch(
                gymClass.getIdClass(),
                user.getUserId(),
                getCurrentBranchId(user)
        );
    }

    private Integer getCurrentBranchId(User user) {
        return user != null && user.getBranch() != null
                ? user.getBranch().getId()
                : null;
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

        map.put("trainerId",
                gymClass.getTrainer() != null
                ? gymClass.getTrainer().getUserId()
                : null);

        map.put("trainerName",
                gymClass.getTrainer() != null
                ? gymClass.getTrainer().getFullName()
                : "Sin entrenador");

        map.put("branchId", getClassBranchId(gymClass));
        map.put("branchName", getClassBranchName(gymClass));

        return map;
    }

    private Map<String, Object> toUserSimpleMap(User user) {

        Map<String, Object> map = new HashMap<>();

        map.put("userId", user.getUserId());
        map.put("fullName", user.getFullName());
        map.put("status", user.getStatus());

        return map;
    }

    private Integer getClassBranchId(GymClass gymClass) {

        if (gymClass.getBranch() != null) {
            return gymClass.getBranch().getId();
        }

        if (gymClass.getTrainer() != null
                && gymClass.getTrainer().getBranch() != null) {
            return gymClass.getTrainer().getBranch().getId();
        }

        return null;
    }

    private String getClassBranchName(GymClass gymClass) {

        if (gymClass.getBranch() != null) {
            return gymClass.getBranch().getName();
        }

        if (gymClass.getTrainer() != null
                && gymClass.getTrainer().getBranch() != null) {
            return gymClass.getTrainer().getBranch().getName();
        }

        return "Sin sucursal";
    }
}