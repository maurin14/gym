package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.GymClassService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrainerScheduleController {

    private final GymClassService gymClassService;

    public TrainerScheduleController(GymClassService gymClassService) {
        this.gymClassService = gymClassService;
    }

    @GetMapping("/trainer/schedules")
    public String trainerSchedules(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Integer branchId = user.getBranch() != null ? user.getBranch().getId() : null;

        model.addAttribute("classes", gymClassService.getClassesByTrainerAndBranch(
                user.getUserId(),
                branchId));
        return "trainer/schedules/list";
    }
}
