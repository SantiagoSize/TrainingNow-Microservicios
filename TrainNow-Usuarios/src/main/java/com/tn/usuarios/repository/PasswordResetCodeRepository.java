package com.tn.usuarios.repository;

import com.tn.usuarios.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findTopByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(String email);

    void deleteByEmailIgnoreCase(String email);
}
