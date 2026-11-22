package com.foro.moderation_service;

import com.foro.moderation_service.client.ForoClient;
import com.foro.moderation_service.client.UsuarioClient;
import com.foro.moderation_service.client.UsuarioClient.UsuarioRemoto;
import com.foro.moderation_service.dto.ModeracionRequest;
import com.foro.moderation_service.exception.AccesoDenegadoException;
import com.foro.moderation_service.exception.ConflictoException;
import com.foro.moderation_service.model.EstadoReporte;
import com.foro.moderation_service.model.Moderacion;
import com.foro.moderation_service.model.Reporte;
import com.foro.moderation_service.model.TipoAccion;
import com.foro.moderation_service.model.TipoContenido;
import com.foro.moderation_service.repository.ModeracionRepository;
import com.foro.moderation_service.repository.ReporteRepository;
import com.foro.moderation_service.service.AutorizacionService;
import com.foro.moderation_service.service.ModeracionServiceImpl;
import com.foro.moderation_service.service.accion.AccionModeracionFactory;
import com.foro.moderation_service.service.accion.BanearAccion;
import com.foro.moderation_service.service.accion.DesbanearAccion;
import com.foro.moderation_service.service.accion.DescartarAccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Moderacion con el patron Factory (user-service y forum-service simulados con mocks)
class ModeracionServiceTest {

    private ForoClient foroClient;
    private ReporteRepository reporteRepository;
    private AccionModeracionFactory factory;
    private ModeracionServiceImpl service;
    private Reporte reportePendiente;

    @BeforeEach
    void preparar() {
        foroClient = mock(ForoClient.class);
        reporteRepository = mock(ReporteRepository.class);
        ModeracionRepository moderacionRepository = mock(ModeracionRepository.class);
        UsuarioClient usuarioClient = mock(UsuarioClient.class);
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(new UsuarioRemoto(1L, "admin_foro", "ADMIN"));
        when(usuarioClient.obtenerUsuario(4L)).thenReturn(new UsuarioRemoto(4L, "juan_gamer", "USUARIO"));
        when(moderacionRepository.save(any(Moderacion.class))).thenAnswer(i -> i.getArgument(0));

        factory = new AccionModeracionFactory(List.of(
                new BanearAccion(foroClient, reporteRepository),
                new DesbanearAccion(foroClient),
                new DescartarAccion(reporteRepository)));
        service = new ModeracionServiceImpl(factory, reporteRepository, moderacionRepository,
                new AutorizacionService(usuarioClient));

        reportePendiente = new Reporte();
        reportePendiente.setId(10L);
        reportePendiente.setTipoContenido(TipoContenido.TEMA);
        reportePendiente.setContenidoId(3L);
        reportePendiente.setEstado(EstadoReporte.PENDIENTE);
        when(reporteRepository.findById(10L)).thenReturn(Optional.of(reportePendiente));
        when(reporteRepository.findByTipoContenidoAndContenidoIdAndEstado(TipoContenido.TEMA, 3L, EstadoReporte.PENDIENTE))
                .thenReturn(List.of(reportePendiente));
    }

    @Test
    void laFabricaEntregaUnaAccionPorCadaTipo() {
        for (TipoAccion tipo : TipoAccion.values()) {
            assertThat(factory.crear(tipo).tipo()).isEqualTo(tipo);
        }
    }

    @Test
    void banearDesdeUnReporteOcultaElTemaYResuelveElReporte() {
        var respuesta = service.moderar(new ModeracionRequest(TipoAccion.BANEAR, 10L, null, null, "Spam"), 1L);

        verify(foroClient).moderar(TipoContenido.TEMA, 3L, true, "Spam", 1L);
        assertThat(reportePendiente.getEstado()).isEqualTo(EstadoReporte.RESUELTO);
        assertThat(respuesta.reporteId()).isEqualTo(10L);
    }

    @Test
    void descartarMantieneElContenidoYCierraElReporte() {
        service.moderar(new ModeracionRequest(TipoAccion.DESCARTAR, 10L, null, null, "No incumple"), 1L);

        verify(foroClient, never()).moderar(any(), anyLong(), anyBoolean(), anyString(), anyLong());
        assertThat(reportePendiente.getEstado()).isEqualTo(EstadoReporte.DESCARTADO);
    }

    @Test
    void desbanearDirectoSinReporte() {
        service.moderar(new ModeracionRequest(TipoAccion.DESBANEAR, null, TipoContenido.COMENTARIO, 6L, "Apelacion"), 1L);

        verify(foroClient).moderar(TipoContenido.COMENTARIO, 6L, false, "Apelacion", 1L);
    }

    @Test
    void unReporteYaResueltoNoSePuedeVolverAModerar() {
        reportePendiente.setEstado(EstadoReporte.DESCARTADO);
        assertThatThrownBy(() -> service.moderar(new ModeracionRequest(TipoAccion.BANEAR, 10L, null, null, "x"), 1L))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    void unUsuarioSinRolAdminNoPuedeModerar() {
        assertThatThrownBy(() -> service.moderar(new ModeracionRequest(TipoAccion.BANEAR, 10L, null, null, "x"), 4L))
                .isInstanceOf(AccesoDenegadoException.class);
        verify(foroClient, never()).moderar(any(), anyLong(), anyBoolean(), anyString(), anyLong());
    }
}
