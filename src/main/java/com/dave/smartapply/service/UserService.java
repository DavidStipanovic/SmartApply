package com.dave.smartapply.service;

import com.dave.smartapply.model.User;
import com.dave.smartapply.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // User erstellen (Registrierung)
    @Transactional
    public User createUser(String email, String password, String firstName, String lastName) {
        log.info("Creating new user with email: {}", email);

        // Check ob Email schon existiert
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email bereits vergeben!");
        }

        // Passwort verschlüsseln
        String encodedPassword = passwordEncoder.encode(password);

        // User erstellen
        User user = new User(email, encodedPassword, firstName, lastName);
        user.setIsApproved(false); // Admin muss freischalten

        User saved = userRepository.save(user);
        log.info("User created successfully with ID: {}", saved.getId());

        return saved;
    }

    // User by Email finden
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // User by ID finden
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Alle User
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // User genehmigen (Admin-Funktion)
    @Transactional
    public User approveUser(Long userId) {
        log.info("Approving user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden!"));

        user.setIsApproved(true);
        return userRepository.save(user);
    }

    // User löschen
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        userRepository.deleteById(userId);
    }

    // Passwort überprüfen
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // Passwort ändern
    @Transactional
    public User updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden!"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    // Profil aktualisieren
    @Transactional
    public User updateProfile(Long userId, String firstName, String lastName, String profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden!"));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (profileImage != null) user.setProfileImage(profileImage);

        return userRepository.save(user);
    }

    // User direkt speichern (für Auth)
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    // === HIER EINFÜGEN: ===

    // Profil aktualisieren (Erweitert für alle Felder)
    // Profil aktualisieren (Erweitert für alle Felder)
    @Transactional
    public User updateGenericProfile(Long userId, User formData) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden!"));

        // Stammdaten
        user.setFirstName(formData.getFirstName());
        user.setLastName(formData.getLastName());
        // Falls du ein Feld "fullName" hast, aktualisieren wir es auch:
        if (user.getFirstName() != null && user.getLastName() != null) {
            user.setFullName(user.getFirstName() + " " + user.getLastName());
        }

        // Neue Profil-Daten
        user.setJobTitle(formData.getJobTitle());
        user.setPhone(formData.getPhone());
        user.setAboutMe(formData.getAboutMe());
        user.setLinkedinUrl(formData.getLinkedinUrl());
        user.setGithubUrl(formData.getGithubUrl());
        user.setXingUrl(formData.getXingUrl());
        user.setStreet(formData.getStreet());
        user.setCity(formData.getCity());
        user.setZipCode(formData.getZipCode());

        return userRepository.save(user);
    }
}
