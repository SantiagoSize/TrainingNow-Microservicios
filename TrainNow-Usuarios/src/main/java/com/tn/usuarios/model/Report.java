package com.tn.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Reporte de un usuario hecho por otro usuario (ej: desde el menú de opciones de un
 * contacto en el chat). Queda pendiente hasta que un admin lo revisa: descartarlo o
 * ir directo a sancionar (ver AdminSanctionScreen en la app).
 */
@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Quién reporta. */
    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private String reporterName;

    /** A quién se reporta. */
    @Column(nullable = false)
    private Long reportedId;

    @Column(nullable = false)
    private String reportedName;

    /** Motivo del reporte (obligatorio, ej: "Acoso o lenguaje ofensivo en el chat"). */
    @Column(nullable = false)
    private String reason;

    /** Detalle libre opcional que agrega el usuario que reporta. */
    @Column(length = 1000)
    private String details;

    /** PENDING, REVIEWED (se sancionó), DISMISSED (se descartó). */
    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private Long timestamp;

    @PrePersist
    void onCreate() {
        if (timestamp == null) timestamp = System.currentTimeMillis();
        if (status == null || status.isBlank()) status = "PENDING";
    }
}
