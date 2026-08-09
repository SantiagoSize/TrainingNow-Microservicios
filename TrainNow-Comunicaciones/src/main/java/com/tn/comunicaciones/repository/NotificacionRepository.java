package com.tn.comunicaciones.repository;

import com.tn.comunicaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUserIdOrderByDateDesc(Long userId);
}
