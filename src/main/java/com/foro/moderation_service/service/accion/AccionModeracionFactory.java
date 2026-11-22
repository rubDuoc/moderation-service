package com.foro.moderation_service.service.accion;

import com.foro.moderation_service.model.TipoAccion;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Patron Factory: entrega la accion de moderacion que corresponde al tipo pedido por el admin.
// Para agregar una accion nueva basta con crear otra clase AccionModeracion; el servicio no cambia.
@Component
public class AccionModeracionFactory {

    private final Map<TipoAccion, AccionModeracion> acciones = new EnumMap<>(TipoAccion.class);

    public AccionModeracionFactory(List<AccionModeracion> implementaciones) {
        implementaciones.forEach(accion -> acciones.put(accion.tipo(), accion));
    }

    public AccionModeracion crear(TipoAccion tipo) {
        AccionModeracion accion = acciones.get(tipo);
        if (accion == null) {
            throw new IllegalArgumentException("Accion de moderacion no soportada: " + tipo);
        }
        return accion;
    }
}
