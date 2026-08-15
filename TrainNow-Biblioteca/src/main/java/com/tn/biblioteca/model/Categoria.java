package com.tn.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Categoría de la biblioteca como entidad propia (independiente de los ejercicios), para que
 * el admin pueda crear una categoría vacía y recién después agregarle ejercicios. Antes, las
 * categorías solo existían como el valor del campo "category" de un ejercicio, así que no se
 * podía crear una sin forzar los datos de un ejercicio completo.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = System.currentTimeMillis();
    }
}
