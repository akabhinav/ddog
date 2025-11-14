# Quick Start - Scalable Deployment

Get the ObservX platform running with **3 collectors, 2 storage instances, 2 query services, load balancing, and full monitoring** in under 5 minutes!

## Prerequisites

- Docker & Docker Compose
- **Minimum Resources**:
  - CPU: 8 cores
  - RAM: 16GB
  - Disk: 20GB

## Step 1: Start the Platform

```bash
# Start all services with scalable configuration
docker-compose -f docker-compose-scalable.yml up -d

# This will start:
# - 3 Collector instances (load balanced)
# - 2 Storage instances (parallel Kafka consumption)
# - 2 Query service instances (load balanced)
# - Nginx load balancer
# - Redis for distributed caching
# - TimescaleDB for time-series storage
# - Kafka for event streaming
# - Prometheus for metrics
# - Grafana for visualization
```

## Step 2: Verify Services are Running

```bash
# Check all services
docker-compose -f docker-compose-scalable.yml ps

# Check health
curl http://localhost:8080/health
```

Expected output: `ObservX Platform - Healthy`

## Step 3: Send Test Data

```bash
# Send a single metric
curl -X POST http://localhost:8080/api/v1/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test.cpu.usage",
    "type": "GAUGE",
    "value": 45.5,
    "tags": {
      "host": "server-1",
      "datacenter": "us-east-1"
    },
    "unit": "percent"
  }'

# Send batch metrics (higher throughput)
curl -X POST http://localhost:8080/api/v1/metrics/batch \
  -H "Content-Type: application/json" \
  -d '[
    {"name": "cpu.usage", "type": "GAUGE", "value": 45.5, "tags": {"host": "server-1"}, "unit": "percent"},
    {"name": "memory.usage", "type": "GAUGE", "value": 78.2, "tags": {"host": "server-1"}, "unit": "percent"},
    {"name": "disk.usage", "type": "GAUGE", "value": 62.0, "tags": {"host": "server-1"}, "unit": "percent"}
  ]'
```

## Step 4: Query Data

Wait 5-10 seconds for data to be processed, then query:

```bash
# Query metrics
curl -X POST http://localhost:8080/api/v1/query/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "metricName": "test.cpu.usage",
    "startTime": "'$(date -u -d '1 hour ago' '+%Y-%m-%dT%H:%M:%SZ')'",
    "endTime": "'$(date -u '+%Y-%m-%dT%H:%M:%SZ')'"
  }' | jq '.'
```

## Step 5: Access Monitoring

### Grafana Dashboards
```
URL: http://localhost:3000
Username: admin
Password: admin
```

**Available Dashboards**:
- Platform Overview
- Service Metrics
- Infrastructure Metrics

### Prometheus Metrics
```
URL: http://localhost:9090
```

**Useful Queries**:
- `rate(http_server_requests_seconds_count[5m])` - Request rate
- `jvm_memory_used_bytes{area="heap"}` - Heap memory
- `resilience4j_circuitbreaker_state` - Circuit breaker states

## Step 6: Load Testing

Test the platform's performance:

```bash
# Install Apache Bench (if not installed)
# Ubuntu: sudo apt-get install apache2-utils
# Mac: brew install httpd

# Send 10,000 requests with 100 concurrent connections
ab -n 10000 -c 100 -p payload.json -T application/json \
   http://localhost:8080/api/v1/metrics
```

Create `payload.json`:
```json
{
  "name": "load.test.metric",
  "type": "COUNTER",
  "value": 1,
  "tags": {"test": "load"}
}
```

**Expected Performance**:
- **Throughput**: 10,000+ requests/second
- **Latency (P95)**: < 10ms
- **Error Rate**: < 0.1%

## Architecture Overview

```
                    ┌─────────────┐
                    │   Nginx LB  │  :8080
                    │ (Least Conn)│
                    └──────┬──────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────▼────┐      ┌────▼────┐      ┌────▼────┐
    │Collector│      │Collector│      │Collector│
    │    1    │      │    2    │      │    3    │
    └────┬────┘      └────┬────┘      └────┬────┘
         │                 │                 │
         └─────────────────┼─────────────────┘
                           │
                    ┌──────▼──────┐
                    │    Kafka    │
                    │ (6 partitions)│
                    └──────┬──────┘
                           │
         ┌─────────────────┴─────────────────┐
         │                                   │
    ┌────▼────┐                        ┌────▼────┐
    │ Storage │                        │ Storage │
    │    1    │                        │    2    │
    └────┬────┘                        └────┬────┘
         │                                   │
         └─────────────────┬─────────────────┘
                           │
                    ┌──────▼──────┐
                    │ TimescaleDB │
                    └──────▲──────┘
                           │
         ┌─────────────────┴─────────────────┐
         │                                   │
    ┌────┴────┐                        ┌────┴────┐
    │  Query  │                        │  Query  │
    │    1    │                        │    2    │
    └─────────┘                        └─────────┘
```

