#!/usr/bin/env bash

curl -X POST http://localhost:8080/api/v1/hierarchy \
  -H "Content-Type: application/json" \
  -d '{
   "name": "Headquarters",
    "organizationId": "0331d4e4-44a5-452b-be30-b375ba5c9cb3"
  }'