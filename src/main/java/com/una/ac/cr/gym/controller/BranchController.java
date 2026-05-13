/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



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
    public String saveBranch(@Valid @ModelAttribute("branch") Branch branch,
                             BindingResult result) {

        if (result.hasErrors()) {
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
    public String changeBranchStatus(@PathVariable int id) {
        Branch branch = branchService.getById(id);

        if (branch != null) {
            branch.setActive(!branch.isActive());
            branchService.save(branch);
        }

        return "redirect:/admin/branches?success=status";
    }

    @GetMapping("/admin/branches/delete/{id}")
    public String deleteBranch(@PathVariable int id) {
        branchService.delete(id);
        return "redirect:/admin/branches?success=delete";
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