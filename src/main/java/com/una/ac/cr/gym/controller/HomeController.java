package com.una.ac.cr.gym.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    
    @RequestMapping("/")
    public String page(HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/login";
        }
        return "index";
    }

}
