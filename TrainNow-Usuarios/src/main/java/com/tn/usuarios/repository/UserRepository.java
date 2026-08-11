package com.tn.usuarios.repository;

import com.tn.usuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByRole(String role);

    List<User> findByRoleAndNameContainingIgnoreCaseOrRoleAndLastNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
            String role1, String name,
            String role2, String lastName,
            String role3, String email);
}
