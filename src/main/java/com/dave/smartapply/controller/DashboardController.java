package com.dave.smartapply.controller;

import com.dave.smartapply.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final ApplicationService applicationService;

    @GetMapping("/")
    public String dashboard(Model model) {
        log.info("Loading dashboard");

        // JavaScript macht die Auth-Check!
        // User Name wird via JavaScript aus localStorage geladen
        model.addAttribute("greeting", getGreeting());

        return "dashboard";
    }

    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();

        if (hour >= 5 && hour < 12) {
            return "Guten Morgen";
        } else if (hour >= 12 && hour < 18) {
            return "Guten Tag";
        } else {
            return "Guten Abend";
        }
    }
}