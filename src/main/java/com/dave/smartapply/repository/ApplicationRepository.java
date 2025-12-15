package com.dave.smartapply.repository;

import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // ========================================
    // ✅ NEU: User-spezifische Queries
    // ========================================

    // Alle Bewerbungen eines Users
    List<Application> findByUserId(Long userId);

    // Bewerbungen eines Users nach Status
    List<Application> findByUserIdAndStatus(Long userId, ApplicationStatus status);

    // Bewerbungen eines Users nach Firma
    List<Application> findByUserIdAndCompanyNameContainingIgnoreCase(Long userId, String companyName);

    // Bewerbungen eines Users mit Deadline vor Datum
    List<Application> findByUserIdAndDeadlineBefore(Long userId, LocalDate date);

    // Alle Bewerbungen eines Users sortiert nach Datum
    List<Application> findByUserIdOrderByApplicationDateDesc(Long userId);

    // Anzahl Bewerbungen eines Users
    Long countByUserId(Long userId);

    // Anzahl Bewerbungen eines Users nach Status
    Long countByUserIdAndStatus(Long userId, ApplicationStatus status);

    // Offene Bewerbungen eines Users (nicht rejected/accepted)
    List<Application> findByUserIdAndStatusNotInOrderByApplicationDateDesc(Long userId, List<ApplicationStatus> statuses);

    // ========================================
    // ⚠️ ALTE Queries (können später entfernt werden)
    // ========================================

    // Alle Bewerbungen finden nach Status (DEPRECATED - nutze findByUserIdAndStatus)
    List<Application> findByStatus(ApplicationStatus status);

    // Alle Bewerbungen nach Firma (DEPRECATED - nutze findByUserIdAndCompanyNameContainingIgnoreCase)
    List<Application> findByCompanyNameContainingIgnoreCase(String companyName);

    // Alle Bewerbungen mit Deadline vor einem bestimmten Datum (DEPRECATED)
    List<Application> findByDeadlineBefore(LocalDate date);

    // Alle Bewerbungen sortiert nach Datum (DEPRECATED)
    List<Application> findAllByOrderByApplicationDateDesc();

    // Zähle Bewerbungen nach Status (DEPRECATED)
    Long countByStatus(ApplicationStatus status);

    // Query: Finde alle offenen Bewerbungen (DEPRECATED)
    List<Application> findByStatusNotInOrderByApplicationDateDesc(List<ApplicationStatus> statuses);
}
