#!/usr/bin/env bash


curl -X POST http://localhost:8080/api/v1/device-templates \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Drone Template",
    "description": "Default telemetry template for drones",
    "organizationId": "0331d4e4-44a5-452b-be30-b375ba5c9cb3"
  }'