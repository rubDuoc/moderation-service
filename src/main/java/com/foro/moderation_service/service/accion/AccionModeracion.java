package com.foro.moderation_service.service.accion;

import com.foro.moderation_service.model.TipoAccion;

// Producto del patron Factory: cada accion del administrador implementa esta interfaz
public interface AccionModeracion {

    TipoAccion tipo();

    void ejecutar(ContextoModeracion contexto);
}
