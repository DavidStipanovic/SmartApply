package com.dave.smartapply.controller;

import com.dave.smartapply.model.User;
import com.dave.smartapply.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;

    // Hilfsmethode: Holt die E-Mail sicher aus dem Authentication-Objekt
    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Fall 1: Das Principal ist direkt unser User-Objekt (Das passiert bei dir!)
        if (principal instanceof User) {
            return ((User) principal).getEmail();
        }
        // Fall 2: Es ist ein UserDetails Objekt (Spring Standard)
        else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        // Fall 3: Es ist einfach nur ein String (z.B. bei einfacher Session)
        else {
            return principal.toString();
        }
    }

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        String email = getEmailFromAuth(authentication);

        if (email == null) {
            return "redirect:/login";
        }

        log.info("Lade Profil für Email: '{}'", email);

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.error("Kritisch: User mit Email '{}' nicht in DB gefunden!", email);
            return "redirect:/login?error=user_sync_error";
        }

        model.addAttribute("user", userOptional.get());
        return "profile/form";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute User formData,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        String email = getEmailFromAuth(authentication);

        if (email == null) {
            return "redirect:/login";
        }

        Optional<User> currentUserOpt = userService.findByEmail(email);

        if (currentUserOpt.isEmpty()) {
            return "redirect:/login?error=user_not_found";
        }

        try {
            User currentUser = currentUserOpt.get();
            // Update mit der ID des eingeloggten Users
            userService.updateGenericProfile(currentUser.getId(), formData);

            redirectAttributes.addFlashAttribute("successMessage", "Profil erfolgreich aktualisiert!");
        } catch (Exception e) {
            log.error("Fehler beim Speichern", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Fehler: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}