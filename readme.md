# 🔍 Search Engine Project

## 📋 Overview

This project is a microservices-based search engine that crawls web pages, indexes their content, and provides an efficient query interface for searching. The system leverages modern technologies such as Spring Boot, Kafka, PostgreSQL, and Elasticsearch (via inverted index) to build a scalable and performant search solution.

The frontend of the application is built using **Angular**, providing a user-friendly interface for interacting with the search engine. 🚀

## 🏗️ Architecture

The project consists of the following microservices:

1. **⚙️ Config Server**
   - Centralized configuration management using Spring Cloud Config

2. **🔍 Discovery Service**
   - Service discovery using Eureka, running on port `8761`

3. **🌐 Gateway Service**
   - API Gateway using Spring Cloud Gateway
   - Routes requests to respective services based on URL patterns

4. **🕷️ Crawler Service**
   - Crawls web pages starting from a given URL
   - Extracts content and sends crawled data to Kafka

5. **📚 Indexer Service**
   - Builds an inverted index from crawled pages
   - Computes TF-IDF scores and PageRank for ranking

6. **❓ Query Service**
   - Handles user queries and provides ranked search results
   - Implements caching with an LRU cache and offers query suggestions

## 🛠️ Technologies Used

### 🔧 Backend
- **☕ Spring Boot**: Core framework for building microservices
- **☁️ Spring Cloud**: For service discovery, configuration management, and API gateway
- **📨 Kafka**: For asynchronous communication between services
- **🐘 PostgreSQL**: Persistent storage for crawled pages, inverted index, and other metadata
- **🍜 Jsoup**: HTML parsing library used in the crawler service
- **💾 LRU Cache**: Custom implementation for caching frequent queries
- **🌳 Trie & N-Gram Models**: For efficient prefix-based and n-gram-based query suggestions

### 🎨 Frontend
- **🅰️ Angular**: Frontend framework for building the user interface
- **📝 TypeScript**: Programming language for Angular development
- **🎨 HTML/CSS**: For UI structure and styling

## 🚀 Getting Started

### 📋 Prerequisites

- ☕ Java 17+
- 📦 Maven
- 🐳 Docker (for running Kafka and PostgreSQL)
- 📱 Node.js (14.x or higher) and Angular CLI (for frontend development)

### 💻 Installation

1. **📥 Clone the Repository**

   ```bash
   git clone https://github.com/suyashnehete/SearchEngine.git 
   cd search-engine
   ```

2. **🔧 Start Dependencies**

   Ensure you have Kafka and PostgreSQL running. You can use Docker Compose to start them:

   ```yaml
   services:
    postgres:
        container_name: postgres-search-engine
        image: postgres
        environment:
            POSTGRES_USER: admin
            POSTGRES_PASSWORD: admin
            PGDATA: /var/lib/postgresql/data
            POSTGRES_DB: search_engine
        volumes:
        - postgres:/data/postgres
        ports:
        - 5432:5432
        networks:
        - search-engine-network
        restart: unless-stopped

    zookeeper:
        image: confluentinc/cp-zookeeper:7.3.0
        environment:
            ZOOKEEPER_CLIENT_PORT: 2181
        ports:
        - "2181:2181"

    kafka:
        image: confluentinc/cp-kafka:7.3.0
        depends_on:
        - zookeeper
        ports:
        - "9092:9092"
        environment:
            KAFKA_BROKER_ID: 1
            KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
            KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
            KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
            KAFKA_REPLICA_FETCH_MAX_BYTES: 52428800
            KAFKA_PRODUCER_MAX_REQUEST_SIZE: 52428800
            KAFKA_CONSUMER_MAX_PARTITION_FETCH_BYTES: 52428800
            CONNECT_PRODUCER_MAX_REQUEST_SIZE: 52428800
            CONNECT_CONSUMER_MAX_PARTITION_FETCH_BYTES: 52428800
            KAFKA_MESSAGE_MAX_BYTES: 52428800
        
    networks:
        search-engine-network:
            driver: bridge

    volumes:
        postgres:
            driver: local
   ```

   Start the services:
   ```bash
   docker-compose up -d
   ```

3. **🏗️ Build and Run Backend Services**

   Each service can be built and run independently:

   ```bash
   # Example for Crawler Service
   cd search_engine_microservice/crawler
   mvn clean install
   java -jar target/crawler-0.0.1-SNAPSHOT.jar
   ```

   Repeat similar steps for discovery, gateway, indexer, and query services.

4. **🅰️ Set Up Angular Frontend**

   Navigate to the frontend directory and install dependencies:
   ```bash
   cd search-engine-ui
   npm install
   ```

   Start the Angular development server:
   ```bash
   ng serve
   ```

   The frontend will be available at http://localhost:4200. 🎉


## 🎨 Frontend Details

The frontend is built using Angular and communicates with the backend services through the API Gateway. Key features include:

- **🔍 Search Interface**: A responsive search bar where users can input queries and view ranked search results
- **💡 Query Suggestions**: As users type, they receive real-time suggestions based on Trie, N-Gram models, and user context
- **📄 Pagination**: Results are paginated for better user experience
- **📊 User Query Logging**: User queries are logged for analytics purposes
- **📱 Responsive Design**: Works seamlessly across desktop and mobile devices

### 🏃‍♂️ Running the Frontend

1. **📦 Install Dependencies**
   ```bash
   npm install
   ```

2. **🚀 Run Development Server**
   ```bash
   ng serve
   ```


## ⚙️ Configuration

Configuration files for each service are located under `config-server/src/main/resources/configurations`. They include settings for:
- 🗄️ Database connections
- 📨 Kafka configuration
- 🌐 Service URLs
- 💾 Cache settings
- 🎨 Frontend API endpoints

---