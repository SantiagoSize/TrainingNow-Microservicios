package com.tn.usuarios.repository;

import com.tn.usuarios.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findTopByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(String email);

    void deleteByEmailIgnoreCase(String email);
}
