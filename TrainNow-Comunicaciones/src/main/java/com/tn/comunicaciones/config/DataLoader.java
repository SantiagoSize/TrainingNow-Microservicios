package com.tn.comunicaciones.config;

import com.tn.comunicaciones.model.Notificacion;
import com.tn.comunicaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Seed inicial: notificación de bienvenida para el usuario de prueba. */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final NotificacionRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        repository.save(Notificacion.builder()
                .userId(3L) // usuario seed de tn-usuarios
                .title("¡Bienvenido a Training Now!")
                .message("Explora la biblioteca de ejercicios y comienza tu primera rutina.")
                .type("SYSTEM")
                .priority("NORMAL")
                .build());
    }
}
