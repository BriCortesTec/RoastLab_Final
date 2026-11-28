# Nota de integración de RoastLab

Esta nota sustituye instrucciones de una entrega anterior que describían endpoints, canales de notificación y credenciales que ya no corresponden al repositorio actual. Para operar el proyecto, consulta:

- [`README.md`](README.md): módulos, contrato actual de la API y ejecución local.
- [`deploy/DEPLOY.md`](deploy/DEPLOY.md): configuración de producción y despliegue.
- [`bruno/RoastLab`](bruno/RoastLab): solicitudes para revisar la API.

## Comportamiento actual

- Las cuentas se crean desde el módulo de usuarios con permisos de administrador; no existe registro público en `/api/auth/registro`.
- Las notificaciones automáticas de tueste completado usan correo y WhatsApp Cloud API. SMS/Twilio ya no forma parte de esta integración.
- WhatsApp requiere credenciales de Meta fuera del repositorio, el permiso correspondiente y la plantilla aprobada documentada en `deploy/DEPLOY.md`.
- La aplicación valida los datos en el cliente para mostrar errores junto al formulario y vuelve a validarlos en Spring Boot antes de escribir en la base de datos.
- No guardes contraseñas, secretos JWT ni tokens de Meta en Git. Usa los archivos externos indicados en la guía de despliegue.
