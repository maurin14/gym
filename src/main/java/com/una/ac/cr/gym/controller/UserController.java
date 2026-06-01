package com.una.ac.cr.gym.controller;

import org.springframework.ui.Model;
import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.BranchService;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
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

    @Autowired
    private BranchService branchService;
    
    @GetMapping({"", "/"})
    public String index(@RequestParam(defaultValue = "0") int page,
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

        int size = 5;
        int currentPage = Math.max(page, 0);
        Page<User> userPage = uService.filterUsersByPage(fullName, role, currentPage, size);

        if (currentPage >= userPage.getTotalPages() && userPage.getTotalPages() > 0) {
            currentPage = userPage.getTotalPages() - 1;
            userPage = uService.filterUsersByPage(fullName, role, currentPage, size);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", currentPage);
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
        u.setBranch(new Branch());

        model.addAttribute("userNew", u);
        addFormAttributes(model);
        return "user/formUser";
    }
    
    @PostMapping("/save")
    public String save(User userNew, Model model, HttpSession session, RedirectAttributes redirect){
        String access = uService.validateUserAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        Map<String, String> fieldErrors = uService.validateFields(userNew);
        boolean isNew = userNew.getUserId() == null;

        if (!isNew && uService.getUserById(userNew.getUserId()) == null) {
            fieldErrors.put("form", "El usuario que intenta editar no existe.");
        }

        if(!fieldErrors.isEmpty()){
            if (userNew.getBranch() == null) {
                userNew.setBranch(new Branch());
            }
            model.addAttribute("userNew", userNew);
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("messageError", "Revise los datos.");
            addFormAttributes(model);
            return "user/formUser";
        }

        boolean result = uService.save(userNew);
        
        if(result){
            if(isNew){
                redirect.addFlashAttribute("messageSuccess", "Guardado.");
            }else{
                redirect.addFlashAttribute("messageSuccess", "Actualizado.");
            }
        }else{
            redirect.addFlashAttribute("messageError", "No se pudo guardar.");
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
            redirect.addFlashAttribute("messageError", "No encontrado.");
            return "redirect:/users";
        }

        model.addAttribute("userNew", u);
        if (u.getBranch() == null) {
            u.setBranch(new Branch());
        }
        addFormAttributes(model);
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
            redirect.addFlashAttribute("messageError", "No encontrado.");
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
            redirect.addFlashAttribute("messageSuccess", "Eliminado.");
        }else{
            redirect.addFlashAttribute("messageError", "No se pudo eliminar.");
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

    private void addFormAttributes(Model model) {
        model.addAttribute("branches", branchService.getActiveBranches());
    }
}
