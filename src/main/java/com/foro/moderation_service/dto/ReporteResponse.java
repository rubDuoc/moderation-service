package com.foro.moderation_service.dto;

import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoContenido;
import lombok.Builder;

import java.time.LocalDateTime;

// Patron Builder: la respuesta se arma paso a paso desde la entidad
@Builder
public record ReporteResponse(
        Long id,
        TipoContenido tipoContenido,
        Long contenidoId,
        Long usuarioId,
        String motivo,
        String extracto,
        EstadoReporte estado,
        LocalDateTime fechaReporte,
        LocalDateTime fechaResolucion
) {
    public static ReporteResponse from(Reporte reporte) {
        return ReporteResponse.builder()
                .id(reporte.getId())
                .tipoContenido(reporte.getTipoContenido())
                .contenidoId(reporte.getContenidoId())
                .usuarioId(reporte.getUsuarioId())
                .motivo(reporte.getMotivo())
                .extracto(reporte.getExtracto())
                .estado(reporte.getEstado())
                .fechaReporte(reporte.getFechaReporte())
                .fechaResolucion(reporte.getFechaResolucion())
                .build();
    }
}
