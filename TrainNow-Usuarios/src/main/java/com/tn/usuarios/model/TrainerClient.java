package com.tn.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Relación entrenador-cliente.
 */
@Entity
@Table(name = "trainer_clients",
       uniqueConstraints = @UniqueConstraint(columnNames = {"trainerId", "clientId"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long trainerId;

    @Column(nullable = false)
    @NotNull
    private Long clientId;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING, ACTIVE, PAUSED, FINISHED

    private Long startDate;
    private Long endDate;

    @Column(length = 1000)
    private String trainerNotes;

    @Column(length = 1000)
    private String clientGoals;

    private Double sessionPrice;

    @Builder.Default
    private Integer sessionsPerWeek = 3;

    private Long lastInteractionDate;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    @PrePersist
    void onCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
