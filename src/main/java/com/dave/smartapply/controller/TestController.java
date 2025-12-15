package com.dave.smartapply.controller;

import com.dave.smartapply.model.User;
import com.dave.smartapply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    // Test-User erstellen
    @PostMapping("/create-user")
    public Map<String, Object> createTestUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.createUser(email, password, firstName, lastName);
            response.put("success", true);
            response.put("message", "User erstellt!");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getFullName(),
                    "isApproved", user.getIsApproved(),
                    "createdAt", user.getCreatedAt().toString()
            ));
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    // Alle User anzeigen
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // User genehmigen
    @PostMapping("/approve-user/{userId}")
    public Map<String, Object> approveUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.approveUser(userId);
            response.put("success", true);
            response.put("message", "User approved!");
            response.put("isApproved", user.getIsApproved());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
}