# moderation-service

Microservicio de **gestión de publicaciones y comentarios por parte de un administrador** del Sistema de Foros
en Línea (Desarrollo Full Stack III - DSY2205): los usuarios reportan contenido que incumple las normas y un
ADMIN lo banea, desbanea o descarta el reporte. Generado con el arquetipo propio
[foro-arquetipo](https://github.com/rubDuoc/foro-arquetipo).

- Spring Boot 4 · Java 21 · Spring Data JPA · Oracle Autonomous Database (Oracle Cloud)
- Puerto local: **8085**
- Se comunica con **user-service** (roles) y **forum-service** (obtener y banear contenido)

## Patrones de diseño

- **Factory**: `AccionModeracionFactory` entrega la acción que corresponde (`BanearAccion`, `DesbanearAccion`,
  `DescartarAccion`). Para agregar una acción nueva basta con otra clase que implemente `AccionModeracion`.
- **Builder**: respuestas `ReporteResponse`, `ModeracionResponse` y `ErrorResponse`.

## Flujo

1. Un usuario reporta un tema o comentario (`POST /api/reportes`). Se guarda una copia del contenido (extracto).
2. El ADMIN revisa la cola (`GET /api/reportes?estado=PENDIENTE`).
3. El ADMIN modera (`POST /api/moderaciones`):
   - **BANEAR**: el contenido se oculta en el foro y sus reportes pendientes quedan RESUELTOS.
   - **DESBANEAR**: el contenido vuelve a mostrarse (por ejemplo, tras una apelación).
   - **DESCARTAR**: el reporte no procede; el contenido sigue publicado.
4. Cada acción queda en el historial (`GET /api/moderaciones`): quién, qué, cuándo y por qué.

## Roles y privilegios (header `X-Usuario-Id`)

| Operación | USUARIO | ADMIN |
|---|---|---|
| Reportar contenido | Sí | Sí |
| Ver sus propios reportes | Sí | Todos |
| Ver la cola, moderar, ver historial, eliminar reportes | No | Sí |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/estado` | Estado del servicio |
| POST | `/api/reportes` | Reportar `{tipoContenido, contenidoId, motivo}` |
| GET | `/api/reportes?estado=` | Reportes (ADMIN); `estado=PENDIENTE` es la cola |
| GET | `/api/reportes/{id}` · `/api/reportes/usuario/{id}` | Reporte o reportes de un usuario |
| DELETE | `/api/reportes/{id}` | Eliminar reporte (ADMIN) |
| POST | `/api/moderaciones` | `{accion, reporteId}` o `{accion, tipoContenido, contenidoId}` (ADMIN) |
| GET | `/api/moderaciones` · `/api/moderaciones/{tipo}/{id}` | Historial (ADMIN) |

Errores en JSON: 400, 403, 404, 409 (reporte duplicado o ya resuelto), 500 y 503.

## Base de datos

Script: `src/main/resources/db/script.sql` (tablas `REPORTES` y `MODERACIONES` con datos de ejemplo).
Orden de ejecución de los scripts del sistema: **user-service → forum-service → moderation-service**.

## Ejecutar

Requiere **user-service** y **forum-service** corriendo. Copiar el wallet de Oracle en `wallet/` y en CMD:

```
set DB_PASSWORD=clave_de_oracle
mvnw spring-boot:run
```

Variables opcionales: `USER_SERVICE_URL`, `FORUM_SERVICE_URL`, `DB_USERNAME`, `DB_SERVICE`, `TNS_ADMIN`, `PORT`,
`CORS_ORIGENES`.

## Despliegue en la nube (Render)

URL publica: **https://foro-moderation-service-1-0.onrender.com** (por ejemplo, `/api/estado`).
Imagen Docker publicada en Docker Hub y desplegada en Render; el wallet de Oracle llega como archivo secreto.

## Postman

Colección con tests automáticos en `postman/moderation-service.postman_collection.json`
(importar en Postman junto con el entorno `postman/render.postman_environment.json` para probar contra Render → *Run collection*). Reporta, banea, desbanea y elimina sus reportes de prueba.
