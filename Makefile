DOCKER_COMPOSE_PATH = docker-compose.yml
BACKEND_PATH = backend
FRONTEND_PATH = frontend

.PHONY: backend backend-dev-profile backend-docker frontend lint-frontend fix-frontend check-frontend frontend-docker run-all restart-all help

backend: # Building and running backend (locally)
	@echo "Building and running backend (locally):"
	cd $(BACKEND_PATH) && mvn spring-boot:run

backend-dev-profile: # Building and running backend (locally) with dev profile
	@echo "Building and running backend (locally) with the dev profile:"
	cd $(BACKEND_PATH) && mvn spring-boot:run -Dspring-boot.run.profiles=dev

backend-docker: # Building and running backend (with Docker)
	@echo "Building and running backend (with Docker):"
	cd $(BACKEND_PATH) && docker compose -f ../$(DOCKER_COMPOSE_PATH) up backend --build

frontend: # Installing frontend dependencies and running application locally
	@echo "Installing frontend dependencies and running application (locally):"
	cd $(FRONTEND_PATH) && npm install && npm run dev

lint-frontend: # Linting frontend code
	@echo "Linting frontend code:"
	cd $(FRONTEND_PATH) && npm run lint:check

fix-frontend: # Fixing frontend code issues
	@echo "Fixing frontend code issues:"
	cd $(FRONTEND_PATH) && npm run fix

check-frontend: # Running full frontend code quality check:
	@echo "Running full frontend code quality check:"
	cd $(FRONTEND_PATH) && npm run check

frontend-docker: # Building and running frontend (with Docker)
	@echo "Building and running frontend (with Docker):"
	cd $(FRONTEND_PATH) && docker compose -f ../$(DOCKER_COMPOSE_PATH) up frontend --build

run-all: # Running all services
	@echo "Running all services:"
	docker compose -f $(DOCKER_COMPOSE_PATH) up

restart-all: # Removing volumes, rebuilding images and starting all services
	@echo "Removing volumes, rebuilding images and starting all services"
	docker compose -f $(DOCKER_COMPOSE_PATH) down -v
	docker compose -f $(DOCKER_COMPOSE_PATH) up --build

help: # Show this help message
	@echo "Available make commands:"
	@awk 'BEGIN {FS = ":.*?#"} /^[a-zA-Z_-]+:.*?#/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
