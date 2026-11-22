package com.foro.moderation_service.controller;

import com.foro.moderation_service.dto.ModeracionRequest;
import com.foro.moderation_service.dto.ModeracionResponse;
import com.foro.moderation_service.model.TipoContenido;
import com.foro.moderation_service.service.ModeracionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Acciones del administrador sobre temas y comentarios (todas requieren rol ADMIN)
@RestController
@RequestMapping("/api/moderaciones")
public class ModeracionController {

    public static final String USUARIO_HEADER = "X-Usuario-Id";

    private final ModeracionService moderacionService;

    public ModeracionController(ModeracionService moderacionService) {
        this.moderacionService = moderacionService;
    }

    // BANEAR, DESBANEAR o DESCARTAR
    @PostMapping
    public ResponseEntity<ModeracionResponse> moderar(@Valid @RequestBody ModeracionRequest request,
                                                      @RequestHeader(USUARIO_HEADER) Long actorId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moderacionService.moderar(request, actorId));
    }

    @GetMapping
    public List<ModeracionResponse> historial(@RequestHeader(USUARIO_HEADER) Long actorId) {
        return moderacionService.historial(actorId);
    }

    @GetMapping("/{tipo}/{contenidoId}")
    public List<ModeracionResponse> historialDeContenido(@PathVariable TipoContenido tipo, @PathVariable Long contenidoId,
                                                         @RequestHeader(USUARIO_HEADER) Long actorId) {
        return moderacionService.historialDeContenido(tipo, contenidoId, actorId);
    }
}
