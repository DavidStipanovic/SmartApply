package com.dave.smartapply.service.impl;

import com.dave.smartapply.model.User;
import com.dave.smartapply.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public Application createApplication(Application application, Long userId) {
        log.info("Creating new application for company: {} (User ID: {})", application.getCompanyName(), userId);

        // User laden und zuweisen
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        application.setUser(user);

        // Setze Default-Status falls nicht gesetzt
        if (application.getStatus() == null) {
            application.setStatus(ApplicationStatus.DRAFT);
        }

        // Setze Bewerbungsdatum auf heute, falls nicht gesetzt
        if (application.getApplicationDate() == null) {
            application.setApplicationDate(LocalDate.now());
        }

        Application saved = applicationRepository.save(application);
        log.info("Application created with ID: {} for User: {}", saved.getId(), userId);
        return saved;
    }

    @Override
    public Application updateApplication(Long id, Application application, Long userId) {
        log.info("Updating application with ID: {} (User ID: {})", id, userId);

        return applicationRepository.findById(id)
                .map(existing -> {
                    // Security Check: Nur eigene Bewerbungen bearbeiten
                    if (!existing.getUserId().equals(userId)) {
                        log.error("User {} tried to update application {} owned by user {}",
                                userId, id, existing.getUserId());
                        throw new RuntimeException("Not authorized to update this application");
                    }

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
    public void deleteApplication(Long id, Long userId) {
        log.info("Deleting application with ID: {} (User ID: {})", id, userId);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));

        // Security Check: Nur eigene Bewerbungen löschen
        if (!application.getUserId().equals(userId)) {
            log.error("User {} tried to delete application {} owned by user {}",
                    userId, id, application.getUserId());
            throw new RuntimeException("Not authorized to delete this application");
        }

        applicationRepository.deleteById(id);
        log.info("Application deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationById(Long id, Long userId) {
        log.debug("Fetching application with ID: {} (User ID: {})", id, userId);

        return applicationRepository.findById(id)
                .filter(app -> app.getUserId().equals(userId)); // Nur eigene Bewerbungen
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getAllApplications(Long userId) {
        log.debug("Fetching all applications for User ID: {}", userId);
        return applicationRepository.findByUserIdOrderByApplicationDateDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByStatus(ApplicationStatus status, Long userId) {
        log.debug("Fetching applications with status: {} for User ID: {}", status, userId);
        return applicationRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> searchByCompanyName(String companyName, Long userId) {
        log.debug("Searching applications by company name: {} for User ID: {}", companyName, userId);
        return applicationRepository.findByUserIdAndCompanyNameContainingIgnoreCase(userId, companyName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getUpcomingDeadlines(int days, Long userId) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        log.debug("Fetching applications with deadlines before: {} for User ID: {}", futureDate, userId);
        return applicationRepository.findByUserIdAndDeadlineBefore(userId, futureDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getOpenApplications(Long userId) {
        log.debug("Fetching all open applications for User ID: {}", userId);
        List<ApplicationStatus> closedStatuses = List.of(
                ApplicationStatus.REJECTED,
                ApplicationStatus.ACCEPTED
        );
        return applicationRepository.findByUserIdAndStatusNotInOrderByApplicationDateDesc(userId, closedStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountByStatus(ApplicationStatus status, Long userId) {
        log.debug("Counting applications with status: {} for User ID: {}", status, userId);
        return applicationRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public Application updateStatus(Long id, ApplicationStatus newStatus, Long userId) {
        log.info("Updating status for application ID: {} to {} (User ID: {})", id, newStatus, userId);

        return applicationRepository.findById(id)
                .map(application -> {
                    // Security Check
                    if (!application.getUserId().equals(userId)) {
                        log.error("User {} tried to update status of application {} owned by user {}",
                                userId, id, application.getUserId());
                        throw new RuntimeException("Not authorized to update this application");
                    }

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
    public long getTotalApplications(Long userId) {
        long count = applicationRepository.countByUserId(userId);
        log.debug("Total applications for User ID {}: {}", userId, count);
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveApplications(Long userId) {
        List<ApplicationStatus> closedStatuses = List.of(
                ApplicationStatus.REJECTED,
                ApplicationStatus.ACCEPTED
        );
        long count = applicationRepository.findByUserIdAndStatusNotInOrderByApplicationDateDesc(userId, closedStatuses).size();
        log.debug("Active applications for User ID {}: {}", userId, count);
        return count;
    }
}