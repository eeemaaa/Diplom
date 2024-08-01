package com.example.demo.repository;

import com.example.demo.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d WHERE LOWER(d.documentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchDocuments(@Param("keyword") String keyword);

    @Query("SELECT d FROM Document d WHERE LOWER(d.category) = LOWER(:category)")
    List<Document> findByCategory(@Param("category") String category);
}
