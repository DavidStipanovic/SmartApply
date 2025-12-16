package com.dave.smartapply.repository;

import com.dave.smartapply.model.Appointment;
import com.dave.smartapply.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUser(User user);
}
