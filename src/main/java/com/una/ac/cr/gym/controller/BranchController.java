/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.AttendanceService;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.GymClassService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



/**
 *
 * @author sharo
 */
@Controller
public class BranchController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private GymClassService gymClassService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/admin/branches")
    public String adminList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) Integer branchId,
                            @RequestParam(required = false) String active,
                            Model model) {

        int currentPage = Math.max(page, 0);
        Page<Branch> branches = branchService.getAdminPage(branchId, active, PageRequest.of(currentPage, 5));

        if (currentPage >= branches.getTotalPages() && branches.getTotalPages() > 0) {
            currentPage = branches.getTotalPages() - 1;
            branches = branchService.getAdminPage(branchId, active, PageRequest.of(currentPage, 5));
        }

        model.addAttribute("branches", branches);
        model.addAttribute("branchId", branchId);
        model.addAttribute("active", active);
        model.addAttribute("branchOptions", branchService.getAll());

        return "branches/admin/listBranch";
    }

    @GetMapping("/admin/branches/ajax/list")
    public String adminAjaxList(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) Integer branchId,
                                @RequestParam(required = false) String active,
                                Model model) {

        int currentPage = Math.max(page, 0);
        Page<Branch> branches = branchService.getAdminPage(branchId, active, PageRequest.of(currentPage, 5));

        if (currentPage >= branches.getTotalPages() && branches.getTotalPages() > 0) {
            currentPage = branches.getTotalPages() - 1;
            branches = branchService.getAdminPage(branchId, active, PageRequest.of(currentPage, 5));
        }

        model.addAttribute("branches", branches);
        model.addAttribute("branchId", branchId);
        model.addAttribute("active", active);

        return "branches/admin/tableBranch :: tableBranch";
    }

    @GetMapping("/admin/branches/new")
    public String newBranch(Model model) {
        model.addAttribute("branch", new Branch());
        return "branches/admin/formBranch";
    }

    @PostMapping("/admin/branches/save")
    public String saveBranch(@ModelAttribute("branch") Branch branch,
                             Model model) {

        Map<String, String> fieldErrors = branchService.validateFields(branch);
        if (branch.getId() > 0 && branchService.getById(branch.getId()) == null) {
            fieldErrors.put("form", "message.branch.editMissing");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("branch", branch);
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("messageError", "message.form.review");
            return "branches/admin/formBranch";
        }

        boolean isNew = branch.getId() == 0;
        branchService.save(branch);

        return "redirect:/admin/branches?success=" + (isNew ? "save" : "edit");
    }

    @GetMapping("/admin/branches/edit/{id}")
    public String editBranch(@PathVariable int id, Model model) {
        Branch branch = branchService.getById(id);

        if (branch == null) {
            return "redirect:/admin/branches?error=notfound";
        }

        model.addAttribute("branch", branch);
        return "branches/admin/formBranch";
    }

    @GetMapping("/admin/branches/delete/{id}")
    public String deleteBranch(@PathVariable int id,
                               RedirectAttributes redirectAttributes) {
        Branch branch = branchService.getById(id);

        if (branch == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "message.branch.notFound");
            return "redirect:/admin/branches";
        }

        try {
            branchService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "message.branch.deleted");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "message.branch.relatedEquipment");
        }

        return "redirect:/admin/branches";
    }

    @GetMapping({"/branches", "/client/branches"})
    public String userList(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String name,
                           Model model) {

        Page<Branch> branches = branchService.getPage(name, "true", PageRequest.of(page, 6));

        model.addAttribute("branches", branches);
        model.addAttribute("name", name);

        return "branches/user/listBranch";
    }

    @GetMapping({"/branches/{id}", "/client/branches/{id}"})
    public String userDetail(@PathVariable int id, Model model) {
        Branch branch = branchService.getById(id);

        if (branch == null || !branch.isActive()) {
            return "redirect:/client/branches";
        }

        model.addAttribute("branch", branch);
        model.addAttribute("classes", gymClassService.getActiveClassesByBranch(id));
        return "branches/user/detailBranch";
    }

    @PostMapping("/client/branches/{branchId}/classes/{classId}/enroll")
    public String enrollInBranchClass(@PathVariable int branchId,
                                      @PathVariable int classId,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        GymClass gymClass = gymClassService.getClassById(classId);

        if (gymClass == null) {
            redirectAttributes.addFlashAttribute("messageError", "message.branch.enrollError");
            return "redirect:/client/branches/" + branchId;
        }

        if (!classBelongsToBranch(gymClass, branchId)) {
            redirectAttributes.addFlashAttribute("messageError", "message.branch.classMismatch");
            return "redirect:/client/branches/" + branchId;
        }

        String result = attendanceService.enrollClientInClass(user, classId);

        if (result.isEmpty()) {
            redirectAttributes.addFlashAttribute("messageSuccess", "message.branch.enrolled");
        } else {
            redirectAttributes.addFlashAttribute("messageError", enrollmentMessageKey(result));
        }

        return "redirect:/client/branches/" + branchId;
    }

    private String enrollmentMessageKey(String result) {
        if ("Debe iniciar sesion.".equals(result)) {
            return "message.branch.loginRequired";
        }
        if ("Clase no encontrada.".equals(result)) {
            return "message.branch.classNotFound";
        }
        if ("La clase no esta disponible.".equals(result)) {
            return "message.branch.classUnavailable";
        }
        if ("Ya estas inscrito en esta clase.".equals(result)) {
            return "message.branch.alreadyEnrolled";
        }
        if ("No hay cupos disponibles.".equals(result)) {
            return "message.branch.noCapacity";
        }
        return "message.branch.enrollError";
    }

    private boolean classBelongsToBranch(GymClass gymClass, int branchId) {
        if (gymClass.getBranch() != null && gymClass.getBranch().getId() == branchId) {
            return true;
        }

        return gymClass.getBranch() == null
                && gymClass.getTrainer() != null
                && gymClass.getTrainer().getBranch() != null
                && gymClass.getTrainer().getBranch().getId() == branchId;
    }
}
