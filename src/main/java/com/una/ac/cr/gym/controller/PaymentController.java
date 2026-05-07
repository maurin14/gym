package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Payment;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService service;

    @Autowired
    private BranchService branchService;

   

    @GetMapping("/admin/payments")
    public String adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Integer branchId,
            Model model) {

        Page<Payment> payments = service.getPage(status, paymentMethod, branchId, PageRequest.of(page, 5));

        model.addAttribute("payments", payments);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("branchId", branchId);
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("role", "ADMIN");

        return "admin/payments/listPayment";
    }

    @GetMapping("/admin/payments/ajax/list")
    public String adminAjaxList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) Integer branchId,
            Model model) {

        Page<Payment> payments = service.getPage(status, paymentMethod, branchId, PageRequest.of(page, 5));

        model.addAttribute("payments", payments);
        return "admin/payments/tablePayment :: tablePayment";
    }

    @GetMapping("/admin/payments/new")
    public String add(Model model) {
        model.addAttribute("payment", new Payment());
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("role", "ADMIN");
        return "admin/payments/formPayment";
    }

    @PostMapping("/admin/payments/save")
public String save(
        @Valid @ModelAttribute("payment") Payment payment,
        BindingResult result,
        @RequestParam(required = false) Integer branchId,
        Model model) {

    Branch branch = (branchId != null) ? branchService.getById(branchId) : null;

    if (branch == null) {
        result.rejectValue("branch", "error.payment", "La sucursal es obligatoria.");
    } else {
        payment.setBranch(branch);
    }

    if (result.hasErrors()) {
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("role", "ADMIN");
        return "admin/payments/formPayment";
    }

    service.save(payment);
    return "redirect:/admin/payments?success=save";
}

    @GetMapping("/admin/payments/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Payment payment = service.getById(id);

        if (payment == null) {
            return "redirect:/admin/payments?error=notfound";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("branches", branchService.getAll());
        model.addAttribute("role", "ADMIN");
        return "admin/payments/formPayment";
    }

    @GetMapping("/admin/payments/delete/{id}")
    public String delete(@PathVariable int id) {
        service.delete(id);
        return "redirect:/admin/payments?success=delete";
    }


    @GetMapping("/payments/my-payments")
    public String myPayments(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            Model model) {

        Page<Payment> payments = service.getUserPayments(userId, status, paymentMethod, PageRequest.of(page, 5));

        model.addAttribute("payments", payments);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("userId", userId);
        model.addAttribute("role", "USER");

        return "user/payments/listPayment";
    }

    @GetMapping("/payments/{id}")
    public String paymentDetail(
            @PathVariable int id,
            @RequestParam Integer userId,
            Model model) {

        Payment payment = service.getById(id);

        if (payment == null || !payment.getUserId().equals(userId)) {
            return "redirect:/payments/my-payments?userId=" + userId;
        }

        model.addAttribute("payment", payment);
        model.addAttribute("role", "USER");
        model.addAttribute("userId", userId);

        return "user/payments/detailPayment";
    }
}