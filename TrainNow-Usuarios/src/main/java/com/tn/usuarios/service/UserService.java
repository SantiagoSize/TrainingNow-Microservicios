package com.tn.usuarios.service;

import com.tn.usuarios.dto.UserDto;
import com.tn.usuarios.exception.DuplicateEmailException;
import com.tn.usuarios.exception.InvalidCredentialsException;
import com.tn.usuarios.exception.ResourceNotFoundException;
import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de usuarios: CRUD, login y búsquedas por rol.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String ROLE_TRAINER = "TRAINER";
    private static final String ROLE_CLIENT = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public UserDto create(UserDto dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateEmailException("El email ya existe: " + dto.getEmail());
        }
        User user = dto.toEntity();
        user.setId(null);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return UserDto.fromEntity(userRepository.save(user));
    }

    public UserDto update(Long id, UserDto dto) {
        User existing = findOrThrow(id);

        // Email único si cambió
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(existing.getEmail())
                && userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateEmailException("El email ya existe: " + dto.getEmail());
        }

        existing.setRole(dto.getRole() != null ? dto.getRole() : existing.getRole());
        existing.setName(dto.getName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail() != null ? dto.getEmail() : existing.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        existing.setBirthDate(dto.getBirthDate());
        existing.setHeight(dto.getHeight());
        existing.setWeight(dto.getWeight());
        existing.setGender(dto.getGender());
        existing.setSpecializations(dto.getSpecializations());
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

    public void delete(Long id) {
        userRepository.delete(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public UserDto login(String email, String rawPassword) {
        User user = userRepository.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));
        if (rawPassword == null || !passwordEncoder.matches(rawPassword.trim(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales incorrectas");
        }
        return UserDto.fromEntity(user);
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
        return userRepository
                .findByRoleAndNameContainingIgnoreCaseOrRoleAndLastNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
                        role, q, role, q, role, q)
                .stream().map(UserDto::fromEntity).toList();
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: id=" + id));
    }
}
