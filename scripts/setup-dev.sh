#!/bin/bash

# Search Engine Development Environment Setup Script
# This script sets up the complete development environment for the search engine project

set -e

echo "🔍 Search Engine Development Environment Setup"
echo "=============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    local missing_deps=()
    
    # Check Java 17
    if command_exists java; then
        java_version=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$java_version" -ge 17 ]; then
            print_success "Java $java_version found"
        else
            print_error "Java 17+ required, found Java $java_version"
            missing_deps+=("java-17")
        fi
    else
        print_error "Java not found"
        missing_deps+=("java-17")
    fi
    
    # Check Maven
    if command_exists mvn; then
        mvn_version=$(mvn -version | head -n1 | cut -d' ' -f3)
        print_success "Maven $mvn_version found"
    else
        print_error "Maven not found"
        missing_deps+=("maven")
    fi
    
    # Check Node.js
    if command_exists node; then
        node_version=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
        if [ "$node_version" -ge 16 ]; then
            print_success "Node.js v$(node --version | cut -d'v' -f2) found"
        else
            print_error "Node.js 16+ required, found v$(node --version | cut -d'v' -f2)"
            missing_deps+=("nodejs-16+")
        fi
    else
        print_error "Node.js not found"
        missing_deps+=("nodejs-16+")
    fi
    
    # Check npm
    if command_exists npm; then
        npm_version=$(npm --version)
        print_success "npm $npm_version found"
    else
        print_error "npm not found"
        missing_deps+=("npm")
    fi
    
    # Check Docker
    if command_exists docker; then
        docker_version=$(docker --version | cut -d' ' -f3 | cut -d',' -f1)
        print_success "Docker $docker_version found"
    else
        print_error "Docker not found"
        missing_deps+=("docker")
    fi
    
    # Check Docker Compose
    if command_exists docker-compose || docker compose version >/dev/null 2>&1; then
        if command_exists docker-compose; then
            compose_version=$(docker-compose --version | cut -d' ' -f3 | cut -d',' -f1)
            print_success "Docker Compose $compose_version found"
        else
            compose_version=$(docker compose version --short)
            print_success "Docker Compose $compose_version found"
        fi
    else
        print_error "Docker Compose not found"
        missing_deps+=("docker-compose")
    fi
    
    if [ ${#missing_deps[@]} -ne 0 ]; then
        print_error "Missing dependencies: ${missing_deps[*]}"
        print_status "Please install the missing dependencies and run this script again."
        exit 1
    fi
    
    print_success "All prerequisites satisfied!"
}

# Setup Git hooks
setup_git_hooks() {
    print_status "Setting up Git hooks..."
    
    # Create hooks directory if it doesn't exist
    mkdir -p .git/hooks
    
    # Create pre-commit hook
    cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash

echo "Running pre-commit checks..."

# Check if we're in the root directory
if [ ! -f "package.json" ] && [ ! -f "search-engine-ui/package.json" ]; then
    echo "Error: Not in project root directory"
    exit 1
fi

# Run backend code quality checks
echo "Checking Java code quality..."
if [ -d "search_engine_microservice" ]; then
    cd search_engine_microservice
    for service in */; do
        if [ -f "$service/pom.xml" ]; then
            echo "Checking $service..."
            cd "$service"
            mvn compile spotbugs:check checkstyle:check -q
            if [ $? -ne 0 ]; then
                echo "Code quality check failed for $service"
                exit 1
            fi
            cd ..
        fi
    done
    cd ..
fi

# Run frontend code quality checks
echo "Checking Angular code quality..."
if [ -d "search-engine-ui" ]; then
    cd search-engine-ui
    npm run lint
    if [ $? -ne 0 ]; then
        echo "Frontend linting failed"
        exit 1
    fi
    cd ..
fi

echo "Pre-commit checks passed!"
EOF
    
    chmod +x .git/hooks/pre-commit
    print_success "Git pre-commit hook installed"
}

# Setup development configuration
setup_dev_config() {
    print_status "Setting up development configuration..."
    
    # Create development environment file
    cat > .env.dev << 'EOF'
# Development Environment Configuration
DB_USERNAME=admin
DB_PASSWORD=admin
DB_NAME=search_engine
DB_HOST=localhost
DB_PORT=5432

REDIS_HOST=localhost
REDIS_PORT=6379

KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Development profiles
SPRING_PROFILES_ACTIVE=development

# Logging levels
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_SUYASH_SE=DEBUG

# JVM Options for development
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC

# Angular development
NG_CLI_ANALYTICS=false
EOF
    
    print_success "Development environment configuration created"
}

# Setup IDE configuration
setup_ide_config() {
    print_status "Setting up IDE configuration..."
    
    # Create .vscode directory and settings
    mkdir -p .vscode
    
    cat > .vscode/settings.json << 'EOF'
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.format.settings.url": ".vscode/eclipse-java-google-style.xml",
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": true,
        "source.fixAll": true
    },
    "typescript.preferences.importModuleSpecifier": "relative",
    "angular.enable-strict-mode-prompt": false,
    "files.exclude": {
        "**/node_modules": true,
        "**/target": true,
        "**/.git": true,
        "**/.DS_Store": true
    },
    "search.exclude": {
        "**/node_modules": true,
        "**/target": true,
        "**/dist": true
    }
}
EOF
    
    cat > .vscode/extensions.json << 'EOF'
{
    "recommendations": [
        "vscjava.vscode-java-pack",
        "angular.ng-template",
        "ms-vscode.vscode-typescript-next",
        "esbenp.prettier-vscode",
        "ms-vscode.vscode-eslint",
        "redhat.vscode-yaml",
        "ms-kubernetes-tools.vscode-kubernetes-tools",
        "ms-vscode-remote.remote-containers"
    ]
}
EOF
    
    # Download Google Java Style for Eclipse
    curl -s -o .vscode/eclipse-java-google-style.xml \
        https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml
    
    print_success "VS Code configuration created"
}

