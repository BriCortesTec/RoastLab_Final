# RoastLab — revisión y pruebas antes del despliegue

## Correcciones hechas en este ZIP

- El enlace privado de verificación **solo** se envía por email. No se manda por WhatsApp, ni siquiera al número de pruebas, ni se registra en el mensaje de bienvenida de la bitácora.
- La plantilla `roastlab_bienvenida` ahora utiliza **un parámetro (nombre)**. Es una **plantilla nueva** respecto de la plantilla anterior de dos variables: debe volver a enviarse a revisión/aprobación en Meta antes de probarla.
- La activación de correo requiere un **POST** con token en el cuerpo, iniciado al pulsar «Confirmar mi correo». Abrir el enlace no confirma automáticamente una cuenta; evita activaciones por escáneres de email.
- La interfaz elimina el token de la barra de direcciones después de leerlo, para minimizar su persistencia en historial.
- Continúa la bandera `app.notifications.roast-completed.enabled=false`, para no notificar tuestes automáticamente.

## Pruebas mínimas antes de generar el commit y desplegar

Desde una carpeta de pruebas en el mismo VPS (sin sobrescribir `/opt/roastlab/backend.jar`):

1. Comprueba `java -version`, `mvn -version` y `node -v` (el proyecto usa Java 21). Si Maven no existe, `cd backend && bash mvnw ...` puede descargarlo, si el VPS tiene conexión.
2. Backend: `cd backend && mvn clean verify` (o `bash mvnw clean verify`). Si hay pruebas de integración que necesitan MySQL, usa una base de datos de pruebas aislada.
3. Frontend: `cd frontend && npm ci && VITE_API_URL=/api npm run build`.
4. Revisa los scripts SQL V1–V9 y respalda MySQL **antes** de permitir que Flyway cambie el esquema de producción.
5. Registro admin → cuenta nueva: se guarda como correo **no verificado**. La cuenta no puede iniciar sesión todavía.
6. Verifica que el correo de bienvenida llegue a la casilla real; sin hacer clic, la cuenta continúa bloqueada. Al abrir el enlace debe verse un botón; **solo al pulsarlo** se marca verificada. Segundo uso debe rechazarse.
7. Prueba enlace vencido, enlace inválido, reenvío de verificación y recuperación de contraseña (el enlace de recuperación vence en 30 minutos y debe ser de un solo uso).
8. Verifica que la bitácora no exponga tokens y que el WhatsApp de bienvenida incluya únicamente el nombre. Actualiza y aprueba en Meta la plantilla `roastlab_bienvenida` de un parámetro. Con número Meta de prueba, solo llegará a destinatarios autorizados.
9. Confirma configuración de correo en `/opt/roastlab/config/application-prod.properties`: si se usa Gmail SMTP, requiere host, puerto, usuario, contraseña de aplicación, autenticación TLS y remitente válido. Si se usa Postfix local, requiere su configuración operativa y prueba de entrega. **Que Postfix esté activo no prueba entrega.**
10. Comprueba que `/opt/roastlab/config/application-prod.properties` sigue siendo privado. No lo metas al ZIP, al commit ni a los logs. Comprueba que el VPS tenga `app.frontend-url=https://roastlab.space` y `app.cors.allowed-origins` apropiado.
11. Solo tras pasar las pruebas, prepara un commit de código y despliega con respaldo del JAR actual, de los archivos estáticos y de la base de datos. Comprobar `/api/auth/login`, HTTPS, Nginx, MySQL y la bitácora tras despliegue.

## Estado de verificación de este archivo

Estas son modificaciones de código y documentación. El **VPS no se modificó** y el ZIP no contiene credenciales reales. Es necesario compilar, ejecutar las pruebas y verificar envíos reales en el VPS antes de considerarlo listo para producción.
