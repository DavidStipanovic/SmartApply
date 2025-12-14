package com.dave.smartapply.service;

import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApplicationService {

    // CRUD Operations
    Application createApplication(Application application);

    Application updateApplication(Long id, Application application);

    void deleteApplication(Long id);

    Optional<Application> getApplicationById(Long id);

    List<Application> getAllApplications();

    // Business Logic
    List<Application> getApplicationsByStatus(ApplicationStatus status);

    List<Application> searchByCompanyName(String companyName);

    List<Application> getUpcomingDeadlines(int days);

    List<Application> getOpenApplications();

    Long getCountByStatus(ApplicationStatus status);

    Application updateStatus(Long id, ApplicationStatus newStatus);

    // Statistics
    long getTotalApplications();

    long getActiveApplications();
}
