package com.foro.moderation_service.service;

import com.foro.moderation_service.client.UsuarioClient;
import com.foro.moderation_service.client.UsuarioClient.UsuarioRemoto;
import com.foro.moderation_service.exception.AccesoDenegadoException;
import org.springframework.stereotype.Service;

// Privilegios: cualquier usuario reporta; solo un ADMIN ve la cola y modera
@Service
public class AutorizacionService {

    private final UsuarioClient usuarioClient;

    public AutorizacionService(UsuarioClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    public UsuarioRemoto validarUsuario(Long actorId) {
        return usuarioClient.obtenerUsuario(actorId);
    }

    public void validarAdmin(Long actorId) {
        if (!usuarioClient.obtenerUsuario(actorId).esAdmin()) {
            throw new AccesoDenegadoException("Esta operacion requiere rol ADMIN");
        }
    }

    // El mismo usuario (por ejemplo, para ver sus propios reportes) o un ADMIN
    public void validarMismoUsuarioOAdmin(Long actorId, Long usuarioId) {
        UsuarioRemoto actor = usuarioClient.obtenerUsuario(actorId);
        if (!actor.esAdmin() && !actor.id().equals(usuarioId)) {
            throw new AccesoDenegadoException("Solo puedes ver tus propios reportes");
        }
    }
}
