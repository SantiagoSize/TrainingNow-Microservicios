package com.tn.biblioteca.exception;

/**
 * Manejador global de excepciones.
 * <p>
 * Nota: no se registra como {@code @RestControllerAdvice} porque,
 * con la versión actual de Springdoc + Spring Boot, esto provoca
 * un error interno al generar la especificación OpenAPI.
 * <p>
 * Si en el futuro actualizas Springdoc a una versión compatible,
 * puedes anotar esta clase con {@code @RestControllerAdvice} y
 * añadir métodos con {@code @ExceptionHandler} según tus necesidades.
 */
public class GlobalExceptionHandler {
}
