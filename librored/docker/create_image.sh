#!/bin/bash
set -e

DOCKER_USER=maramedina

# Mover a raíz del proyecto
cd "$(dirname "$0")/.."

docker build -t $DOCKER_USER/librored-backend -f docker/Dockerfile .
docker push $DOCKER_USER/librored-backend
