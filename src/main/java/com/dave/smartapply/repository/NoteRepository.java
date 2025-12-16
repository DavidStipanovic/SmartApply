package com.dave.smartapply.repository;

import com.dave.smartapply.model.Note;
import com.dave.smartapply.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserOrderByCreatedAtDesc(User user);
}