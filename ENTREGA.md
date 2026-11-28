# RoastLab · paquete de entrega

## Base de trabajo

Esta entrega parte del commit `261deb52ce1662ba16353042f27da6218c3df554` de `main`.

## Cambios incluidos

- El alta administrativa crea la cuenta pendiente de verificación y, después de guardar, intenta enviar el correo de bienvenida y la plantilla de WhatsApp al número autorizado de pruebas configurado.
- Los enlaces de verificación duran 24 horas, son de un solo uso y se guardan como hash. No se permite iniciar sesión hasta verificar el correo; se puede solicitar otro enlace.
- La recuperación de contraseña usa tokens aleatorios de un solo uso, con vencimiento de 30 minutos, almacenados como hash. El correo evita revelar si una cuenta existe.
- Los teléfonos mexicanos nuevos y los que se actualicen se normalizan a `52` + 10 dígitos. La migración V9 normaliza los formatos anteriores reconocidos.
- Los avisos automáticos de tueste quedan apagados por configuración. El código y la plantilla existente se conservan.
- Se agregó la solicitud de plantilla [`backend/deploy/whatsapp-template-roastlab_bienvenida.json`](backend/deploy/whatsapp-template-roastlab_bienvenida.json) para crear `roastlab_bienvenida` en Meta.

## Configuración requerida antes de enviar mensajes

1. En `backend/src/main/resources/application-local.properties` o `/opt/roastlab/config/application-prod.properties`, configura Gmail SMTP con el correo y la contraseña de aplicación; usa la misma dirección en `app.mail.from`.
2. Configura `meta.whatsapp.phone-number-id`, `meta.whatsapp.access-token`, `meta.whatsapp.welcome-test-recipient` y `meta.whatsapp.welcome-mode=test`. El destinatario debe estar autorizado en la cuenta de Meta de pruebas. La opción `production` queda preparada para cuando se use el número real y se cuente con consentimiento.
3. Crea y espera aprobación de la plantilla `roastlab_bienvenida` en Meta Business Manager con idioma `es_MX` y dos variables de cuerpo: nombre y enlace de verificación. El JSON incluido documenta el contenido exacto.
4. Mantén `app.notifications.roast-completed.enabled=false` durante esta prueba. Se puede reactivar con `ROASTLAB_ROAST_NOTIFICATIONS_ENABLED=true`.
5. El primer arranque ejecuta Flyway V9. Haz respaldo de la base antes de actualizar una instalación existente.

Guías oficiales: [configuración SMTP de Gmail](https://support.google.com/mail/answer/7104828), [contraseñas de aplicación de Google](https://support.google.com/mail/answer/185833) y [plantillas de WhatsApp](https://developers.facebook.com/docs/whatsapp/api/messages/message-templates/interactive-message-templates/).

## Verificación del paquete

- Compilación de producción del frontend: correcta (`npm run build`).
- JSON de la plantilla de WhatsApp: válido.
- Pruebas y empaquetado Java: no ejecutados en el entorno de creación porque solo tiene Java Runtime, no `javac`, y no pudo resolver Maven Central. Los envíos reales también requieren los secretos de Gmail/Meta indicados arriba.
- Esta entrega es el ZIP para extraer y revisar/subir. No se incluye una publicación hecha desde este entorno.
