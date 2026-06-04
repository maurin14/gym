package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
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
                        Locale locale,
                        Model model) {
        addNoCacheHeaders(response);

        User user = uService.login(username, password);

        if (user == null) {
            model.addAttribute("messageError", "login.error");
            return "login";
        }

        session.setAttribute("user", user);

        if ("administrator".equals(user.getRole())) {
            return "redirect:/admin/home?lang=" + locale.getLanguage();
        }
        
        if("trainer".equals(user.getRole())){
            return "redirect:/trainer/home?lang=" + locale.getLanguage();
        }

        return "redirect:/client/home?lang=" + locale.getLanguage();
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response, Locale locale) {
        String language = locale != null ? locale.getLanguage() : "es";

        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        addNoCacheHeaders(response);
        return "redirect:/login?logout&lang=" + language;
    }

    private void addNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
