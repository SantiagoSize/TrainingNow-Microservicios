package com.tn.usuarios.service;

import com.tn.usuarios.dto.AuditLogDto;
import com.tn.usuarios.model.AuditLog;
import com.tn.usuarios.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Registro de actividad administrativa. Cada acción relevante (crear/editar
 * ejercicios, renombrar categorías, sancionar usuarios, publicar rutinas
 * globales) queda guardada acá para poder auditar quién hizo qué y cuándo.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogDto record(AuditLogDto dto) {
        AuditLog entity = dto.toEntity();
        return AuditLogDto.fromEntity(auditLogRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc()
                .stream().map(AuditLogDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getByActor(Long actorId) {
        return auditLogRepository.findByActorIdOrderByTimestampDesc(actorId)
                .stream().map(AuditLogDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getByTargetType(String targetType) {
        return auditLogRepository.findByTargetTypeOrderByTimestampDesc(targetType)
                .stream().map(AuditLogDto::fromEntity).toList();
    }
}
