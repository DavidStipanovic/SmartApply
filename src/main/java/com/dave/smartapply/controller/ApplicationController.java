package com.dave.smartapply.controller;

import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;
import com.dave.smartapply.model.User;
import com.dave.smartapply.service.ApplicationService;
import com.dave.smartapply.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    // ✅ Helper: Current User ID aus Session holen
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            log.debug("Current user ID from session: {}", user.getId());
            return user.getId();
        }

        log.error("No authenticated user found in security context");
        throw new RuntimeException("User not authenticated");
    }

    // Bewerbungsübersicht MIT allen Statistiken
    @GetMapping
    public String listApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {

        Long userId = getCurrentUserId();
        log.info("Loading applications list for user {} - status: {}, search: {}", userId, status, search);

        List<Application> applications;

        // Filter nach Status oder Suche
        if (status != null && !status.isEmpty()) {
            applications = applicationService.getApplicationsByStatus(ApplicationStatus.valueOf(status), userId);
        } else if (search != null && !search.isEmpty()) {
            applications = applicationService.searchByCompanyName(search, userId);
        } else {
            applications = applicationService.getAllApplications(userId);
        }

        // ALLE Statistiken hier (nur für diesen User!)
        model.addAttribute("applications", applications);
        model.addAttribute("totalCount", applicationService.getTotalApplications(userId));
        model.addAttribute("activeCount", applicationService.getActiveApplications(userId));
        model.addAttribute("draftCount", applicationService.getCountByStatus(ApplicationStatus.DRAFT, userId));
        model.addAttribute("appliedCount", applicationService.getCountByStatus(ApplicationStatus.APPLIED, userId));
        model.addAttribute("interviewCount",
                applicationService.getCountByStatus(ApplicationStatus.INTERVIEW_SCHEDULED, userId) +
                        applicationService.getCountByStatus(ApplicationStatus.INTERVIEW_DONE, userId));
        model.addAttribute("offerCount", applicationService.getCountByStatus(ApplicationStatus.OFFER_RECEIVED, userId));
        model.addAttribute("rejectedCount", applicationService.getCountByStatus(ApplicationStatus.REJECTED, userId));

        // Alle Status für Filter-Dropdown
        model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchTerm", search);

        return "applications/list";
    }

    // Formular für neue Bewerbung anzeigen
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Long userId = getCurrentUserId();
        log.info("Showing create application form for user: {}", userId);

        Application application = new Application();
        application.setStatus(ApplicationStatus.DRAFT);

        model.addAttribute("appDetails", application);
        model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
        model.addAttribute("isEdit", false);

        return "applications/form";
    }

    // Neue Bewerbung speichern
    @PostMapping
    public String createApplication(
            @ModelAttribute("appDetails") Application application,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        Long userId = getCurrentUserId();
        log.info("Creating application for company: {} (User: {})", application.getCompanyName(), userId);

        if (result.hasErrors()) {
            log.warn("Validation errors while creating application");
            model.addAttribute("isEdit", false);
            model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
            return "applications/form";
        }

        try {
            Application saved = applicationService.createApplication(application, userId);
            log.info("Application created successfully with ID: {} for user: {}", saved.getId(), userId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Bewerbung bei " + saved.getCompanyName() + " erfolgreich erstellt!");

            return "redirect:/applications";
        } catch (Exception e) {
            log.error("Error creating application for user: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Erstellen der Bewerbung: " + e.getMessage());
            return "redirect:/applications/new";
        }
    }

    // Einzelne Bewerbung anzeigen (Detail-View)
    @GetMapping("/{id}")
    public String showApplication(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId();
        log.info("Showing application details for ID: {} (User: {})", id, userId);

        return applicationService.getApplicationById(id, userId)
                .map(application -> {
                    model.addAttribute("appDetails", application);
                    return "applications/detail";
                })
                .orElseGet(() -> {
                    log.warn("Application not found or not authorized - ID: {}, User: {}", id, userId);
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Bewerbung nicht gefunden oder keine Berechtigung!");
                    return "redirect:/applications";
                });
    }

    // Formular zum Bearbeiten anzeigen
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId();
        log.info("Showing edit form for application ID: {} (User: {})", id, userId);

        return applicationService.getApplicationById(id, userId)
                .map(application -> {
                    model.addAttribute("appDetails", application);
                    model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
                    model.addAttribute("isEdit", true);
                    return "applications/form";
                })
                .orElseGet(() -> {
                    log.warn("Application not found or not authorized - ID: {}, User: {}", id, userId);
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Bewerbung nicht gefunden oder keine Berechtigung!");
                    return "redirect:/applications";
                });
    }

    // Bewerbung aktualisieren
    @PostMapping("/{id}")
    public String updateApplication(
            @PathVariable Long id,
            @ModelAttribute("appDetails") Application application,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        Long userId = getCurrentUserId();
        log.info("Updating application ID: {} (User: {})", id, userId);

        if (result.hasErrors()) {
            log.warn("Validation errors while updating application");
            model.addAttribute("isEdit", true);
            model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
            return "applications/form";
        }

        try {
            Application updated = applicationService.updateApplication(id, application, userId);
            log.info("Application updated successfully: {} (User: {})", updated.getId(), userId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Bewerbung bei " + updated.getCompanyName() + " erfolgreich aktualisiert!");

            return "redirect:/applications/" + id;
        } catch (Exception e) {
            log.error("Error updating application for user: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Aktualisieren: " + e.getMessage());
            return "redirect:/applications/" + id + "/edit";
        }
    }

    // Bewerbung löschen
    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId();
        log.info("Deleting application ID: {} (User: {})", id, userId);

        try {
            applicationService.deleteApplication(id, userId);
            log.info("Application deleted successfully: {} (User: {})", id, userId);

            redirectAttributes.addFlashAttribute("successMessage", "Bewerbung erfolgreich gelöscht!");
        } catch (Exception e) {
            log.error("Error deleting application for user: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Löschen: " + e.getMessage());
        }

        return "redirect:/applications";
    }

    // Status schnell ändern
    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            RedirectAttributes redirectAttributes) {

        Long userId = getCurrentUserId();
        log.info("Updating status for application ID: {} to {} (User: {})", id, status, userId);

        try {
            Application updated = applicationService.updateStatus(id, status, userId);
            log.info("Status updated successfully (User: {})", userId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Status auf '" + status.getDisplayName() + "' geändert!");
        } catch (Exception e) {
            log.error("Error updating status for user: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Status-Update: " + e.getMessage());
        }

        return "redirect:/applications/" + id;
    }
}