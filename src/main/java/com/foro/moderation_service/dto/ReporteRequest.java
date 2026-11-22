package com.foro.moderation_service.dto;

import com.foro.moderation_service.model.TipoContenido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// El usuario que reporta se toma del header X-Usuario-Id
public record ReporteRequest(

        @NotNull(message = "El tipoContenido es obligatorio (TEMA o COMENTARIO)")
        TipoContenido tipoContenido,

        @NotNull(message = "El contenidoId es obligatorio")
        @Positive(message = "El contenidoId debe ser positivo")
        Long contenidoId,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 10, max = 300, message = "El motivo debe tener entre 10 y 300 caracteres")
        String motivo
) {
}
