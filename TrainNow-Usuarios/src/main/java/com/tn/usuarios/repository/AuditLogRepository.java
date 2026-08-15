package com.tn.usuarios.repository;

import com.tn.usuarios.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByTimestampDesc();

    List<AuditLog> findByActorIdOrderByTimestampDesc(Long actorId);

    List<AuditLog> findByTargetTypeOrderByTimestampDesc(String targetType);
}