## Scaling Operations

### Scale Collectors
```bash
# Scale to 5 collectors
docker-compose -f docker-compose-scalable.yml up -d --scale collector-1=5

# Nginx will automatically load balance across all 5
```

### Scale Query Services
```bash
# Scale to 4 query services
docker-compose -f docker-compose-scalable.yml up -d --scale query-service-1=4
```

### Scale Storage (Kafka Consumers)
```bash
# Scale to 3 storage instances
docker-compose -f docker-compose-scalable.yml up -d --scale storage-1=3

# Note: Max consumers = number of Kafka partitions (6)
```

## Monitoring & Alerts

### Check Circuit Breaker Status
```bash
# Check all collectors
curl http://localhost:8081/actuator/circuitbreakers | jq '.'

# Expected output shows circuit state for each collector
```

### Check Rate Limiter Status
```bash
curl http://localhost:8081/actuator/ratelimiters | jq '.'
```

### View Metrics in Prometheus
```bash
# Open Prometheus
open http://localhost:9090

# Useful queries:
# - resilience4j_circuitbreaker_calls_seconds_count
# - resilience4j_ratelimiter_available_permissions
# - kafka_producer_record_send_total
# - jvm_memory_used_bytes
```

## Performance Tuning

### Increase Throughput
Edit `docker-compose-scalable.yml` and adjust:

```yaml
# Increase Kafka batching
KAFKA_PRODUCER_PROPERTIES_LINGER_MS: 50
KAFKA_PRODUCER_PROPERTIES_BATCH_SIZE: 65536

# Increase collector heap
JAVA_OPTS: "-Xmx2g -Xms1g ..."
```

### Reduce Latency
```yaml
# Reduce Kafka batching
KAFKA_PRODUCER_PROPERTIES_LINGER_MS: 1
KAFKA_PRODUCER_PROPERTIES_BATCH_SIZE: 16384

# Increase connection pool
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE: 30
```

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose -f docker-compose-scalable.yml logs collector-1

# Check health
docker-compose -f docker-compose-scalable.yml ps
```

### High Latency
```bash
# Check circuit breaker state (might be open)
curl http://localhost:8081/actuator/health | jq '.components.circuitBreakers'

# Check Kafka lag
docker exec -it observx-kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group observx-storage \
  --describe
```

### Out of Memory
```bash
# Increase JVM heap for collectors
# Edit docker-compose-scalable.yml:
JAVA_OPTS: "-Xmx2g -Xms1g -XX:+UseG1GC"

# Restart
docker-compose -f docker-compose-scalable.yml restart collector-1
```

### Redis Connection Issues
```bash
# Check Redis
docker exec -it observx-redis-master redis-cli ping

# Should return: PONG
```

## Stop the Platform

```bash
# Stop all services
docker-compose -f docker-compose-scalable.yml down

# Stop and remove all data
docker-compose -f docker-compose-scalable.yml down -v
```

## Resource Usage

Expected resource usage with default configuration:

| Component | CPU | Memory | Disk |
|-----------|-----|--------|------|
| 3x Collectors | 3 cores | 4.5GB | - |
| 2x Storage | 2 cores | 3GB | - |
| 2x Query | 2 cores | 3GB | - |
| Nginx | 0.5 core | 256MB | - |
| Redis | 0.5 core | 512MB | 1GB |
| TimescaleDB | 2 cores | 2GB | 10GB |
| Kafka + ZK | 2.5 cores | 2.5GB | 2GB |
| Prometheus | 1 core | 1GB | 2GB |
| Grafana | 0.5 core | 512MB | 500MB |
| **Total** | **~14 cores** | **~17GB** | **~15GB** |

## Next Steps

1. **Configure Alerts**: Set up alerting rules in Prometheus
2. **Custom Dashboards**: Create Grafana dashboards for your metrics
3. **Authentication**: Add API keys or OAuth2
4. **TLS**: Enable HTTPS for production
5. **Backup**: Set up database backups
6. **Cloud Deployment**: Deploy to Kubernetes

## Additional Resources

- [Scalability Architecture](./SCALABILITY.md) - Detailed scalability guide
- [Architecture Documentation](./ARCHITECTURE.md) - System design deep dive
- [README](./README.md) - Main documentation

## Support

For issues or questions:
- Check logs: `docker-compose -f docker-compose-scalable.yml logs`
- View metrics: http://localhost:3000 (Grafana)
- Check health: `curl http://localhost:8080/health`

---

**You now have a production-ready, horizontally scalable observability platform running locally!** 🚀
