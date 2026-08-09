package com.tn.rutinas.repository;

import com.tn.rutinas.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUserIdOrderByStartTimeDesc(Long userId);

    List<WorkoutSession> findByUserIdAndStatusIgnoreCaseOrderByStartTimeDesc(Long userId, String status);
}
