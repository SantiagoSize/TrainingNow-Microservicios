package com.tn.rutinas.repository;

import com.tn.rutinas.model.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    List<ExerciseLog> findBySessionIdOrderByOrderInSessionAsc(Long sessionId);

    void deleteBySessionId(Long sessionId);
}
