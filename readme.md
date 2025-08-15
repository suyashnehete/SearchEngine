# 🔍 Search Engine with OAuth2, JWT & RBAC

## 📋 Overview

A comprehensive **microservices-based search engine** with **OAuth2 authentication**, **JWT tokens**, and **Role-Based Access Control (RBAC)**. The system intelligently crawls web pages, indexes content using TF-IDF and PageRank algorithms, and provides secure search functionality with granular admin controls.

### 🎯 Key Features

- **🔐 OAuth2 + JWT Authentication** - Secure login with role-based access control
- **👥 Role-Based Permissions** - Admin vs User access levels with granular controls
- **🕷️ Intelligent Web Crawling** - Smart content discovery with rate limiting and politeness
- **📚 Advanced Indexing** - TF-IDF scoring with PageRank algorithm for relevance ranking
- **🔍 Fast Search** - Optimized query processing with caching and suggestions
- **🎨 Modern Angular UI** - Responsive design with role-based navigation
- **📊 Admin Dashboard** - Comprehensive system monitoring and control panel
- **🐳 Production Ready** - Docker containerization with health checks and monitoring

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

### ⚡ One-Command Setup

```bash
# Clone the repository
git clone https://github.com/suyashnehete/SearchEngine.git
cd SearchEngine

# Validate the setup
./validate-setup.sh

# Build all services
./build-all.sh

# Start the complete system
./start-services.sh
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

## 📖 Detailed Setup Instructions

### 1️⃣ Infrastructure Services

Start the required infrastructure components:

```bash
# Start PostgreSQL, Kafka, Zookeeper, and Redis
docker-compose up -d postgres kafka zookeeper redis

# Verify services are running
docker-compose ps
```

### 2️⃣ Backend Microservices

Start services in the correct dependency order:

```bash
# 1. Service Discovery (Required first)
cd search_engine_microservice/discovery
mvn spring-boot:run &

# Wait 30 seconds for discovery to be ready
sleep 30

# 2. Configuration Server
cd ../config-server
mvn spring-boot:run &

# 3. Authentication Server
cd ../auth-server
mvn spring-boot:run &

# 4. API Gateway
cd ../gateway
mvn spring-boot:run &

# 5. Business Services (can start in parallel)
cd ../crawler && mvn spring-boot:run &
cd ../indexer && mvn spring-boot:run &
cd ../query && mvn spring-boot:run &
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

## 🐳 Docker Deployment

### 🚀 Development Environment

```bash
# Start all services with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### 🏭 Production Environment

```bash
# Use production configuration
docker-compose -f docker-compose.prod.yml up -d

# Scale services as needed
docker-compose -f docker-compose.prod.yml up -d --scale crawler=3 --scale indexer=2
```

### 🔧 Environment Configuration

Create a `.env` file from the template:

```bash
cp .env.example .env
```

Key configuration options:

```bash
# Database Configuration
DB_USERNAME=admin
DB_PASSWORD=secure_password
DB_NAME=search_engine

# OAuth2 Configuration
OAUTH2_CLIENT_ID=search-engine-ui
OAUTH2_CLIENT_SECRET=your-secure-secret
OAUTH2_ISSUER=http://localhost:8080

# Performance Tuning
CRAWLER_MAX_PAGES=10000
CRAWLER_THREADS=10
CACHE_SIZE=5000
INDEX_BATCH_SIZE=1000
```

## 🧪 Testing & Quality Assurance

### 🔧 Backend Testing

```bash
# Run all service tests
./test-all.sh

# Test specific service
cd search_engine_microservice/auth-server
mvn test

# Integration tests
mvn verify -P integration-tests
```

### 🎨 Frontend Testing

```bash
cd search-engine-ui

# Unit tests
npm test

# End-to-end tests
npm run e2e

# Test coverage
npm run test:coverage
```

### 🔍 Security Testing

```bash
# Test OAuth2 flow
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code&code=test&client_id=search-engine-ui"

# Test JWT validation
curl -H "Authorization: Bearer invalid_token" \
  http://localhost:8081/api/indexer-service/admin/stats
