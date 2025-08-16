# Development Environment Setup Guide

This guide provides comprehensive instructions for setting up and working with the Search Engine project development environment.

## 🚀 Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/suyashnehete/SearchEngine.git
cd SearchEngine

# 2. Run the automated setup
make setup

# 3. Start development environment
make dev-start

# 4. Build all services
make build

# 5. Run tests
make test
```

## 📋 Prerequisites

Before starting, ensure you have the following installed:

### Required Software

| Software           | Version | Purpose                       |
| ------------------ | ------- | ----------------------------- |
| **Java**           | 17+     | Backend microservices         |
| **Maven**          | 3.6+    | Build automation              |
| **Node.js**        | 16+     | Frontend development          |
| **npm**            | 8+      | Package management            |
| **Docker**         | 20+     | Containerization              |
| **Docker Compose** | 2+      | Multi-container orchestration |

### Optional but Recommended

- **Git** 2.30+ for version control
- **VS Code** with recommended extensions
- **IntelliJ IDEA** or **Eclipse** for Java development
- **Postman** or **Insomnia** for API testing

## 🛠️ Development Environment Setup

### Automated Setup

The project includes an automated setup script that configures everything:

```bash
# Make the script executable and run it
chmod +x scripts/setup-dev.sh
./scripts/setup-dev.sh

# Or use the Makefile
make setup
```

The setup script will:

- ✅ Check all prerequisites
- ✅ Install Git hooks for code quality
- ✅ Configure IDE settings (VS Code)
- ✅ Install frontend dependencies
- ✅ Setup backend development tools
- ✅ Start infrastructure services
- ✅ Create development scripts

### Manual Setup (Alternative)

If you prefer manual setup or the automated script fails:

#### 1. Infrastructure Services

```bash
# Start PostgreSQL, Redis, Kafka, and Zookeeper
docker-compose up -d postgres redis kafka zookeeper

# Verify services are running
docker-compose ps
```

#### 2. Frontend Setup

```bash
cd search-engine-ui

# Install dependencies
npm install

# Install development tools
npm install --save-dev @angular-eslint/schematics prettier husky lint-staged

# Setup ESLint
npx ng add @angular-eslint/schematics --skip-confirmation
```

#### 3. Backend Setup

```bash
# Each service can be built independently
cd search_engine_microservice/auth-server
mvn clean compile

# Or build all services
cd search_engine_microservice
for service in */; do
    if [ -f "$service/pom.xml" ]; then
        cd "$service" && mvn clean compile && cd ..
    fi
done
```

## 🏗️ Project Structure

```
SearchEngine/
├── scripts/                        # Development and build scripts
│   ├── setup-dev.sh               # Automated development setup
│   ├── build-all.sh               # Build all services
│   ├── test-all.sh                # Run all tests
│   └── cleanup-dev.sh             # Clean development environment
├── search_engine_microservice/     # Backend microservices
│   ├── discovery/                  # Eureka service registry
│   ├── config-server/             # Configuration management
│   ├── auth-server/               # OAuth2 authentication
│   ├── gateway/                   # API gateway
│   ├── crawler/                   # Web crawling service
│   ├── indexer/                   # Content indexing service
│   ├── query/                     # Search query service
│   ├── checkstyle.xml             # Java code style configuration
│   └── owasp-suppressions.xml     # Security scan suppressions
├── search-engine-ui/              # Angular frontend
│   ├── .eslintrc.json             # ESLint configuration
│   ├── .prettierrc                # Prettier configuration
│   └── src/                       # Source code
├── docker-compose.yml             # Infrastructure services
├── Makefile                       # Development commands
├── sonar-project.properties       # SonarQube configuration
├── .env.dev                       # Development environment variables
└── DEVELOPMENT.md                 # This file
```

## 🔧 Development Workflow

### Daily Development

```bash
# 1. Start your development session
make dev-start

# 2. Check environment status
make status

# 3. Build and test before making changes
make build
make test

# 4. Make your changes...

# 5. Run quality checks
make quality-check

# 6. Commit your changes (pre-commit hooks will run automatically)
git add .
git commit -m "Your commit message"
```

### Available Commands

| Command              | Description                      |
| -------------------- | -------------------------------- |
| `make help`          | Show all available commands      |
| `make setup`         | Setup development environment    |
| `make dev-start`     | Start development environment    |
| `make dev-stop`      | Stop development environment     |
| `make build`         | Build all services               |
| `make test`          | Run all tests                    |
| `make lint`          | Run code linting                 |
| `make format`        | Format all code                  |
| `make security-scan` | Run security vulnerability scans |
| `make quality-check` | Run comprehensive quality checks |
| `make clean`         | Clean all build artifacts        |
| `make status`        | Check environment status         |

### Service-Specific Development

#### Backend Services

```bash
# Start a specific service
cd search_engine_microservice/auth-server
mvn spring-boot:run

