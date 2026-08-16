package com.tn.usuarios.repository;

import com.tn.usuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByRole(String role);

    /**
     * Reemplaza el derivado findByRoleAnd...Or...Or... (nombre de método larguísimo con Role
     * repetido 3 veces): con la búsqueda de texto explícita en JPQL no hay ambigüedad posible
     * sobre cómo se agrupan los AND/OR. Busca por nombre, apellido o correo conteniendo [q].
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND ("
            + "LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<User> searchByRoleAndText(@Param("role") String role, @Param("q") String q);
}
