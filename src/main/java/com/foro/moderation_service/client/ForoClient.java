package com.foro.moderation_service.client;

import com.foro.moderation_service.exception.AccesoDenegadoException;
import com.foro.moderation_service.exception.RecursoNoEncontradoException;
import com.foro.moderation_service.exception.ServicioNoDisponibleException;
import com.foro.moderation_service.model.TipoContenido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

// Comunicacion con forum-service: obtiene el contenido reportado y aplica el baneo o desbaneo
@Component
public class ForoClient {

    private static final String USUARIO_HEADER = "X-Usuario-Id";

    private final RestClient restClient;

    public ForoClient(@Value("${servicios.foro.url}") String foroUrl) {
        this.restClient = RestClient.builder().baseUrl(foroUrl).build();
    }

    // Texto del contenido reportado (titulo del tema o texto del comentario)
    public String obtenerExtracto(TipoContenido tipo, Long id) {
        try {
            if (tipo == TipoContenido.TEMA) {
                Map<?, ?> tema = restClient.get().uri("/api/temas/{id}", id).retrieve().body(Map.class);
                return tema.get("titulo") + ": " + tema.get("contenido");
            }
            Map<?, ?> comentario = restClient.get().uri("/api/comentarios/{id}", id).retrieve().body(Map.class);
            return String.valueOf(comentario.get("contenido"));
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe un " + tipo.name().toLowerCase() + " con id " + id);
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("No se pudo consultar el servicio del foro");
        }
    }

    // PATCH /api/temas/{id}/moderacion o /api/comentarios/{id}/moderacion en nombre del admin
    public void moderar(TipoContenido tipo, Long id, boolean baneado, String motivo, Long adminId) {
        String ruta = tipo == TipoContenido.TEMA ? "/api/temas/{id}/moderacion" : "/api/comentarios/{id}/moderacion";
        try {
            restClient.patch().uri(ruta, id)
                    .header(USUARIO_HEADER, String.valueOf(adminId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("baneado", baneado, "motivo", motivo == null ? "" : motivo))
                    .retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe un " + tipo.name().toLowerCase() + " con id " + id);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new AccesoDenegadoException("El foro rechazo la moderacion: se requiere rol ADMIN");
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("No se pudo comunicar con el servicio del foro");
        }
    }
}