```

## 🔧 Configuration & Customization

### 🌍 Environment-Specific Configuration

#### Development Configuration

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

#### Production Configuration

```yaml
# application-prod.yml
spring:
  profiles:
    active: production
  datasource:
    url: jdbc:postgresql://postgres:5432/search_engine
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

logging:
  level:
    org.springframework.security: WARN
    com.suyash.se: INFO
```

### ⚙️ Service-Specific Settings

#### Crawler Configuration

```yaml
crawler:
  max-pages: 10000
  threads: 5
  delay-ms: 1000
  user-agent: "SearchEngine-Crawler/1.0"
  respect-robots-txt: true
  max-depth: 5
```

#### Indexer Configuration

```yaml
indexer:
  batch-size: 1000
  tf-idf:
    min-term-frequency: 2
    max-document-frequency: 0.8
  pagerank:
    iterations: 50
    damping-factor: 0.85
```

#### Search Configuration

```yaml
search:
  max-results: 100
  cache:
    size: 10000
    ttl-minutes: 60
  suggestions:
    max-suggestions: 10
    min-query-length: 2
```

## 🚀 Production Deployment

### ☁️ Cloud Deployment Options

#### AWS Deployment

```bash
# Deploy to AWS EKS
eksctl create cluster --name search-engine --region us-west-2
kubectl apply -f k8s/aws/

# Use AWS RDS for PostgreSQL
# Use AWS ElastiCache for Redis
# Use AWS MSK for Kafka
```

#### Google Cloud Deployment

```bash
# Deploy to Google GKE
gcloud container clusters create search-engine --zone us-central1-a
kubectl apply -f k8s/gcp/

# Use Cloud SQL for PostgreSQL
# Use Memorystore for Redis
# Use Cloud Pub/Sub for messaging
```

#### Azure Deployment

```bash
# Deploy to Azure AKS
az aks create --resource-group search-engine --name search-engine-cluster
kubectl apply -f k8s/azure/

