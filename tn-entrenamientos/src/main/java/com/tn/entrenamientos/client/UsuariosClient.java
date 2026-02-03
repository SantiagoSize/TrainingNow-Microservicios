package com.tn.entrenamientos.client;

import com.tn.entrenamientos.client.dto.UserProfileDTO;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UsuariosClient {

    private final RestClient restClient;

    public UsuariosClient(@Value("${clients.usuarios.base-url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(Objects.requireNonNull(baseUrl, "baseUrl must not be null"))
                .build();
    }

    /**
     * Obtiene el perfil del usuario por su ID. En este punto asumimos
     * que el microservicio de usuarios expone un endpoint compatible.
     */
    public UserProfileDTO getUserById(Long userId) {
        ResponseEntity<UserProfileDTO> response = restClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .toEntity(UserProfileDTO.class);
        return response.getBody();
    }
}
