package com.tn.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Código de recuperación de contraseña (6 dígitos, expira en 10 minutos, un solo uso).
 */
@Entity
@Table(name = "password_reset_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Long expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean used = false;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @PrePersist
    void onCreate() {
        createdAt = System.currentTimeMillis();
        if (used == null) used = false;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
