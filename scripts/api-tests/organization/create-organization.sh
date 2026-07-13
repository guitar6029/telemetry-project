#!/usr/bin/env bash

curl -X POST http://localhost:8080/api/v1/organizations \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Acme Industries",
    "slug":"acme-industries"
  }'