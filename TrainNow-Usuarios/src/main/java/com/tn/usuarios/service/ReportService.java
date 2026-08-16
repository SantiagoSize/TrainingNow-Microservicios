package com.tn.usuarios.service;

import com.tn.usuarios.dto.ReportDto;
import com.tn.usuarios.model.Report;
import com.tn.usuarios.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reportes de usuarios (ej: acoso en el chat, sospecha de bot). Cualquier usuario logueado
 * puede crear uno; solo un admin puede listarlos/resolverlos (igual que AuditLogService).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportDto create(ReportDto dto) {
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new IllegalArgumentException("El motivo del reporte es obligatorio");
        }
        if (dto.getReporterId() == null || dto.getReportedId() == null) {
            throw new IllegalArgumentException("Falta el usuario que reporta o el reportado");
        }
        if (dto.getReporterId().equals(dto.getReportedId())) {
            throw new IllegalArgumentException("No puedes reportarte a ti mismo");
        }
        dto.setStatus("PENDING");
        dto.setId(null);
        return ReportDto.fromEntity(reportRepository.save(dto.toEntity()));
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAll(String status) {
        List<Report> reports = (status == null || status.isBlank())
                ? reportRepository.findAllByOrderByTimestampDesc()
                : reportRepository.findByStatusOrderByTimestampDesc(status);
        return reports.stream().map(ReportDto::fromEntity).toList();
    }

    public ReportDto resolve(Long id, String status) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
        report.setStatus(status);
        return ReportDto.fromEntity(reportRepository.save(report));
    }
}
