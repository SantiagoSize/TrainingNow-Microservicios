package com.tn.usuarios.config;

import com.tn.usuarios.model.Genero;
import com.tn.usuarios.model.Objetivo;
import com.tn.usuarios.model.Role;
import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.RoleRepository;
import com.tn.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Carga inicial de roles (ADMIN, TRAINER, CLIENT) y usuarios de prueba:
 * - Admin
 * - Varios atletas (incluido Santiago Serrano)
 * - Una entrenadora de ejemplo
 */
@Component
@RequiredArgsConstructor
public class UsuariosDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuariosDataLoader.class);

    private static final List<String> ROLES = List.of("ADMIN", "TRAINER", "CLIENT");

    private static final String ADMIN_EMAIL = "admin@trainingnow.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String CLIENT_EMAIL = "client@trainingnow.com";
    private static final String CLIENT_PASSWORD = "client123";
    private static final String SANTIAGO_EMAIL = "santiago.serrano@trainingnow.com";
    private static final String COACH_EMAIL = "coach.lucia@trainingnow.com";
    private static final String ATHLETE_EMAIL = "athlete.carlos@trainingnow.com";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        loadRoles();
        loadAdminUser();
        loadClientUser();
        loadSantiagoUser();
        loadSampleCoach();
        loadSampleAthlete();
    }

    private void loadRoles() {
        for (String roleName : ROLES) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Rol creado: {}", roleName);
            }
        }
    }

    private void loadAdminUser() {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            log.debug("Usuario Admin ya existe");
            return;
        }
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado"));

        User admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setNombre("Administrador");
        admin.setApellidos("TrainingNow");
        admin.setTelefono("+34 600 000 001");
        admin.setFechaNacimiento(LocalDate.of(1985, 1, 15));
        admin.setGenero(Genero.MASCULINO);
        admin.setPesoActual(82.0);
        admin.setAltura(178);
        admin.setObjetivo(Objetivo.MANTENER);
        admin.setRole(adminRole);
        admin.setActivo(true);

        userRepository.save(admin);
        log.info("Usuario Admin creado: {}", ADMIN_EMAIL);
    }

    private void loadClientUser() {
        if (userRepository.findByEmail(CLIENT_EMAIL).isPresent()) {
            log.debug("Usuario Client de prueba ya existe");
            return;
        }
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENT no encontrado"));

        User client = new User();
        client.setEmail(CLIENT_EMAIL);
        client.setPassword(passwordEncoder.encode(CLIENT_PASSWORD));
        client.setNombre("Laura");
        client.setApellidos("García Pérez");
        client.setTelefono("+34 612 345 678");
        client.setFechaNacimiento(LocalDate.of(1995, 6, 20));
        client.setGenero(Genero.FEMENINO);
        client.setPesoActual(65.5);
        client.setAltura(165);
        client.setObjetivo(Objetivo.PERDIDA_PESO);
        client.setRole(clientRole);
        client.setActivo(true);

        userRepository.save(client);
        log.info("Usuario Client de prueba creado: {}", CLIENT_EMAIL);
    }

    private void loadSantiagoUser() {
        if (userRepository.findByEmail(SANTIAGO_EMAIL).isPresent()) {
            log.debug("Usuario Santiago Serrano ya existe");
            return;
        }
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENT no encontrado"));

        User santiago = new User();
        santiago.setEmail(SANTIAGO_EMAIL);
        santiago.setPassword(passwordEncoder.encode("santiago123"));
        santiago.setNombre("Santiago");
        santiago.setApellidos("Serrano");
        santiago.setTelefono("+34 622 111 222");
        santiago.setFechaNacimiento(LocalDate.of(1992, 3, 10));
        santiago.setGenero(Genero.MASCULINO);
        santiago.setPesoActual(78.0);
        santiago.setAltura(180);
        santiago.setObjetivo(Objetivo.GANAR_MUSCULO);
        santiago.setRole(clientRole);
        santiago.setActivo(true);

        userRepository.save(santiago);
        log.info("Usuario de prueba creado: {} (Santiago Serrano)", SANTIAGO_EMAIL);
    }

    private void loadSampleCoach() {
        if (userRepository.findByEmail(COACH_EMAIL).isPresent()) {
            log.debug("Usuario coach de prueba ya existe");
            return;
        }
        Role trainerRole = roleRepository.findByName("TRAINER")
                .orElseThrow(() -> new IllegalStateException("Rol TRAINER no encontrado"));

        User coach = new User();
        coach.setEmail(COACH_EMAIL);
        coach.setPassword(passwordEncoder.encode("coach123"));
        coach.setNombre("Lucía");
        coach.setApellidos("Martín");
        coach.setTelefono("+34 633 222 333");
        coach.setFechaNacimiento(LocalDate.of(1988, 11, 5));
        coach.setGenero(Genero.FEMENINO);
        coach.setPesoActual(60.0);
        coach.setAltura(170);
        coach.setObjetivo(Objetivo.MANTENER);
        coach.setRole(trainerRole);
        coach.setActivo(true);

        userRepository.save(coach);
        log.info("Entrenadora de prueba creada: {}", COACH_EMAIL);
    }

    private void loadSampleAthlete() {
        if (userRepository.findByEmail(ATHLETE_EMAIL).isPresent()) {
            log.debug("Atleta de prueba adicional ya existe");
            return;
        }
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENT no encontrado"));

        User athlete = new User();
        athlete.setEmail(ATHLETE_EMAIL);
        athlete.setPassword(passwordEncoder.encode("carlos123"));
        athlete.setNombre("Carlos");
        athlete.setApellidos("López");
        athlete.setTelefono("+34 644 333 444");
        athlete.setFechaNacimiento(LocalDate.of(1998, 9, 22));
        athlete.setGenero(Genero.MASCULINO);
        athlete.setPesoActual(72.5);
        athlete.setAltura(175);
        athlete.setObjetivo(Objetivo.PERDIDA_PESO);
        athlete.setRole(clientRole);
        athlete.setActivo(true);

        userRepository.save(athlete);
        log.info("Atleta de prueba adicional creado: {}", ATHLETE_EMAIL);
    }
}
