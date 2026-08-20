# Training Now! — Microservicios

Backend de **Training Now!**, una app de entrenamiento físico que conecta usuarios, entrenadores personales y administradores de gimnasio. Este repo contiene 4 microservicios independientes en Spring Boot 4.1.0 (Java 17, Maven), cada uno con su propia base de datos MySQL.

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| `TrainNow-Usuarios` | 8081 | Usuarios, login/JWT, sanciones (ban/suspensión), auditoría, reportes de usuario |
| `TrainNow-Biblioteca` | 8082 | Categorías y ejercicios (biblioteca de la app) |
| `TrainNow-Rutinas` | 8083 | Rutinas, sesiones de entrenamiento (workouts), asistencia |
| `TrainNow-Comunicaciones` | 8084 | Chat/mensajes (con adjuntos) y notificaciones |

Consumidos por la app Android **Training Now!** (repo hermano `TrainingNow`) vía Retrofit, y documentados con Swagger/OpenAPI en cada servicio.

## Puesta en marcha local

Requisitos: JDK 17, Maven, MySQL (dev: Laragon, `root` sin password, puerto 3306, `createDatabaseIfNotExist=true` — las 4 bases se crean solas al primer arranque).

Por cada servicio (en su propia terminal, en este orden no importa salvo que Usuarios conviene primero para generar tokens de prueba):

```
cd TrainNow-Usuarios
mvn clean compile
mvn spring-boot:run
```

Repetir para `TrainNow-Biblioteca`, `TrainNow-Rutinas`, `TrainNow-Comunicaciones`. Cada uno levanta en su puerto, crea/actualiza su esquema (`ddl-auto=update`) y siembra datos de demo (`config/DataLoader.java` de cada servicio; ver `CREDENCIALES_USUARIOS.txt` en este repo para las cuentas de prueba).

## Documentación de la API (Swagger)

Con los servicios corriendo:

- Usuarios: http://localhost:8081/swagger-ui.html
- Biblioteca: http://localhost:8082/swagger-ui.html
- Rutinas: http://localhost:8083/swagger-ui.html
- Comunicaciones: http://localhost:8084/swagger-ui.html

Todos los endpoints tienen `@ApiResponses` documentando los códigos HTTP posibles (200/201/204/400/403/404/409, etc.). Usuarios y Biblioteca además exponen el botón **Authorize** (JWT bearer) porque tienen endpoints protegidos — ver sección Seguridad.

## Endpoints por servicio

### TrainNow-Usuarios (`/api/users`, `/api/trainer-clients`, `/api/reports`, `/api/audit-logs`)
- `UserController` — `GET /`, `/trainers`, `/trainers/search`, `/clients`, `/clients/search`, `/email/{email}`, `/{id}`; `POST /login`, `/` (registro), `/admin-create`; `PATCH /{id}/ban|unban|suspend|unsuspend|heartbeat`; `PUT /{id}`; `DELETE /{id}`.
- `PasswordResetController` — `POST /api/users/password-reset/request|verify|confirm`.
- `TrainerClientController` — `GET /trainer/{trainerId}`, `/trainer/{trainerId}/status/{status}`, `/client/{clientId}`; `POST /`.
- `ReportController` — `POST /`, `GET /`, `PATCH /{id}/resolve`.
- `AuditLogController` — `POST /`, `GET /` (filtro opcional `targetType`).

### TrainNow-Biblioteca (`/api/categories`, `/api/exercises`)
- `CategoriaController` — `GET /`; `POST /`; `PUT /{oldName}` (renombrar); `DELETE /{name}`.
- `EjercicioController` — `GET /`, `/search`, `/category/{category}`, `/{id}`; `POST /`; `PUT /{id}`; `DELETE /{id}`.

### TrainNow-Rutinas (`/api/routines`, `/api/workouts`, `/api/attendance`)
- `RutinaController` — `GET /`, `/public`, `/owner/{ownerId}`, `/creator/{creatorId}`, `/{id}`, `/{id}/exercises`; `POST /`, `/{id}/exercises`; `PUT /{id}`; `DELETE /{id}`.
- `WorkoutController` — `GET /sessions/user/{userId}`, `/sessions/user/{userId}/status/{status}`, `/sessions/{id}`, `/sessions/{sessionId}/logs`; `POST /sessions`, `/logs`; `PUT /sessions/{id}`; `DELETE /sessions/{id}`.
- `AttendanceController` — `POST /`; `GET /user/{userId}`, `/user/{userId}/report/{month}` (formato `yyyy-MM`, calcula adherencia/rachas).

