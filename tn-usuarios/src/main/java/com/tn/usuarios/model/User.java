package com.tn.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad de usuario del sistema.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellidos;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "El rol es obligatorio")
    private String rol; // ADMIN, TRAINER, CLIENT

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
