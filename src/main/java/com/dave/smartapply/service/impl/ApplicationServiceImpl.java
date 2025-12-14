package com.dave.smartapply.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dave.smartapply.service.ApplicationService;
import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;
import com.dave.smartapply.repository.ApplicationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;

    @Override
    public Application createApplication(Application application) {
        log.info("Creating new application for company: {}", application.getCompanyName());

        // Setze Default-Status falls nicht gesetzt
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.DRAFT);
        }

        // Setze Bewerbungsdatum auf heute, falls nicht gesetzt
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(LocalDate.now());
        }

        Application saved = applicationRepository.save(application);
        log.info("Application created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Application updateApplication(Long id, Application application) {
        log.info("Updating application with ID: {}", id);

        return applicationRepository.findById(id)
                .map(existing -> {
                    existing.setCompanyName(application.getCompanyName());
                    existing.setPosition(application.getPosition());
                    existing.setStatus(application.getStatus());
                    existing.setApplicationDate(application.getApplicationDate());
                    existing.setDeadline(application.getDeadline());
                    existing.setContactPerson(application.getContactPerson());
                    existing.setContactEmail(application.getContactEmail());
                    existing.setContactPhone(application.getContactPhone());
                    existing.setNotes(application.getNotes());
                    existing.setJobUrl(application.getJobUrl());
                    existing.setSalaryExpectation(application.getSalaryExpectation());

                    Application updated = applicationRepository.save(existing);
                    log.info("Application updated successfully: {}", updated.getId());
                    return updated;
                })
                .orElseThrow(() -> {
                    log.error("Application not found with ID: {}", id);
                    return new RuntimeException("Application not found with id: " + id);
                });
    }

    @Override
    public void deleteApplication(Long id) {
        log.info("Deleting application with ID: {}", id);

        if (!applicationRepository.existsById(id)) {
            log.error("Application not found with ID: {}", id);
            throw new RuntimeException("Application not found with id: " + id);
        }

        applicationRepository.deleteById(id);
        log.info("Application deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationById(Long id) {
        log.debug("Fetching application with ID: {}", id);
        return applicationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getAllApplications() {
        log.debug("Fetching all applications");
        return applicationRepository.findAllByOrderByApplicationDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        log.debug("Fetching applications with status: {}", status);
        return applicationRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> searchByCompanyName(String companyName) {
        log.debug("Searching applications by company name: {}", companyName);
        return applicationRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getUpcomingDeadlines(int days) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        log.debug("Fetching applications with deadlines before: {}", futureDate);
        return applicationRepository.findByDeadlineBefore(futureDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getOpenApplications() {
        log.debug("Fetching all open applications");
        List<ApplicationStatus> closedStatuses = List.of(
                ApplicationStatus.REJECTED,
                ApplicationStatus.ACCEPTED
        );
        return applicationRepository.findByStatusNotInOrderByApplicationDateDesc(closedStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountByStatus(ApplicationStatus status) {
        log.debug("Counting applications with status: {}", status);
        return applicationRepository.countByStatus(status);
    }

    @Override
    public Application updateStatus(Long id, ApplicationStatus newStatus) {
        log.info("Updating status for application ID: {} to {}", id, newStatus);

        return applicationRepository.findById(id)
                .map(application -> {
                    application.setStatus(newStatus);
                    Application updated = applicationRepository.save(application);
                    log.info("Status updated successfully for application: {}", updated.getId());
                    return updated;
                })
                .orElseThrow(() -> {
                    log.error("Application not found with ID: {}", id);
                    return new RuntimeException("Application not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalApplications() {
        long count = applicationRepository.count();
        log.debug("Total applications: {}", count);
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveApplications() {
        List<ApplicationStatus> closedStatuses = List.of(
                ApplicationStatus.REJECTED,
                ApplicationStatus.ACCEPTED
        );
        long count = applicationRepository.findByStatusNotInOrderByApplicationDateDesc(closedStatuses).size();
        log.debug("Active applications: {}", count);
        return count;
    }
}
