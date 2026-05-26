/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.service.BranchService;
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

    @GetMapping("/admin/branches")
    public String adminList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String active,
                            Model model) {

        Page<Branch> branches = branchService.getPage(name, active, PageRequest.of(page, 5));

        model.addAttribute("branches", branches);
        model.addAttribute("name", name);
        model.addAttribute("active", active);

        return "branches/admin/listBranch";
    }

    @GetMapping("/admin/branches/ajax/list")
    public String adminAjaxList(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String active,
                                Model model) {

        Page<Branch> branches = branchService.getPage(name, active, PageRequest.of(page, 5));
        model.addAttribute("branches", branches);

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
        if (!fieldErrors.isEmpty()) {
            model.addAttribute("branch", branch);
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("messageError", "No se pudo guardar. Revise los campos marcados.");
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

    @GetMapping("/admin/branches/status/{id}")
    public String changeBranchStatus(@PathVariable int id,
                                     RedirectAttributes redirectAttributes) {
        if (!branchService.toggleStatus(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sucursal no encontrada.");
            return "redirect:/admin/branches";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Estado de la sucursal actualizado correctamente.");
        return "redirect:/admin/branches";
    }

    @GetMapping("/admin/branches/delete/{id}")
    public String deleteBranch(@PathVariable int id,
                               RedirectAttributes redirectAttributes) {
        Branch branch = branchService.getById(id);

        if (branch == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sucursal no encontrada.");
            return "redirect:/admin/branches";
        }

        try {
            branchService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sucursal eliminada correctamente.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No se puede eliminar la sucursal porque tiene equipos asociados.");
        }

        return "redirect:/admin/branches";
    }

    @GetMapping("/branches")
    public String userList(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String name,
                           Model model) {

        Page<Branch> branches = branchService.getPage(name, "true", PageRequest.of(page, 6));

        model.addAttribute("branches", branches);
        model.addAttribute("name", name);

        return "branches/user/listBranch";
    }

    @GetMapping("/branches/{id}")
    public String userDetail(@PathVariable int id, Model model) {
        Branch branch = branchService.getById(id);

        if (branch == null || !branch.isActive()) {
            return "redirect:/branches";
        }

        model.addAttribute("branch", branch);
        return "branches/user/detailBranch";
    }
}
