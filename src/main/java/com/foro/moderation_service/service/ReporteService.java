package com.foro.moderation_service.service;

import com.foro.moderation_service.dto.ReporteRequest;
import com.foro.moderation_service.dto.ReporteResponse;
import com.foro.moderation_service.model.EstadoReporte;

import java.util.List;

public interface ReporteService {

    // Cualquier usuario registrado
    ReporteResponse reportar(ReporteRequest request, Long actorId);

    // Solo ADMIN: todos los reportes o solo los de un estado (por ejemplo, la cola PENDIENTE)
    List<ReporteResponse> listar(EstadoReporte estado, Long actorId);

    // El usuario que reporto o un ADMIN
    ReporteResponse obtenerPorId(Long id, Long actorId);

    List<ReporteResponse> obtenerPorUsuario(Long usuarioId, Long actorId);

    // Solo ADMIN
    void eliminar(Long id, Long actorId);
}
