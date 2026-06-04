package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Equipment;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.EquipmentService;
import jakarta.servlet.http.HttpSession;
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
public class ControllerEquipment {

    private final EquipmentService equipmentService;
    private final BranchService branchService;

    public ControllerEquipment(EquipmentService equipmentService,
            BranchService branchService) {
        this.equipmentService = equipmentService;
        this.branchService = branchService;
    }

    @GetMapping("/admin/equipment")
    public String adminEquipment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            Model model,
            HttpSession session) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        int pageSize = 5;
        int currentPage = Math.max(page, 0);
        Page<Equipment> data = getEquipmentPage(currentPage, pageSize, branchId, min, max);

        if (currentPage >= data.getTotalPages() && data.getTotalPages() > 0) {
            currentPage = data.getTotalPages() - 1;
            data = getEquipmentPage(currentPage, pageSize, branchId, min, max);
        }

        model.addAttribute("equipment", data.getContent());
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", data.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("minCost", min);
        model.addAttribute("maxCost", max);

        return "admin/equipment";
    }

    @GetMapping("/admin/equipment/new")
    public String newEquipment(Model model, HttpSession session) {
        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Equipment equipment = new Equipment();
        equipment.setBranch(new Branch());
        equipment.setAvailable("true");

        prepareForm(model, equipment, "title.equipment.new", null);
        return "admin/equipment_form";
    }

    @GetMapping("/admin/equipment/edit/{id}")
    public String editEquipment(@PathVariable int id, Model model,
            HttpSession session, RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Equipment equipment = equipmentService.getById(id);

        if (equipment == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "message.equipment.notFound");
            return "redirect:/admin/equipment";
        }

        if (equipment.getBranch() == null) {
            equipment.setBranch(new Branch());
        }

        prepareForm(model, equipment, "title.equipment.edit", null);
        return "admin/equipment_form";
    }

    @PostMapping("/admin/equipment/save")
    public String saveEquipment(Equipment equipment, Model model,
            HttpSession session, RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Map<String, String> fieldErrors = equipmentService.validate(equipment);

        if (equipment.getId() > 0 && equipmentService.getById(equipment.getId()) == null) {
            fieldErrors.put("form", "message.equipment.editMissing");
        }

        if (!fieldErrors.isEmpty()) {
            if (equipment.getBranch() == null) {
                equipment.setBranch(new Branch());
            }

            String title = equipment.getId() == 0
                    ? "title.equipment.new"
                    : "title.equipment.edit";
            prepareForm(model, equipment, title, fieldErrors);
            model.addAttribute("messageError", "message.form.review");
            return "admin/equipment_form";
        }

        if (equipment.getId() == 0) {
            equipmentService.save(equipment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "message.equipment.saved");
        } else {
            equipmentService.update(equipment.getId(), equipment);
            redirectAttributes.addFlashAttribute("successMessage",
                    "message.equipment.updated");
        }

        return "redirect:/admin/equipment";
    }

    @GetMapping("/admin/equipment/delete/{id}")
    public String deleteEquipment(@PathVariable int id, HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        Equipment equipment = equipmentService.getById(id);

        if (equipment == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "message.equipment.notFound");
            return "redirect:/admin/equipment";
        }

        equipmentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "message.equipment.deleted");
        return "redirect:/admin/equipment";
    }

    @GetMapping("/admin/equipment/status/{id}")
    public String changeEquipmentStatus(@PathVariable int id, HttpSession session,
            RedirectAttributes redirectAttributes) {

        String redirect = requireAdmin(session);
        if (redirect != null) {
            return redirect;
        }

        equipmentService.toggleAvailability(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "message.equipment.statusUpdated");
        return "redirect:/admin/equipment";
    }

    @GetMapping("/client/equipment")
    public String clientEquipment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Integer branchId,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if ("administrator".equals(user.getRole())) {
            return "redirect:/admin/equipment";
        }

        Page<Equipment> data = branchId != null
                ? equipmentService.findByBranchId(branchId, PageRequest.of(page, size))
                : equipmentService.getAll(PageRequest.of(page, size));

        model.addAttribute("equipment", data.getContent());
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", data.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("selectedBranchId", branchId);

        return "client/equipment";
    }

    @GetMapping("/equipment")
    public String publicEquipmentRedirect() {
        return "redirect:/client/equipment";
    }

    @GetMapping("/eq")
    public String oldEquipmentIndexRedirect() {
        return "redirect:/client/equipment";
    }

    @GetMapping("/equip")
    public String oldEquipmentListRedirect() {
        return "redirect:/client/equipment";
    }

    @GetMapping("/add")
    public String oldAddRedirect() {
        return "redirect:/admin/equipment/new";
    }

    @GetMapping("/edit/{id}")
    public String oldEditRedirect(@PathVariable int id) {
        return "redirect:/admin/equipment/edit/" + id;
    }

    @GetMapping("/delete/{id}")
    public String oldDeleteRedirect(@PathVariable int id) {
        return "redirect:/admin/equipment/delete/" + id;
    }

    private Page<Equipment> getEquipmentPage(int page, int size,
            Integer branchId, Double min, Double max) {

        if (branchId != null) {
            return equipmentService.findByBranchId(branchId, PageRequest.of(page, size));
        }

        if (min != null && max != null) {
            return equipmentService.findByCostBetween(min, max, PageRequest.of(page, size));
        }

        return equipmentService.getAll(PageRequest.of(page, size));
    }

    private void prepareForm(Model model, Equipment equipment, String title,
            Map<String, String> fieldErrors) {

        model.addAttribute("equipmentItem", equipment);
        model.addAttribute("branches", branchService.getAll());
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
