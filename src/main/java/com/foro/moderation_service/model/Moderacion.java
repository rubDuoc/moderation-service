package com.foro.moderation_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Historial: cada accion de un administrador queda registrada (quien, que, cuando y por que)
@Entity
@Table(name = "MODERACIONES")
@Getter
@Setter
@NoArgsConstructor
public class Moderacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoContenido tipoContenido;

    @Column(nullable = false)
    private Long contenidoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAccion accion;

    // Administrador que realizo la accion (id de user-service)
    @Column(nullable = false)
    private Long adminId;

    @Column(length = 300)
    private String motivo;

    // Reporte que origino la accion (puede ser una accion directa sin reporte)
    private Long reporteId;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