# Run tests for a specific service
mvn test

# Check code quality
mvn checkstyle:check spotbugs:check
```

#### Frontend Development

```bash
cd search-engine-ui

# Start development server
npm start

# Run tests
npm test

# Run linting
npm run lint

# Format code
npm run format
```

## 🧪 Testing Strategy

### Test Types and Coverage

The project follows a testing pyramid approach:

```
┌─────────────────────────────────────┐
│           E2E Tests (5%)            │
├─────────────────────────────────────┤
│        Integration Tests (15%)      │
├─────────────────────────────────────┤
│          Unit Tests (80%)           │
└─────────────────────────────────────┘
```

### Running Tests

```bash
# Run all tests
make test

# Run backend tests only
cd search_engine_microservice
for service in */; do
    if [ -f "$service/pom.xml" ]; then
        cd "$service" && mvn test && cd ..
    fi
done

# Run frontend tests only
cd search-engine-ui
npm test

# Run tests with coverage
npm run test:ci
```

### Test Coverage Requirements

- **Minimum Coverage**: 80% for all services
- **Unit Tests**: Focus on business logic and utilities
- **Integration Tests**: Test service interactions and database operations
- **E2E Tests**: Cover critical user journeys

## 🔍 Code Quality Standards

### Automated Quality Checks

The project enforces code quality through:

- **Pre-commit hooks**: Run automatically before each commit
- **ESLint**: TypeScript/Angular code linting
- **Prettier**: Code formatting
- **Checkstyle**: Java code style enforcement
- **SpotBugs**: Static analysis for Java
- **OWASP Dependency Check**: Security vulnerability scanning
- **SonarQube**: Comprehensive code quality analysis

### Code Style Guidelines

#### Java (Backend)

- Follow Google Java Style Guide
- Maximum line length: 120 characters
- Use meaningful variable and method names
- Add JavaDoc for public APIs
- Minimum test coverage: 80%

#### TypeScript/Angular (Frontend)

- Follow Angular Style Guide
- Use TypeScript strict mode
- Implement accessibility standards (WCAG 2.1)
- Use reactive programming patterns (RxJS)
- Write unit tests for components and services

### Quality Gates

Before code can be merged:

- ✅ All tests must pass
- ✅ Code coverage ≥ 80%
- ✅ No critical security vulnerabilities
- ✅ Linting passes without errors
- ✅ Code formatting is consistent

## 🐳 Docker Development

### Infrastructure Services

```bash
# Start all infrastructure
make docker-up

# View logs
make docker-logs

# Stop all infrastructure
make docker-down

# Check container status
docker-compose ps
```

### Service Configuration

| Service    | Port | Purpose              |
| ---------- | ---- | -------------------- |
| PostgreSQL | 5432 | Primary database     |
| Redis      | 6379 | Caching and sessions |
| Kafka      | 9092 | Message queuing      |
| Zookeeper  | 2181 | Kafka coordination   |

### Environment Variables

Development environment variables are configured in `.env.dev`:

```bash
# Source the environment
source .env.dev

# Or use with docker-compose
docker-compose --env-file .env.dev up -d
```

## 🔧 IDE Configuration

### VS Code (Recommended)

The setup script automatically configures VS Code with:

- **Extensions**: Java, Angular, TypeScript, Docker, Kubernetes
- **Settings**: Auto-formatting, linting, debugging
- **Tasks**: Build, test, and run configurations

### IntelliJ IDEA

For IntelliJ IDEA users:

1. Import the project as a Maven project
2. Enable annotation processing for Lombok
3. Install plugins: Angular/TypeScript, Docker, SonarLint
4. Configure code style to use Google Java Style

## 🚨 Troubleshooting

### Common Issues

#### Port Conflicts

```bash
# Check what's using a port
lsof -i :8080

# Kill process using port
kill -9 $(lsof -t -i:8080)
```

#### Docker Issues

```bash
# Clean Docker system
docker system prune -a

# Restart Docker daemon
sudo systemctl restart docker  # Linux
# Or restart Docker Desktop on macOS/Windows
```

#### Maven Issues

```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Force update dependencies
mvn clean install -U
```

#### Node.js Issues

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Getting Help

1. **Check logs**: Use `make docker-logs` or service-specific logs
2. **Verify environment**: Run `make status`
3. **Clean and rebuild**: Run `make clean && make build`
4. **Check documentation**: Review service-specific README files
5. **Ask for help**: Create an issue in the project repository

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [Docker Documentation](https://docs.docker.com/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [SonarQube Documentation](https://docs.sonarqube.org/)

## 🤝 Contributing

1. Follow the development workflow outlined above
2. Ensure all quality checks pass
3. Write comprehensive tests
4. Update documentation as needed
5. Submit pull requests with clear descriptions

---

**Happy coding! 🚀**
