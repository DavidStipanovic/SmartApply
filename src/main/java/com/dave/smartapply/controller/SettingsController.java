package com.dave.smartapply.controller;

import com.dave.smartapply.dto.SettingsDto;
import com.dave.smartapply.model.User;
import com.dave.smartapply.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserRepository userRepository;

    // Speicherpfad für Bilder
    private static final String UPLOAD_DIR = "uploads/profile-images/";

    @Autowired
    public SettingsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Hilfsmethode: Holt die E-Mail sicher aus dem Authentication-Objekt,
     * egal ob es ein String, UserDetails oder dein Custom User ist.
     */
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Fall 1: Dein Custom User Objekt liegt direkt im Context (passiert oft bei JWT)
        if (principal instanceof User) {
            return ((User) principal).getEmail();
        }
        // Fall 2: Spring Security Standard UserDetails
        else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        // Fall 3: Es ist nur ein String (die E-Mail selbst)
        else {
            return principal.toString();
        }
    }

    @GetMapping
    public String showSettings(Model model, Authentication authentication) {
        // 1. E-Mail sauber extrahieren
        String email = getEmailFromAuth(authentication);
        if (email == null) return "redirect:/login";

        // 2. User laden (diesmal mit der ECHTEN E-Mail)
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden mit E-Mail: " + email));

        // 3. DTO befüllen
        SettingsDto settingsDto = new SettingsDto();
        settingsDto.setFirstName(currentUser.getFirstName());
        settingsDto.setLastName(currentUser.getLastName());
        settingsDto.setEmail(currentUser.getEmail());
        settingsDto.setRole(currentUser.getJobTitle());
        settingsDto.setBioHtml(currentUser.getAboutMe());

        // Fallback für Felder, die noch nicht in der DB sind
        settingsDto.setCountry(currentUser.getCity() != null ? currentUser.getCity() : "Deutschland (DE)");
        settingsDto.setTimezone("(UTC+01:00) Berlin");

        // 4. Model Attribute setzen
        model.addAttribute("settingsDto", settingsDto);
        model.addAttribute("activePage", "settings");
        model.addAttribute("activeTab", "details");
        model.addAttribute("currentUser", currentUser); // Wichtig für Sidebar Avatar

        // Listen für Dropdowns
        model.addAttribute("countryList", Arrays.asList("Deutschland (DE)", "Österreich (AT)", "Schweiz (CH)", "USA (US)"));
        model.addAttribute("timezoneList", Arrays.asList("(UTC+01:00) Berlin", "(UTC+00:00) London", "(UTC-05:00) New York"));

        return "settings/settings";
    }

    @PostMapping("/save")
    public String saveSettings(@ModelAttribute SettingsDto settingsDto,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        String email = getEmailFromAuth(authentication);
        if (email == null) return "redirect:/login";

        User userToUpdate = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User beim Speichern nicht gefunden: " + email));

        // Mapping: Formular -> Datenbank
        userToUpdate.setFirstName(settingsDto.getFirstName());
        userToUpdate.setLastName(settingsDto.getLastName());
        userToUpdate.setJobTitle(settingsDto.getRole());
        userToUpdate.setAboutMe(settingsDto.getBioHtml());

        // Optional: Wenn du Land/Stadt in User.java hast:
        // userToUpdate.setCity(settingsDto.getCountry());

        // Bild-Upload verarbeiten
        MultipartFile file = settingsDto.getProfileImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                // Dateiname säubern und eindeutig machen
                String originalName = file.getOriginalFilename();
                String ext = "";
                if(originalName != null && originalName.contains(".")) {
                    ext = originalName.substring(originalName.lastIndexOf("."));
                }
                String fileName = "user_" + userToUpdate.getId() + "_" + System.currentTimeMillis() + ext;

                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                try (var inputStream = file.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }

                // Relativen Pfad in DB speichern (für Browser Zugriff)
                // Wir müssen "/uploads/profile-images/" davor setzen, wenn wir es im HTML nutzen
                // Aber hier speichern wir nur den Dateinamen, der Rest passiert im Template oder Getter
                userToUpdate.setProfileImage("/uploads/profile-images/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Fehler beim Bild-Upload");
                return "redirect:/settings";
            }
        }

        userRepository.save(userToUpdate);

        redirectAttributes.addFlashAttribute("successMessage", "Profil erfolgreich gespeichert.");
        return "redirect:/settings";
    }
}