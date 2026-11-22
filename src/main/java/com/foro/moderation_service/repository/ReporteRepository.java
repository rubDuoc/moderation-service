package com.foro.moderation_service.repository;

import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findAllByOrderByFechaReporteDesc();

    // Cola de moderacion: los mas antiguos primero
    List<Reporte> findByEstadoOrderByFechaReporteAsc(EstadoReporte estado);

    List<Reporte> findByUsuarioIdOrderByFechaReporteDesc(Long usuarioId);

    List<Reporte> findByTipoContenidoAndContenidoIdAndEstado(TipoContenido tipo, Long contenidoId, EstadoReporte estado);

    // Evita que un mismo usuario reporte dos veces el mismo contenido mientras esta pendiente
    boolean existsByUsuarioIdAndTipoContenidoAndContenidoIdAndEstado(Long usuarioId, TipoContenido tipo,
                                                                     Long contenidoId, EstadoReporte estado);
}
