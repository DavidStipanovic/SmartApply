package com.dave.smartapply.config;

import com.dave.smartapply.model.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.stereotype.Component;

@ControllerAdvice
@Component
public class GlobalControllerAdvice {

    /**
     * Fügt das User-Objekt automatisch VOR jedem Controller dem Model hinzu.
     * Dies ist die elegantere Lösung als Model.addAttribute in jedem Controller.
     */
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        // HINWEIS: Dies ist eine MOCK-IMPLEMENTIERUNG für den Test.
        // In einer echten App würde hier der über Spring Security angemeldete
        // Benutzer aus der SecurityContext und der Datenbank geladen werden.

        User mockUser = new User();
        // Setze den Vornamen, der unten links angezeigt werden soll
        mockUser.setFirstName("Dave");

        // Setze den Plan/die Rolle, die auf der zweiten Zeile angezeigt wird
        mockUser.setJobTitle("Free Plan");

        // Optional: Setze ein Anfangsbuchstaben für das Avatar-Icon
        mockUser.setProfileImage("D");

        return mockUser;
    }
}
