.PHONY: help build up down logs clean test scalable-up scalable-down scalable-logs

help:
	@echo "ObservX Platform - Available Commands"
	@echo "======================================"
	@echo "Development Commands:"
	@echo "  make build         - Build all services"
	@echo "  make up            - Start all services (simple)"
	@echo "  make down          - Stop all services"
	@echo "  make logs          - View logs"
	@echo "  make clean         - Clean build artifacts"
	@echo "  make test          - Run tests"
	@echo "  make restart       - Restart all services"
	@echo ""
	@echo "Scalable Deployment Commands:"
	@echo "  make scalable-up   - Start scalable deployment (3 collectors, 2 storage, 2 query, LB, monitoring)"
	@echo "  make scalable-down - Stop scalable deployment"
	@echo "  make scalable-logs - View scalable deployment logs"
	@echo "  make monitoring    - Open Grafana dashboards"
	@echo "  make metrics       - Open Prometheus"
	@echo ""
	@echo "Scaling Commands:"
	@echo "  make scale-collectors REPLICAS=5  - Scale collectors to N replicas"
	@echo "  make scale-query REPLICAS=4       - Scale query services to N replicas"

build:
	@echo "Building ObservX Platform..."
	mvn clean package -DskipTests

up:
	@echo "Starting ObservX Platform (Simple)..."
	docker-compose up -d
	@echo "Platform started! Gateway available at http://localhost:8080"

down:
	@echo "Stopping ObservX Platform..."
	docker-compose down

logs:
	docker-compose logs -f

clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	docker-compose down -v
	docker-compose -f docker-compose-scalable.yml down -v

test:
	@echo "Running tests..."
	mvn test

restart: down up
	@echo "Platform restarted!"

status:
	@echo "Service Status:"
	@docker-compose ps

# Scalable deployment commands
scalable-up:
	@echo "Starting ObservX Platform (Scalable)..."
	@echo "This will start:"
	@echo "  - 3 Collector instances (load balanced)"
	@echo "  - 2 Storage instances"
	@echo "  - 2 Query service instances (load balanced)"
	@echo "  - Redis for caching"
	@echo "  - Prometheus + Grafana for monitoring"
	docker-compose -f docker-compose-scalable.yml up -d
	@echo ""
	@echo "Platform started!"
	@echo "  Gateway:    http://localhost:8080"
	@echo "  Grafana:    http://localhost:3000 (admin/admin)"
	@echo "  Prometheus: http://localhost:9090"

scalable-down:
	@echo "Stopping scalable deployment..."
	docker-compose -f docker-compose-scalable.yml down

scalable-logs:
	docker-compose -f docker-compose-scalable.yml logs -f

scalable-status:
	@echo "Scalable Deployment Status:"
	@docker-compose -f docker-compose-scalable.yml ps

monitoring:
	@echo "Opening Grafana..."
	@open http://localhost:3000 || xdg-open http://localhost:3000 || echo "Open http://localhost:3000 in your browser"

metrics:
	@echo "Opening Prometheus..."
	@open http://localhost:9090 || xdg-open http://localhost:9090 || echo "Open http://localhost:9090 in your browser"

# Scaling commands
scale-collectors:
	@echo "Scaling collectors to $(REPLICAS) replicas..."
	docker-compose -f docker-compose-scalable.yml up -d --scale collector-1=$(REPLICAS)

scale-query:
	@echo "Scaling query services to $(REPLICAS) replicas..."
	docker-compose -f docker-compose-scalable.yml up -d --scale query-service-1=$(REPLICAS)
