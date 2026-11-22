package com.foro.moderation_service.service;

import com.foro.moderation_service.dto.ModeracionRequest;
import com.foro.moderation_service.dto.ModeracionResponse;
import com.foro.moderation_service.exception.ConflictoException;
import com.foro.moderation_service.exception.RecursoNoEncontradoException;
import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Moderacion;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoContenido;
import com.foro.moderation_service.repository.ModeracionRepository;
import com.foro.moderation_service.repository.ReporteRepository;
import com.foro.moderation_service.service.accion.AccionModeracionFactory;
import com.foro.moderation_service.service.accion.ContextoModeracion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ModeracionServiceImpl implements ModeracionService {

    private final AccionModeracionFactory accionFactory;
    private final ReporteRepository reporteRepository;
    private final ModeracionRepository moderacionRepository;
    private final AutorizacionService autorizacionService;

    public ModeracionServiceImpl(AccionModeracionFactory accionFactory, ReporteRepository reporteRepository,
                                 ModeracionRepository moderacionRepository, AutorizacionService autorizacionService) {
        this.accionFactory = accionFactory;
        this.reporteRepository = reporteRepository;
        this.moderacionRepository = moderacionRepository;
        this.autorizacionService = autorizacionService;
    }

    @Override
    public ModeracionResponse moderar(ModeracionRequest request, Long actorId) {
        autorizacionService.validarAdmin(actorId);
        ContextoModeracion contexto = armarContexto(request, actorId);

        // Patron Factory: la fabrica entrega la accion (BANEAR, DESBANEAR o DESCARTAR) y esta se ejecuta
        accionFactory.crear(request.accion()).ejecutar(contexto);

        Moderacion moderacion = new Moderacion();
        moderacion.setTipoContenido(contexto.tipoContenido());
        moderacion.setContenidoId(contexto.contenidoId());
        moderacion.setAccion(request.accion());
        moderacion.setAdminId(actorId);
        moderacion.setMotivo(request.motivo());
        moderacion.setReporteId(contexto.reporte() == null ? null : contexto.reporte().getId());
        moderacion.setFecha(LocalDateTime.now());
        Moderacion guardada = moderacionRepository.save(moderacion);
        log.info("Moderacion: {} {} {}, por adminId={}", request.accion(), contexto.tipoContenido(),
                contexto.contenidoId(), actorId);
        return ModeracionResponse.from(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeracionResponse> historial(Long actorId) {
        autorizacionService.validarAdmin(actorId);
        return moderacionRepository.findAllByOrderByFechaDesc().stream().map(ModeracionResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeracionResponse> historialDeContenido(TipoContenido tipo, Long contenidoId, Long actorId) {
        autorizacionService.validarAdmin(actorId);
        return moderacionRepository.findByTipoContenidoAndContenidoIdOrderByFechaDesc(tipo, contenidoId).stream()
                .map(ModeracionResponse::from).toList();
    }

    // Con reporteId el contenido sale del reporte (que debe estar pendiente); sin el, del cuerpo de la peticion
    private ContextoModeracion armarContexto(ModeracionRequest request, Long actorId) {
        if (request.reporteId() != null) {
            Reporte reporte = reporteRepository.findById(request.reporteId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe un reporte con id " + request.reporteId()));
            if (reporte.getEstado() != EstadoReporte.PENDIENTE) {
                throw new ConflictoException("El reporte " + reporte.getId() + " ya fue " + reporte.getEstado().name().toLowerCase());
            }
            return new ContextoModeracion(reporte.getTipoContenido(), reporte.getContenidoId(), request.motivo(), actorId, reporte);
        }
        if (request.tipoContenido() == null || request.contenidoId() == null) {
            throw new ConflictoException("Indica un reporteId, o bien tipoContenido y contenidoId");
        }
        return new ContextoModeracion(request.tipoContenido(), request.contenidoId(), request.motivo(), actorId, null);
    }
}
