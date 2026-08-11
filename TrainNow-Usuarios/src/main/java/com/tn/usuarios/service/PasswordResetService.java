package com.tn.usuarios.service;

import com.tn.usuarios.exception.InvalidResetCodeException;
import com.tn.usuarios.exception.ResourceNotFoundException;
import com.tn.usuarios.model.PasswordResetCode;
import com.tn.usuarios.model.User;
import com.tn.usuarios.repository.PasswordResetCodeRepository;
import com.tn.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * Recuperación de contraseña en 3 pasos:
 * 1. request  → genera código de 6 dígitos (expira en 10 min) y lo envía por EmailJS.
 * 2. verify   → valida email + código (sin consumirlo).
 * 3. confirm  → valida y consume el código, actualiza la contraseña (BCrypt).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private static final long EXPIRATION_MS = 10 * 60 * 1000L; // 10 minutos
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailJsClient emailJsClient;

    /** Paso 1: genera y envía el código. */
    public void requestCode(String email) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con ese email"));

        // Invalidar códigos anteriores del mismo email
        codeRepository.deleteByEmailIgnoreCase(user.getEmail());

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        codeRepository.save(PasswordResetCode.builder()
                .email(user.getEmail())
                .code(code)
                .expiresAt(System.currentTimeMillis() + EXPIRATION_MS)
                .build());

        emailJsClient.sendResetCode(user.getEmail(), user.getName(), code);
    }

    /** Paso 2: valida el código sin consumirlo. */
    @Transactional(readOnly = true)
    public void verifyCode(String email, String code) {
        findValidCode(email, code);
    }

    /** Paso 3: valida, consume el código y cambia la contraseña. */
    public void confirmReset(String email, String code, String newPassword) {
        PasswordResetCode reset = findValidCode(email, code);
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No existe una cuenta con ese email"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        reset.setUsed(true);
        codeRepository.save(reset);
    }

    private PasswordResetCode findValidCode(String email, String code) {
        PasswordResetCode reset = codeRepository
                .findTopByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(email.trim())
                .orElseThrow(() -> new InvalidResetCodeException("Código inválido o no solicitado"));
        if (reset.isExpired()) {
            throw new InvalidResetCodeException("El código expiró. Solicita uno nuevo");
        }
        if (!reset.getCode().equals(code.trim())) {
            throw new InvalidResetCodeException("Código incorrecto");
        }
        return reset;
    }
}
