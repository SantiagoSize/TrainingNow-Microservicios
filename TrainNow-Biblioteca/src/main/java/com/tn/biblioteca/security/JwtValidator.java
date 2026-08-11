package com.tn.biblioteca.security;

import com.tn.biblioteca.exception.ForbiddenOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Valida los tokens JWT emitidos por TrainNow-Usuarios (secreto compartido).
 * Se usa para restringir la escritura de la biblioteca a los administradores.
 */
@Component
public class JwtValidator {

    private static final Pattern ROLE = Pattern.compile("\"role\":\"([A-Z]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

    @Value("${jwt.secret:trainingnow-secret-dev-2026-cambiar-en-produccion}")
    private String secret;

    /** Exige que el header Authorization contenga un token válido de un ADMIN. */
    public void requireAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ForbiddenOperationException("Solo un administrador puede modificar la biblioteca");
        }
        String[] parts = authHeader.substring(7).trim().split("\\.");
        if (parts.length != 3 || !sign(parts[0] + "." + parts[1]).equals(parts[2])) {
            throw new ForbiddenOperationException("Token inválido");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Matcher exp = EXP.matcher(payload);
        if (!exp.find() || Long.parseLong(exp.group(1)) < System.currentTimeMillis()) {
            throw new ForbiddenOperationException("Token expirado, inicia sesión de nuevo");
        }
        Matcher role = ROLE.matcher(payload);
        if (!role.find() || !"ADMIN".equals(role.group(1))) {
            throw new ForbiddenOperationException("Solo un administrador puede modificar la biblioteca");
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Error validando token", e);
        }
    }
}
