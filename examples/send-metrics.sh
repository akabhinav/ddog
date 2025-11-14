#!/bin/bash

# Example script to send metrics to ObservX

COLLECTOR_URL="${COLLECTOR_URL:-http://localhost:8081}"

echo "Sending sample metrics to $COLLECTOR_URL..."

# CPU metric
curl -X POST $COLLECTOR_URL/api/v1/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "cpu.usage",
    "type": "GAUGE",
    "value": 45.5,
    "tags": {
      "host": "server-1",
      "datacenter": "us-east-1"
    },
    "unit": "percent"
  }'

echo ""

# Memory metric
curl -X POST $COLLECTOR_URL/api/v1/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "memory.usage",
    "type": "GAUGE",
    "value": 78.2,
    "tags": {
      "host": "server-1",
      "datacenter": "us-east-1"
    },
    "unit": "percent"
  }'

echo ""

# Request counter
curl -X POST $COLLECTOR_URL/api/v1/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "http.requests.total",
    "type": "COUNTER",
    "value": 1000,
    "tags": {
      "service": "api-gateway",
      "endpoint": "/users",
      "method": "GET"
    },
    "unit": "count"
  }'

echo ""
echo "Metrics sent successfully!"
