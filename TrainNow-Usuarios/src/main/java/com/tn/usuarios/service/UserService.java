package com.tn.usuarios.service;

import com.tn.usuarios.dto.UserDto;
import com.tn.usuarios.exception.DuplicateEmailException;
import com.tn.usuarios.exception.ForbiddenOperationException;
import com.tn.usuarios.exception.InvalidCredentialsException;
import com.tn.usuarios.exception.ResourceNotFoundException;
import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.TrainerClientRepository;
import com.tn.usuarios.repository.UserRepository;
import com.tn.usuarios.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Lógica de negocio de usuarios: CRUD, login y búsquedas por rol.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    /** Dominio corporativo reservado para el personal (admins y entrenadores). */
    private static final String STAFF_DOMAIN = "@trainingnow.com";

    /** Dominios de correo aceptados en el registro público (usuarios normales). */
    private static final Set<String> DOMINIOS_PERMITIDOS = Set.of(
            "gmail.com", "hotmail.com", "outlook.com", "yahoo.com");
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_TRAINER = "TRAINER";
    private static final String ROLE_CLIENT = "USER";

    private final UserRepository userRepository;
    private final TrainerClientRepository trainerClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return UserDto.fromEntity(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public UserDto getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }

    /**
     * Registro público (desde la app). Reglas de seguridad:
     * - El dominio corporativo @trainingnow.com está prohibido: solo el admin crea staff.
     * - El rol se fuerza siempre a USER, ignore lo que envíe el cliente.
     */
    public UserDto create(UserDto dto) {
        if (isStaffEmail(dto.getEmail())) {
            throw new ForbiddenOperationException(
                    "El dominio @trainingnow.com está reservado para el personal. Contacta a un administrador.");
        }
        if (!isDominioPermitido(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Solo se aceptan correos de: " + String.join(", ", DOMINIOS_PERMITIDOS));
        }
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateEmailException("El email ya existe: " + dto.getEmail());
        }
        User user = dto.toEntity();
        user.setId(null);
        user.setRole(ROLE_CLIENT); // nunca ADMIN/TRAINER por registro público
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return UserDto.fromEntity(userRepository.save(user));
    }

    /**
     * Creación de usuarios por un administrador. Reglas:
     * - adminId debe pertenecer a un ADMIN activo (ni baneado ni suspendido).
     * - Roles ADMIN y TRAINER exigen correo @trainingnow.com.
     * - Rol USER no puede usar el dominio corporativo.
     */
    public UserDto createByAdmin(Long adminId, UserDto dto) {
        // adminId ya viene validado por requireActiveAdmin (token JWT)
        String role = dto.getRole() == null ? ROLE_CLIENT : dto.getRole().toUpperCase();
        if (!role.equals(ROLE_ADMIN) && !role.equals(ROLE_TRAINER) && !role.equals(ROLE_CLIENT)) {
            throw new IllegalArgumentException("Rol inválido: " + role);
        }
        boolean staffRole = role.equals(ROLE_ADMIN) || role.equals(ROLE_TRAINER);
        if (staffRole && !isStaffEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Los usuarios ADMIN y TRAINER deben usar correo corporativo @trainingnow.com");
        }
        if (!staffRole && isStaffEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "El dominio @trainingnow.com es exclusivo del personal (ADMIN/TRAINER)");
        }
        if (role.equals(ROLE_TRAINER)
                && (dto.getSpecializations() == null || dto.getSpecializations().isBlank())) {
            throw new IllegalArgumentException("La especialidad es obligatoria para entrenadores");
        }
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateEmailException("El email ya existe: " + dto.getEmail());
        }

        User user = dto.toEntity();
        user.setId(null);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return UserDto.fromEntity(userRepository.save(user));
    }

    private boolean isStaffEmail(String email) {
        return email != null && email.trim().toLowerCase().endsWith(STAFF_DOMAIN);
    }

    /** Dominio permitido para registro público: gmail/hotmail/outlook/yahoo. */
    private boolean isDominioPermitido(String email) {
        if (email == null || !email.contains("@")) return false;
        String dominio = email.substring(email.indexOf('@') + 1).trim().toLowerCase();
        return DOMINIOS_PERMITIDOS.contains(dominio);
    }

    /**
     * Edición de perfil (self-service, sin token de admin). Email y rol son
     * intencionalmente inmutables por esta vía: ningún usuario (ni admin ni
     * entrenador) puede cambiarse su propio correo o escalar su rol editando
     * su perfil. Cambios de rol solo existen al crear la cuenta (createByAdmin).
     */
    public UserDto update(Long id, UserDto dto) {
        User existing = findOrThrow(id);

        existing.setName(dto.getName());
        existing.setLastName(dto.getLastName());
        existing.setPhone(dto.getPhone());
        existing.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        existing.setBirthDate(dto.getBirthDate());
        existing.setHeight(dto.getHeight());
        existing.setWeight(dto.getWeight());
        existing.setGender(dto.getGender());
        existing.setSpecializations(dto.getSpecializations());
        existing.setBio(dto.getBio());
        existing.setPromoImageUrl(dto.getPromoImageUrl());
        existing.setSuspendedUntil(dto.getSuspendedUntil());
        existing.setSuspendReason(dto.getSuspendReason());
        existing.setIsBanned(dto.getIsBanned() != null ? dto.getIsBanned() : false);
        existing.setBanReason(dto.getBanReason());

        // Password: si viene vacío o es el mismo hash devuelto por GET, se conserva.
        String incoming = dto.getPassword();
        if (incoming != null && !incoming.isBlank() && !incoming.equals(existing.getPassword())) {
            existing.setPassword(passwordEncoder.encode(incoming));
        }

        return UserDto.fromEntity(userRepository.save(existing));
    }

    /**
     * Elimina una cuenta. Antes de admin/entrenador no había ninguna validación: se podía
     * borrar el último administrador del sistema (dejando la app sin nadie que administre), y
     * borrar un entrenador dejaba sus filas en trainer_clients "colgando" (trainerId apuntando
     * a un usuario que ya no existe). Ahora:
     * - Bloquea borrar el último ADMIN.
     * - Borra en cascada las relaciones trainer_clients donde el usuario es entrenador o
     *   cliente, para no dejar referencias huérfanas en esta base de datos.
     */
    public void delete(Long id) {
        User target = findOrThrow(id);
        if (ROLE_ADMIN.equals(target.getRole()) && userRepository.findByRole(ROLE_ADMIN).size() <= 1) {
            throw new ForbiddenOperationException("No puedes eliminar al último administrador del sistema");
        }
        trainerClientRepository.deleteAll(trainerClientRepository.findByTrainerId(id));
        trainerClientRepository.deleteAll(trainerClientRepository.findByClientId(id));
        userRepository.delete(target);
    }

    /**
     * "Heartbeat" de presencia: la app llama esto cada cierto tiempo mientras el usuario
     * tiene la app abierta, para que otros (ej. en el chat) puedan ver si sigue conectado.
     * No valida token a propósito: es un ping de bajo costo, no una operación sensible.
     */
    public void heartbeat(Long id) {
        User user = findOrThrow(id);
        user.setLastActiveAt(System.currentTimeMillis());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto login(String email, String rawPassword) {
        User user = userRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));
        if (rawPassword == null || !passwordEncoder.matches(rawPassword.trim(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales incorrectas");
        }
        // Bloqueos por sanciones
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            String motivo = user.getBanReason() != null ? " Motivo: " + user.getBanReason() : "";
            throw new ForbiddenOperationException("Tu cuenta fue baneada permanentemente." + motivo);
        }
        if (user.getSuspendedUntil() != null && user.getSuspendedUntil() > System.currentTimeMillis()) {
            String hasta = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm")
                    .format(new java.util.Date(user.getSuspendedUntil()));
            String motivo = user.getSuspendReason() != null ? " Motivo: " + user.getSuspendReason() : "";
            throw new ForbiddenOperationException("Tu cuenta está suspendida hasta el " + hasta + "." + motivo);
        }
        UserDto dto = UserDto.fromEntity(user);
        dto.setToken(jwtService.createToken(user.getId(), user.getRole(), user.getEmail()));
        return dto;
    }

    // ==================== Sanciones (solo admin, validado por token) ====================

    public UserDto banUser(Long targetId, String reason) {
        User target = findOrThrow(targetId);
        if (ROLE_ADMIN.equals(target.getRole())) {
            throw new ForbiddenOperationException("No se puede sancionar a otro administrador");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo del baneo es obligatorio");
        }
        target.setIsBanned(true);
        target.setBanReason(reason);
        return UserDto.fromEntity(userRepository.save(target));
    }

    public UserDto unbanUser(Long targetId) {
        User target = findOrThrow(targetId);
        target.setIsBanned(false);
        target.setBanReason(null);
        return UserDto.fromEntity(userRepository.save(target));
    }

    public UserDto suspendUser(Long targetId, Long untilMillis, String reason) {
        User target = findOrThrow(targetId);
        if (ROLE_ADMIN.equals(target.getRole())) {
            throw new ForbiddenOperationException("No se puede sancionar a otro administrador");
        }
        if (untilMillis == null || untilMillis <= System.currentTimeMillis()) {
            throw new IllegalArgumentException("La fecha de fin de suspensión debe ser futura");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo de la suspensión es obligatorio");
        }
        target.setSuspendedUntil(untilMillis);
        target.setSuspendReason(reason);
        return UserDto.fromEntity(userRepository.save(target));
    }

    public UserDto unsuspendUser(Long targetId) {
        User target = findOrThrow(targetId);
        target.setSuspendedUntil(null);
        target.setSuspendReason(null);
        return UserDto.fromEntity(userRepository.save(target));
    }

    /** Verifica que el token pertenezca a un ADMIN activo. Devuelve su id. */
    @Transactional(readOnly = true)
    public Long requireActiveAdmin(String authHeader) {
        JwtService.TokenClaims claims = jwtService.fromAuthHeader(authHeader);
        User admin = userRepository.findById(claims.userId())
                .orElseThrow(() -> new ForbiddenOperationException("Operación permitida solo para administradores"));
        boolean suspended = admin.getSuspendedUntil() != null
                && admin.getSuspendedUntil() > System.currentTimeMillis();
        if (!ROLE_ADMIN.equals(admin.getRole()) || Boolean.TRUE.equals(admin.getIsBanned()) || suspended) {
            throw new ForbiddenOperationException("Operación permitida solo para administradores");
        }
        return admin.getId();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getTrainers() {
        return userRepository.findByRole(ROLE_TRAINER).stream().map(UserDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchTrainers(String q) {
        return searchByRole(ROLE_TRAINER, q);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getClients() {
        return userRepository.findByRole(ROLE_CLIENT).stream().map(UserDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchClients(String q) {
        return searchByRole(ROLE_CLIENT, q);
    }

    private List<UserDto> searchByRole(String role, String q) {
        if (q == null || q.isBlank()) {
            return userRepository.findByRole(role).stream().map(UserDto::fromEntity).toList();
        }
        return userRepository.searchByRoleAndText(role, q.trim())
                .stream().map(UserDto::fromEntity).toList();
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: id=" + id));
    }
}
