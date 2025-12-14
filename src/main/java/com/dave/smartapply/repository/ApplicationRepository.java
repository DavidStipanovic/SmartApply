package com.dave.smartapply.repository;

import com.dave.smartapply.model.Application;
import com.dave.smartapply.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    //Alle Bewerbungen finden nach Status
    List<Application> findByStatus(ApplicationStatus status);

    //Alle Bewerbungen nach Firma
    List<Application> findByCompanyNameContainingIgnoreCase(String companyName);

    //Alle Bewerbungen mit Deadline vor einem bestimmten Datum
    List<Application> findByDeadlineBefore(LocalDate date);

    //Alle Bewerbungen sortiert nach Datum
    List<Application> findAllByOrderByApplicationDateDesc();

    //Zähle Bewerbungen nach Status
    Long countByStatus(ApplicationStatus status);

    //Query: Finde alle offenen Bewerbungen (nicht rejected/accepted)
    List<Application> findByStatusNotInOrderByApplicationDateDesc(List<ApplicationStatus> statuses);

}
