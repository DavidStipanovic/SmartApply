package com.dave.smartapply.service;

import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApplicationService {

    // ========================================
    // ✅ CRUD Operations (mit userId)
    // ========================================

    Application createApplication(Application application, Long userId);

    Application updateApplication(Long id, Application application, Long userId);

    void deleteApplication(Long id, Long userId);

    Optional<Application> getApplicationById(Long id, Long userId);

    List<Application> getAllApplications(Long userId);

    // ========================================
    // ✅ Business Logic (mit userId)
    // ========================================

    List<Application> getApplicationsByStatus(ApplicationStatus status, Long userId);

    List<Application> searchByCompanyName(String companyName, Long userId);

    List<Application> getUpcomingDeadlines(int days, Long userId);

    List<Application> getOpenApplications(Long userId);

    Long getCountByStatus(ApplicationStatus status, Long userId);

    Application updateStatus(Long id, ApplicationStatus newStatus, Long userId);

    // ========================================
    // ✅ Statistics (mit userId)
    // ========================================

    long getTotalApplications(Long userId);

    long getActiveApplications(Long userId);
}
