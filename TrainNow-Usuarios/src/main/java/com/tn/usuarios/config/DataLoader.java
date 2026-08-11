package com.tn.usuarios.config;

import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Seed inicial: admin, entrenador y usuario de prueba (solo si la tabla está vacía),
 * más un set de usuarios de muestra para demostración (idempotente: se agregan aunque
 * ya existan otros usuarios, pero no se duplican si ya fueron creados).
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Cuentas base/demo que deben mostrar un avatar generado si aún no tienen foto. */
    private static final List<String> EMAILS_CON_AVATAR_AUTOMATICO = Arrays.asList(
            "admin@trainingnow.com", "entrenador@trainingnow.com", "usuario@gmail.com",
            "maria.gonzalez@gmail.com", "diego.munoz@gmail.com",
            "camila.herrera@gmail.com", "valentina.soto@trainingnow.com"
    );

    private static final Color[] PALETA_AVATAR = {
            new Color(0x00A63C), // verde TrainingNow
            new Color(0x1976D2), // azul
            new Color(0xE53935), // rojo
            new Color(0xFB8C00), // naranjo
            new Color(0x8E24AA), // morado
            new Color(0x00838F)  // turquesa
    };

    @Override
    public void run(String... args) {
        repararTelefonosNulos();

        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .role("ADMIN")
                    .name("Admin")
                    .lastName("TrainingNow")
                    .email("admin@trainingnow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .build());

            userRepository.save(User.builder()
                    .role("TRAINER")
                    .name("Carlos")
                    .lastName("Entrenador")
                    .email("entrenador@trainingnow.com")
                    .password(passwordEncoder.encode("entrenador123"))
                    .specializations("Fuerza,Hipertrofia")
                    .build());

            userRepository.save(User.builder()
                    .role("USER")
                    .name("Santiago")
                    .lastName("Usuario")
                    .email("usuario@gmail.com")
                    .password(passwordEncoder.encode("user123"))
                    .build());
        }

        crearUsuariosDeMuestra();
        repararFotosDePerfil();
    }

    /**
     * Corrige filas existentes con phone = NULL en la base de datos (registros creados antes
     * de que User.phone tuviera @Builder.Default). Se ejecuta en cada arranque del microservicio
     * para no depender de una intervención manual con SQL.
     */
    private void repararTelefonosNulos() {
        var afectados = userRepository.findAll().stream()
                .filter(u -> u.getPhone() == null)
                .toList();
        if (afectados.isEmpty()) return;
        afectados.forEach(u -> u.setPhone(""));
        userRepository.saveAll(afectados);
    }

    /**
     * Usuarios de muestra con datos completos (nombre, apellidos, teléfono, datos físicos)
     * para dejar la app lista con contenido de demostración. Se crean una sola vez por email
     * (no dependen de que la tabla esté vacía), así que quedan disponibles aunque ya existan
     * las cuentas base de admin/entrenador/usuario.
     */
    private void crearUsuariosDeMuestra() {
        crearSiNoExiste(User.builder()
                .role("USER")
                .name("María Fernanda")
                .lastName("González Soto")
                .email("maria.gonzalez@gmail.com")
                .phone("+56912345678")
                .password(passwordEncoder.encode("demo123"))
                .gender("Femenino")
                .height(165.0)
                .weight(62.5)
                .birthDate(epochMillis(1998, 3, 14))
                .build());

        crearSiNoExiste(User.builder()
                .role("USER")
                .name("Diego Andrés")
                .lastName("Muñoz Rojas")
                .email("diego.munoz@gmail.com")
                .phone("+56987654321")
                .password(passwordEncoder.encode("demo123"))
                .gender("Masculino")
                .height(178.0)
                .weight(78.0)
                .birthDate(epochMillis(1995, 7, 22))
                .build());

        crearSiNoExiste(User.builder()
                .role("USER")
                .name("Camila Paz")
                .lastName("Herrera Lagos")
                .email("camila.herrera@gmail.com")
                .phone("+56933221144")
                .password(passwordEncoder.encode("demo123"))
                .gender("Femenino")
                .height(160.0)
                .weight(58.0)
                .birthDate(epochMillis(2000, 11, 5))
                .build());

        crearSiNoExiste(User.builder()
                .role("TRAINER")
                .name("Valentina")
                .lastName("Soto Pizarro")
                .email("valentina.soto@trainingnow.com")
                .phone("+56955667788")
                .password(passwordEncoder.encode("demo123"))
                .specializations("Yoga,Movilidad,Rehabilitación")
                .build());
    }

    private void crearSiNoExiste(User user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) return;
        userRepository.save(user);
    }

    private long epochMillis(int year, int month, int day) {
        return LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.of("America/Santiago"))
                .toInstant()
                .toEpochMilli();
    }

    /**
     * Genera y asigna un avatar (iniciales sobre un círculo de color, como imagen PNG pequeña
     * en base64) a las cuentas base/demo que todavía no tengan foto de perfil. No toca cuentas
     * de usuarios reales que se registraron por su cuenta: eso queda a su elección desde el
     * carousel de bienvenida o su perfil.
     */
    private void repararFotosDePerfil() {
        var sinFoto = userRepository.findAll().stream()
                .filter(u -> EMAILS_CON_AVATAR_AUTOMATICO.contains(u.getEmail().toLowerCase()))
                .filter(u -> u.getProfilePhotoUrl() == null || u.getProfilePhotoUrl().isBlank())
                .toList();
        if (sinFoto.isEmpty()) return;

        sinFoto.forEach(u -> {
            String iniciales = obtenerIniciales(u.getName(), u.getLastName());
            Color color = PALETA_AVATAR[Math.abs(u.getEmail().hashCode()) % PALETA_AVATAR.length];
            String avatar = generarAvatarDataUri(iniciales, color);
            if (avatar != null) u.setProfilePhotoUrl(avatar);
        });
        userRepository.saveAll(sinFoto);
    }

    private String obtenerIniciales(String nombre, String apellido) {
        String i1 = (nombre != null && !nombre.isBlank()) ? nombre.trim().substring(0, 1) : "";
        String i2 = (apellido != null && !apellido.isBlank()) ? apellido.trim().substring(0, 1) : "";
        String resultado = (i1 + i2).toUpperCase();
        return resultado.isBlank() ? "TN" : resultado;
    }

    /**
     * Dibuja un avatar circular simple (iniciales blancas sobre fondo de color) y lo devuelve
     * como data URI PNG en base64. Al ser un color plano con texto, el resultado pesa pocos KB.
     */
    private String generarAvatarDataUri(String iniciales, Color color) {
        int size = 128;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.fillRect(0, 0, size, size);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 52));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(iniciales);
            int textAscent = fm.getAscent();
            g.drawString(iniciales, (size - textWidth) / 2f, (size + textAscent) / 2f - 8);
        } finally {
            g.dispose();
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            return null;
        }
    }
}
