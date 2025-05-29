DOCKER_COMPOSE_PATH = docker-compose.yml
BACKEND_PATH = backend
FRONTEND_PATH = frontend
DATABASE_PATH = db

.PHONY: backend frontend infra

backend:
	@echo "Building backend..."
	cd $(BACKEND_PATH) && docker compose -f ../$(DOCKER_COMPOSE_PATH) up backend --build

frontend:
	@echo "Installing frontend dependencies..."
	cd $(FRONTEND_PATH) && npm install && npm run dev

infra:
	@echo "Building infrastructure..."
	docker compose -f $(DOCKER_COMPOSE_PATH) up minio mysql