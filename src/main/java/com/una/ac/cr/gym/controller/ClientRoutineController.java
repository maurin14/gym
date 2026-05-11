package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.service.RoutineServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client/routines")
public class ClientRoutineController {

    @Autowired
    private RoutineServices routineService;

    @GetMapping({"", "/"})
    public String showRoutineCatalog(Model model) {
        List<Routine> routines = routineService.getRoutines()
                .stream()
                .filter(Routine::isState)
                .toList();

        model.addAttribute("title", "Rutinas disponibles");
        model.addAttribute("routines", routines);
        return "client/routine_catalog";
    }

    @GetMapping("/{id}")
    public String showRoutineDetail(@PathVariable("id") int idRoutine, Model model) {
        Routine routine = routineService.getRoutine(idRoutine);

        if (routine == null || !routine.isState()) {
            return "redirect:/client/routines";
        }

        model.addAttribute("title", "Detalle de la rutina");
        model.addAttribute("routine", routine);
        return "client/routine_detail";
    }
}
