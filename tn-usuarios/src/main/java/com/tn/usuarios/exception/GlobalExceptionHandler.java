package com.tn.usuarios.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo global de excepciones. Excluido de la documentación OpenAPI para evitar
 * incompatibilidad con SpringDoc en Spring Boot 3.5 (ControllerAdviceBean).
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
}
