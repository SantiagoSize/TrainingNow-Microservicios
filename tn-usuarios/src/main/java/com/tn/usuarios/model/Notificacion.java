package com.tn.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notificación simple para que los entrenadores se comuniquen con los atletas.
 * Se modela como un mensaje dirigido a un usuario concreto.
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

    /**
     * Identificador del usuario destinatario de la notificación.
     * Se corresponde con el id de la entidad User.
     */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    @Builder.Default
    private Boolean leido = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

