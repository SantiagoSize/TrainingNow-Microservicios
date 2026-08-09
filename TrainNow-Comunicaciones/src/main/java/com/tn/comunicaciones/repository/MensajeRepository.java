package com.tn.comunicaciones.repository;

import com.tn.comunicaciones.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    /** Conversación completa entre dos usuarios, en orden cronológico. */
    @Query("""
            SELECT m FROM Mensaje m
            WHERE (m.senderId = :a AND m.receiverId = :b)
               OR (m.senderId = :b AND m.receiverId = :a)
            ORDER BY m.timestamp ASC
            """)
    List<Mensaje> findConversation(@Param("a") Long a, @Param("b") Long b);

    /** Todos los mensajes donde participa el usuario (para lista de chats). */
    @Query("""
            SELECT m FROM Mensaje m
            WHERE m.senderId = :userId OR m.receiverId = :userId
            ORDER BY m.timestamp DESC
            """)
    List<Mensaje> findByParticipant(@Param("userId") Long userId);
}
