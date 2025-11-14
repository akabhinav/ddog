#!/bin/bash

# Example script to query metrics from ObservX

QUERY_URL="${QUERY_URL:-http://localhost:8083}"

echo "Querying metrics from $QUERY_URL..."

# Query CPU metrics
echo "CPU metrics:"
curl -X POST $QUERY_URL/api/v1/query/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "metricName": "cpu.usage",
    "startTime": "'$(date -u -d '1 hour ago' '+%Y-%m-%dT%H:%M:%SZ')'",
    "endTime": "'$(date -u '+%Y-%m-%dT%H:%M:%SZ')'"
  }' | jq '.'

echo ""

# Get aggregated metrics
echo "Aggregated CPU metrics:"
curl -X POST $QUERY_URL/api/v1/query/metrics/aggregate \
  -H "Content-Type: application/json" \
  -d '{
    "metricName": "cpu.usage",
    "startTime": "'$(date -u -d '1 hour ago' '+%Y-%m-%dT%H:%M:%SZ')'",
    "endTime": "'$(date -u '+%Y-%m-%dT%H:%M:%SZ')'",
    "aggregation": "avg"
  }' | jq '.'

echo ""
