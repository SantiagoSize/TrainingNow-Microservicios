package com.tn.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Rol del usuario en la plataforma (ADMIN, TRAINER, CLIENT).
 */
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // ADMIN, TRAINER, CLIENT
}
