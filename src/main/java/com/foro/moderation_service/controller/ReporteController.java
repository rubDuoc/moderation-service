package com.foro.moderation_service.controller;

import com.foro.moderation_service.dto.ReporteRequest;
import com.foro.moderation_service.dto.ReporteResponse;
import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.service.ReporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    // Header con el id del usuario que realiza la operacion (el que inicio sesion)
    public static final String USUARIO_HEADER = "X-Usuario-Id";

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // Cualquier usuario reporta un tema o comentario
    @PostMapping
    public ResponseEntity<ReporteResponse> reportar(@Valid @RequestBody ReporteRequest request,
                                                    @RequestHeader(USUARIO_HEADER) Long actorId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.reportar(request, actorId));
    }

    // Solo ADMIN. ?estado=PENDIENTE devuelve la cola de moderacion
    @GetMapping
    public List<ReporteResponse> listar(@RequestParam(required = false) EstadoReporte estado,
                                        @RequestHeader(USUARIO_HEADER) Long actorId) {
        return reporteService.listar(estado, actorId);
    }

    // El usuario que reporto o un ADMIN
    @GetMapping("/{id}")
    public ReporteResponse obtenerPorId(@PathVariable Long id, @RequestHeader(USUARIO_HEADER) Long actorId) {
        return reporteService.obtenerPorId(id, actorId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ReporteResponse> obtenerPorUsuario(@PathVariable Long usuarioId,
                                                   @RequestHeader(USUARIO_HEADER) Long actorId) {
        return reporteService.obtenerPorUsuario(usuarioId, actorId);
    }

    // Solo ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestHeader(USUARIO_HEADER) Long actorId) {
        reporteService.eliminar(id, actorId);
        return ResponseEntity.noContent().build();
    }
}
