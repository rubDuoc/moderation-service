package com.foro.moderation_service.dto;

import com.foro.moderation_service.model.Moderacion;
import com.foro.moderation_service.model.TipoAccion;
import com.foro.moderation_service.model.TipoContenido;
import lombok.Builder;

import java.time.LocalDateTime;

// Patron Builder: registro del historial de moderacion
@Builder
public record ModeracionResponse(
        Long id,
        TipoContenido tipoContenido,
        Long contenidoId,
        TipoAccion accion,
        Long adminId,
        String motivo,
        Long reporteId,
        LocalDateTime fecha
) {
    public static ModeracionResponse from(Moderacion moderacion) {
        return ModeracionResponse.builder()
                .id(moderacion.getId())
                .tipoContenido(moderacion.getTipoContenido())
                .contenidoId(moderacion.getContenidoId())
                .accion(moderacion.getAccion())
                .adminId(moderacion.getAdminId())
                .motivo(moderacion.getMotivo())
                .reporteId(moderacion.getReporteId())
                .fecha(moderacion.getFecha())
                .build();
    }
}
