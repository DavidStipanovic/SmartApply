package com.dave.smartapply.controller;

import com.dave.smartapply.model.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @GetMapping
    public String listDocuments(Model model) {
        // Mock-Daten, damit die Seite nicht leer ist
        List<Document> documents = Arrays.asList(
                new Document(1L, "Lebenslauf_2025.pdf", "PDF", "1.2 MB", "Lebenslauf", LocalDate.now().minusDays(2)),
                new Document(2L, "Masterzeugnis.pdf", "PDF", "3.5 MB", "Zeugnis", LocalDate.of(2024, 8, 15)),
                new Document(3L, "Anschreiben_Google.docx", "DOCX", "0.5 MB", "Anschreiben", LocalDate.now().minusDays(10)),
                new Document(4L, "Arbeitszeugnis_Siemens.pdf", "PDF", "2.1 MB", "Zeugnis", LocalDate.of(2023, 12, 1))
        );

        model.addAttribute("documents", documents);
        return "documents/list";
    }
}