package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.service.RoutineServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/client/routines")
public class ClientRoutineController {

    @Autowired
    private RoutineServices routineService;

    @GetMapping({"", "/"})
    public String showRoutineCatalog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String difficulty,
            @RequestParam(defaultValue = "") String type,
            Model model) {

        addRoutineCatalogAttributes(model, page, size, difficulty, type);
        model.addAttribute("title", "title.routine.catalog");
        return "client/routine_catalog";
    }

    @GetMapping("/fragment")
    public String showRoutineCatalogFragment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String difficulty,
            @RequestParam(defaultValue = "") String type,
            Model model) {

        addRoutineCatalogAttributes(model, page, size, difficulty, type);
        return "client/fragments/routine_cards :: routineResults";
    }

    @GetMapping("/{id}")
    public String showRoutineDetail(@PathVariable("id") int idRoutine, Model model) {
        Routine routine = routineService.getRoutine(idRoutine);

        if (routine == null || !routine.isState()) {
            return "redirect:/client/routines";
        }

        model.addAttribute("title", "title.routine.detail");
        model.addAttribute("routine", routine);
        return "client/routine_detail";
    }

    private void addRoutineCatalogAttributes(Model model, int page, int size, String difficulty, String type) {
        int pageSize = normalizeSize(size);
        int currentPage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Routine> routinePage = routineService.getClientRoutines(difficulty, type, pageable);

        if (currentPage >= routinePage.getTotalPages() && routinePage.getTotalPages() > 0) {
            currentPage = routinePage.getTotalPages() - 1;
            pageable = PageRequest.of(currentPage, pageSize);
            routinePage = routineService.getClientRoutines(difficulty, type, pageable);
        }

        model.addAttribute("routinePage", routinePage);
        model.addAttribute("routines", routinePage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", routinePage.getTotalPages());
        model.addAttribute("totalItems", routinePage.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("type", type);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return 6;
        }

        return Math.min(size, 12);
    }
}
