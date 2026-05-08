package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class BranchController {

    @Autowired
    private BranchService service;


    @GetMapping("/admin/branches")
    public String adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String active,
            @RequestParam(required = false) String province,
            Model model) {

        Page<Branch> branches = service.getPage(name, active, province, PageRequest.of(page, 5));

        model.addAttribute("branches", branches);
        model.addAttribute("name", name);
        model.addAttribute("active", active);
        model.addAttribute("province", province);
        model.addAttribute("role", "ADMIN");

        return "admin/branches/listBranch";
    }

    @GetMapping("/admin/branches/ajax/list")
    public String adminAjaxList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String active,
            @RequestParam(required = false) String province,
            Model model) {

        Page<Branch> branches = service.getPage(name, active, province, PageRequest.of(page, 5));

        model.addAttribute("branches", branches);
        return "admin/branches/tableBranch :: tableBranch";
    }

    @GetMapping("/admin/branches/new")
    public String newBranch(Model model) {
        model.addAttribute("branch", new Branch());
        model.addAttribute("role", "ADMIN");
        return "admin/branches/formBranch";
    }

    @PostMapping("/admin/branches/save")
    public String saveBranch(
            @Valid @ModelAttribute("branch") Branch branch,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("role", "ADMIN");
            return "admin/branches/formBranch";
        }

        service.save(branch);
        return "redirect:/admin/branches?success=save";
    }

    @GetMapping("/admin/branches/edit/{id}")
    public String editBranch(@PathVariable int id, Model model) {
        Branch branch = service.getById(id);

        if (branch == null) {
            return "redirect:/admin/branches?error=notfound";
        }

        model.addAttribute("branch", branch);
        model.addAttribute("role", "ADMIN");
        return "admin/branches/formBranch";
    }

    @GetMapping("/admin/branches/delete/{id}")
    public String deleteBranch(@PathVariable int id) {
        service.delete(id);
        return "redirect:/admin/branches?success=delete";
    }

    @GetMapping("/admin/branches/toggle/{id}")
    public String toggleBranch(@PathVariable int id) {
        Branch branch = service.getById(id);

        if (branch != null) {
            branch.setActive(!branch.isActive());
            service.save(branch);
        }

        return "redirect:/admin/branches?success=toggle";
    }

    // =========================
    // USER
    // =========================

    @GetMapping("/branches")
    public String userList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            Model model) {

        Page<Branch> branches = service.getPage(name, "true", province, PageRequest.of(page, 6));

        model.addAttribute("branches", branches);
        model.addAttribute("name", name);
        model.addAttribute("province", province);
        model.addAttribute("role", "USER");

        return "user/branches/listBranch";
    }

    @GetMapping("/branches/{id}")
    public String userDetail(@PathVariable int id, Model model) {
        Branch branch = service.getById(id);

        if (branch == null || !branch.isActive()) {
            return "redirect:/branches";
        }

        model.addAttribute("branch", branch);
        model.addAttribute("role", "USER");
        return "user/branches/detailBranch";
    }
}