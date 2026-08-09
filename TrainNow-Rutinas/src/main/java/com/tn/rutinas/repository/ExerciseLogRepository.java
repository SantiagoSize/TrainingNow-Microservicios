package com.tn.rutinas.repository;

import com.tn.rutinas.model.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    List<ExerciseLog> findBySessionIdOrderByOrderInSessionAsc(Long sessionId);

    void deleteBySessionId(Long sessionId);
}
