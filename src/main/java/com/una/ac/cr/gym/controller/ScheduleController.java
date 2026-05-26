package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Schedule;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final BranchService branchService;

    public ScheduleController(ScheduleService scheduleService,
            BranchService branchService) {
        this.scheduleService = scheduleService;
        this.branchService = branchService;
    }

    @GetMapping("/admin/schedules")
    public String adminSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            Model model,
            HttpSession session) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        int pageSize = 5;
        int currentPage = Math.max(page, 0);
        Page<Schedule> data = getSchedulesPage(currentPage, pageSize, branchId, start, end);

        if (currentPage >= data.getTotalPages() && data.getTotalPages() > 0) {
            currentPage = data.getTotalPages() - 1;
            data = getSchedulesPage(currentPage, pageSize, branchId, start, end);
        }

        addListAttributes(model, data, currentPage, pageSize, branchId, start, end);

        return "admin/schedules";
    }

    @GetMapping("/admin/schedules/new")
    public String newSchedule(Model model, HttpSession session) {
        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Schedule schedule = new Schedule();
        schedule.setBranch(new Branch());
        schedule.setActive(true);

        prepareForm(model, schedule, "Nuevo horario", null);
        return "admin/schedule_form";
    }

    @GetMapping("/admin/schedules/edit/{id}")
    public String editSchedule(@PathVariable int id, Model model,
            HttpSession session, RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Schedule schedule = scheduleService.getById(id);

        if (schedule == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El horario solicitado no existe.");
            return "redirect:/admin/schedules";
        }

        if (schedule.getBranch() == null) {
            schedule.setBranch(new Branch());
        }

        prepareForm(model, schedule, "Editar horario", null);
        return "admin/schedule_form";
    }

    @PostMapping("/admin/schedules/save")
    public String saveSchedule(Schedule schedule, Model model,
            HttpSession session, RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Map<String, String> fieldErrors = scheduleService.validate(schedule);

        if (schedule.getId() > 0 && scheduleService.getById(schedule.getId()) == null) {
            fieldErrors.put("form", "El horario que intenta editar no existe.");
        }

        if (!fieldErrors.isEmpty()) {
            if (schedule.getBranch() == null) {
                schedule.setBranch(new Branch());
            }

            String title = schedule.getId() == 0 ? "Nuevo horario" : "Editar horario";
            prepareForm(model, schedule, title, fieldErrors);
            model.addAttribute("messageError", "Revise los campos marcados.");
            return "admin/schedule_form";
        }

        if (schedule.getId() == 0) {
            scheduleService.save(schedule);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Horario registrado correctamente.");
        } else {
            scheduleService.update(schedule.getId(), schedule);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Horario actualizado correctamente.");
        }

        return "redirect:/admin/schedules";
    }

    @GetMapping("/admin/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable int id, HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Schedule schedule = scheduleService.getById(id);

        if (schedule == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El horario solicitado no existe.");
            return "redirect:/admin/schedules";
        }

        scheduleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Horario eliminado correctamente.");
        return "redirect:/admin/schedules";
    }

    @GetMapping("/admin/schedules/status/{id}")
    public String changeScheduleStatus(@PathVariable int id, HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        scheduleService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Estado del horario actualizado correctamente.");
        return "redirect:/admin/schedules";
    }

    @GetMapping("/client/schedules")
    public String clientSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer branchId,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if ("administrator".equals(user.getRole())) {
            return "redirect:/admin/schedules";
        }

        Page<Schedule> data = branchId != null
                ? scheduleService.getbyId(branchId, PageRequest.of(page, size))
                : scheduleService.getAll(PageRequest.of(page, size));

        addListAttributes(model, data, page, size, branchId, null, null);
        return "client/schedules";
    }

    @GetMapping("/schedules")
    public String publicSchedulesRedirect() {
        return "redirect:/client/schedules";
    }

    @GetMapping("/schedule")
    public String oldScheduleListRedirect() {
        return "redirect:/client/schedules";
    }

    @GetMapping("/schedule/sche")
    public String oldScheduleIndexRedirect() {
        return "redirect:/client/schedules";
    }

    @GetMapping("/schedule/add")
    public String oldScheduleAddRedirect() {
        return "redirect:/admin/schedules/new";
    }

    @GetMapping("/schedule/edit/{id}")
    public String oldScheduleEditRedirect(@PathVariable int id) {
        return "redirect:/admin/schedules/edit/" + id;
    }

    @GetMapping("/schedule/delete/{id}")
    public String oldScheduleDeleteRedirect(@PathVariable int id) {
        return "redirect:/admin/schedules/delete/" + id;
    }

    private Page<Schedule> getSchedulesPage(int page, int size,
            Integer branchId, String start, String end) {

        if (branchId != null) {
            return scheduleService.getbyId(branchId, PageRequest.of(page, size));
        }

        if (start != null && !start.isBlank()
                && end != null && !end.isBlank()) {
            return scheduleService.findByBranchIdAndStartTimeBetween(
                    LocalTime.parse(start),
                    LocalTime.parse(end),
                    PageRequest.of(page, size));
        }

        return scheduleService.getAll(PageRequest.of(page, size));
    }

    private void addListAttributes(Model model, Page<Schedule> data,
            int page, int size, Integer branchId, String start, String end) {

        model.addAttribute("schedules", data.getContent());
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", data.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
    }

    private void prepareForm(Model model, Schedule schedule, String title,
            Map<String, String> fieldErrors) {

        model.addAttribute("schedule", schedule);
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute("title", title);
        model.addAttribute("fieldErrors", fieldErrors);
    }

    private String requireAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!"administrator".equals(user.getRole())) {
            return "redirect:/client/home";
        }

        return null;
    }
}
