# 🔍 Search Engine with OAuth2, JWT & RBAC

## 📋 Overview

A **microservices-based search engine** with **OAuth2 authentication**, **JWT tokens**, and **Role-Based Access Control (RBAC)**. The system crawls web pages, indexes content using TF-IDF algorithms, and provides secure search functionality with admin controls.

### 🎯 Key Features

- **🔐 OAuth2 + JWT Authentication** - Secure login with role-based access control
- **👥 Role-Based Permissions** - Admin vs User access levels
- **🕷️ Web Crawling** - Content discovery and extraction
- **📚 Content Indexing** - TF-IDF scoring for relevance ranking
- **🔍 Search Functionality** - Query processing with result ranking
- **🎨 Modern Angular UI** - Responsive design with role-based navigation
- **📊 Admin Dashboard** - System monitoring and control panel
- **🐳 Docker Support** - Containerized development environment

## 🏗️ System Architecture

### 🔧 Microservices Overview

| Service              | Port | Description               | Access Level | Key Features                          |
| -------------------- | ---- | ------------------------- | ------------ | ------------------------------------- |
| **🔍 Discovery**     | 8761 | Eureka service registry   | Internal     | Service discovery, health monitoring  |
| **⚙️ Config Server** | 8888 | Centralized configuration | Internal     | External config management            |
| **🔐 Auth Server**   | 8080 | OAuth2 + JWT provider     | Public       | User authentication, token generation |
| **🌐 Gateway**       | 8081 | API Gateway with security | Public       | Request routing, JWT validation, CORS |
| **🕷️ Crawler**       | 8082 | Web crawling service      | Mixed        | URL processing, content extraction    |
| **📚 Indexer**       | 8083 | Content indexing service  | Admin Only   | TF-IDF calculation, PageRank scoring  |
| **❓ Query**         | 8084 | Search query processing   | Public       | Search execution, result ranking      |
| **🎨 Angular UI**    | 4200 | Frontend application      | Public       | User interface, admin panel           |

### 🛡️ Security & Access Control

#### **🔓 Public Access (No Authentication Required)**

- **Search Functionality** - Anyone can search indexed content
- **URL Submission** - Anyone can submit URLs for crawling
- **User Registration/Login** - Account creation and authentication

#### **👤 User Role (Authenticated Users)**

- All public features plus:
- **Personal Search History** - Track and manage search queries
- **Profile Management** - Update user information and preferences
- **Enhanced Search Features** - Personalized results and suggestions

#### **🔐 Admin Role (System Administrators)**

- **System Control**: Start/stop services, manage crawler operations
- **Index Management**: Reindex content, optimize search index, clear data
- **Content Moderation**: Block domains, manage crawl policies, content filtering
- **Analytics & Monitoring**: View system metrics, search analytics, performance data
- **User Management**: Manage user accounts, roles, and permissions

## 🛠️ Technology Stack

### 🔧 Backend Technologies

- **☕ Java 17** - Modern LTS version with enhanced performance
- **🍃 Spring Boot 3.4.2** - Latest microservices framework
- **☁️ Spring Cloud 2024.0.0** - Service discovery, gateway, configuration
- **🔐 Spring Security OAuth2** - Authentication and authorization server
- **📨 Apache Kafka** - Event streaming and asynchronous messaging
- **🐘 PostgreSQL** - Primary database for persistent storage
- **🔴 Redis** - Caching layer and session storage
- **🍜 Jsoup** - HTML parsing and content extraction
- **📊 Micrometer + Prometheus** - Metrics collection and monitoring

### 🎨 Frontend Technologies

- **🅰️ Angular 15** - Modern TypeScript-based frontend framework
- **📝 TypeScript** - Type-safe JavaScript with enhanced tooling
- **🔐 angular-oauth2-oidc** - OAuth2/OIDC client library
- **🎨 Bootstrap 5** - Responsive UI components and styling
- **📊 FontAwesome** - Comprehensive icon library
- **🔧 RxJS** - Reactive programming for async operations

### 🐳 Infrastructure & DevOps

