package com.example.demo.controller;

import com.example.demo.model.Document;
import com.example.demo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private DocumentRepository documentRepo;

    @GetMapping("/admin")
    public String adminHome(Model model) {
        model.addAttribute("message", "Welcome to Admin Panel!");
        return "admin/home";
    }

    @GetMapping("/admin/editDocuments")
    public String editDocuments(Model model) {
        List<Document> documents = documentRepo.findAll();
        model.addAttribute("documents", documents);
        return "admin/edit_documents"; // шаблон edit_documents.html
    }
}