### TrainNow-Comunicaciones (`/api/messages`, `/api/notifications`)
- `MensajeController` — `GET /conversation/{userA}/{userB}`, `/user/{userId}`, `/conversations/{userId}`; `POST /` (texto), `/upload` (multipart, imagen/video); `PATCH /{id}/read`.
- `NotificacionController` — `GET /user/{userId}`, `/{id}`; `POST /`; `PUT /{id}`; `PATCH /{id}/read`; `DELETE /{id}`.

## Seguridad

Cada servicio tiene su propio `SecurityConfig.java` (paquete `security`): CSRF deshabilitado (API REST stateless), CORS abierto, sesión `STATELESS`, y un `JwtAuthenticationFilter extends OncePerRequestFilter` que lee `Authorization: Bearer <token>`, valida la firma HMAC-SHA256 (secreto compartido `jwt.secret`) y, si es válido, publica en el `SecurityContext` una autoridad `ROLE_<rol>`. El filtro nunca lanza excepción: si el token falta o es inválido, la request sigue como anónima y `authorizeHttpRequests()` decide.

- **TrainNow-Usuarios** (`src/main/java/com/tn/usuarios/security/SecurityConfig.java`): exigen rol `ADMIN` — `POST /api/users/admin-create`, `PATCH /api/users/*/ban|unban|suspend|unsuspend`, `DELETE /api/users/*`, `POST`/`GET /api/audit-logs`, `GET /api/reports`, `PATCH /api/reports/*/resolve`. Todo lo demás (login, registro, listar, buscar, `PUT /api/users/{id}` para editar el propio perfil) sigue abierto. 401 → `{"error":"Falta el token de autorización"}`, 403 → `{"error":"Operación permitida solo para administradores"}`.
  - Segunda capa manual: `UserService.requireActiveAdmin(authHeader)` — además de exigir rol ADMIN, verifica que ese admin no esté baneado/suspendido (algo que un JWT emitido antes de la sanción no reflejaría).
- **TrainNow-Biblioteca** (`src/main/java/com/tn/biblioteca/security/SecurityConfig.java`): lectura de categorías/ejercicios abierta; `POST`/`PUT`/`DELETE` en `/api/categories/**` y `/api/exercises/**` exigen rol `ADMIN`. 403 → `{"error":"Solo un administrador puede modificar la biblioteca"}`.
  - Segunda capa manual: `JwtValidator.requireAdmin(authHeader)` (`security/JwtValidator.java`), mismo criterio.
- **TrainNow-Rutinas** y **TrainNow-Comunicaciones**: sin endpoints restringidos por rol (`anyRequest().permitAll()`); el filtro JWT igual está activo y deja la infraestructura lista, pero no bloquea nada hoy.

Cómo probarlo: login como admin (`POST /api/users/login`) → tomar el `token` de la respuesta → en Swagger UI de Usuarios/Biblioteca, botón **Authorize**, pegar solo el token (sin la palabra "Bearer") → los "Try it out" ya mandan el header solos. Detalle completo de cuentas y pasos en `CREDENCIALES_USUARIOS.txt`.

Passwords siempre hasheadas con BCrypt (`PasswordEncoder` en `SecurityConfig` de Usuarios).

## Validaciones

**Bean Validation** (anotaciones en DTOs/entidades — rechazo automático con 400 vía `GlobalExceptionHandler`):
- `TrainNow-Usuarios/src/main/java/com/tn/usuarios/dto/LoginRequest.java` — email/password `@NotBlank`.
- `TrainNow-Usuarios/.../dto/PasswordResetConfirm.java` — email/code `@NotBlank`, `newPassword` `@NotBlank @Size(min=6)`.
- `TrainNow-Usuarios/.../model/User.java` — role/name/email/password `@NotBlank`, email `@Email`.
- `TrainNow-Usuarios/.../model/TrainerClient.java` — trainerId/clientId `@NotNull`.
- `TrainNow-Biblioteca/.../model/Ejercicio.java` — name/category `@NotBlank`.
- `TrainNow-Biblioteca/.../model/Categoria.java` — name `@NotBlank`.
- `TrainNow-Rutinas/.../model/Rutina.java` — name `@NotBlank`.
- `TrainNow-Rutinas/.../model/WorkoutSession.java` — userId `@NotNull`.
- `TrainNow-Rutinas/.../model/AttendanceDay.java` — userId/date `@NotNull`.
- `TrainNow-Comunicaciones/.../model/Mensaje.java` — senderId/receiverId `@NotNull`, content `@NotBlank`.
- `TrainNow-Comunicaciones/.../model/Notificacion.java` — userId `@NotNull`, title/message `@NotBlank`.

