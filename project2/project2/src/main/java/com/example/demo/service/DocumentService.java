package com.example.demo.service;

import com.example.demo.model.Document;
import com.example.demo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> findAll() {
        return documentRepository.findAll();
    }
}
