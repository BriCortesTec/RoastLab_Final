# Despliegue de RoastLab en el VPS (Ubuntu, 1 GB de RAM)

Dominio: `roastlab.site` · Sistema: Ubuntu · Todo corre en el mismo servidor: MySQL, backend (JAR + systemd), frontend (build estático en Nginx), Postfix.

> **Compilar en tu computadora, no en el VPS.** Maven y Node no caben cómodos en 1 GB; el script `deploy-local.sh` compila aquí y sube el resultado.

## 0. Antes de empezar

1. **DNS** (panel donde compraste `roastlab.site`): crea los registros `A` de `@`, `www` y `mail` apuntando a la IP del VPS. Los demás (MX, SPF, DKIM, DMARC) te los imprime el paso 4.
2. **Puerto 25**: prueba `nc -vz -w 5 gmail-smtp-in.l.google.com 25`. Si hace timeout, el proveedor lo bloquea: abre un ticket de soporte pidiendo desbloquear el SMTP saliente.
3. Copia la carpeta `deploy/` al VPS: `scp -r deploy root@IP:/root/`.

## 1. Preparar el servidor (como root, en el VPS)

```bash
cd /root/deploy
bash 01-preparar-vps.sh
DB_PASSWORD='una-clave-fuerte-nueva' bash 02-base-de-datos.sh
```

## 2. Configuración secreta del backend

```bash
cp application-prod.properties.example /opt/roastlab/config/application-prod.properties
nano /opt/roastlab/config/application-prod.properties     # password de BD, JWT, Twilio
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
VPS=root@157.230.2.215 bash deploy/deploy-local.sh
```

En el VPS: `systemctl status roastlab` y `journalctl -u roastlab -f`. En el primer arranque Flyway crea las 14 tablas y carga los datos de prueba.

## 5. Nginx + HTTPS (cuando el DNS ya resuelva)

```bash
EMAIL=tu@correo.com bash 03-nginx-y-ssl.sh
```

## 6. Correo propio: Postfix + SPF + DKIM

```bash
bash 04-postfix-dkim.sh
```

Imprime los registros DNS que debes crear (MX, SPF, DKIM, DMARC). Después:

```bash
echo "hola" | mail -s "Prueba RoastLab" tu_correo@gmail.com
tail -f /var/log/mail.log          # debe decir status=sent
```

Verifica la calidad en https://www.mail-tester.com (SPF y DKIM en verde). Si el PTR no está configurado, algunos correos caerán en spam.

## 7. Twilio

- **SMS**: con cuenta de prueba solo envía a números *verificados* en la consola de Twilio.
- **WhatsApp**: cada número que vaya a recibir mensajes debe unirse al *sandbox* (enviar el código `join ...` al número de Twilio). `twilio.whatsapp-from=whatsapp:+14155238886`.
- Prueba desde Bruno: `06-notificaciones` → "Prueba de SMS / WhatsApp".

## Comprobación final

| Prueba | Esperado |
|---|---|
| `https://roastlab.site` | Login con candado (HTTPS válido) |
| `curl -s https://roastlab.site/api/cafes` | `401` en JSON (protegido) |
| Login `admin@roastlab.site` / `RoastLab#2026` | Entra al panel |
| `systemctl status roastlab nginx postfix mysql` | todos `active` |
| `free -m` | swap en uso moderado, sin OOM |
