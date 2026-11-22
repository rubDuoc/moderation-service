package com.foro.moderation_service.model;

// Acciones que puede tomar un administrador (cada una tiene su clase, creada por AccionModeracionFactory)
public enum TipoAccion {
    BANEAR,
    DESBANEAR,
    DESCARTAR
}
