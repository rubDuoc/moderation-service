package com.foro.moderation_service.repository;

import com.foro.moderation_service.model.Moderacion;
import com.foro.moderation_service.model.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeracionRepository extends JpaRepository<Moderacion, Long> {

    List<Moderacion> findAllByOrderByFechaDesc();

    List<Moderacion> findByTipoContenidoAndContenidoIdOrderByFechaDesc(TipoContenido tipo, Long contenidoId);

    List<Moderacion> findByAdminIdOrderByFechaDesc(Long adminId);
}
