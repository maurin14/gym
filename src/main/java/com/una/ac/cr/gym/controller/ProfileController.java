package com.una.ac.cr.gym.controller;


import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService uService;

    @GetMapping("/profile/password")
    public String passwordForm(HttpSession session, RedirectAttributes redirect){
        if(session.getAttribute("user") == null){
            return "redirect:/login";
        }

        return "profile/changePassword";
    }

    @PostMapping("/profile/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirect){

        String message = uService.changePassword(session, currentPassword, newPassword, confirmPassword);

        if(message != null){
            redirect.addFlashAttribute("messageError", message);
            return "redirect:/profile/password";
        }

        redirect.addFlashAttribute("messageSuccess", "Contraseña actualizada correctamente");
        return "redirect:/";
    }
}