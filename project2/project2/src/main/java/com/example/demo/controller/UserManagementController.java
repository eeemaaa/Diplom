package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/addUser")
    public String showAddUserForm(Model model, Authentication authentication) {
        if (!"user".equals(authentication.getName())) {
            return "redirect:/welcome"; // Redirect if not the initial user
        }
        model.addAttribute("newUser", new User());
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute User newUser, Authentication authentication) {
        if (!"user".equals(authentication.getName())) {
            return "redirect:/welcome"; // Redirect if not the initial user
        }
        userService.save(newUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String viewUsers(Model model, Authentication authentication) {
        if (!"user".equals(authentication.getName())) {
            return "redirect:/welcome"; // Redirect if not the initial user
        }
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "viewUsers";
    }

    @GetMapping("/editUser/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (!"user".equals(authentication.getName())) {
            return "redirect:/welcome"; // Redirect if not the initial user
        }
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, Authentication authentication) {
        if (!"user".equals(authentication.getName())) {
            return "redirect:/welcome"; // Redirect if not the initial user
        }
        userService.update(id, user);
        return "redirect:/admin/users";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Authentication authentication) {
        if (!"user".equals(authentication.getName())) {
            return "redirect:/welcome"; // Redirect if not the initial user
        }
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