- **🐳 Docker & Docker Compose** - Containerization and orchestration
- **📊 Prometheus** - Metrics collection and alerting
- **🔍 Eureka** - Service discovery and registration
- **🌐 Nginx** - Reverse proxy and static file serving
- **🔧 Maven** - Build automation and dependency management

## 🚀 Quick Start Guide

### 📋 Prerequisites

Ensure you have the following installed:

- **☕ Java 17+** - Required for Spring Boot applications
- **📦 Maven 3.6+** - For building Java services
- **📱 Node.js 16+** - For Angular development
- **🅰️ Angular CLI** - For Angular build tools
- **🐳 Docker & Docker Compose** - For containerization

### ⚡ Development Setup

```bash
# Clone the repository
git clone https://github.com/suyashnehete/SearchEngine.git
cd SearchEngine

# Start infrastructure services
docker-compose up -d

# The system is now ready for development
```

### 🎯 Access Points

After successful startup, access the system at:

- **🎨 Main Application**: http://localhost:4200
- **🔐 Admin Panel**: http://localhost:4200/admin (Admin users only)
- **🔍 Service Discovery**: http://localhost:8761
- **🌐 API Gateway**: http://localhost:8081
- **📊 Health Checks**: http://localhost:8081/actuator/health

### 👤 Default User Accounts

| Username | Password   | Role  | Capabilities                       |
| -------- | ---------- | ----- | ---------------------------------- |
| `admin`  | `admin123` | ADMIN | Full system control and management |
| `user`   | `user123`  | USER  | Search and URL submission          |

## 📖 Development Setup Instructions

### 1️⃣ Infrastructure Services

Start the required infrastructure components:

```bash
# Start all infrastructure services
docker-compose up -d

# Verify services are running
docker-compose ps
```

### 2️⃣ Backend Microservices

Start services in the correct dependency order:

```bash
# 1. Service Discovery (Required first)
cd search_engine_microservice/discovery
mvn spring-boot:run

# In separate terminals, start other services:
# 2. Configuration Server
cd search_engine_microservice/config-server
mvn spring-boot:run

# 3. Authentication Server (Fixed JWT bean conflict)
cd search_engine_microservice/auth-server
mvn spring-boot:run

# 4. API Gateway (Fixed reactive CORS filter)
cd search_engine_microservice/gateway
mvn spring-boot:run

# 5. Business Services (Fixed Kafka config and JCache)
cd search_engine_microservice/crawler
mvn spring-boot:run

cd search_engine_microservice/indexer
mvn spring-boot:run

cd search_engine_microservice/query
mvn spring-boot:run
```

### 3️⃣ Frontend Application

```bash
# Install dependencies and start Angular
cd search-engine-ui
npm install
npm start

# The UI will be available at http://localhost:4200
```

### 4️⃣ Verification Steps

1. **Check Service Discovery**: Visit http://localhost:8761 to see all registered services
2. **Test Authentication**: Login at http://localhost:4200 with admin/admin123
3. **Submit a URL**: Use the crawler interface to add content
4. **Perform Search**: Test the search functionality
5. **Access Admin Panel**: Verify admin controls work properly

## 🔐 Authentication & Authorization Flow

### 🔄 OAuth2 Authorization Code Flow

1. **User Access**: User visits Angular application
2. **Authentication Check**: App checks for valid JWT token
3. **Login Redirect**: If not authenticated, redirect to OAuth2 server
4. **Credential Validation**: User enters username/password
5. **Authorization Code**: Server returns authorization code
6. **Token Exchange**: Angular exchanges code for JWT access token
7. **Token Storage**: Secure token storage in browser
8. **API Requests**: All API calls include JWT Bearer token
9. **Token Validation**: Gateway validates JWT on each request
10. **Role Enforcement**: Services enforce role-based permissions

### 🛡️ Security Implementation Details

#### **Gateway-Level Security**

```yaml
# Public endpoints (no authentication required)
/auth-server/** - OAuth2 authentication flows
/query-service/** (GET) - Public search functionality
/crawler-service/crawler (POST) - Public URL submission

# Admin-only endpoints (ADMIN role required)
/crawler-service/admin/** - System management
/indexer-service/admin/** - Index operations
/monitoring/** - System metrics and health

# Authenticated endpoints (valid JWT required)
All other endpoints require authentication
```

