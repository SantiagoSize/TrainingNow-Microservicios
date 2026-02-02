package com.tn.comunicaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad de notificación para usuarios.
 */
@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId; // referencia externa al microservicio de usuarios

    @Column(nullable = false)
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // INFO, ALERTA, MENSAJE

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    @Column(nullable = false)
    @NotNull
    @Builder.Default
    private Boolean leido = false;

    @Column(name = "fecha_envio", nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaEnvio;
}
