package com.tn.usuarios.config;

import com.tn.usuarios.model.TrainerClient;
import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.TrainerClientRepository;
import com.tn.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Seed inicial: admin, entrenador y usuario de prueba (solo si la tabla está vacía),
 * más un set de usuarios de muestra para demostración (idempotente: se agregan aunque
 * ya existan otros usuarios, pero no se duplican si ya fueron creados).
 *
 * Roster completo de cuentas de muestra (para probar los 3 roles sin errores de login):
 * 3 usuarios normales, 3 entrenadores y 2 administradores, todos con nombre y apellido
 * reales (nada de "Usuario 1"/"Usuario 2"). También se seedean relaciones
 * entrenador-cliente para que las listas de "mis clientes"/"mi entrenador" y el chat
 * no queden vacías al probar.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TrainerClientRepository trainerClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    /** Cuentas base/demo que deben mostrar un avatar generado si aún no tienen foto. */
    private static final List<String> EMAILS_CON_AVATAR_AUTOMATICO = Arrays.asList(
            "admin@trainingnow.com", "entrenador@trainingnow.com", "usuario@gmail.com",
            "maria.gonzalez@gmail.com", "diego.munoz@gmail.com",
            "valentina.soto@trainingnow.com", "rodrigo.fuentes@trainingnow.com",
            "francisca.torres@trainingnow.com"
    );

    private static final Color[] PALETA_AVATAR = {
            new Color(0x00A63C), // verde TrainingNow
            new Color(0x1976D2), // azul
            new Color(0xE53935), // rojo
            new Color(0xFB8C00), // naranjo
            new Color(0x8E24AA), // morado
            new Color(0x00838F)  // turquesa
    };

    /** Datos físicos de respaldo para completar cuentas que quedaron con campos en NULL. */
    private record DatosDemograficos(
            String email, String phone, String gender, double height, double weight,
            int anio, int mes, int dia
    ) {}

    private static final List<DatosDemograficos> DATOS_DEMOGRAFICOS = List.of(
            new DatosDemograficos("admin@trainingnow.com", "+56900001111", "Masculino", 178, 80, 1988, 5, 10),
            new DatosDemograficos("entrenador@trainingnow.com", "+56911112222", "Masculino", 180, 82, 1990, 2, 18),
            new DatosDemograficos("usuario@gmail.com", "+56922223333", "Masculino", 175, 74, 1999, 3, 20),
            new DatosDemograficos("maria.gonzalez@gmail.com", "+56912345678", "Femenino", 165, 62.5, 1998, 3, 14),
            new DatosDemograficos("diego.munoz@gmail.com", "+56987654321", "Masculino", 178, 78, 1995, 7, 22),
            new DatosDemograficos("valentina.soto@trainingnow.com", "+56955667788", "Femenino", 168, 60, 1993, 9, 25),
            new DatosDemograficos("rodrigo.fuentes@trainingnow.com", "+56966778899", "Masculino", 182, 85, 1991, 11, 30),
            new DatosDemograficos("francisca.torres@trainingnow.com", "+56911223344", "Femenino", 163, 58, 1996, 8, 8)
    );

    @Override
    public void run(String... args) {
        asegurarColumnaFotoAmplia();
        repararTelefonosNulos();
        repararNombresBase();

        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .role("ADMIN")
                    .name("Admin")
                    .lastName("TrainingNow")
                    .email("admin@trainingnow.com")
                    .password(passwordEncoder.encode("Admin123"))
                    .build());

            userRepository.save(User.builder()
                    .role("TRAINER")
                    .name("Carlos")
                    .lastName("Mendoza Silva")
                    .email("entrenador@trainingnow.com")
                    .password(passwordEncoder.encode("Entrenador123"))
                    .specializations("Fuerza,Hipertrofia")
                    .bio(BIO_CARLOS)
                    .build());

            userRepository.save(User.builder()
                    .role("USER")
                    .name("Santiago")
                    .lastName("Vargas Reyes")
                    .email("usuario@gmail.com")
                    .password(passwordEncoder.encode("User1234"))
                    .build());
        }

        crearUsuariosDeMuestra();
        crearUsuariosSancionadosDePrueba();
        completarDatosDemograficos();
        crearRelacionesEntrenadorCliente();
        completarBiosEntrenadores();
        asignarFotosRealesEntrenadores();
        repararFotosDePerfil();
        asegurarContrasenasDemoValidas();
    }

    /**
     * Las contraseñas de las cuentas base/demo se cambiaron a un formato que cumple la
     * validación del registro (min. 8 caracteres, mayúscula, minúscula y número). Los bloques
     * de arriba (save/crearSiNoExiste) solo escriben la contraseña la primera vez que se crea
     * la fila, así que en una base de datos que ya tenía estas cuentas con la contraseña vieja
     * (ej. "admin123") no se actualizaría sola. Este método corre en cada arranque y fuerza
     * la contraseña vigente en cada cuenta demo, igual patrón que repararTelefonosNulos().
     */
    private void asegurarContrasenasDemoValidas() {
        java.util.Map<String, String> contrasenas = java.util.Map.ofEntries(
                java.util.Map.entry("admin@trainingnow.com", "Admin123"),
                java.util.Map.entry("entrenador@trainingnow.com", "Entrenador123"),
                java.util.Map.entry("usuario@gmail.com", "User1234"),
                java.util.Map.entry("francisca.torres@trainingnow.com", "Demo1234"),
                java.util.Map.entry("valentina.soto@trainingnow.com", "Demo1234"),
                java.util.Map.entry("rodrigo.fuentes@trainingnow.com", "Demo1234"),
                java.util.Map.entry("maria.gonzalez@gmail.com", "Demo1234"),
                java.util.Map.entry("diego.munoz@gmail.com", "Demo1234"),
                java.util.Map.entry("baneado.demo@gmail.com", "Demo1234"),
                java.util.Map.entry("suspendido.demo@gmail.com", "Demo1234")
        );
        contrasenas.forEach((email, pass) -> userRepository.findByEmailIgnoreCase(email).ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(pass));
            userRepository.save(u);
        }));
    }

    /**
     * @Column(columnDefinition = "TEXT") en User.profilePhotoUrl/promoImageUrl no basta: con
     * spring.jpa.hibernate.ddl-auto=update, Hibernate crea columnas nuevas pero NO altera el
     * tipo de una columna que ya existía como VARCHAR(255) (limitación conocida del modo
     * "update"). Si alguna de estas columnas quedó creada como VARCHAR(255) en una ejecución
     * anterior, cualquier avatar/foto en base64 la revienta con "Data too long".
     *
     * TEXT de MySQL además tiene un límite propio de 65 535 bytes: una foto real comprimida
     * (no un avatar de iniciales chico) en base64 con el prefijo "data:image/jpeg;base64,"
     * fácilmente supera eso (una foto de ~100 KB queda en ~140 000 caracteres). Por eso se usa
     * MEDIUMTEXT (hasta 16 MB), no TEXT. Se fuerza aquí el ALTER TABLE de forma idempotente y
     * segura (no falla si la columna ya es MEDIUMTEXT).
     */
    private void asegurarColumnaFotoAmplia() {
        for (String columna : new String[]{"profile_photo_url", "promo_image_url"}) {
            try {
                jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN " + columna + " MEDIUMTEXT");
                log.info("Columna {} verificada/ampliada a MEDIUMTEXT", columna);
            } catch (Exception e) {
                log.warn("No se pudo ampliar la columna {} a MEDIUMTEXT: {}", columna, e.getMessage());
            }
        }
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
     * Las cuentas base (admin/entrenador/usuario) solo se crean una vez, cuando la tabla
     * está vacía (ver arriba). En instalaciones que ya tenían esas 3 cuentas de antes, el
     * apellido quedó con el placeholder original ("Entrenador"/"Usuario") aunque el código
     * ya use uno real: este método corrige esas filas existentes en cada arranque, igual
     * que repararTelefonosNulos().
     */
    private void repararNombresBase() {
        userRepository.findByEmailIgnoreCase("entrenador@trainingnow.com").ifPresent(u -> {
            if ("Entrenador".equals(u.getLastName())) {
                u.setLastName("Mendoza Silva");
                userRepository.save(u);
            }
        });
        userRepository.findByEmailIgnoreCase("usuario@gmail.com").ifPresent(u -> {
            if ("Usuario".equals(u.getLastName())) {
                u.setLastName("Vargas Reyes");
                userRepository.save(u);
            }
        });
    }

    /**
     * Usuarios de muestra con datos completos (nombre, apellidos, teléfono, datos físicos)
     * para dejar la app lista con contenido de demostración. Se crean una sola vez por email
     * (no dependen de que la tabla esté vacía), así que quedan disponibles aunque ya existan
     * las cuentas base de admin/entrenador/usuario.
     *
     * Junto con las 3 cuentas base de arriba, el roster completo queda en: 3 usuarios
     * normales (Santiago, María Fernanda, Diego Andrés), 3 entrenadores (Carlos, Valentina,
     * Rodrigo) y 2 administradores (Admin TrainingNow, Francisca).
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
                .role("TRAINER")
                .name("Valentina")
                .lastName("Soto Pizarro")
                .email("valentina.soto@trainingnow.com")
                .phone("+56955667788")
                .password(passwordEncoder.encode("demo123"))
                .specializations("Yoga,Movilidad,Rehabilitación")
                .bio(BIO_VALENTINA)
                .build());

        crearSiNoExiste(User.builder()
                .role("TRAINER")
                .name("Rodrigo")
                .lastName("Fuentes Aravena")
                .email("rodrigo.fuentes@trainingnow.com")
                .phone("+56966778899")
                .password(passwordEncoder.encode("demo123"))
                .specializations("Crossfit,Acondicionamiento Físico,Pérdida de grasa")
                .bio(BIO_RODRIGO)
                .build());

        crearSiNoExiste(User.builder()
                .role("ADMIN")
                .name("Francisca")
                .lastName("Torres Bravo")
                .email("francisca.torres@trainingnow.com")
                .phone("+56911223344")
                .password(passwordEncoder.encode("demo123"))
                .build());
    }

    /**
     * Dos cuentas de prueba ya sancionadas (fuera del roster oficial de 3 usuarios/3
     * entrenadores/2 admins), para poder mostrar el flujo de baneo/suspensión al profesor sin tener que aplicar la
     * sanción a mano antes de cada demo. Se crean una sola vez (idempotente); la suspensión
     * queda fijada a 7 días desde el primer arranque en el que se crean.
     */
    private void crearUsuariosSancionadosDePrueba() {
        crearSiNoExiste(User.builder()
                .role("USER")
                .name("Usuario")
                .lastName("Baneado (prueba)")
                .email("baneado.demo@gmail.com")
                .phone("+56900000001")
                .password(passwordEncoder.encode("demo123"))
                .isBanned(true)
                .banReason("Cuenta de prueba: demuestra el bloqueo de login por baneo")
                .build());

        crearSiNoExiste(User.builder()
                .role("USER")
                .name("Usuario")
                .lastName("Suspendido (prueba)")
                .email("suspendido.demo@gmail.com")
                .phone("+56900000002")
                .password(passwordEncoder.encode("demo123"))
                .suspendedUntil(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)
                .suspendReason("Cuenta de prueba: demuestra el bloqueo de login por suspensión (7 días)")
                .build());
    }

    /**
     * Completa teléfono, género, altura, peso y fecha de nacimiento de las cuentas base/demo
     * que quedaron con esos campos en NULL (ej. las 3 cuentas base, creadas antes de tener
     * datos físicos, o cuentas demo agregadas en una versión anterior de este seed). Solo
     * rellena lo que esté vacío: si el usuario ya editó su perfil desde la app, no se pisa
     * nada. Se ejecuta en cada arranque, igual que repararTelefonosNulos/repararNombresBase.
     */
    private void completarDatosDemograficos() {
        for (DatosDemograficos d : DATOS_DEMOGRAFICOS) {
            userRepository.findByEmailIgnoreCase(d.email()).ifPresent(u -> {
                boolean cambio = false;
                if (u.getPhone() == null || u.getPhone().isBlank()) {
                    u.setPhone(d.phone());
                    cambio = true;
                }
                if (u.getGender() == null) {
                    u.setGender(d.gender());
                    cambio = true;
                }
                if (u.getHeight() == null) {
                    u.setHeight(d.height());
                    cambio = true;
                }
                if (u.getWeight() == null) {
                    u.setWeight(d.weight());
                    cambio = true;
                }
                if (u.getBirthDate() == null) {
                    u.setBirthDate(epochMillis(d.anio(), d.mes(), d.dia()));
                    cambio = true;
                }
                if (cambio) userRepository.save(u);
            });
        }
    }

    private void crearSiNoExiste(User user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) return;
        userRepository.save(user);
    }

    /**
     * Relaciones entrenador-cliente de muestra: así "mis clientes" (entrenador) y "mi
     * entrenador" (usuario) no quedan vacíos al probar, y se puede abrir un chat real entre
     * cuentas ya vinculadas sin tener que crear la relación a mano primero.
     */
    private void crearRelacionesEntrenadorCliente() {
        vincular("entrenador@trainingnow.com", "usuario@gmail.com", "ACTIVE", 4);
        vincular("entrenador@trainingnow.com", "maria.gonzalez@gmail.com", "ACTIVE", 3);
        vincular("valentina.soto@trainingnow.com", "diego.munoz@gmail.com", "ACTIVE", 2);
        vincular("rodrigo.fuentes@trainingnow.com", "usuario@gmail.com", "PENDING", 3);
    }

    private void vincular(String emailEntrenador, String emailCliente, String status, int sesionesPorSemana) {
        Optional<User> trainer = userRepository.findByEmailIgnoreCase(emailEntrenador);
        Optional<User> client = userRepository.findByEmailIgnoreCase(emailCliente);
        if (trainer.isEmpty() || client.isEmpty()) return;

        Long trainerId = trainer.get().getId();
        Long clientId = client.get().getId();
        if (trainerClientRepository.findByTrainerIdAndClientId(trainerId, clientId).isPresent()) return;

        trainerClientRepository.save(TrainerClient.builder()
                .trainerId(trainerId)
                .clientId(clientId)
                .status(status)
                .sessionsPerWeek(sesionesPorSemana)
                .startDate(System.currentTimeMillis())
                .build());
    }

    private long epochMillis(int year, int month, int day) {
        return LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.of("America/Santiago"))
                .toInstant()
                .toEpochMilli();
    }

    /**
     * Fotos reales (generadas con IA) de los 3 entrenadores de muestra: foto de perfil
     * (retrato cuadrado, se ve en círculo en toda la app) y foto de la publicación del Foro
     * (imagen horizontal tipo anuncio, ver {@code promoImageUrl}). Los archivos están en
     * resources/avatares/, ya comprimidos (máx. 800px de lado, JPEG, mismo criterio que
     * ImageCompressor.kt en la app). Se cargan una sola vez: si el entrenador ya tiene su
     * propia foto/publicación subida desde la app, no se pisa.
     */
    /** Bios completas (inventadas para la demo: años de experiencia + dónde se titularon),
     *  que se muestran en la vista completa de la publicación del Foro. Las bios cortas
     *  originales del seed no tenían nada de esto. */
    private static final String BIO_CARLOS =
            "Entrenador certificado con más de 10 años de experiencia en fuerza e hipertrofia. "
            + "Se tituló en Educación Física en la Universidad de Chile y se especializó en "
            + "Preparación Física en el Instituto Nacional del Deporte. Te ayudo a progresar con "
            + "rutinas simples, medibles y sostenibles en el tiempo.";
    private static final String BIO_VALENTINA =
            "Kinesióloga y profesora de yoga con 7 años de experiencia en movilidad y "
            + "rehabilitación de lesiones deportivas. Se tituló en la Universidad Andrés Bello y "
            + "se certificó como instructora de yoga en Pilates Institute Chile. Combina ejercicios "
            + "terapéuticos con clases de yoga para complementar tu entrenamiento de fuerza.";
    private static final String BIO_RODRIGO =
            "Entrenador con 9 años de experiencia en Crossfit y acondicionamiento físico. Se "
            + "tituló como Preparador Físico en el instituto IPP Chile y cuenta con certificación "
            + "Crossfit Level 2. Entreno gente común que quiere sentirse mejor: fuerza funcional, "
            + "resistencia y hábitos que se mantienen en el tiempo.";

    /** Bios cortas originales del seed, para detectar cuentas que quedaron con la versión vieja
     *  (sin años de experiencia ni titulación) y así completarlas sin pisar una bio que el
     *  entrenador ya haya editado a mano desde la app. */
    private static final Map<String, String> BIO_ANTIGUA_POR_EMAIL = Map.of(
            "entrenador@trainingnow.com", "Entrenador certificado con foco en fuerza e hipertrofia. Te ayudo a progresar con rutinas simples y sostenibles.",
            "valentina.soto@trainingnow.com", "Especialista en movilidad y rehabilitación de lesiones. Clases de yoga para complementar tu entrenamiento de fuerza.",
            "rodrigo.fuentes@trainingnow.com", "Entreno gente común que quiere sentirse mejor: fuerza funcional, resistencia y hábitos que se mantienen en el tiempo."
    );

    private static final Map<String, String> BIO_NUEVA_POR_EMAIL = Map.of(
            "entrenador@trainingnow.com", BIO_CARLOS,
            "valentina.soto@trainingnow.com", BIO_VALENTINA,
            "rodrigo.fuentes@trainingnow.com", BIO_RODRIGO
    );

    /**
     * Completa la bio de los 3 entrenadores de muestra con datos completos (años de experiencia,
     * dónde se titularon) para que la vista completa de su publicación en el Foro no se vea a
     * medio terminar. Solo reemplaza si la bio está vacía o todavía es la versión corta original
     * del seed; si el entrenador ya la editó desde la app, no se toca.
     */
    private void completarBiosEntrenadores() {
        BIO_NUEVA_POR_EMAIL.forEach((email, bioNueva) -> {
            userRepository.findByEmailIgnoreCase(email).ifPresent(u -> {
                String bioActual = u.getBio();
                boolean esPlaceholder = bioActual == null || bioActual.isBlank()
                        || bioActual.equals(BIO_ANTIGUA_POR_EMAIL.get(email));
                if (esPlaceholder && !bioNueva.equals(bioActual)) {
                    u.setBio(bioNueva);
                    userRepository.save(u);
                    log.info("Bio completa asignada a {}", email);
                }
            });
        });
    }

    private static final Map<String, String[]> FOTOS_REALES_ENTRENADORES = Map.of(
            "entrenador@trainingnow.com", new String[]{"carlos_perfil.jpg", "carlos_foro.jpg"},
            "valentina.soto@trainingnow.com", new String[]{"valentina_perfil.jpg", "valentina_foro.jpg"},
            "rodrigo.fuentes@trainingnow.com", new String[]{"rodrigo_perfil.jpg", "rodrigo_foro.jpg"}
    );

    private void asignarFotosRealesEntrenadores() {
        FOTOS_REALES_ENTRENADORES.forEach((email, archivos) -> {
            userRepository.findByEmailIgnoreCase(email).ifPresent(u -> {
                boolean cambio = false;
                // "Vacío" O sigue siendo el avatar de iniciales autogenerado (repararFotosDePerfil
                // ya le había puesto uno a estas cuentas demo antes de tener foto real). Ese avatar
                // se guarda como PNG; una foto real subida por la app siempre es JPEG. Si ya es
                // JPEG, es porque el entrenador subió su propia foto desde la app: no se pisa.
                boolean esPlaceholder = u.getProfilePhotoUrl() == null || u.getProfilePhotoUrl().isBlank()
                        || u.getProfilePhotoUrl().startsWith("data:image/png");
                if (esPlaceholder) {
                    String dataUri = cargarImagenComoDataUri("avatares/" + archivos[0]);
                    if (dataUri != null) {
                        u.setProfilePhotoUrl(dataUri);
                        cambio = true;
                    }
                }
                if (u.getPromoImageUrl() == null || u.getPromoImageUrl().isBlank()) {
                    String dataUri = cargarImagenComoDataUri("avatares/" + archivos[1]);
                    if (dataUri != null) {
                        u.setPromoImageUrl(dataUri);
                        cambio = true;
                    }
                }
                if (cambio) {
                    userRepository.save(u);
                    log.info("Foto real asignada a {}", email);
                }
            });
        });
    }

    /** Lee un archivo JPEG del classpath (resources/) y lo devuelve como data URI base64. */
    private String cargarImagenComoDataUri(String rutaClasspath) {
        try (InputStream in = new ClassPathResource(rutaClasspath).getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.warn("No se pudo cargar la imagen {}: {}", rutaClasspath, e.getMessage());
            return null;
        }
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
        try {
            userRepository.saveAll(sinFoto);
        } catch (Exception e) {
            // No debe tumbar el arranque del microservicio por un problema al generar avatares.
            log.warn("No se pudieron guardar los avatares generados: {}", e.getMessage());
        }
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