# Install frontend dependencies
install_frontend_deps() {
    print_status "Installing frontend dependencies..."
    
    if [ -d "search-engine-ui" ]; then
        cd search-engine-ui
        
        # Install dependencies
        npm install
        
        # Install development tools
        npm install --save-dev \
            @angular-eslint/builder \
            @angular-eslint/eslint-plugin \
            @angular-eslint/eslint-plugin-template \
            @angular-eslint/schematics \
            @angular-eslint/template-parser \
            @typescript-eslint/eslint-plugin \
            @typescript-eslint/parser \
            eslint \
            prettier \
            husky \
            lint-staged
        
        # Setup Angular ESLint
        if [ ! -f ".eslintrc.json" ]; then
            npx ng add @angular-eslint/schematics --skip-confirmation
        fi
        
        cd ..
        print_success "Frontend dependencies installed"
    else
        print_warning "Frontend directory not found, skipping frontend setup"
    fi
}

# Setup backend development tools
setup_backend_tools() {
    print_status "Setting up backend development tools..."
    
    # Create parent pom.xml for code quality plugins
    cat > search_engine_microservice/pom-parent.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.suyash.se</groupId>
    <artifactId>search-engine-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spotbugs.version>4.7.3</spotbugs.version>
        <checkstyle.version>3.3.0</checkstyle.version>
        <jacoco.version>0.8.8</jacoco.version>
    </properties>
    
    <build>
        <pluginManagement>
            <plugins>
                <!-- SpotBugs for static analysis -->
                <plugin>
                    <groupId>com.github.spotbugs</groupId>
                    <artifactId>spotbugs-maven-plugin</artifactId>
                    <version>${spotbugs.version}</version>
                    <configuration>
                        <effort>Max</effort>
                        <threshold>Low</threshold>
                        <failOnError>true</failOnError>
                    </configuration>
                </plugin>
                
                <!-- Checkstyle for code style -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-checkstyle-plugin</artifactId>
                    <version>${checkstyle.version}</version>
                    <configuration>
                        <configLocation>google_checks.xml</configLocation>
                        <encoding>UTF-8</encoding>
                        <consoleOutput>true</consoleOutput>
                        <failsOnError>true</failsOnError>
                    </configuration>
                </plugin>
                
                <!-- JaCoCo for code coverage -->
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>${jacoco.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>prepare-agent</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>report</id>
                            <phase>test</phase>
                            <goals>
                                <goal>report</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>check</id>
                            <goals>
                                <goal>check</goal>
                            </goals>
                            <configuration>
                                <rules>
                                    <rule>
                                        <element>BUNDLE</element>
                                        <limits>
                                            <limit>
                                                <counter>INSTRUCTION</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>0.80</minimum>
                                            </limit>
                                        </limits>
                                    </rule>
                                </rules>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
                
                <!-- OWASP Dependency Check -->
                <plugin>
                    <groupId>org.owasp</groupId>
                    <artifactId>dependency-check-maven</artifactId>
                    <version>8.4.0</version>
                    <configuration>
                        <failBuildOnCVSS>7</failBuildOnCVSS>
                        <suppressionFiles>
                            <suppressionFile>owasp-suppressions.xml</suppressionFile>
                        </suppressionFiles>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
EOF
    
    print_success "Backend development tools configured"
}

# Start infrastructure services
start_infrastructure() {
    print_status "Starting infrastructure services..."
    
    # Check if docker-compose.yml exists
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found"
        return 1
    fi
    
    # Start services
    if command_exists docker-compose; then
        docker-compose up -d postgres redis kafka zookeeper
    else
        docker compose up -d postgres redis kafka zookeeper
    fi
    
    # Wait for services to be ready
    print_status "Waiting for services to be ready..."
    sleep 10
    
    # Check PostgreSQL
    for i in {1..30}; do
        if docker exec postgres-search-engine pg_isready -U admin >/dev/null 2>&1; then
            print_success "PostgreSQL is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "PostgreSQL failed to start"
            return 1
        fi
        sleep 2
    done
    
    # Check Redis
    for i in {1..30}; do
        if docker exec redis-search-engine redis-cli ping >/dev/null 2>&1; then
            print_success "Redis is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "Redis failed to start"
            return 1
        fi
        sleep 2
    done
    
    print_success "Infrastructure services started successfully"
}

# Create development scripts
create_dev_scripts() {
    print_status "Creating development scripts..."
    
    mkdir -p scripts
    
    # Create build script
    cat > scripts/build-all.sh << 'EOF'
#!/bin/bash

echo "Building all services..."

# Build backend services
cd search_engine_microservice
for service in */; do
    if [ -f "$service/pom.xml" ]; then
        echo "Building $service..."
        cd "$service"
        mvn clean compile -q
        cd ..
    fi
done
cd ..

# Build frontend
if [ -d "search-engine-ui" ]; then
    echo "Building frontend..."
    cd search-engine-ui
    npm run build
    cd ..
fi

echo "Build completed!"
EOF
    
    # Create test script
    cat > scripts/test-all.sh << 'EOF'
#!/bin/bash

echo "Running all tests..."

# Test backend services
cd search_engine_microservice
for service in */; do
    if [ -f "$service/pom.xml" ]; then
        echo "Testing $service..."
        cd "$service"
        mvn test -q
        cd ..
    fi
done
cd ..

# Test frontend
if [ -d "search-engine-ui" ]; then
    echo "Testing frontend..."
    cd search-engine-ui
    npm test -- --watch=false --browsers=ChromeHeadless
    cd ..
fi

echo "All tests completed!"
EOF
    
    # Create cleanup script
    cat > scripts/cleanup-dev.sh << 'EOF'
#!/bin/bash

echo "Cleaning up development environment..."

# Stop and remove containers
if command -v docker-compose >/dev/null 2>&1; then
    docker-compose down -v
else
    docker compose down -v
fi

# Clean Maven targets
find search_engine_microservice -name target -type d -exec rm -rf {} + 2>/dev/null || true

# Clean npm modules and dist
if [ -d "search-engine-ui" ]; then
    cd search-engine-ui
    rm -rf node_modules dist
    cd ..
fi

echo "Cleanup completed!"
EOF
    
    chmod +x scripts/*.sh
    print_success "Development scripts created"
}

# Main execution
main() {
    echo
    print_status "Starting development environment setup..."
    echo
    
    check_prerequisites
    echo
    
    setup_git_hooks
    echo
    
    setup_dev_config
    echo
    
    setup_ide_config
    echo
    
    install_frontend_deps
    echo
    
    setup_backend_tools
    echo
    
    create_dev_scripts
    echo
    
    start_infrastructure
    echo
    
    print_success "Development environment setup completed!"
    echo
    print_status "Next steps:"
    echo "  1. Source the environment: source .env.dev"
    echo "  2. Build all services: ./scripts/build-all.sh"
    echo "  3. Run tests: ./scripts/test-all.sh"
    echo "  4. Start development servers manually or use your IDE"
    echo
    print_status "Available scripts:"
    echo "  - ./scripts/build-all.sh    - Build all services"
    echo "  - ./scripts/test-all.sh     - Run all tests"
    echo "  - ./scripts/cleanup-dev.sh  - Clean development environment"
    echo
}

# Run main function
main "$@"