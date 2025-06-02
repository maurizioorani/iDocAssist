package com.marsk.docassist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marsk.docassist.model.OcrTextDocument;

@Repository
public interface OcrTextDocumentRepository extends JpaRepository<OcrTextDocument, Long> {
    List<OcrTextDocument> findByOriginalFilenameContainingIgnoreCase(String filename);
    List<OcrTextDocument> findAllByOrderByCreatedAtDesc();
}