#!/usr/bin/env bash
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

docker compose \
  --project-directory "$ROOT_DIR" \
  -f "$ROOT_DIR/docker/docker-compose.yml" \
  "$@"

