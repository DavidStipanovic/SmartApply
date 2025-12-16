package com.dave.smartapply.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String location; // Ort
    private String participants;// Personen/Emails (als einfacher String erstmal)

    private String category; // Speichere Arbeit, Freizeit etc

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Damit jeder nur seine eigenen Termine sieht
}
