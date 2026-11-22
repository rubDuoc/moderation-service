package com.foro.moderation_service.dto;

import com.foro.moderation_service.model.TipoAccion;
import com.foro.moderation_service.model.TipoContenido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Accion del admin. Con reporteId, el contenido se toma del reporte; sin reporteId hay que indicar
// tipoContenido y contenidoId (accion directa, por ejemplo desbanear tras una apelacion).
public record ModeracionRequest(

        @NotNull(message = "La accion es obligatoria (BANEAR, DESBANEAR o DESCARTAR)")
        TipoAccion accion,

        @Positive(message = "El reporteId debe ser positivo")
        Long reporteId,

        TipoContenido tipoContenido,

        @Positive(message = "El contenidoId debe ser positivo")
        Long contenidoId,

        @Size(max = 300, message = "El motivo no puede superar 300 caracteres")
        String motivo
) {
}
