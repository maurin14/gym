package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing() {
        return "index";
    }

    @GetMapping("/admin/home")
    public String adminHome(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!"administrator".equals(user.getRole())) {
            return "redirect:/client/home";
        }

        return "admin/index";
    }

    @GetMapping("/client/home")
    public String clientHome(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if ("administrator".equals(user.getRole())) {
            return "redirect:/admin/home";
        }

        return "client/client_home";
    }

    @GetMapping("/user/home")
    public String oldUserHome() {
        return "redirect:/client/home";
    }
}
