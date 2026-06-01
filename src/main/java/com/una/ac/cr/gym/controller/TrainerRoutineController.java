package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.AttendanceService;
import com.una.ac.cr.gym.service.RoutineServices;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TrainerRoutineController {

    private final RoutineServices routineService;
    private final AttendanceService attendanceService;

    public TrainerRoutineController(RoutineServices routineService,
            AttendanceService attendanceService) {
        this.routineService = routineService;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/trainer/routines")
    public String trainerRoutines(
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String routineType,
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        User trainer = (User) session.getAttribute("user");
        if (trainer == null) {
            return "redirect:/login";
        }

        List<Integer> clientIds = getTrainerClientIds(trainer);
        int currentPage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(currentPage, 5);
        Page<Routine> routinePage = routineService.getRoutinesAssignedToClients(
                clientIds, difficultyLevel, routineType, pageable);

        if (currentPage >= routinePage.getTotalPages() && routinePage.getTotalPages() > 0) {
            currentPage = routinePage.getTotalPages() - 1;
            pageable = PageRequest.of(currentPage, 5);
            routinePage = routineService.getRoutinesAssignedToClients(
                    clientIds, difficultyLevel, routineType, pageable);
        }

        model.addAttribute("title", "title.routine.catalog");
        model.addAttribute("routines", routinePage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", routinePage.getTotalPages());
        model.addAttribute("difficultyLevel", difficultyLevel);
        model.addAttribute("routineType", routineType);
        model.addAttribute("routineBasePath", "/trainer/routines");
        model.addAttribute("canManageRoutines", false);

        return "routine/routine_list";
    }

    @GetMapping("/trainer/routines/details/{idRoutine}")
    public String trainerRoutineDetails(@PathVariable int idRoutine,
            HttpSession session,
            Model model) {
        User trainer = (User) session.getAttribute("user");
        if (trainer == null) {
            return "redirect:/login";
        }

        if (!routineService.routineBelongsToClients(idRoutine, getTrainerClientIds(trainer))) {
            return "redirect:/trainer/routines";
        }

        Routine routine = routineService.getRoutine(idRoutine);

        if (routine == null) {
            return "redirect:/trainer/routines";
        }

        model.addAttribute("title", "title.routine.detail");
        model.addAttribute("routine", routine);
        model.addAttribute("routineBasePath", "/trainer/routines");
        model.addAttribute("canManageRoutines", false);

        return "routine/routine_details";
    }

    private List<Integer> getTrainerClientIds(User trainer) {
        Integer branchId = trainer.getBranch() != null ? trainer.getBranch().getId() : null;

        return attendanceService.getTrainerClients(trainer.getUserId(), branchId)
                .stream()
                .map(User::getUserId)
                .toList();
    }
}
