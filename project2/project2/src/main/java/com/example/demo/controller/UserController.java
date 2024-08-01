package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class UserController {

    @GetMapping
    public String userDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "dashboard";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("message", "Welcome to the index page!");
        return "index";
    }
}
