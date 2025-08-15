#!/bin/bash

echo "🚀 Starting Search Engine Services..."

# Start infrastructure services first
echo "🐳 Starting infrastructure services (PostgreSQL, Kafka, Redis)..."
docker-compose up -d postgres kafka zookeeper redis

# Wait for services to be ready
echo "⏳ Waiting for infrastructure services to be ready..."
sleep 30

# Start microservices in order
echo "🔍 Starting Discovery Service..."
cd search_engine_microservice/discovery
java -jar target/discovery-*.jar &
DISCOVERY_PID=$!
cd ../..

echo "⏳ Waiting for Discovery Service..."
sleep 20

echo "⚙️ Starting Config Server..."
cd search_engine_microservice/config-server
java -jar target/config-server-*.jar &
CONFIG_PID=$!
cd ../..

echo "⏳ Waiting for Config Server..."
sleep 15

echo "🔐 Starting Auth Server..."
cd search_engine_microservice/auth-server
java -jar target/auth-server-*.jar &
AUTH_PID=$!
cd ../..

echo "⏳ Waiting for Auth Server..."
sleep 15

echo "🌐 Starting Gateway..."
cd search_engine_microservice/gateway
java -jar target/gateway-*.jar &
GATEWAY_PID=$!
cd ../..

echo "⏳ Waiting for Gateway..."
sleep 15

echo "🕷️ Starting Crawler Service..."
cd search_engine_microservice/crawler
java -jar target/crawler-*.jar &
CRAWLER_PID=$!
cd ../..

echo "📚 Starting Indexer Service..."
cd search_engine_microservice/indexer
java -jar target/indexer-*.jar &
INDEXER_PID=$!
cd ../..

echo "❓ Starting Query Service..."
cd search_engine_microservice/query
java -jar target/query-*.jar &
QUERY_PID=$!
cd ../..

echo "🎨 Starting Angular UI..."
cd search-engine-ui
npm start &
UI_PID=$!
cd ..

echo "🎉 All services started!"
echo "📋 Service URLs:"
echo "   🔍 Discovery Service: http://localhost:8761"
echo "   🔐 Auth Server: http://localhost:8080"
echo "   🌐 Gateway: http://localhost:8081"
echo "   🎨 Angular UI: http://localhost:4200"
echo ""
echo "👤 Default Users:"
echo "   Admin: admin/admin123"
echo "   User: user/user123"
echo ""
echo "🛑 To stop all services, run: ./stop-services.sh"

# Save PIDs for cleanup
echo "$DISCOVERY_PID $CONFIG_PID $AUTH_PID $GATEWAY_PID $CRAWLER_PID $INDEXER_PID $QUERY_PID $UI_PID" > .service_pids