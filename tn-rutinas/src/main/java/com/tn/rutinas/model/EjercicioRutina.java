package com.tn.rutinas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entidad que relaciona una rutina con un ejercicio (referencia a tn-biblioteca).
 */
@Entity
@Table(name = "ejercicios_rutina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EjercicioRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rutina_id", nullable = false)
    @NotNull(message = "La rutina es obligatoria")
    private Long rutinaId;

    @Column(name = "ejercicio_id", nullable = false)
    @NotNull(message = "El ejercicio es obligatorio")
    private Long ejercicioId; // referencia externa a tn-biblioteca

    @Column(nullable = false)
    @NotNull
    @Min(value = 1, message = "Las series deben ser al menos 1")
    private Integer series;

    @Column(nullable = false)
    @NotNull
    @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
    private Integer repeticiones;

    @Column(nullable = false)
    @NotNull
    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer orden;
}
