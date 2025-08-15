#!/bin/bash

echo "🧪 Running Tests for Search Engine Services..."

# Test all microservices
services=("discovery" "config-server" "gateway" "auth-server" "crawler" "indexer" "query")

for service in "${services[@]}"; do
    echo "🔍 Testing $service..."
    cd "search_engine_microservice/$service"
    mvn test
    if [ $? -eq 0 ]; then
        echo "✅ $service tests passed"
    else
        echo "❌ $service tests failed"
        exit 1
    fi
    cd ../..
done

echo "🎨 Testing Angular UI..."
cd search-engine-ui
npm test -- --watch=false --browsers=ChromeHeadless
if [ $? -eq 0 ]; then
    echo "✅ Angular UI tests passed"
else
    echo "❌ Angular UI tests failed"
    exit 1
fi
cd ..

echo "🎉 All tests passed!"