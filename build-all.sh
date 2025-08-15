#!/bin/bash

echo "🚀 Building Search Engine Microservices..."

# Build all microservices
services=("discovery" "config-server" "gateway" "auth-server" "crawler" "indexer" "query")

for service in "${services[@]}"; do
    echo "📦 Building $service..."
    cd "search_engine_microservice/$service"
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✅ $service built successfully"
    else
        echo "❌ Failed to build $service"
        exit 1
    fi
    cd ../..
done

echo "🎨 Building Angular UI..."
cd search-engine-ui
npm install
npm run build
if [ $? -eq 0 ]; then
    echo "✅ Angular UI built successfully"
else
    echo "❌ Failed to build Angular UI"
    exit 1
fi
cd ..

echo "🎉 All services built successfully!"