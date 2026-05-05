package com.una.ac.cr.gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
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