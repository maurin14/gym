/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Payment;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.PaymentService;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin/payments")
    public String adminList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String paymentMethod,
                            @RequestParam(required = false) Integer branchId,
                            Model model) {

        int currentPage = Math.max(page, 0);
        Page<Payment> payments = paymentService.getPage(status, paymentMethod, branchId, PageRequest.of(currentPage, 5));

        if (currentPage >= payments.getTotalPages() && payments.getTotalPages() > 0) {
            currentPage = payments.getTotalPages() - 1;
            payments = paymentService.getPage(status, paymentMethod, branchId, PageRequest.of(currentPage, 5));
        }

        model.addAttribute("payments", payments);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("branchId", branchId);
        model.addAttribute("branches", branchService.getAll());

        return "payments/admin/listPayment";
    }

    @GetMapping("/admin/payments/ajax/list")
    public String adminAjaxList(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) String paymentMethod,
                                @RequestParam(required = false) Integer branchId,
                                Model model) {

        int currentPage = Math.max(page, 0);
        Page<Payment> payments = paymentService.getPage(status, paymentMethod, branchId, PageRequest.of(currentPage, 5));

        if (currentPage >= payments.getTotalPages() && payments.getTotalPages() > 0) {
            currentPage = payments.getTotalPages() - 1;
            payments = paymentService.getPage(status, paymentMethod, branchId, PageRequest.of(currentPage, 5));
        }

        model.addAttribute("payments", payments);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("branchId", branchId);

        return "payments/admin/tablePayment :: tablePayment";
    }

    @GetMapping("/admin/payments/new")
    public String newPayment(Model model) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        model.addAttribute("payment", payment);
        model.addAttribute("branches", branchService.getActiveBranches());
        model.addAttribute("clients", userService.getClients());
        return "payments/admin/formPayment";
    }

    @PostMapping("/admin/payments/save")
    public String savePayment(@ModelAttribute("payment") Payment payment,
                              Model model) {

        Integer branchId = payment.getBranch() != null ? payment.getBranch().getId() : null;
        Branch branch = branchId != null && branchId > 0 ? branchService.getById(branchId) : null;
        payment.setBranch(branch);

        Map<String, String> fieldErrors = paymentService.validateFields(payment);

        User client = payment.getIdUser() != null ? userService.getUserById(payment.getIdUser()) : null;
        if (client == null || !"client".equals(client.getRole())) {
            fieldErrors.put("idUser", "message.payment.selectClient");
        }

        if (payment.getId() > 0 && paymentService.getById(payment.getId()) == null) {
            fieldErrors.put("form", "message.payment.editMissing");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("payment", payment);
            model.addAttribute("branches", branchService.getActiveBranches());
            model.addAttribute("clients", userService.getClients());
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("messageError", "message.form.review");
            return "payments/admin/formPayment";
        }

        boolean isNew = payment.getId() == 0;
        paymentService.save(payment);

        return "redirect:/admin/payments?success=" + (isNew ? "save" : "edit");
    }

    @GetMapping("/admin/payments/edit/{id}")
    public String editPayment(@PathVariable int id, Model model) {
        Payment payment = paymentService.getById(id);

        if (payment == null) {
            return "redirect:/admin/payments?error=notfound";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("branches", branchService.getActiveBranches());
        model.addAttribute("clients", userService.getClients());

        return "payments/admin/formPayment";
    }

    @GetMapping("/admin/payments/delete/{id}")
    public String deletePayment(@PathVariable int id) {
        paymentService.delete(id);
        return "redirect:/admin/payments?success=delete";
    }

    @GetMapping("/payments")
    public String userPayments(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String paymentMethod,
                               HttpSession session,
                               Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Page<Payment> payments = paymentService.getUserPayments(
                user.getUserId(),
                status,
                paymentMethod,
                PageRequest.of(page, 5)
        );

        model.addAttribute("payments", payments);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("userId", user.getUserId());

        return "payments/user/listPayment";
    }

    @GetMapping("/payments/{id}")
    public String userPaymentDetail(@PathVariable int id,
                                    HttpSession session,
                                    Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Payment payment = paymentService.getById(id);

        if (payment == null || !payment.getIdUser().equals(user.getUserId())) {
            return "redirect:/payments";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("userId", user.getUserId());

        return "payments/user/detailPayment";
    }
}
