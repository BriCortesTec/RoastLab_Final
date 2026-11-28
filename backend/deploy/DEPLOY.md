# Despliegue de RoastLab en el VPS (Ubuntu, 4 GB de RAM)

Dominio: `roastlab.space` · Sistema: Ubuntu · Todo corre en el mismo servidor: MySQL, backend (JAR + systemd) y frontend (build estático en Nginx). El correo de la aplicación usa Gmail SMTP.

> Es posible compilar en el VPS dentro de una **carpeta aislada de pruebas**, sin sobrescribir el JAR activo. El script `deploy-local.sh` es una alternativa para compilar en la computadora.

## 0. Antes de empezar

1. **Gmail**: activa verificación en dos pasos y genera una contraseña de aplicación para RoastLab. No uses ni compartas la contraseña normal de la cuenta.
2. Copia la carpeta `deploy/` al VPS: `scp -r backend/deploy ubuntu@13.185.56.85:~/`.

## 1. Preparar el servidor (como root, en el VPS)

```bash
cd /home/ubuntu/deploy
bash 01-preparar-vps.sh
DB_PASSWORD='una-clave-fuerte-nueva' bash 02-base-de-datos.sh
```

## 2. Configuración secreta del backend

```bash
# SOLO si el archivo de producción NO existe: nunca sobrescribas credenciales existentes.
# cp application-prod.properties.example /opt/roastlab/config/application-prod.properties
nano /opt/roastlab/config/application-prod.properties     # password de BD, JWT, Gmail y Meta
chown roastlab:roastlab /opt/roastlab/config/application-prod.properties
chmod 600 /opt/roastlab/config/application-prod.properties
openssl rand -base64 48        # úsalo como app.jwt.secret
```

## 3. Servicio systemd

```bash
cp roastlab.service /etc/systemd/system/roastlab.service
systemctl daemon-reload && systemctl enable roastlab
```

## 4. Subir backend y frontend (desde TU computadora)

```bash
VPS=ubuntu@13.185.56.85 bash backend/deploy/deploy-local.sh
```

En el VPS: `systemctl status roastlab` y `journalctl -u roastlab -f`. En el primer arranque Flyway aplica V1–V9, incluye verificación de correo y crea los roles. El archivo de ejemplo de la carpeta database es independiente.

### Primer acceso y cuentas de evaluación

Cuando el servicio esté activo y hayas comprobado las migraciones, ejecuta desde la terminal del VPS:

```bash
sudo apt-get install -y python3-bcrypt
sudo python3 /home/ubuntu/deploy/crear-admin.py
```

El comando pide el correo y la contraseña en la terminal y crea el primer administrador. Si ya existe un administrador, conserva la cuenta existente. Después entra como administrador en **Usuarios** y crea las cuentas de gerente y tostador que usarán las personas evaluadoras. También puedes entrar con el gerente para dar de alta al tostador. Comprueba cada acceso en una ventana privada.

## 5. Nginx + HTTPS (cuando el DNS ya resuelva)

```bash
EMAIL=tu@correo.com bash 03-nginx-y-ssl.sh
```

## 6. Gmail SMTP

Si eliges Gmail SMTP, en `application-prod.properties` configura `spring.mail.username` con la cuenta Gmail, `spring.mail.password` con su contraseña de aplicación y `app.mail.from` con esa misma dirección. Reinicia el backend después de modificar el archivo. La configuración usa TLS en el puerto 587. Prueba el envío desde **Notificaciones → Probar correo** y confirma que la bitácora quede en `ENVIADO`.

Para usar un remitente `@roastlab.space`, Gmail debe permitir ese remitente como alias verificado. La autenticación se realiza con la cuenta configurada.

## 7. WhatsApp Cloud API (Meta)

- **SMS**: descartado para esta entrega.
- **WhatsApp**: integración mediante Meta WhatsApp Cloud API. En el archivo
  externo `/opt/roastlab/config/application-prod.properties`, configura
  `meta.whatsapp.phone-number-id`, `meta.whatsapp.access-token` y
  `meta.whatsapp.api-version`. El access token debe tener el permiso
  `whatsapp_business_messaging`.
- Registra el número de negocio en Cloud API y crea/aprueba la plantilla
  `roastlab_tueste_completado` en español de México (`es_MX`) con cinco
  parámetros de texto en el cuerpo.
- Crea y aprueba `roastlab_bienvenida` en `es_MX` usando el payload de
  [`whatsapp-template-roastlab_bienvenida.json`](whatsapp-template-roastlab_bienvenida.json).
  Su cuerpo debe tener **una variable: el nombre**. Por seguridad, el enlace de verificación **solo se manda al correo**, nunca por WhatsApp ni al número compartido de pruebas.
  Configura `meta.whatsapp.welcome-test-recipient` con un número autorizado de
  prueba y conserva `meta.whatsapp.welcome-mode=test`. Las bienvenidas nunca se
  envían al teléfono que acaba de registrar en este modo. Para una etapa futura,
  tras cambiar al número de negocio de producción y contar con consentimiento
  del destinatario, se puede cambiar a `meta.whatsapp.welcome-mode=production`.
- Los avisos automáticos de tueste se conservan en código pero quedan apagados
  con `app.notifications.roast-completed.enabled=false`.
- La prueba manual de texto requiere una ventana de atención abierta
  con el destinatario; para iniciar conversaciones, Meta exige una plantilla
  aprobada y el consentimiento previo del destinatario.
- No guardes el token en Git, en `application.properties` versionado ni en el
  frontend. El archivo externo de configuración se protege con permisos `600`.

## Comprobación final

| Prueba | Esperado |
|---|---|
| `https://roastlab.space` | Login con candado (HTTPS válido) |
| `curl -s https://roastlab.space/api/cafes` | `401` en JSON (protegido) |
| Login con la cuenta administradora creada en el VPS | Entra al panel |
| `systemctl status roastlab nginx mysql` | todos `active` |
| `free -m` | swap en uso moderado, sin OOM |
