DOCKER_COMPOSE_PATH = docker-compose.yml
BACKEND_PATH = backend
FRONTEND_PATH = frontend
DATABASE_PATH = db

.PHONY: backend frontend infra network infra-restart backend-docker frontend-docker

backend:
	@echo "Building and running application (locally):"
	cd $(BACKEND_PATH) && mvn spring-boot:run

backend-docker:
	@echo "Building and running backend (with Docker):"
	cd $(BACKEND_PATH) && docker compose -f ../$(DOCKER_COMPOSE_PATH) up backend --build

frontend:
	@echo "Installing frontend dependencies and running application (locally):"
	cd $(FRONTEND_PATH) && npm install && npm run dev

lint-frontend:
	@echo "Linting frontend code:"
	cd $(FRONTEND_PATH) && npm run lint:check

fix-frontend:
	@echo "Fixing frontend code issues:"
	cd $(FRONTEND_PATH) && npm run fix

check-frontend:
	@echo "Running full frontend code quality check:"
	cd $(FRONTEND_PATH) && npm run check

frontend-docker:
	@echo "Building and running frontend (with Docker):"
	cd $(FRONTEND_PATH) && docker compose -f ../$(DOCKER_COMPOSE_PATH) up frontend --build

network:
	@echo "Creating Docker network ('campus-network'):"
	docker network create campus-network

infra:
	@echo "Running infrastructure services (MinIO and MySQL):"
	docker compose -f $(DOCKER_COMPOSE_PATH) up minio mysql mysql-dashboard

infra-restart:
	@echo "Cleaning up Docker containers and images:"
	docker compose -f $(DOCKER_COMPOSE_PATH) down minio mysql mysql-dashboard -v
	make infra
