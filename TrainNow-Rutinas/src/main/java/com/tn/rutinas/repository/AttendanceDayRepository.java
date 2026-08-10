package com.tn.rutinas.repository;

import com.tn.rutinas.model.AttendanceDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceDayRepository extends JpaRepository<AttendanceDay, Long> {

    Optional<AttendanceDay> findByUserIdAndDate(Long userId, String date);

    /** Días del usuario cuyo "date" empieza por el prefijo yyyy-MM. */
    List<AttendanceDay> findByUserIdAndDateStartingWithOrderByDateAsc(Long userId, String monthPrefix);

    List<AttendanceDay> findByUserIdOrderByDateAsc(Long userId);
}