#### **Service-Level Security**

- **JWT Validation**: Each service validates JWT tokens independently
- **Method Security**: `@PreAuthorize` annotations on sensitive operations
- **Role Mapping**: JWT claims mapped to Spring Security authorities
- **CORS Configuration**: Proper cross-origin resource sharing setup

## 🎨 Frontend Features & User Experience

### 🔍 Search Interface

- **Real-time Search**: Instant results as you type
- **Query Suggestions**: Intelligent autocomplete using Trie and N-gram models
- **Advanced Filtering**: Filter by content type, date, relevance
- **Pagination**: Efficient handling of large result sets
- **Search History**: Track and revisit previous searches (authenticated users)
- **Result Ranking**: TF-IDF and PageRank-based relevance scoring

### 🔐 Authentication Experience

- **Seamless OAuth2 Flow**: Secure login with minimal friction
- **Role-Based Navigation**: UI adapts based on user permissions
- **Session Management**: Automatic token refresh and secure logout
- **User Profile**: View and manage account information
- **Permission Feedback**: Clear indication of access levels

### 👑 Admin Dashboard Features

- **System Overview**: Real-time status of all services
- **Crawler Management**: Start/stop crawler, view queue status
- **Index Operations**: Reindex content, optimize performance, clear data
- **Content Moderation**: Manage blocked domains and crawl policies
- **Analytics Dashboard**: Search patterns, system performance metrics
- **User Management**: View and manage user accounts and roles

## 🔧 API Documentation

### 🔐 Authentication Endpoints

```http
# Get OAuth2 authorization
GET /auth-server/oauth2/authorize?client_id=search-engine-ui&response_type=code&redirect_uri=http://localhost:4200/auth/callback

# Exchange code for token
POST /auth-server/oauth2/token
Content-Type: application/x-www-form-urlencoded
grant_type=authorization_code&code={code}&client_id=search-engine-ui&client_secret=search-engine-secret

# Get user profile
GET /auth-server/api/user/profile
Authorization: Bearer {jwt_token}

# JWT public keys
GET /auth-server/.well-known/jwks.json
```

### 🕷️ Crawler Service Endpoints

```http
# Submit URL for crawling (Public)
POST /api/crawler-service/crawler
Content-Type: application/json
{
  "url": "https://example.com",
  "priority": "5",
  "maxDepth": "3"
}

# Start crawler service (Admin only)
POST /api/crawler-service/admin/start
Authorization: Bearer {admin_jwt_token}

# Stop crawler service (Admin only)
POST /api/crawler-service/admin/stop
Authorization: Bearer {admin_jwt_token}

# View crawler queue (Admin only)
GET /api/crawler-service/admin/queue
Authorization: Bearer {admin_jwt_token}

# Get crawler statistics (Admin only)
GET /api/crawler-service/admin/stats
Authorization: Bearer {admin_jwt_token}
```

### 📚 Indexer Service Endpoints

```http
# Reindex all content (Admin only)
POST /api/indexer-service/admin/reindex
Authorization: Bearer {admin_jwt_token}

# Optimize search index (Admin only)
POST /api/indexer-service/admin/optimize
Authorization: Bearer {admin_jwt_token}

# Clear entire index (Admin only)
DELETE /api/indexer-service/admin/index
Authorization: Bearer {admin_jwt_token}

# Get index statistics (Admin only)
GET /api/indexer-service/admin/stats
Authorization: Bearer {admin_jwt_token}
```

### ❓ Query Service Endpoints

```http
# Search content (Public)
GET /api/query-service/search?q={query}&page=0&size=10
# Optional: &sort=relevance|date&filter=type:web

# Get search suggestions (Public)
GET /api/query-service/suggestions?q={partial_query}&limit=5

# Get search analytics (Admin only)
GET /api/query-service/analytics
Authorization: Bearer {admin_jwt_token}
```

## 📊 Monitoring & Observability

### 🔍 Health Checks

All services expose comprehensive health endpoints:

```bash
# Check overall system health
curl http://localhost:8081/actuator/health

# Individual service health
curl http://localhost:8080/actuator/health  # Auth Server
curl http://localhost:8082/actuator/health  # Crawler
curl http://localhost:8083/actuator/health  # Indexer
curl http://localhost:8084/actuator/health  # Query Service
```

