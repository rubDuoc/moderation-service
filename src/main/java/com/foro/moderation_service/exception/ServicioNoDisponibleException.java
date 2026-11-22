package com.foro.moderation_service.exception;

// 503: no se pudo comunicar con otro microservicio (user-service o forum-service)
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