**Validaciones de negocio** (todas en `TrainNow-Usuarios/src/main/java/com/tn/usuarios/service/UserService.java`):
- `isStaffEmail()` — dominio `@trainingnow.com` reservado a personal (admin/entrenador).
- `create()` — registro público prohíbe dominio staff y fuerza rol `USER`; email duplicado rechazado.
- `createByAdmin()` — exige `@trainingnow.com` para ADMIN/TRAINER (lo prohíbe para USER), exige `specializations` si es TRAINER.
- `update()` — email y rol son inmutables una vez creada la cuenta.
- `delete()` — bloquea eliminar al último ADMIN del sistema.
- `login()` — bloquea acceso si `isBanned` o `suspendedUntil` está en el futuro, devolviendo el motivo.
- `banUser()` / `suspendUser()` — no se puede sancionar a otro ADMIN; motivo obligatorio; fecha de suspensión debe ser futura.

## Tests

Todos los tests de integración usan `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvc`, contra una base **H2 en memoria** (`MODE=MySQL`, `ddl-auto=create-drop`) configurada en `src/test/resources/application.properties` de cada servicio — no tocan la MySQL de desarrollo. Los filtros de Spring Security están activos en los tests (se construyen tokens JWT reales para los casos admin-only).

- **TrainNow-Usuarios**: `TnUsuariosApplicationTests` (smoke test), `UserControllerIntegrationTest` (registro/login/listado), `ReportControllerIntegrationTest` (crear reporte libre vs listar/resolver solo-admin), `PasswordResetIntegrationTest` (flujo request→verify→confirm), `AuditLogControllerIntegrationTest` (guardar/listar, exige token admin).
- **TrainNow-Biblioteca**: `TnBibliotecaApplicationTests`, `EjercicioControllerIntegrationTest`, `CategoriaControllerIntegrationTest` (CRUD completo, JWT admin construido a mano con el mismo secreto HMAC).
- **TrainNow-Rutinas**: `TnRutinasApplicationTests`, `AttendanceIntegrationTest` (asistencia + cálculo de adherencia/rachas), `RutinaWorkoutIntegrationTest` (seed de rutinas públicas, crear rutina + ejercicios).
- **TrainNow-Comunicaciones**: `TnComunicacionesApplicationTests`, `MensajeControllerIntegrationTest` (enviar/leer conversación), `NotificacionControllerIntegrationTest` (seed, crear, marcar leída, eliminar).

Ejecutar en cada servicio: `mvn test`.

## Estructura de carpetas (idéntica en los 4 servicios, paquete base `com.tn.<servicio>`)

```
src/main/java/com/tn/<servicio>/
├── controller/    endpoints REST
├── service/       lógica de negocio y validaciones manuales
├── repository/    Spring Data JPA
├── model/         entidades JPA (+ anotaciones Bean Validation)
├── dto/           objetos de entrada/salida (+ anotaciones Bean Validation)
├── security/      SecurityConfig, JwtAuthenticationFilter (+ JwtService/JwtValidator en Usuarios/Biblioteca)
├── config/        DataLoader (seed de datos demo), SwaggerConfig
├── exception/      GlobalExceptionHandler y excepciones custom
src/test/java/com/tn/<servicio>/   tests de integración (MockMvc + H2)
src/main/resources/application.properties   config de BD, puerto, jwt.secret
```

## Stack técnico

Spring Boot 4.1.0, Spring Security, Spring Data JPA, Bean Validation, MySQL (runtime) / H2 (test), Lombok, springdoc-openapi 3.1.0. Sin librería JWT externa: el token se firma y valida a mano con `javax.crypto.Mac` (HMAC-SHA256).

## Credenciales de prueba

Ver `CREDENCIALES_USUARIOS.txt` en la raíz de este repo: usuarios/contraseñas por rol, links de Swagger y cómo obtener/usar el token JWT.
