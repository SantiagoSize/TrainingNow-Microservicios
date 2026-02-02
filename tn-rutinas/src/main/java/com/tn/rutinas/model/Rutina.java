package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad de rutina de entrenamiento.
 */
@Entity
@Table(name = "rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario propietario es obligatorio")
    private Long usuarioId; // dueño de la rutina (referencia externa)

    @Column(name = "duracion_semanas")
    private Integer duracionSemanas;

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Boolean activa = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
