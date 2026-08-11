package com.tn.usuarios.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente REST de EmailJS (https://www.emailjs.com).
 * Envía el correo de recuperación usando una plantilla configurada en el dashboard.
 *
 * Configuración en application.properties:
 *   emailjs.enabled=true|false   (false = modo dev: imprime el código en consola)
 *   emailjs.service-id=service_xxx
 *   emailjs.template-id=template_xxx
 *   emailjs.public-key=xxxxx     (Public Key de la cuenta)
 *   emailjs.private-key=xxxxx    (Private Key: Account -> Security)
 */
@Component
@Slf4j
public class EmailJsClient {

    @Value("${emailjs.enabled:false}")
    private boolean enabled;

    @Value("${emailjs.service-id:}")
    private String serviceId;

    @Value("${emailjs.template-id:}")
    private String templateId;

    @Value("${emailjs.public-key:}")
    private String publicKey;

    @Value("${emailjs.private-key:}")
    private String privateKey;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    /**
     * Envía el código de recuperación al correo del usuario.
     * En modo dev (enabled=false) solo lo registra en el log.
     */
    public void sendResetCode(String toEmail, String userName, String code) {
        if (!enabled) {
            log.warn("[EmailJS DESACTIVADO - modo dev] Código de recuperación para {}: {}", toEmail, code);
            return;
        }
        String body = """
                {
                  "service_id": "%s",
                  "template_id": "%s",
                  "user_id": "%s",
                  "accessToken": "%s",
                  "template_params": {
                    "to_email": "%s",
                    "user_name": "%s",
                    "code": "%s"
                  }
                }
                """.formatted(serviceId, templateId, publicKey, privateKey,
                              toEmail, userName == null ? "atleta" : userName, code);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.emailjs.com/api/v1.0/email/send"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Correo de recuperación enviado a {}", toEmail);
            } else {
                log.error("EmailJS respondió {}: {}", response.statusCode(), response.body());
                throw new IllegalStateException("No se pudo enviar el correo de recuperación");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a EmailJS", e);
            throw new IllegalStateException("No se pudo enviar el correo de recuperación");
        }
    }
}
