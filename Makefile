# Search Engine Development Makefile
# This Makefile provides common development tasks for the search engine project

.PHONY: help setup clean build test lint format security-scan docker-up docker-down

# Default target
help: ## Show this help message
	@echo "Search Engine Development Commands:"
	@echo "=================================="
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

setup: ## Setup development environment
	@echo "Setting up development environment..."
	@chmod +x scripts/setup-dev.sh
	@./scripts/setup-dev.sh

clean: ## Clean all build artifacts
	@echo "Cleaning build artifacts..."
	@chmod +x scripts/cleanup-dev.sh
	@./scripts/cleanup-dev.sh

build: ## Build all services
	@echo "Building all services..."
	@chmod +x scripts/build-all.sh
	@./scripts/build-all.sh

test: ## Run all tests
	@echo "Running all tests..."
	@chmod +x scripts/test-all.sh
	@./scripts/test-all.sh

lint: ## Run linting for all code
	@echo "Running linting..."
	@cd search-engine-ui && npm run lint
	@echo "Backend linting integrated with Maven build"

format: ## Format all code
	@echo "Formatting code..."
	@cd search-engine-ui && npm run format
	@echo "Java formatting handled by IDE/Maven plugins"

format-check: ## Check code formatting
	@echo "Checking code formatting..."
	@cd search-engine-ui && npm run format:check

security-scan: ## Run security vulnerability scans
	@echo "Running security scans..."
	@cd search_engine_microservice && \
	for service in */; do \
		if [ -f "$$service/pom.xml" ]; then \
			echo "Scanning $$service..."; \
			cd "$$service" && mvn org.owasp:dependency-check-maven:check -q && cd ..; \
		fi \
	done
	@cd search-engine-ui && npm audit

docker-up: ## Start infrastructure services
	@echo "Starting infrastructure services..."
	@docker-compose up -d postgres redis kafka zookeeper

docker-down: ## Stop infrastructure services
	@echo "Stopping infrastructure services..."
	@docker-compose down

docker-logs: ## View infrastructure logs
	@docker-compose logs -f

quality-check: ## Run comprehensive quality checks
	@echo "Running quality checks..."
	@$(MAKE) lint
	@$(MAKE) security-scan
	@$(MAKE) test

ci-build: ## CI/CD build pipeline
	@echo "Running CI build pipeline..."
	@$(MAKE) clean
	@$(MAKE) build
	@$(MAKE) quality-check

dev-start: ## Start development environment
	@echo "Starting development environment..."
	@$(MAKE) docker-up
	@echo "Infrastructure started. You can now start individual services."
	@echo "Backend services: cd search_engine_microservice/<service> && mvn spring-boot:run"
	@echo "Frontend: cd search-engine-ui && npm start"

dev-stop: ## Stop development environment
	@echo "Stopping development environment..."
	@$(MAKE) docker-down

status: ## Check development environment status
	@echo "Development Environment Status:"
	@echo "==============================="
	@echo "Docker containers:"
	@docker-compose ps
	@echo ""
	@echo "Java version:"
	@java -version 2>&1 | head -n1
	@echo ""
	@echo "Node.js version:"
	@node --version
	@echo ""
	@echo "Maven version:"
	@mvn -version | head -n1

install-deps: ## Install all dependencies
	@echo "Installing dependencies..."
	@cd search-engine-ui && npm install
	@echo "Maven dependencies will be installed during build"

update-deps: ## Update all dependencies
	@echo "Updating dependencies..."
	@cd search-engine-ui && npm update
	@echo "Maven dependencies updated via pom.xml changes"

# Development workflow targets
dev-workflow: ## Complete development workflow (clean, build, test)
	@$(MAKE) clean
	@$(MAKE) build
	@$(MAKE) test
	@echo "Development workflow completed successfully!"

pre-commit: ## Run pre-commit checks
	@echo "Running pre-commit checks..."
	@$(MAKE) format-check
	@$(MAKE) lint
	@$(MAKE) test

# Documentation targets
docs: ## Generate project documentation
	@echo "Generating documentation..."
	@echo "API documentation available at: http://localhost:8081/swagger-ui.html (when services are running)"
	@echo "Code coverage reports in: search_engine_microservice/*/target/site/jacoco/"
	@echo "Frontend coverage in: search-engine-ui/coverage/"