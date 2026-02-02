package com.tn.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad de ejercicio o recurso de la biblioteca.
 */
@Entity
@Table(name = "ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // VIDEO, ARTICULO, EJERCICIO

    private String url;

    @Column(name = "duracion_segundos")
    private Integer duracion; // en segundos

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
