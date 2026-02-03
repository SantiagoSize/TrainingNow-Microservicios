package com.tn.usuarios.service;

import com.tn.usuarios.converter.UserConverter;
import com.tn.usuarios.dto.*;
import com.tn.usuarios.model.Role;
import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.RoleRepository;
import com.tn.usuarios.repository.UserRepository;
import com.tn.usuarios.security.CustomUserDetails;
import com.tn.usuarios.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    @SuppressWarnings("null")
    public LoginResponseDTO register(@NonNull RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
        }
        Role role = roleRepository.findByName(request.getRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + request.getRol()));
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userConverter.toEntity(request, encodedPassword, role);
        User savedUser = Objects.requireNonNull(userRepository.save(user), "saved user");
        String token = jwtUtil.generateToken(savedUser.getEmail());
        UserDTO userDTO = userConverter.toDTO(savedUser);
        return new LoginResponseDTO(token, "Bearer", userDTO);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        if (!Boolean.TRUE.equals(user.getActivo())) {
            throw new BadCredentialsException("Usuario desactivado");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        UserDTO userDTO = userConverter.toDTO(user);
        return new LoginResponseDTO(token, "Bearer", userDTO);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = Objects.requireNonNull(userDetails.getUser(), "user must not be null");
        return userConverter.toDTO(user);
    }
}
