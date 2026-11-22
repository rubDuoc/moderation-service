package com.foro.moderation_service.service.accion;

import com.foro.moderation_service.exception.ConflictoException;
import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoAccion;
import com.foro.moderation_service.repository.ReporteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// DESCARTAR: el reporte no procede; el contenido se mantiene publicado
@Component
public class DescartarAccion implements AccionModeracion {

    private final ReporteRepository reporteRepository;

    public DescartarAccion(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.DESCARTAR;
    }

    @Override
    public void ejecutar(ContextoModeracion contexto) {
        Reporte reporte = contexto.reporte();
        if (reporte == null) {
            throw new ConflictoException("Para descartar se debe indicar el reporteId");
        }
        reporte.setEstado(EstadoReporte.DESCARTADO);
        reporte.setFechaResolucion(LocalDateTime.now());
        reporteRepository.save(reporte);
    }
}
