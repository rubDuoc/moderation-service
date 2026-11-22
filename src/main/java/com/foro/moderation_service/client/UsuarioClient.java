package com.foro.moderation_service.client;

import com.foro.moderation_service.exception.AccesoDenegadoException;
import com.foro.moderation_service.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Consulta a user-service quien es un usuario y que rol tiene (el rol nunca se toma del cliente)
@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(@Value("${servicios.usuarios.url}") String usuariosUrl) {
        this.restClient = RestClient.builder().baseUrl(usuariosUrl).build();
    }

    public UsuarioRemoto obtenerUsuario(Long id) {
        try {
            return restClient.get().uri("/api/usuarios/{id}", id).retrieve().body(UsuarioRemoto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccesoDenegadoException("El usuario " + id + " que realiza la operacion no existe");
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("No se pudo consultar el servicio de usuarios");
        }
    }

    public record UsuarioRemoto(Long id, String username, String rol) {

        public boolean esAdmin() {
            return "ADMIN".equals(rol);
        }
    }
}
