package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication.getName().equals("user")) {
            return "redirect:/dashboard";
        }
        return "redirect:/";
    }
}
