package com.tn.usuarios.security;

import com.tn.usuarios.exception.ForbiddenOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Emisión y validación de tokens JWT (HS256) sin librerías externas.
 * El token viaja en el header: Authorization: Bearer <token>
 *
 * Claims: sub (userId), role, email, exp (epoch millis).
 */
@Component
public class JwtService {

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 horas
    private static final String HEADER_B64 = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
    private static final Pattern SUB = Pattern.compile("\"sub\":\"(\\d+)\"");
    private static final Pattern ROLE = Pattern.compile("\"role\":\"([A-Z]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

    @Value("${jwt.secret:trainingnow-secret-dev-2026-cambiar-en-produccion}")
    private String secret;

    /** Datos extraídos de un token válido. */
    public record TokenClaims(Long userId, String role) { }

    public String createToken(Long userId, String role, String email) {
        long exp = System.currentTimeMillis() + EXPIRATION_MS;
        String payloadJson = "{\"sub\":\"" + userId + "\",\"role\":\"" + role
                + "\",\"email\":\"" + email + "\",\"exp\":" + exp + "}";
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = sign(HEADER_B64 + "." + payload);
        return HEADER_B64 + "." + payload + "." + signature;
    }

    /** Valida firma y expiración. Lanza 403 si el token es inválido. */
    public TokenClaims validate(String token) {
        if (token == null || token.isBlank()) {
            throw new ForbiddenOperationException("Falta el token de autorización");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
            throw new ForbiddenOperationException("Token inválido");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Matcher exp = EXP.matcher(payload);
        if (!exp.find() || Long.parseLong(exp.group(1)) < System.currentTimeMillis()) {
            throw new ForbiddenOperationException("Token expirado, inicia sesión de nuevo");
        }
        Matcher sub = SUB.matcher(payload);
        Matcher role = ROLE.matcher(payload);
        if (!sub.find() || !role.find()) {
            throw new ForbiddenOperationException("Token inválido");
        }
        return new TokenClaims(Long.parseLong(sub.group(1)), role.group(1));
    }

    /** Extrae y valida el token del header "Authorization: Bearer x". */
    public TokenClaims fromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ForbiddenOperationException("Falta el token de autorización");
        }
        return validate(authHeader.substring(7).trim());
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Error firmando token", e);
        }
    }
}
