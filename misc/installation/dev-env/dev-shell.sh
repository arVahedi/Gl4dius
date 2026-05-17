#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd -- "$SCRIPT_DIR/../../.." && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yaml"

echo "SCRIPT_DIR=$SCRIPT_DIR"
echo "PROJECT_ROOT=$PROJECT_ROOT"
echo "COMPOSE_FILE=$COMPOSE_FILE"

docker compose \
  -f "$COMPOSE_FILE" \
  --project-directory "$PROJECT_ROOT" \
  up -d --build

docker exec -it gl4dius bash
