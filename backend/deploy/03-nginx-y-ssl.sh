#!/bin/bash
# Ejecutar en el VPS como root DESPUES de que el DNS de roastlab.space apunte a esta IP.
#   EMAIL=tu@correo.com bash 03-nginx-y-ssl.sh
set -euo pipefail
: "${EMAIL:?Define EMAIL para los avisos de Lets Encrypt}"
cd "$(dirname "$0")"

cp nginx-roastlab.conf /etc/nginx/sites-available/roastlab
ln -sf /etc/nginx/sites-available/roastlab /etc/nginx/sites-enabled/roastlab
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl reload nginx

# Certificado gratuito de Let's Encrypt (renovacion automatica con el timer de certbot)
certbot --nginx -d roastlab.space -d www.roastlab.space --non-interactive --agree-tos -m "$EMAIL" --redirect
systemctl list-timers | grep -i certbot || true
echo "HTTPS listo: https://roastlab.space"
