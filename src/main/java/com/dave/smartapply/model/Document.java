package com.dave.smartapply.model;

import java.time.LocalDate;

// Eine einfache Klasse für die Anzeige (später eine @Entity)
public class Document {
    private Long id;
    private String name;
    private String type; // z.B. "PDF", "DOCX"
    private String size; // z.B. "2.4 MB"
    private String category; // z.B. "Lebenslauf", "Zeugnis"
    private LocalDate uploadDate;

    public Document(Long id, String name, String type, String size, String category, LocalDate uploadDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.category = category;
        this.uploadDate = uploadDate;
    }

    // Getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getSize() { return size; }
    public String getCategory() { return category; }
    public LocalDate getUploadDate() { return uploadDate; }
}