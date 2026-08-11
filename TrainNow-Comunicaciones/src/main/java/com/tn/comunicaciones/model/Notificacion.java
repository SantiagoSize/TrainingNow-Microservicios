package com.tn.comunicaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Notificación de usuario. Campos alineados al contrato del cliente Android (NotificationDto).
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Destinatario (referencia externa a tn-usuarios). */
    @Column(nullable = false)
    @NotNull
    private Long userId;

    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    private String title;

    @Column(nullable = false, length = 2000)
    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private String type = "SYSTEM"; // SYSTEM, MESSAGE, ROUTINE, REMINDER, ADMIN

    /** Fecha de la notificación (epoch millis). */
    private Long date;

    @Builder.Default
    private Boolean isRead = false;

    private String actionType;
    private String actionData;

    @Builder.Default
    private String priority = "NORMAL"; // LOW, NORMAL, HIGH

    private Long expiresAt;
    private Long senderId;
    private String iconUrl;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @PrePersist
    void onCreate() {
        createdAt = System.currentTimeMillis();
        if (date == null) date = createdAt;
        if (isRead == null) isRead = false;
    }
}
