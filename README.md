# Microservicio Usuarios

Cuentas, roles y autenticación de la plazoleta de comidas (HU 1, 5, 6 y 8). Puerto **8081**, base de datos
**`usuarios_db`**. Es quien emite el token JWT que usan `plazoleta` y `pedidos`.

Arquitectura hexagonal: `domain` (Java puro, sin Spring: modelos, casos de uso y puertos), `application`
(DTOs, mappers y handlers) e `infrastructure` (REST, JPA, JWT, seguridad y cliente HTTP a Plazoleta).
Java 17 y Spring Boot 4.

## Cómo levantarlo

1. Crear la base de datos (Postgres no la crea sola): `CREATE DATABASE usuarios_db;`
2. Variables de entorno: `DB_PASSWORD` (obligatoria) y `JWT_SECRET` (opcional; debe ser **la misma** en los
   tres microservicios, el valor por defecto ya coincide).
3. `./mvnw spring-boot:run`
4. Swagger: <http://localhost:8081/swagger-ui.html>.

Al arrancar se crean solos los roles (`ADMINISTRADOR`, `PROPIETARIO`, `EMPLEADO`, `CLIENTE`) y el usuario
administrador **`admin@plazoleta.com` / `admin1234`**, con el que se empieza todo el flujo.

Pruebas unitarias: `./mvnw test`.

## Endpoints

| HU | Método y ruta | Acceso | Qué hace |
|----|---------------|--------|----------|
| 5 | `POST /usuarios/login` | Público | Valida correo y clave y devuelve `{ "token": "..." }` |
| 1 | `POST /usuarios/propietario` | ADMINISTRADOR | Crea un propietario (debe ser mayor de edad) |
| 6 | `POST /usuarios/empleado` | PROPIETARIO | Crea un empleado de un restaurante del propietario autenticado |
| 8 | `POST /usuarios/cliente` | Público | Registro de cliente |
| — | `GET /usuarios/{id}` | ADMIN, PROPIETARIO, EMPLEADO | Datos básicos: nombre, celular, rol y restaurante del empleado |
| — | `GET /usuarios/{id}/es-propietario` | Público (interno) | `true`/`false`; lo usa Plazoleta al crear un restaurante |
| — | `GET /usuarios/id-por-correo?correo=...` | Público (interno) | Id del usuario; lo usa Plazoleta para saber quién es el dueño |

Body de propietario y cliente:

```json
{
  "nombre": "Juan", "apellido": "Perez", "documentoDeIdentidad": "123456789",
  "celular": "+573005698325", "fechaNacimiento": "1990-05-20",
  "correo": "juan@correo.com", "clave": "1234"
}
```

El de empleado es igual más `"idRestaurante": 1`.

Los errores siguen el formato común `{ "status": <código>, "mensaje": "..." }`: 400 (datos inválidos),
401 (credenciales incorrectas o token inválido), 403 (rol incorrecto o restaurante ajeno), 404 (usuario
inexistente), 409 (correo o documento ya registrado) y 503 (Plazoleta no responde).

## Reglas de negocio

- Campos obligatorios: nombre, apellido, documento, celular, correo y clave.
- Correo con estructura válida; documento **solo numérico**; celular de **máximo 13 caracteres** que puede
  empezar por `+` (ej. `+573005698325`).
- El propietario debe ser **mayor de edad** (18 años, calculado con la fecha de nacimiento).
- Correo y documento son únicos.
- La clave se guarda encriptada con **BCrypt**; nunca se devuelve.

## Decisiones de diseño

- **JWT**: el token lleva como `subject` el correo y como claims el `id`, el `rol` y el `nombre`, y dura
  120 minutos. Los otros microservicios lo validan con el mismo `JWT_SECRET`, sin llamar a Usuarios.
- **Relación empleado-restaurante**: se guarda aquí (`usuarios.id_restaurante`). Al crear un empleado se
  consulta a Plazoleta (`GET /restaurantes/{id}`) que el restaurante exista y sea del propietario autenticado.
- **Endpoints internos públicos**: `es-propietario` e `id-por-correo` no exigen token porque los llama
  Plazoleta servicio a servicio y solo devuelven un booleano o un id.
- **CORS**: se permite el frontend en `http://localhost:5173`.
