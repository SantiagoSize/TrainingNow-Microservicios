package com.tn.entrenamientos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sesiones_entrenamiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rutina_ejercicio_id", nullable = false)
    private RutinaEjercicio rutinaEjercicio;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Integer seriesRealizadas;

    @Column(nullable = false)
    private Integer repeticionesPorSerie;

    @Column(nullable = false)
    private Double pesoLevantado;
}

