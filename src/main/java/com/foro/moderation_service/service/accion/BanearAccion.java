package com.foro.moderation_service.service.accion;

import com.foro.moderation_service.client.ForoClient;
import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoAccion;
import com.foro.moderation_service.repository.ReporteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// BANEAR: oculta el contenido en el foro y da por resueltos todos sus reportes pendientes
@Component
public class BanearAccion implements AccionModeracion {

    private final ForoClient foroClient;
    private final ReporteRepository reporteRepository;

    public BanearAccion(ForoClient foroClient, ReporteRepository reporteRepository) {
        this.foroClient = foroClient;
        this.reporteRepository = reporteRepository;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.BANEAR;
    }

    @Override
    public void ejecutar(ContextoModeracion contexto) {
        foroClient.moderar(contexto.tipoContenido(), contexto.contenidoId(), true, contexto.motivo(), contexto.adminId());
        List<Reporte> pendientes = reporteRepository.findByTipoContenidoAndContenidoIdAndEstado(
                contexto.tipoContenido(), contexto.contenidoId(), EstadoReporte.PENDIENTE);
        pendientes.forEach(reporte -> {
            reporte.setEstado(EstadoReporte.RESUELTO);
            reporte.setFechaResolucion(LocalDateTime.now());
        });
        reporteRepository.saveAll(pendientes);
    }
}
