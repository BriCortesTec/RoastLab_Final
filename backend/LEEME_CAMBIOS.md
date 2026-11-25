# RoastLab – cambios sobre tu commit 2b051b8 (Bri)

Todo está construido **encima de tu código** (tu JwtService, CustomUserDetailsService, filtro JWT,
AuthenticationManager y tu V5 de roles). No reemplacé tu diseño; lo extendí.

## Cómo aplicarlo
1. Haz copia de tu `application-local.properties` (no viene en este zip).
2. Descomprime encima de tu repo (reemplaza archivos).
3. Tu `application-local.properties` debe tener: `spring.datasource.password`, `app.jwt.secret` (tu base64 de siempre).
4. Recrea la BD local para que Flyway corra V1..V7:
   `mysql -u root -p -e "DROP DATABASE IF EXISTS roastlab_final; CREATE DATABASE roastlab_final CHARACTER SET utf8mb4;"`
5. `cd backend && ./mvnw spring-boot:run` y prueba el login (`admin@roastlab.site` / `RoastLab#2026`).
6. Si Maven marca error, pégame solo las líneas `ERROR`.

## Qué cambió en TUS archivos
| Archivo | Cambio |
|---|---|
| `JwtService` | + `jti` en el token y `extraerJti/extraerExpiracion` (para el logout). Expiración con valor por defecto 2 h |
| `JwtAuthenticationFilter` | + rechaza tokens revocados (lista negra) y ya no hace `printStackTrace` |
| `CustomUserDetailsService` | devuelve `AuthenticatedUser` (id, correo, rol canónico) en vez de `User` |
| `SecurityConfig` | + `@EnableMethodSecurity`, CORS, respuestas 401/403 en JSON, solo login/registro/recuperación son públicos, `/api/usuarios` y `/api/notificaciones` solo ADMIN |
| `AuthService` | misma lógica de login con `AuthenticationManager`; + logout, perfil, recuperar contraseña, correo de bienvenida |
| Controladores Cafe/LoteCafe/Perfil/Tueste | + `@Valid`, `@PreAuthorize`, paginación y filtros del servidor |
| `V5__insertar_roles.sql` | **intacta**. Mis migraciones son V6 y V7 |

Tus nombres de rol (`tostador`, `gerencia`, `administración`) se conservan; `Roles.java` los traduce a ADMIN / GERENTE / TOSTADOR para Spring Security.

## Archivos nuevos
- `exception/` → `GlobalExceptionHandler` (@RestControllerAdvice, JSON 400/401/403/404/409/422/500)
- `security/` → `AuthenticatedUser`, `Roles`, `JsonSecurityHandlers`
- Usuarios (admin), reportes, **notificaciones** (Twilio SMS/WhatsApp + correo + bitácora)
- `db/migration/V6` (teléfono, tokens, notificaciones) y `V7` (seeds: 12 usuarios, cafés, lotes, perfiles, tuestes)
- `bruno/RoastLab` (colección con login, token y casos de error), `deploy/` (scripts del VPS), `README.md`

## Para la demo: debes poder explicar (te evalúan por separado)
1. Flujo de login: `AuthController` → `AuthService` → `AuthenticationManager` → `JwtService.generarToken`.
2. Cómo el filtro JWT autentica cada petición y por qué un token de logout deja de servir (`tokens_revocados`).
3. `@PreAuthorize` + `Roles.canonico`: por qué un tostador recibe 403 en `/api/usuarios`.
4. Por qué los listados no hacen N+1 (`@EntityGraph` + `default_batch_fetch_size`) y por qué la paginación es en servidor.
5. Qué dispara SMS/WhatsApp: `TuesteService.avisarSiSeCompleto` → `NotificacionService.tuesteCompletado` (asíncrono).

## Contrato de la API para Nadya
- `POST /api/auth/login {email,password}` → `{token, user:{id,name,surname,email,phone,role,active}}`; `role` = `admin|gerente|tostador`.
  **Quitar el selector "Entrar como"**: el rol lo decide el servidor.
- `POST /api/auth/registro {name,surname,email,phone?,password}` · `POST /api/auth/logout` · `GET/PUT /api/auth/me`
- `POST /api/auth/forgot-password {email}` · `POST /api/auth/reset-password {token,password}` (el correo enlaza a `/reset-password?token=...` del frontend)
- Listados: `GET /api/{cafes|lotes|perfiles|tuestes|usuarios}?search=&page=0&size=8` → `{content,page,size,totalElements,totalPages}`
- `GET /api/reportes/resumen` → `{totalRoasts, roastedKg, averageLossPercent, byLevel[], inventoryByOrigin[]}`
- Errores: `{status, message, fieldErrors:{campo:"mensaje"}}` → mostrar `fieldErrors[campo]` bajo cada input.
- Todas las rutas protegidas: header `Authorization: Bearer <token>`.

## Qué NO pude verificar (sé honesto con tu equipo)
- **No pude compilar con Maven** (bloqueado en mi entorno). Revisé con `javac`: no hay errores en el código propio; solo faltan las librerías de Spring.
- Las migraciones V1..V7 sí las probé en MySQL 8: 14 tablas, 12 usuarios (1 admin, 2 gerencia, 9 tostador), 12 cafés/lotes/perfiles/tuestes.
- No probé Twilio, Postfix ni el VPS: dependen de tus credenciales, del DNS y del puerto 25.
