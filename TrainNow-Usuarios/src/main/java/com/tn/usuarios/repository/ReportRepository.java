package com.tn.usuarios.repository;

import com.tn.usuarios.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByTimestampDesc();

    List<Report> findByStatusOrderByTimestampDesc(String status);

    List<Report> findByReportedIdOrderByTimestampDesc(Long reportedId);
}
