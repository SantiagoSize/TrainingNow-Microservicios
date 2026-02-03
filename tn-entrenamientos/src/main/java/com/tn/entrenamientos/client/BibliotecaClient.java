package com.tn.entrenamientos.client;

import com.tn.entrenamientos.client.dto.EjercicioDTO;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BibliotecaClient {

    private final RestClient restClient;

    public BibliotecaClient(@Value("${clients.biblioteca.base-url:http://localhost:8082}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(Objects.requireNonNull(baseUrl, "baseUrl must not be null"))
                .build();
    }

    public EjercicioDTO getEjercicioById(Long ejercicioId) {
        ResponseEntity<EjercicioDTO> response = restClient.get()
                .uri("/api/ejercicios/{id}", ejercicioId)
                .retrieve()
                .toEntity(EjercicioDTO.class);
        return response.getBody();
    }
}
