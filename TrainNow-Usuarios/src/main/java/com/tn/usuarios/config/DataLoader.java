package com.tn.usuarios.config;

import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seed inicial: admin, entrenador y usuario de prueba.
 * Solo se ejecuta si la tabla está vacía.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .role("ADMIN")
                .name("Admin")
                .lastName("TrainingNow")
                .email("admin@admin.tn")
                .password(passwordEncoder.encode("admin123"))
                .build());

        userRepository.save(User.builder()
                .role("TRAINER")
                .name("Carlos")
                .lastName("Entrenador")
                .email("coach@coach.tn")
                .password(passwordEncoder.encode("coach123"))
                .specializations("Fuerza,Hipertrofia")
                .build());

        userRepository.save(User.builder()
                .role("USER")
                .name("Santiago")
                .lastName("Usuario")
                .email("user@user.tn")
                .password(passwordEncoder.encode("user123"))
                .build());
    }
}
