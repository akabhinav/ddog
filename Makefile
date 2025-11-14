.PHONY: help build up down logs clean test

help:
	@echo "ObservX Platform - Available Commands"
	@echo "======================================"
	@echo "make build      - Build all services"
	@echo "make up         - Start all services"
	@echo "make down       - Stop all services"
	@echo "make logs       - View logs"
	@echo "make clean      - Clean build artifacts"
	@echo "make test       - Run tests"
	@echo "make restart    - Restart all services"

build:
	@echo "Building ObservX Platform..."
	mvn clean package -DskipTests

up:
	@echo "Starting ObservX Platform..."
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

test:
	@echo "Running tests..."
	mvn test

restart: down up
	@echo "Platform restarted!"

status:
	@echo "Service Status:"
	@docker-compose ps
