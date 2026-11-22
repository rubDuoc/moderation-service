package com.foro.moderation_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Reporte de un usuario sobre un tema o comentario que incumple las normas del foro
@Entity
@Table(name = "REPORTES")
@Getter
@Setter
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoContenido tipoContenido;

    // Id del tema o comentario en forum-service
    @Column(nullable = false)
    private Long contenidoId;

    // Usuario que reporta (id de user-service)
    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 300)
    private String motivo;

    // Copia del contenido al momento de reportar: el admin lo puede revisar aunque luego se banee
    @Column(nullable = false, length = 500)
    private String extracto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReporte estado;

    @Column(nullable = false)
    private LocalDateTime fechaReporte;

    private LocalDateTime fechaResolucion;
}
