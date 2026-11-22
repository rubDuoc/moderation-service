package com.foro.moderation_service.service;

import com.foro.moderation_service.client.ForoClient;
import com.foro.moderation_service.dto.ReporteRequest;
import com.foro.moderation_service.dto.ReporteResponse;
import com.foro.moderation_service.exception.ConflictoException;
import com.foro.moderation_service.exception.RecursoNoEncontradoException;
import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.repository.ReporteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ReporteServiceImpl implements ReporteService {

    private static final int LARGO_EXTRACTO = 500;

    private final ReporteRepository reporteRepository;
    private final ForoClient foroClient;
    private final AutorizacionService autorizacionService;

    public ReporteServiceImpl(ReporteRepository reporteRepository, ForoClient foroClient,
                              AutorizacionService autorizacionService) {
        this.reporteRepository = reporteRepository;
        this.foroClient = foroClient;
        this.autorizacionService = autorizacionService;
    }

    @Override
    public ReporteResponse reportar(ReporteRequest request, Long actorId) {
        autorizacionService.validarUsuario(actorId);
        if (reporteRepository.existsByUsuarioIdAndTipoContenidoAndContenidoIdAndEstado(
                actorId, request.tipoContenido(), request.contenidoId(), EstadoReporte.PENDIENTE)) {
            throw new ConflictoException("Ya reportaste este contenido y esta pendiente de revision");
        }
        // Valida que el contenido exista en el foro y guarda una copia para que el admin lo revise
        String extracto = foroClient.obtenerExtracto(request.tipoContenido(), request.contenidoId());

        Reporte reporte = new Reporte();
        reporte.setTipoContenido(request.tipoContenido());
        reporte.setContenidoId(request.contenidoId());
        reporte.setUsuarioId(actorId);
        reporte.setMotivo(request.motivo());
        reporte.setExtracto(extracto.length() > LARGO_EXTRACTO ? extracto.substring(0, LARGO_EXTRACTO) : extracto);
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporte.setFechaReporte(LocalDateTime.now());
        Reporte guardado = reporteRepository.save(reporte);
        log.info("Reporte creado: id={}, {} {}, por usuarioId={}", guardado.getId(),
                guardado.getTipoContenido(), guardado.getContenidoId(), actorId);
        return ReporteResponse.from(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponse> listar(EstadoReporte estado, Long actorId) {
        autorizacionService.validarAdmin(actorId);
        List<Reporte> reportes = estado == null
                ? reporteRepository.findAllByOrderByFechaReporteDesc()
                : reporteRepository.findByEstadoOrderByFechaReporteAsc(estado);
        return reportes.stream().map(ReporteResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponse obtenerPorId(Long id, Long actorId) {
        Reporte reporte = buscarReporte(id);
        autorizacionService.validarMismoUsuarioOAdmin(actorId, reporte.getUsuarioId());
        return ReporteResponse.from(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponse> obtenerPorUsuario(Long usuarioId, Long actorId) {
        autorizacionService.validarMismoUsuarioOAdmin(actorId, usuarioId);
        return reporteRepository.findByUsuarioIdOrderByFechaReporteDesc(usuarioId).stream()
                .map(ReporteResponse::from).toList();
    }

    @Override
    public void eliminar(Long id, Long actorId) {
        autorizacionService.validarAdmin(actorId);
        reporteRepository.delete(buscarReporte(id));
        log.info("Reporte eliminado: id={}, por adminId={}", id, actorId);
    }

    private Reporte buscarReporte(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un reporte con id " + id));
    }
}