### 📈 Metrics & Performance

- **Application Metrics**: Response times, error rates, throughput
- **Business Metrics**: Search queries, crawled pages, index size
- **Infrastructure Metrics**: CPU, memory, disk usage, database connections
- **Custom Metrics**: Crawler queue size, index optimization status

### 📋 Service Discovery Dashboard

Visit http://localhost:8761 to view:

- Registered service instances
- Service health status
- Load balancing information
- Service metadata and configuration

## 🐳 Docker Development

### 🚀 Development Environment

```bash
# Start all infrastructure services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### 🔧 Configuration

The system uses default development configuration. Key services:

- **PostgreSQL**: Database for storing crawled content and user data
- **Redis**: Caching layer for improved performance
- **Kafka & Zookeeper**: Message queuing for asynchronous processing


## 🔧 Configuration

### 🌍 Development Configuration

```yaml
# application.yml
spring:
  profiles:
    active: development
  datasource:
    url: jdbc:postgresql://localhost:5432/search_engine
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/.well-known/jwks.json

logging:
  level:
    org.springframework.security: DEBUG
```

### ⚙️ Service Configuration

#### Crawler Settings

```yaml
crawler:
  max-pages: 1000
  threads: 2
  delay-ms: 1000
  user-agent: "SearchEngine-Crawler/1.0"
  max-depth: 3
```

#### Indexer Settings

```yaml
indexer:
  batch-size: 100
  tf-idf:
    min-term-frequency: 1
    max-document-frequency: 0.9
```

#### Search Settings

```yaml
search:
  max-results: 50
  cache:
    size: 1000
    ttl-minutes: 30
```

## 🔧 Development Configuration

### 📈 Performance Tuning

#### Database Optimization

```sql
-- PostgreSQL development indexes
CREATE INDEX idx_inverted_index_word ON inverted_index(word);
CREATE INDEX idx_crawled_page_url ON crawled_page(url);
CREATE INDEX idx_user_username ON users(username);
```

#### Caching Configuration

```yaml
# Redis caching for development
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000 # 1 hour
  data:
    redis:
      repositories:
        enabled: true
```

### 🔧 Debug Commands

#### Service Health Checks

```bash
# Check individual service health
curl http://localhost:8080/actuator/health  # Auth Server
curl http://localhost:8081/actuator/health  # Gateway
curl http://localhost:8082/actuator/health  # Crawler
curl http://localhost:8083/actuator/health  # Indexer
curl http://localhost:8084/actuator/health  # Query Service
```

---

## 🎉 Development Checklist

Before starting development, verify:

- [ ] ✅ All infrastructure services start without errors (docker-compose up -d)
- [ ] ✅ Service discovery shows all registered services (http://localhost:8761)
- [ ] ✅ Angular UI loads at http://localhost:4200
- [ ] ✅ Can login with admin/admin123 and user/user123
- [ ] ✅ Admin panel accessible for admin users only
- [ ] ✅ Can submit URLs for crawling
- [ ] ✅ Search functionality returns relevant results
- [ ] ✅ Role-based access control properly enforced

## 🚀 Development Environment

This is a **development-focused** microservices search engine with:

- **🔐 OAuth2 + JWT Authentication** - Complete security implementation
- **🏗️ Microservices Architecture** - 7 independent services
- **🎨 Angular Frontend** - Modern responsive UI with admin dashboard
- **🐳 Docker Infrastructure** - PostgreSQL, Redis, Kafka for local development
- **🔍 Search Engine** - Web crawling, indexing, and search capabilities

### Project Structure

```
SearchEngine/
├── search_engine_microservice/     # Backend microservices
│   ├── discovery/                  # Eureka service registry
│   ├── config-server/             # Configuration management
│   ├── auth-server/               # OAuth2 authentication
│   ├── gateway/                   # API gateway
│   ├── crawler/                   # Web crawling service
│   ├── indexer/                   # Content indexing service
│   └── query/                     # Search query service
├── search-engine-ui/              # Angular frontend
├── docker-compose.yml             # Infrastructure services
└── readme.md                      # This file
```
