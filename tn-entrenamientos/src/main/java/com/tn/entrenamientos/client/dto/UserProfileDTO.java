package com.tn.entrenamientos.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Perfil remoto del usuario desde el microservicio de usuarios")
public class UserProfileDTO {

    private Long id;
    private String email;
    private String nombre;
    private String apellidos;
    private String genero;
    private Double pesoActual;
    private Integer altura;
    private String objetivo;
}

