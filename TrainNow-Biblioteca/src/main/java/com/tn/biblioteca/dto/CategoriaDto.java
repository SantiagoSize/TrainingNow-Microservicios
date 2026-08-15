package com.tn.biblioteca.dto;

import lombok.*;

/** DTO de categoría: nombre + cuántos ejercicios tiene (puede ser 0, categoría recién creada). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDto {
    private Long id;
    private String name;
    private long exerciseCount;
}
