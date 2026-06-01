package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public String loginForm(Model model, HttpServletResponse response) {
        addNoCacheHeaders(response);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model) {
        addNoCacheHeaders(response);

        User user = uService.login(username, password);

        if (user == null) {
            model.addAttribute("messageError", "Usuario o contraseña incorrectos");
            return "login";
        }

        session.setAttribute("user", user);

        if ("administrator".equals(user.getRole())) {
            return "redirect:/admin/home";
        }
        
        if("trainer".equals(user.getRole())){
            return "redirect:/trainer/home";
        }

        return "redirect:/client/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        addNoCacheHeaders(response);
        return "redirect:/login?logout";
    }

    private void addNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
