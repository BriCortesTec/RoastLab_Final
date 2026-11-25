#!/bin/bash
# Se ejecuta en TU computadora (Git Bash / Linux / macOS), no en el VPS.
# Compila backend y frontend aqui (el VPS de 1 GB no aguanta Maven) y los sube por SSH.
#   VPS=root@157.230.2.215 bash deploy/deploy-local.sh
set -euo pipefail
: "${VPS:?Define VPS, por ejemplo: VPS=root@157.230.2.215}"
cd "$(dirname "$0")/.."

echo "==> Backend (JAR)"
(cd backend && ./mvnw -q clean package -DskipTests)
JAR=$(ls backend/target/*.jar | grep -v original | head -n1)
scp "$JAR" "$VPS:/opt/roastlab/backend.jar.new"

echo "==> Frontend (build estatico)"
(cd frontend && npm ci && VITE_API_URL=https://roastlab.site/api npm run build)
rsync -az --delete frontend/dist/ "$VPS:/var/www/roastlab/"

echo "==> Reiniciar backend"
ssh "$VPS" "mv /opt/roastlab/backend.jar.new /opt/roastlab/backend.jar && chown roastlab:roastlab /opt/roastlab/backend.jar && systemctl restart roastlab && sleep 8 && systemctl is-active roastlab"
echo "Listo: https://roastlab.site"
