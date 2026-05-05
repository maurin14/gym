package com.proyecto01.gym.controller;

import org.springframework.ui.Model;
import com.proyecto01.gym.domain.User;
import com.proyecto01.gym.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService uService;
    
    @GetMapping({"", "/"})
    public String index(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(required = false) String fullName,
                        @RequestParam(required = false) String role,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirect){

        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }

        if((fullName != null && !fullName.trim().isEmpty()) 
                || (role != null && !role.trim().isEmpty())){

            model.addAttribute("users", uService.filterUsers(fullName, role));
            model.addAttribute("fullName", fullName);
            model.addAttribute("role", role);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            return "user/listUser";
        }

        int size = 5;
        Page<User> userPage = uService.getUsersByPage(page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("fullName", fullName);
        model.addAttribute("role", role);

        return "user/listUser";
    }
    
    @GetMapping("/add")
    public String add(Model model, HttpSession session, RedirectAttributes redirect){
        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        User u = new User();
        u.setStatus("active");

        model.addAttribute("userNew", u);
        return "user/formUser";
    }
    
    @PostMapping("/save")
    public String save(User userNew, HttpSession session, RedirectAttributes redirect){
        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        String validation = uService.validate(userNew);

        if(validation != null){
            redirect.addFlashAttribute("messageError", validation);
            return userNew.getUserId() == null ? "redirect:/users/add" : "redirect:/users/edit?id=" + userNew.getUserId();
        }
        
        boolean isNew = userNew.getUserId() == null;
        boolean result = uService.save(userNew);
        
        if(result){
            if(isNew){
                redirect.addFlashAttribute("messageSuccess", "Usuario guardado correctamente");
            }else{
                redirect.addFlashAttribute("messageSuccess", "Usuario actualizado correctamente");
            }
        }else{
            redirect.addFlashAttribute("messageError", "No se pudo guardar el usuario");
        }

        return "redirect:/users";
    }
    
    @GetMapping("/edit")
    public String edit(@RequestParam Integer id, Model model, HttpSession session, RedirectAttributes redirect){
        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        User u = uService.getUserById(id);

        if(u == null){
            redirect.addFlashAttribute("messageError", "Usuario no encontrado");
            return "redirect:/users";
        }

        model.addAttribute("userNew", u);
        return "user/formUser";
    }
    
    @GetMapping("/detail")
    public String detail(@RequestParam Integer id, Model model, HttpSession session, RedirectAttributes redirect){
        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        User u = uService.getUserById(id);
     
        if(u == null){
            redirect.addFlashAttribute("messageError", "Usuario no encontrado");
            return "redirect:/users";
        }

        model.addAttribute("userView", u);
        return "user/detailUser";
    }
    
    @GetMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes redirect){
        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }

        if(uService.delete(id)){
            redirect.addFlashAttribute("messageSuccess", "Usuario eliminado correctamente");
        }else{
            redirect.addFlashAttribute("messageError", "No se pudo eliminar el usuario");
        }

        return "redirect:/users";
    }
    
    @GetMapping("/check")
    @ResponseBody
    public String checkField(@RequestParam String field, 
                             @RequestParam String value,
                             @RequestParam(required = false) Integer userId){

        if(field.equals("idCard")){
            User u = uService.findByIdCard(value);
            return u != null && !u.getUserId().equals(userId) ? "exists" : "available";
        }

        if(field.equals("email")){
            User u = uService.findByEmail(value);
            return u != null && !u.getUserId().equals(userId) ? "exists" : "available";
        }

        if(field.equals("username")){
            User u = uService.findByUsername(value);
            return u != null && !u.getUserId().equals(userId) ? "exists" : "available";
        }

        return "available";
    }
}