# Use Azure Database for PostgreSQL
# Use Azure Cache for Redis
# Use Azure Service Bus for messaging
```

### 🔒 Production Security Hardening

#### SSL/TLS Configuration

```nginx
# nginx.conf for production
server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;

    location / {
        proxy_pass http://search-engine-ui:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### Security Headers

```yaml
# Gateway security configuration
spring:
  security:
    headers:
      frame-options: DENY
      content-type-options: nosniff
      xss-protection: "1; mode=block"
      referrer-policy: strict-origin-when-cross-origin
```

### 📈 Performance Optimization

#### Database Optimization

```sql
-- PostgreSQL performance tuning
CREATE INDEX CONCURRENTLY idx_inverted_index_word ON inverted_index(word);
CREATE INDEX CONCURRENTLY idx_crawled_page_url ON crawled_page(url);
CREATE INDEX CONCURRENTLY idx_user_username ON users(username);

-- Analyze and vacuum regularly
ANALYZE;
VACUUM ANALYZE;
```

#### Caching Strategy

```yaml
# Redis caching configuration
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000 # 1 hour
      cache-null-values: false
  data:
    redis:
      repositories:
        enabled: true
```

## 🔍 Troubleshooting Guide

### 🚨 Common Issues & Solutions

#### Authentication Problems

```bash
# Issue: OAuth2 login fails
# Solution: Check auth-server logs and configuration
docker-compose logs auth-server

# Verify JWT token structure
echo "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9..." | base64 -d

# Check CORS configuration in gateway
curl -H "Origin: http://localhost:4200" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS http://localhost:8081/api/auth-server/oauth2/token
```

#### Service Discovery Issues

```bash
# Issue: Services not registering with Eureka
# Solution: Check network connectivity and configuration
curl http://localhost:8761/eureka/apps

# Verify service registration
curl http://localhost:8761/eureka/apps/AUTH-SERVER
```

#### Database Connection Problems

```bash
# Issue: Database connection failures
# Solution: Check PostgreSQL status and credentials
docker-compose exec postgres psql -U admin -d search_engine -c "\dt"

# Test connection from service
docker-compose exec auth-server nc -zv postgres 5432
```

#### Search Not Working

```bash
# Issue: Search returns no results
# Solution: Check if content is indexed
curl http://localhost:8081/api/indexer-service/admin/stats

# Verify crawler has processed URLs
curl http://localhost:8081/api/crawler-service/admin/stats

# Check query service logs
docker-compose logs query
```

### 📊 Performance Issues

#### High Memory Usage

```bash
# Monitor JVM memory usage
docker stats

# Adjust JVM heap size
export JAVA_OPTS="-Xmx2g -Xms1g"

# Enable garbage collection logging
export JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:+PrintGCDetails"
```

#### Slow Search Response

```bash
# Check database query performance
docker-compose exec postgres psql -U admin -d search_engine
EXPLAIN ANALYZE SELECT * FROM inverted_index WHERE word = 'search';

# Monitor Redis cache hit rate
docker-compose exec redis redis-cli info stats
```

### 🔧 Debug Commands

#### Service Health Checks

```bash
# Check all service health
for port in 8080 8081 8082 8083 8084; do
  echo "Checking port $port:"
  curl -s http://localhost:$port/actuator/health | jq '.status'
done

# Check service dependencies
curl http://localhost:8081/actuator/health | jq '.components'
```

#### Log Analysis

```bash
# View real-time logs
docker-compose logs -f --tail=100

# Search for errors
docker-compose logs | grep -i error

# Filter by service
docker-compose logs auth-server | grep -i "oauth"
```

## 📚 Additional Resources

### 🔗 Useful Links

- **Spring Security OAuth2**: https://spring.io/projects/spring-security-oauth
- **Angular OAuth2 OIDC**: https://github.com/manfredsteyer/angular-oauth2-oidc
- **Docker Compose**: https://docs.docker.com/compose/
- **PostgreSQL**: https://www.postgresql.org/docs/
- **Apache Kafka**: https://kafka.apache.org/documentation/

### 📖 Learning Resources

- **OAuth2 & JWT**: https://oauth.net/2/
- **Microservices Patterns**: https://microservices.io/
- **Spring Cloud**: https://spring.io/projects/spring-cloud
- **Angular Security**: https://angular.io/guide/security

### 🤝 Contributing

1. **Fork the Repository**

   ```bash
   git clone https://github.com/yourusername/SearchEngine.git
   cd SearchEngine
   ```

2. **Create Feature Branch**

   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Make Changes and Test**

   ```bash
   ./validate-setup.sh
   ./test-all.sh
   ```

4. **Commit and Push**

   ```bash
   git commit -m 'Add amazing feature'
   git push origin feature/amazing-feature
   ```

5. **Create Pull Request**
   - Describe your changes
   - Include test results
   - Update documentation if needed

### 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### 🙏 Acknowledgments

- **Spring Boot Team** - For the excellent microservices framework
- **Angular Team** - For the powerful frontend framework
- **OAuth2 Community** - For security standards and best practices
- **Open Source Contributors** - For the various libraries and tools used

---

## 🎉 Success Checklist

Before considering your deployment successful, verify:

- [ ] ✅ All services start without errors
- [ ] ✅ Service discovery shows all registered services
- [ ] ✅ Angular UI loads at http://localhost:4200
- [ ] ✅ Can login with admin/admin123 and user/user123
- [ ] ✅ Admin panel accessible for admin users only
- [ ] ✅ Can submit URLs for crawling
- [ ] ✅ Search functionality returns relevant results
- [ ] ✅ Role-based access control properly enforced
- [ ] ✅ JWT tokens contain proper authorities
- [ ] ✅ All health checks return healthy status

## 🚀 What's Next?

Your OAuth2 + JWT + RBAC Search Engine is now ready for production use! Consider these enhancements:

1. **Enhanced Security**: Implement rate limiting, audit logging, and advanced threat detection
2. **Scalability**: Add horizontal scaling, load balancing, and distributed caching
3. **Analytics**: Implement comprehensive search analytics and user behavior tracking
4. **AI/ML**: Add machine learning for better search relevance and content recommendations
5. **Mobile App**: Develop mobile applications using the same OAuth2 backend
6. **Enterprise Features**: Add SSO integration, advanced user management, and compliance features

**🎊 Congratulations on building a production-ready, secure search engine!**

For questions, issues, or contributions, please visit our [GitHub repository](https://github.com/suyashnehete/SearchEngine) or open an issue.

---

_Built with ❤️ using Spring Boot, Angular, OAuth2, and modern microservices architecture._
