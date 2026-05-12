package com.una.ac.cr.gym.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin/index";
    }

    @GetMapping("/user/home")
    public String userHome() {
        return "user/index";
    }
}