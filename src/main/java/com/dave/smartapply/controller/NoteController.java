package com.dave.smartapply.controller;

import com.dave.smartapply.model.Note;
import com.dave.smartapply.model.User;
import com.dave.smartapply.repository.NoteRepository;
import com.dave.smartapply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserService userService;

    // Kombinierte Methode für Erstellen UND Bearbeiten
    @PostMapping("/notes/save") // Wir nennen es save, das passt besser
    @ResponseBody
    public ResponseEntity<String> saveNote(
            @RequestParam(required = false) Long id, // ID ist optional (null = neu)
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "white") String color) {

        User user = userService.getCurrentUser();
        Note note;

        if (id != null) {
            // BEARBEITEN: Versuchen, existierende Notiz zu laden
            note = noteRepository.findById(id).orElse(new Note());
            // Sicherheitscheck: Gehört die Notiz wirklich diesem User?
            if (!note.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Nicht erlaubt");
            }
        } else {
            // NEU ERSTELLEN
            note = new Note();
            note.setUser(user);
        }

        // Daten setzen (für neu und alt)
        note.setTitle(title);
        note.setContent(content);
        note.setColor(color);

        // save() macht bei JPA automatisch ein Insert oder Update
        noteRepository.save(note);

        return ResponseEntity.ok("Gespeichert");
    }

    // Neue Seite anzeigen
    @GetMapping("/notes")
    public String viewNotesPage(org.springframework.ui.Model model) {
        User user = userService.getCurrentUser();
        List<Note> notes = noteRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("notes", notes);
        return "notes"; // Wir brauchen eine notes.html
    }

    // Notiz löschen
    @PostMapping("/notes/delete")
    public String deleteNote(@RequestParam Long id) {
        noteRepository.deleteById(id);
        return "redirect:/notes";
    }
}
