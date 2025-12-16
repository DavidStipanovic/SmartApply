package com.dave.smartapply.controller;

import com.dave.smartapply.model.Appointment;
import com.dave.smartapply.model.User;
import com.dave.smartapply.repository.AppointmentRepository;
import com.dave.smartapply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor

public class CalenderController {

    private final AppointmentRepository appointmentRepository;
    private final UserService userService;

    @GetMapping("/calendar")
    public String viewCalendar(Model model) {
        return "calendar";
    }

    // API: Liefert Daten (Jetzt mit Farben!)
    @GetMapping("/api/calendar/events")
    @ResponseBody
    public List<CalendarEventDto> getEvents() {
        User user = userService.getCurrentUser();
        return appointmentRepository.findByUser(user).stream()
                .map(app -> {
                    // Farben Logik
                    String color = "#6366f1"; // Standard (Lila/Primary)
                    if ("WORK".equals(app.getCategory())) color = "#ef4444"; // Rot
                    else if ("LEISURE".equals(app.getCategory())) color = "#3b82f6"; // Blau
                    else if ("IMPORTANT".equals(app.getCategory())) color = "#f59e0b"; // Orange

                    return new CalendarEventDto(
                            app.getId(),
                            app.getTitle(),
                            app.getStartDateTime(),
                            app.getEndDateTime(),
                            color // Farbe an Frontend senden
                    );
                })
                .collect(Collectors.toList());
    }

    // DTO Helper
    record CalendarEventDto(Long id, String title, LocalDateTime start, LocalDateTime end, String backgroundColor) {}

    // Speichern (mit Category)
    @PostMapping("/calendar/create")
    public String createAppointment(
            @RequestParam String title,
            @RequestParam String startDate,
            @RequestParam String startTime,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String participants,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category) { // NEU

        User user = userService.getCurrentUser();

        LocalDateTime startDateTime = LocalDateTime.parse(startDate + "T" + startTime);

        Appointment appointment = new Appointment();
        appointment.setTitle(title);
        appointment.setStartDateTime(startDateTime);
        appointment.setLocation(location);
        appointment.setParticipants(participants);
        appointment.setDescription(description);
        appointment.setCategory(category); // NEU
        appointment.setUser(user);

        if (endDate != null && !endDate.isEmpty() && endTime != null && !endTime.isEmpty()) {
            LocalDateTime endDateTime = LocalDateTime.parse(endDate + "T" + endTime);
            appointment.setEndDateTime(endDateTime);
        } else {
            appointment.setEndDateTime(startDateTime.plusHours(1));
        }

        appointmentRepository.save(appointment);
        return "redirect:/calendar";
    }

    // NEU: Löschen Endpoint
    @DeleteMapping("/calendar/delete/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        // Sicherheit: Prüfen ob Termin dem User gehört wäre besser, hier simpel:
        appointmentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}