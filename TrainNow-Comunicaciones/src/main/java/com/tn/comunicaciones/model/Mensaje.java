package com.tn.comunicaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Mensaje de chat entre dos usuarios. Contrato: MessageDto (Android).
 */
@Entity
@Table(name = "messages", indexes = {
        @Index(columnList = "senderId"),
        @Index(columnList = "receiverId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long senderId;

    @Column(nullable = false)
    @NotNull
    private Long receiverId;

    @Column(nullable = false, length = 2000)
    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    @Column(nullable = false)
    private Long timestamp;

    @Builder.Default
    private Boolean isRead = false;

    @PrePersist
    void onCreate() {
        if (timestamp == null) timestamp = System.currentTimeMillis();
        if (isRead == null) isRead = false;
    }
}
