package com.tn.usuarios.repository;

import com.tn.usuarios.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByIdDesc(Long usuarioId);
}

