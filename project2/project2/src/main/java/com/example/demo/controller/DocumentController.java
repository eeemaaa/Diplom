package com.example.demo.controller;

import com.example.demo.model.Document;
import com.example.demo.model.User;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/search")
    public String searchDocuments(@RequestParam("query") String query, Model model) {
        List<Document> documents = documentRepo.searchDocuments(query);
        model.addAttribute("documents", documents);
        return "documents";
    }



    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        model.addAttribute("username", username);
        return "dashboard";
    }
    @GetMapping("/documents/home")
    public String documentsHome(Model model) {
        List<Document> documents = documentRepo.findAll();
        model.addAttribute("documents", documents);
        return "documents";
    }

    @PostMapping("/documentUpload")
    public String documentUpload(@RequestParam("doc") MultipartFile file,
                                 @RequestParam("author") String author,
                                 @RequestParam("description") String description,
                                 @RequestParam("category") String category,
                                 @RequestParam("creationDate") String creationDate,
                                 HttpSession session) {

        try {
            String filename = file.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/documents";
            File saveDir = new File(uploadDir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            Path path = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            Document document = new Document();
            document.setDocumentName(filename);
            document.setPath(path.toString());
            document.setAuthor(author);
            document.setDescription(description);
            document.setCategory(category);
            document.setCreationDate(creationDate);

            // Получаем текущего пользователя
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            User user = userRepository.findByUsername(username);
            document.setUser(user);

            documentRepo.save(document);

            session.setAttribute("msg", "Документ загружен успешно");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("msg", "Ошибка при загрузке документа");
        }

        return "redirect:/documents/home";
    }


    @GetMapping("/documents/list")
    public String listDocuments(Model model) {
        List<Document> documents = documentRepo.findAll();
        model.addAttribute("documents", documents);
        return "documents_list";
    }


    @PostMapping("/documents/delete/{id}")
    public String deleteDocument(@PathVariable("id") Long id, HttpSession session) {
        try {
            documentRepo.deleteById(id);
            session.setAttribute("msg", "Документ успешно удален");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("msg", "Ошибка при удалении документа");
        }
        return "redirect:/documents/list";
    }



    @GetMapping("/documents")
    public String listFiles(@RequestParam(value = "category", required = false) String category, Model model) {
        List<Document> documents;
        if (category != null && !category.isEmpty()) {
            documents = documentRepo.findByCategory(category);
        } else {
            documents = documentRepo.findAll();
        }
        model.addAttribute("documents", documents);
        return "documents";
    }

    @GetMapping("/documents/edit/{id}")
    public String showEditDocumentForm(@PathVariable("id") Long id, Model model) {
        Document document = documentRepo.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        model.addAttribute("document", document);
        return "edit_document";
    }

    @PostMapping("/documents/edit/{id}")
    public String editDocument(@PathVariable("id") Long id,
                               @RequestParam("title") String title,
                               @RequestParam("author") String author,
                               @RequestParam("description") String description,
                               @RequestParam("category") String category,
                               @RequestParam("creationDate") String creationDate,
                               HttpSession session) {
        try {
            Document document = documentRepo.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
            document.setDocumentName(title);
            document.setAuthor(author);
            document.setDescription(description);
            document.setCategory(category);
            document.setCreationDate(creationDate);

            documentRepo.save(document);

            session.setAttribute("msg", "Документ успешно обновлен");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("msg", "Ошибка при обновлении документа");
        }

        return "redirect:/documents/list";
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            Document document = documentRepo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
            Path filePath = Paths.get(document.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read the file!");
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) {
        try {
            Document document = documentRepo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
            Path filePath = Paths.get(document.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read the file!");
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + UriUtils.encode(resource.getFilename(), StandardCharsets.UTF_8))
                    .body(resource);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
