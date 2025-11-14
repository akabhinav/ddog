#!/bin/bash

# Example script to send logs to ObservX

COLLECTOR_URL="${COLLECTOR_URL:-http://localhost:8081}"

echo "Sending sample logs to $COLLECTOR_URL..."

# Info log
curl -X POST $COLLECTOR_URL/api/v1/logs \
  -H "Content-Type: application/json" \
  -d '{
    "level": "INFO",
    "message": "User authentication successful",
    "service": "auth-service",
    "host": "server-1",
    "logger": "com.example.AuthController",
    "tags": {
      "userId": "12345",
      "action": "login"
    },
    "fields": {
      "duration_ms": 50,
      "ip_address": "192.168.1.100"
    }
  }'

echo ""

# Error log
curl -X POST $COLLECTOR_URL/api/v1/logs \
  -H "Content-Type: application/json" \
  -d '{
    "level": "ERROR",
    "message": "Database connection failed",
    "service": "user-service",
    "host": "server-2",
    "logger": "com.example.DatabaseConnection",
    "tags": {
      "error": "connection_timeout",
      "database": "users_db"
    },
    "stackTrace": "java.sql.SQLException: Connection timeout\n\tat com.example.DatabaseConnection.connect()"
  }'

echo ""
echo "Logs sent successfully!"
