package com.tn.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidad de usuario del sistema TrainingNow.
 * Campos alineados al contrato del cliente Android (UserDto).
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El rol es obligatorio")
    @Builder.Default
    private String role = "USER"; // ADMIN, TRAINER, USER

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String lastName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    private String phone;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String profilePhotoUrl;

    /** Fecha de nacimiento en epoch millis (contrato de la app). */
    private Long birthDate;

    private Double height;
    private Double weight;
    private String gender;

    /** Especialidades del entrenador (CSV). */
    @Column(length = 500)
    private String specializations;

    // ===== Sanciones (admin) =====
    private Long suspendedUntil;
    private String suspendReason;

    @Builder.Default
    private Boolean isBanned = false;

    private String banReason;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    @PrePersist
    void onCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
        if (isBanned == null) isBanned = false;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
