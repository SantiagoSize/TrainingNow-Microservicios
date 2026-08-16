package com.tn.usuarios.dto;

import com.tn.usuarios.model.Report;
import lombok.*;

/**
 * DTO de un reporte de usuario. Contrato con el cliente Android.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

    private Long id;
    private Long reporterId;
    private String reporterName;
    private Long reportedId;
    private String reportedName;
    private String reason;
    private String details;
    private String status;
    private Long timestamp;

    public static ReportDto fromEntity(Report report) {
        return ReportDto.builder()
                .id(report.getId())
                .reporterId(report.getReporterId())
                .reporterName(report.getReporterName())
                .reportedId(report.getReportedId())
                .reportedName(report.getReportedName())
                .reason(report.getReason())
                .details(report.getDetails())
                .status(report.getStatus())
                .timestamp(report.getTimestamp())
                .build();
    }

    public Report toEntity() {
        return Report.builder()
                .reporterId(reporterId)
                .reporterName(reporterName)
                .reportedId(reportedId)
                .reportedName(reportedName)
                .reason(reason)
                .details(details)
                .status(status)
                .timestamp(timestamp)
                .build();
    }
}
