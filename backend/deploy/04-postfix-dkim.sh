#!/bin/bash
# Ejecutar en el VPS como root:  bash 04-postfix-dkim.sh
# Configura Postfix (solo envio, escucha unicamente en localhost) y OpenDKIM para roastlab.site.
set -euo pipefail
DOMINIO=roastlab.site
SELECTOR=mail
IP=$(curl -s https://api.ipify.org)

echo "==> Postfix"
postconf -e "myhostname = mail.${DOMINIO}"
postconf -e "mydomain = ${DOMINIO}"
postconf -e "myorigin = \$mydomain"
postconf -e "inet_interfaces = loopback-only"
postconf -e "inet_protocols = ipv4"
postconf -e "mydestination = \$myhostname, localhost.\$mydomain, localhost"
postconf -e "mynetworks = 127.0.0.0/8"
postconf -e "smtp_tls_security_level = may"
postconf -e "smtp_tls_loglevel = 1"
postconf -e "milter_default_action = accept"
postconf -e "milter_protocol = 6"
postconf -e "smtpd_milters = inet:localhost:8891"
postconf -e "non_smtpd_milters = \$smtpd_milters"
echo "${DOMINIO}" > /etc/mailname

echo "==> OpenDKIM"
mkdir -p /etc/opendkim/keys/${DOMINIO}
cat > /etc/opendkim.conf <<CONF
Syslog          yes
UMask           002
Domain          ${DOMINIO}
Selector        ${SELECTOR}
KeyFile         /etc/opendkim/keys/${DOMINIO}/${SELECTOR}.private
Socket          inet:8891@localhost
Canonicalization relaxed/simple
Mode            sv
SubDomains      no
OversignHeaders From
CONF
if [ ! -f /etc/opendkim/keys/${DOMINIO}/${SELECTOR}.private ]; then
  opendkim-genkey -b 2048 -d ${DOMINIO} -D /etc/opendkim/keys/${DOMINIO} -s ${SELECTOR} -v
fi
chown -R opendkim:opendkim /etc/opendkim
chmod 600 /etc/opendkim/keys/${DOMINIO}/${SELECTOR}.private

systemctl enable --now opendkim
systemctl restart opendkim
systemctl restart postfix

cat <<MSG

=====================================================================
 REGISTROS DNS QUE DEBES CREAR EN EL PANEL DE ${DOMINIO}
=====================================================================
 A      @                 ${IP}
 A      www               ${IP}
 A      mail              ${IP}
 MX     @   (prioridad 10)  mail.${DOMINIO}
 TXT    @                 "v=spf1 ip4:${IP} mx ~all"
 TXT    _dmarc            "v=DMARC1; p=none; rua=mailto:admin@${DOMINIO}"
 TXT    ${SELECTOR}._domainkey   (copia TODO el contenido entre comillas de abajo,
                                  unido en una sola linea)
---------------------------------------------------------------------
$(cat /etc/opendkim/keys/${DOMINIO}/${SELECTOR}.txt)
---------------------------------------------------------------------
 PTR (DNS inverso): si el VPS es DigitalOcean, renombra el droplet a
 mail.${DOMINIO} y el PTR se configura solo. En otros proveedores, se pide
 en el panel. Sin PTR muchos correos caen en spam.
=====================================================================
 Prueba:  echo "hola" | mail -s "Prueba RoastLab" tu_correo@gmail.com
 Revisa:  tail -f /var/log/mail.log      (buscar status=sent)
 Calidad: envia a https://www.mail-tester.com y busca SPF/DKIM = OK
MSG
