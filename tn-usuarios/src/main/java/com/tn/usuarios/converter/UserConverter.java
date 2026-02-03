package com.tn.usuarios.converter;

import com.tn.usuarios.dto.RegisterRequestDTO;
import com.tn.usuarios.dto.UserDTO;
import com.tn.usuarios.model.Role;
import com.tn.usuarios.model.User;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad User y los DTOs de registro y perfil.
 * Soporta datos personales, biométricos y relación con Role.
 */
@Component
public class UserConverter {

    /**
     * Convierte RegisterRequestDTO a entidad User (contraseña ya encriptada, rol asignado).
     */
    public User toEntity(RegisterRequestDTO request, String encodedPassword, Role role) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .telefono(request.getTelefono())
                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())
                .pesoActual(request.getPesoActual())
                .altura(request.getAltura())
                .objetivo(request.getObjetivo())
                .role(role)
                .activo(true)
                .build();
    }

    /**
     * Convierte entidad User a UserDTO (perfil sin contraseña).
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        String rolName = user.getRole() != null ? user.getRole().getName() : null;
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getApellidos(),
                user.getTelefono(),
                user.getFechaNacimiento(),
                user.getGenero(),
                user.getPesoActual(),
                user.getAltura(),
                user.getObjetivo(),
                rolName,
                user.getActivo(),
                user.getCreatedAt()
        );
    }
}
