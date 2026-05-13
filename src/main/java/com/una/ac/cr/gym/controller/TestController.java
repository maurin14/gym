/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author PC
 */
@Controller
public class TestController {
    
    @RequestMapping("/")
    public String page(Model model) {
       
        return "equipment/indexEquipment";
    }
    
}
