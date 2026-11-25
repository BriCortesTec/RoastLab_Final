#!/bin/bash
# Ejecutar en el VPS como root:  bash 01-preparar-vps.sh
# Prepara un servidor Ubuntu de 1 GB de RAM: swap, Java, MySQL, Nginx, Certbot, Postfix y OpenDKIM.
set -euo pipefail

echo "==> Swap de 2 GB (imprescindible con 1 GB de RAM)"
if ! swapon --show | grep -q /swapfile; then
  fallocate -l 2G /swapfile
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
  echo '/swapfile none swap sw 0 0' >> /etc/fstab
  echo 'vm.swappiness=20' > /etc/sysctl.d/99-swappiness.conf
  sysctl -p /etc/sysctl.d/99-swappiness.conf
fi

echo "==> Paquetes"
export DEBIAN_FRONTEND=noninteractive
debconf-set-selections <<< "postfix postfix/mailname string roastlab.site"
debconf-set-selections <<< "postfix postfix/main_mailer_type string 'Internet Site'"
apt-get update
apt-get install -y openjdk-21-jre-headless mysql-server nginx certbot python3-certbot-nginx \
                   postfix opendkim opendkim-tools mailutils ufw curl

echo "==> MySQL ligero para 1 GB"
cat > /etc/mysql/mysql.conf.d/99-roastlab.cnf <<'CNF'
[mysqld]
performance_schema = OFF
innodb_buffer_pool_size = 128M
max_connections = 40
key_buffer_size = 8M
CNF
systemctl restart mysql

echo "==> Usuario y carpetas de la aplicacion"
id roastlab &>/dev/null || useradd --system --home /opt/roastlab --shell /usr/sbin/nologin roastlab
mkdir -p /opt/roastlab/config /var/www/roastlab
chown -R roastlab:roastlab /opt/roastlab

echo "==> Firewall"
ufw allow OpenSSH
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable

echo "Listo. Siguiente: 02-base-de-datos.sh"
