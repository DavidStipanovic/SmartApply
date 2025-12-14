package com.dave.smartapply.controller;

import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;
import com.dave.smartapply.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // Dashboard - Übersicht aller Bewerbungen
    @GetMapping
    public String listApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Model model) {

        log.info("Loading applications list - status: {}, search: {}", status, search);

        List<Application> applications;

        // Filter nach Status oder Suche
        if (status != null && !status.isEmpty()) {
            applications = applicationService.getApplicationsByStatus(ApplicationStatus.valueOf(status));
        } else if (search != null && !search.isEmpty()) {
            applications = applicationService.searchByCompanyName(search);
        } else {
            applications = applicationService.getAllApplications();
        }

        // Statistiken für Dashboard
        model.addAttribute("applications", applications);
        model.addAttribute("totalCount", applicationService.getTotalApplications());
        model.addAttribute("activeCount", applicationService.getActiveApplications());
        model.addAttribute("draftCount", applicationService.getCountByStatus(ApplicationStatus.DRAFT));
        model.addAttribute("appliedCount", applicationService.getCountByStatus(ApplicationStatus.APPLIED));
        model.addAttribute("interviewCount",
                applicationService.getCountByStatus(ApplicationStatus.INTERVIEW_SCHEDULED) +
                        applicationService.getCountByStatus(ApplicationStatus.INTERVIEW_DONE));
        model.addAttribute("offerCount", applicationService.getCountByStatus(ApplicationStatus.OFFER_RECEIVED));
        model.addAttribute("rejectedCount", applicationService.getCountByStatus(ApplicationStatus.REJECTED));

        // Alle Status für Filter-Dropdown
        model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchTerm", search);

        return "applications/list";
    }

    // Formular für neue Bewerbung anzeigen
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("Showing create application form");

        Application application = new Application();
        application.setStatus(ApplicationStatus.DRAFT);

        model.addAttribute("application", application);
        model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
        model.addAttribute("isEdit", false);

        return "applications/form";
    }

    // Neue Bewerbung speichern
    @PostMapping
    public String createApplication(
            @ModelAttribute Application application,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        System.out.println("========== POST REQUEST RECEIVED ==========");
        log.info("Creating application for company: {}", application.getCompanyName());

        if (result.hasErrors()) {
            log.warn("Validation errors while creating application");
            return "applications/form";
        }

        try {
            Application saved = applicationService.createApplication(application);
            log.info("Application created successfully with ID: {}", saved.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Bewerbung bei " + saved.getCompanyName() + " erfolgreich erstellt!");

            return "redirect:/applications";
        } catch (Exception e) {
            log.error("Error creating application", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Erstellen der Bewerbung: " + e.getMessage());
            return "redirect:/applications/new";
        }
    }

    // Einzelne Bewerbung anzeigen (Detail-View)
    @GetMapping("/{id}")
    public String showApplication(@PathVariable Long id, Model model) {
        System.out.println("======== LOADING APPLICATION ID: " + id + " ========= ");
        log.info("Showing application details for ID: {}", id);

        return applicationService.getApplicationById(id)
                .map(application -> {
                    System.out.println("Application found: " + application);
                    System.out.println("Company: " + application.getCompanyName());
                    System.out.println("Status: " + application.getStatus());
                    System.out.println("Position: " + application.getPosition());
                    System.out.println("ID: " + application.getId());

                    model.addAttribute("application", application);
                    return "applications/detail";
                })
                .orElseGet(() -> {
                    System.out.println("========== APPLICATION NOT FOUND! ==========");
                    log.warn("Application not found with ID: {}", id);
                    return "redirect:/applications";
                });
    }

    // Formular zum Bearbeiten anzeigen
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Showing edit form for application ID: {}", id);

        return applicationService.getApplicationById(id)
                .map(application -> {
                    model.addAttribute("application", application);
                    model.addAttribute("allStatuses", Arrays.asList(ApplicationStatus.values()));
                    model.addAttribute("isEdit", true);
                    return "applications/form";
                })
                .orElseGet(() -> {
                    log.warn("Application not found with ID: {}", id);
                    redirectAttributes.addFlashAttribute("errorMessage", "Bewerbung nicht gefunden!");
                    return "redirect:/applications";
                });
    }

    // Bewerbung aktualisieren
    @PostMapping("/{id}")
    public String updateApplication(
            @PathVariable Long id,
            @ModelAttribute Application application,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        log.info("Updating application ID: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors while updating application");
            return "applications/form";
        }

        try {
            Application updated = applicationService.updateApplication(id, application);
            log.info("Application updated successfully: {}", updated.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Bewerbung bei " + updated.getCompanyName() + " erfolgreich aktualisiert!");

            return "redirect:/applications/" + id;
        } catch (Exception e) {
            log.error("Error updating application", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Aktualisieren: " + e.getMessage());
            return "redirect:/applications/" + id + "/edit";
        }
    }

    // Bewerbung löschen
    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting application ID: {}", id);

        try {
            applicationService.deleteApplication(id);
            log.info("Application deleted successfully: {}", id);

            redirectAttributes.addFlashAttribute("successMessage", "Bewerbung erfolgreich gelöscht!");
        } catch (Exception e) {
            log.error("Error deleting application", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Löschen: " + e.getMessage());
        }

        return "redirect:/applications";
    }

    // Status schnell ändern (AJAX-fähig)
    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            RedirectAttributes redirectAttributes) {

        log.info("Updating status for application ID: {} to {}", id, status);

        try {
            Application updated = applicationService.updateStatus(id, status);
            log.info("Status updated successfully");

            redirectAttributes.addFlashAttribute("successMessage",
                    "Status auf '" + status.getDisplayName() + "' geändert!");
        } catch (Exception e) {
            log.error("Error updating status", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Fehler beim Status-Update: " + e.getMessage());
        }

        return "redirect:/applications";
    }
}
