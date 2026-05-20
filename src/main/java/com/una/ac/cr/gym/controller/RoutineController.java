/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Routine;
import com.una.ac.cr.gym.service.RoutineServices;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author alira
 */
@Controller
@RequestMapping("/admin/routines")
public class RoutineController {

    @Autowired
    private RoutineServices routineService;

    @GetMapping({"", "/"})
    public String listRoutines(
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String routineType,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int currentPage = Math.max(page, 0);
        Page<Routine> routinePage = routineService.getFilteredRoutines(difficultyLevel, routineType, currentPage);

        if (currentPage >= routinePage.getTotalPages() && routinePage.getTotalPages() > 0) {
            currentPage = routinePage.getTotalPages() - 1;
            routinePage = routineService.getFilteredRoutines(difficultyLevel, routineType, currentPage);
        }

        model.addAttribute("title", "Lista de rutinas");
        model.addAttribute("routines", routinePage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", routinePage.getTotalPages());
        model.addAttribute("difficultyLevel", difficultyLevel);
        model.addAttribute("routineType", routineType);

        return "routine/routine_list";
    }

    @GetMapping("/form")
    public String showRoutineForm(Model model) {
        model.addAttribute("title", "Registrar rutina");
        model.addAttribute("routine", new Routine());
        return "routine/routine_form";
    }

    @PostMapping("/save")
    public String saveRoutine(Routine routine, Model model, RedirectAttributes redirectAttributes) {
        boolean isUpdate = routine.getIdRoutine() > 0;
        Map<String, String> fieldErrors = routineService.validateFields(routine);

        if (isUpdate && routineService.getRoutine(routine.getIdRoutine()) == null) {
            fieldErrors.put("form", "La rutina que intenta editar no existe.");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("title", isUpdate ? "Editar rutina" : "Registrar rutina");
            model.addAttribute("routine", routine);
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("error", "No se pudo guardar. Revise los campos marcados.");
            return "routine/routine_form";
        }

        String result = routineService.saveRoutine(routine);

        if (!result.isEmpty()) {
            model.addAttribute("title", isUpdate ? "Editar rutina" : "Registrar rutina");
            model.addAttribute("routine", routine);
            model.addAttribute("error", result);
            return "routine/routine_form";
        }

        String message = isUpdate ? "Rutina editada correctamente." : "Rutina guardada correctamente.";
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/routines";
    }

    @GetMapping("/edit/{idRoutine}")
    public String editRoutine(@PathVariable int idRoutine, Model model) {
        Routine routine = routineService.getRoutine(idRoutine);

        if (routine == null) {
            return "redirect:/admin/routines";
        }

        model.addAttribute("title", "Editar rutina");
        model.addAttribute("routine", routine);
        return "routine/routine_form";
    }

    @GetMapping("/details/{idRoutine}")
    public String detailsRoutine(@PathVariable int idRoutine, Model model) {
        Routine routine = routineService.getRoutine(idRoutine);

        if (routine == null) {
            return "redirect:/admin/routines";
        }

        model.addAttribute("title", "Detalle de la rutina");
        model.addAttribute("routine", routine);
        return "routine/routine_details";
    }

    @GetMapping("/delete/{idRoutine}")
    public String deleteRoutine(@PathVariable int idRoutine, RedirectAttributes redirectAttributes) {
        String result = routineService.deleteRoutine(idRoutine);

        if (!result.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", result);
            return "redirect:/admin/routines";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Rutina eliminada correctamente.");
        return "redirect:/admin/routines";
    }
}
