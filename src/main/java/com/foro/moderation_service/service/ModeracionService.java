package com.foro.moderation_service.service;

import com.foro.moderation_service.dto.ModeracionRequest;
import com.foro.moderation_service.dto.ModeracionResponse;
import com.foro.moderation_service.model.TipoContenido;

import java.util.List;

// Todas las operaciones son solo para ADMIN
public interface ModeracionService {

    ModeracionResponse moderar(ModeracionRequest request, Long actorId);

    List<ModeracionResponse> historial(Long actorId);

    List<ModeracionResponse> historialDeContenido(TipoContenido tipo, Long contenidoId, Long actorId);
}
