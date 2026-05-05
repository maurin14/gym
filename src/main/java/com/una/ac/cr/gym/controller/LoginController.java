package com.proyecto01.gym.controller;

import com.proyecto01.gym.domain.User;
import com.proyecto01.gym.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService uService;

    @GetMapping("/login")
    public String loginForm(Model model){
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model){

        User user = uService.login(username, password);

        if(user == null){
            model.addAttribute("messageError", "Usuario o contraseña incorrectos");
            return "login";
        }

        session.setAttribute("user", user);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}