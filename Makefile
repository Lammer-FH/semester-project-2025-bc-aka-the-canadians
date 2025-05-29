DOCKER_COMPOSE_PATH = docker-compose.yml
BACKEND_PATH = backend
FRONTEND_PATH = frontend
DATABASE_PATH = db

.PHONY: backend frontend infra

backend:
	@echo "Building and running backend (with Docker):"
	cd $(BACKEND_PATH) && docker compose -f ../$(DOCKER_COMPOSE_PATH) up backend --build

frontend:
	@echo "Installing frontend dependencies and running application (locally):"
	cd $(FRONTEND_PATH) && npm install && npm run dev

infra:
	@echo "Running infrastructure services (MinIO and MySQL):"
	docker compose -f $(DOCKER_COMPOSE_PATH) up minio mysql