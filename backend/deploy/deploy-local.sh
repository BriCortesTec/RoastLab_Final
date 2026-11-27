#!/bin/bash
# Ejecutar desde Git Bash o Linux con un usuario SSH autorizado para sudo.
set -euo pipefail
: "${VPS:?Define VPS con el usuario y servidor de destino}"
cd "$(dirname "$0")/../.."

(cd backend && bash mvnw -q clean package -DskipTests)
(cd frontend && npm ci && VITE_API_URL=/api npm run build)

remote_dir=$(ssh "$VPS" 'mktemp -d')
cleanup() { ssh "$VPS" "rm -rf -- '$remote_dir'"; }
trap cleanup EXIT
scp backend/target/*.jar "$VPS:$remote_dir/backend.jar"
scp -r frontend/dist "$VPS:$remote_dir/"
ssh "$VPS" "sudo install -o roastlab -g roastlab -m 640 '$remote_dir/backend.jar' /opt/roastlab/backend.jar.new &&
  sudo mv /opt/roastlab/backend.jar.new /opt/roastlab/backend.jar &&
  sudo systemctl restart roastlab &&
  sudo cp -R '$remote_dir/dist/.' /var/www/roastlab/"
