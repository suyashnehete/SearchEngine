#!/bin/bash

echo "🛑 Stopping Search Engine Services..."

# Stop microservices
if [ -f .service_pids ]; then
    echo "🔄 Stopping microservices..."
    PIDS=$(cat .service_pids)
    for pid in $PIDS; do
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            echo "✅ Stopped process $pid"
        fi
    done
    rm .service_pids
else
    echo "⚠️ No service PIDs found, attempting to kill by name..."
    pkill -f "discovery-.*\.jar"
    pkill -f "config-server-.*\.jar"
    pkill -f "auth-server-.*\.jar"
    pkill -f "gateway-.*\.jar"
    pkill -f "crawler-.*\.jar"
    pkill -f "indexer-.*\.jar"
    pkill -f "query-.*\.jar"
    pkill -f "ng serve"
fi

# Stop infrastructure services
echo "🐳 Stopping infrastructure services..."
docker-compose down

echo "🎉 All services stopped!"