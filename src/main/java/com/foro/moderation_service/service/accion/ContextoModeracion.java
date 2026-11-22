package com.foro.moderation_service.service.accion;

import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoContenido;

// Datos que necesita una accion de moderacion. reporte es null en una accion directa (sin reporte)
public record ContextoModeracion(TipoContenido tipoContenido, Long contenidoId, String motivo,
                                 Long adminId, Reporte reporte) {
}
