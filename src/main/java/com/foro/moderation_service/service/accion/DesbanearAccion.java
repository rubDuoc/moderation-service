package com.foro.moderation_service.service.accion;

import com.foro.moderation_service.client.ForoClient;
import com.foro.moderation_service.model.TipoAccion;
import org.springframework.stereotype.Component;

// DESBANEAR: el contenido vuelve a mostrarse en el foro (por ejemplo, tras una apelacion)
@Component
public class DesbanearAccion implements AccionModeracion {

    private final ForoClient foroClient;

    public DesbanearAccion(ForoClient foroClient) {
        this.foroClient = foroClient;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.DESBANEAR;
    }

    @Override
    public void ejecutar(ContextoModeracion contexto) {
        foroClient.moderar(contexto.tipoContenido(), contexto.contenidoId(), false, contexto.motivo(), contexto.adminId());
    }
}
