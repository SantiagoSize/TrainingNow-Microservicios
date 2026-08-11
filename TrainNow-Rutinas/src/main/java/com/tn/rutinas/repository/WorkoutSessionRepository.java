package com.tn.rutinas.repository;

import com.tn.rutinas.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUserIdOrderByStartTimeDesc(Long userId);

    List<WorkoutSession> findByUserIdAndStatusIgnoreCaseOrderByStartTimeDesc(Long userId, String status);
}
