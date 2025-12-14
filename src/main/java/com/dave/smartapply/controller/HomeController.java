package com.dave.smartapply.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.dave.smartapply.service.ApplicationService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ApplicationService applicationService;

    @GetMapping("/")
    public String home(Model model) {
        //Dashboard mit den neuesten/wichtigsten Infos
        model.addAttribute("recentApplications",
                applicationService.getAllApplications().stream().limit(5).toList());
        model.addAttribute("openApplications",
                applicationService.getOpenApplications().stream().limit(5).toList());
        model.addAttribute("upcomingDeadLines",
                applicationService.getUpcomingDeadlines(7));

        return "index";

    }
}
